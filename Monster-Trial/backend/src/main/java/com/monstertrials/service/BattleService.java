package com.monstertrials.service;

import com.monstertrials.data.GameData;
import com.monstertrials.model.BattleResult;
import com.monstertrials.model.BattleState;
import com.monstertrials.model.Boss;
import com.monstertrials.model.BossIntent;
import com.monstertrials.model.DamageType;
import com.monstertrials.model.RunState;
import com.monstertrials.model.Potion;
import com.monstertrials.model.Skill;
import com.monstertrials.model.StatusEffect;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class BattleService {
    private static final String POISON = "POISON";
    private static final String BURN = "BURN";
    private static final String STUN = "STUN";
    private static final String SHIELD = "SHIELD";
    private static final String STONE_SHIELD = "STONE_SHIELD";
    private static final String SHIELD_REFLECT = "SHIELD_REFLECT";
    private static final String STATUS_BLOCK = "STATUS_BLOCK";
    private static final String SLOW = "SLOW";
    private static final String CHILLED = "CHILLED";
    private static final String EXPOSE = "EXPOSE";
    private static final String CRACKED_ARMOR = "CRACKED_ARMOR";
    private static final String COMBO_READY = "COMBO_READY";
    private static final String RESISTANCE_TONIC = "RESISTANCE_TONIC";
    private static final int MAX_BATTLE_LOG_ENTRIES = 120;

    private final RunService runService;
    private final SkillService skillService;
    private final RewardService rewardService;
    private final GameData data;

    public BattleService(RunService runService, SkillService skillService, RewardService rewardService, GameData data) {
        this.runService = runService;
        this.skillService = skillService;
        this.rewardService = rewardService;
        this.data = data;
    }

    public RunState startBattle(UUID runId) {
        RunState run = runService.getRun(runId);
        synchronized (run) {
            if (run.isRunComplete()) {
                throw new IllegalStateException("This run is complete. Play again with a defeated boss or start a new run.");
            }
            Boss boss = runService.getCurrentBoss(run);
            return startBattleForBoss(run, boss, "The trial begins against " + boss.getName() + ".");
        }
    }

    public RunState retryBattle(UUID runId) {
        RunState run = runService.getRun(runId);
        synchronized (run) {
            Boss boss = null;
            if (run.getBattle() != null && run.getBattle().getResult() == BattleResult.HERO_LOSE) {
                boss = run.getBattle().getBoss();
            }
            if (boss == null) {
                boss = runService.getCurrentBoss(run);
            }
            return startBattleForBoss(run, boss, "Hero retries the trial against " + boss.getName() + ".");
        }
    }

    public RunState replayBoss(UUID runId) {
        RunState run = runService.getRun(runId);
        synchronized (run) {
            Boss boss = runService.getCurrentBoss(run);
            return replayBoss(runId, boss.getId());
        }
    }

    public RunState replayBoss(UUID runId, String bossId) {
        RunState run = runService.getRun(runId);
        synchronized (run) {
            runService.ensureNoActiveBattle(run);
            Boss boss = data.getBoss(bossId);
            if (run.getBossWinCounts().getOrDefault(boss.getId(), 0) <= 0) {
                throw new IllegalStateException("Defeat " + boss.getName() + " once before playing it again.");
            }
            return startBattleForBoss(run, boss, "Play again started for " + boss.getName() + ".");
        }
    }

    public RunState clearCompletedBattle(UUID runId) {
        return runService.clearCompletedBattle(runId);
    }

    private RunState startBattleForBoss(RunState run, Boss boss, String openingLog) {
        runService.ensureNoActiveBattle(run);
        if (run.getHero().getEquippedSkillIds().isEmpty()) {
            throw new IllegalStateException("Equip at least one skill before starting battle.");
        }
        if (!skillService.hasOffensiveEquipped(run)) {
            throw new IllegalStateException("You must equip at least one offensive skill before starting battle.");
        }

        BattleState battle = new BattleState();
        battle.setBoss(boss);
        battle.setHeroHp(run.getHero().getMaxHp());
        battle.setHeroEnergy(run.getHero().getMaxEnergy());
        battle.setHeroMaxMomentum(100 + run.getUpgradeLevels().getOrDefault("MOMENTUM_DISCIPLINE", 0) * 5);
        battle.setHeroMomentum(run.getUpgradeLevels().getOrDefault("MOMENTUM_DISCIPLINE", 0) >= 3 ? 15 : 0);
        battle.setBossHp(boss.getMaxHp());
        battle.getBattleLog().add(openingLog);
        battle.getBattleLog().add(boss.getName() + " is weak to " + boss.getWeakness().getLabel() + " and resists " + boss.getResistance().getLabel() + ".");
        chooseNextBossIntent(run, battle);

        run.getHero().setCurrentHp(run.getHero().getMaxHp());
        run.getHero().setCurrentEnergy(run.getHero().getMaxEnergy());
        run.setBattle(battle);
        run.setLastReward(null);
        run.touch();
        trimBattleLog(battle);
        return run;
    }

    public RunState useSkill(UUID runId, String skillId) {
        RunState run = runService.getRun(runId);
        synchronized (run) {
            BattleState battle = activeBattle(run);

        if (!run.getHero().getEquippedSkillIds().contains(skillId)) {
            throw new IllegalArgumentException("Skill is not equipped: " + skillId);
        }
        Skill skill = data.getSkill(skillId);

        if (consumeStatus(battle.getHeroStatusEffects(), STUN)) {
            battle.getBattleLog().add("Hero is stunned and skips the turn.");
            applyHeroEndOfTurn(run, battle);
            if (finishIfNeeded(run, battle)) {
                return run;
            }
            executeBossIntent(run, battle);
            applyBossEndOfTurn(run, battle);
            finishRoundOrBattle(run, battle);
            return run;
        }

        if (battle.getHeroEnergy() < skill.getEnergyCost()) {
            throw new IllegalStateException("Not enough energy for " + skill.getName() + ".");
        }
        if (battle.getHeroCooldowns().getOrDefault(skill.getId(), 0) > 0) {
            throw new IllegalStateException(skill.getName() + " is on cooldown.");
        }

        battle.setHeroEnergy(battle.getHeroEnergy() - skill.getEnergyCost());
        if (skill.getCooldown() > 0) {
            battle.getHeroCooldowns().put(skill.getId(), skill.getCooldown() + 1);
        }

        battle.getBattleLog().add("Hero uses " + skill.getName() + ".");
        boolean interrupted = applyHeroSkill(run, battle, skill);
        applyHeroEndOfTurn(run, battle);

        if (finishIfNeeded(run, battle)) {
            return run;
        }

        if (interrupted) {
            battle.getBattleLog().add(battle.getBoss().getName() + "'s intent is interrupted!");
            addMomentum(battle, 12, "interrupting the boss");
        } else {
            executeBossIntent(run, battle);
        }

        applyBossEndOfTurn(run, battle);
        finishRoundOrBattle(run, battle);
        run.touch();
        return run;
        }
    }


    public RunState usePotion(UUID runId, String potionId) {
        RunState run = runService.getRun(runId);
        synchronized (run) {
            BattleState battle = activeBattle(run);

            Potion potion = data.getPotion(potionId);

            if (potion.isConsumesAction() && consumeStatus(battle.getHeroStatusEffects(), STUN)) {
                battle.getBattleLog().add("Hero is stunned and cannot use an action potion this turn.");
                applyHeroEndOfTurn(run, battle);
                if (finishIfNeeded(run, battle)) {
                    return run;
                }
                executeBossIntent(run, battle);
                applyBossEndOfTurn(run, battle);
                finishRoundOrBattle(run, battle);
                return run;
            }

            int quantity = run.getPotionInventory().getOrDefault(potion.getId(), 0);
            if (quantity <= 0) {
                throw new IllegalStateException("You do not have a " + potion.getName() + ".");
            }
            if (potion.isOneUsePerBattle() && battle.getUsedPotionIds().contains(potion.getId())) {
                throw new IllegalStateException(potion.getName() + " can only be used once per battle.");
            }

            battle.getBattleLog().add("Hero uses " + potion.getName() + ".");
            applyPotionEffect(run, battle, potion);
            consumePotion(run, battle, potion);

            if (!potion.isConsumesAction()) {
                battle.getBattleLog().add(potion.getName() + " is a quick-use potion. You can still attack this turn.");
                run.touch();
                return run;
            }

            applyHeroEndOfTurn(run, battle);
            if (finishIfNeeded(run, battle)) {
                return run;
            }

            executeBossIntent(run, battle);
            applyBossEndOfTurn(run, battle);
            finishRoundOrBattle(run, battle);
            run.touch();
            return run;
        }
    }

    private void applyPotionEffect(RunState run, BattleState battle, Potion potion) {
        switch (potion.getEffect()) {
            case "heal" -> {
                if (battle.getHeroHp() >= run.getHero().getMaxHp()) {
                    throw new IllegalStateException("Hero is already at full HP.");
                }
                int oldHp = battle.getHeroHp();
                battle.setHeroHp(Math.min(run.getHero().getMaxHp(), battle.getHeroHp() + 28));
                battle.getBattleLog().add("Healing Potion restores " + (battle.getHeroHp() - oldHp) + " HP.");
            }
            case "resist_next_hit" -> {
                addOrRefreshStatus(battle.getHeroStatusEffects(), RESISTANCE_TONIC, "Resistance Potion", 1, "Hero prepares to resist the next incoming hit.", battle);
            }
            case "cleanse" -> {
                int removed = removeCleansableDebuffs(battle.getHeroStatusEffects());
                if (removed <= 0) {
                    throw new IllegalStateException("Hero has no removable debuffs.");
                }
                battle.getBattleLog().add("Cleansing Potion removes " + removed + " debuff" + (removed == 1 ? "." : "s."));
            }
            default -> throw new IllegalStateException("Unsupported potion effect: " + potion.getEffect());
        }
    }

    private void consumePotion(RunState run, BattleState battle, Potion potion) {
        int quantity = run.getPotionInventory().getOrDefault(potion.getId(), 0);
        if (quantity <= 1) {
            run.getPotionInventory().remove(potion.getId());
        } else {
            run.getPotionInventory().put(potion.getId(), quantity - 1);
        }
        if (potion.isOneUsePerBattle()) {
            battle.getUsedPotionIds().add(potion.getId());
        }
    }

    private int removeCleansableDebuffs(List<StatusEffect> statuses) {
        int before = statuses.size();
        statuses.removeIf(effect -> POISON.equals(effect.getId())
                || BURN.equals(effect.getId())
                || SLOW.equals(effect.getId())
                || CHILLED.equals(effect.getId())
                || EXPOSE.equals(effect.getId()));
        return before - statuses.size();
    }

    private BattleState activeBattle(RunState run) {
        BattleState battle = run.getBattle();
        if (battle == null) {
            throw new IllegalStateException("No battle is active. Start a battle first.");
        }
        if (battle.getResult() != BattleResult.ONGOING) {
            throw new IllegalStateException("The current battle is already finished.");
        }
        return battle;
    }

    private boolean applyHeroSkill(RunState run, BattleState battle, Skill skill) {
        boolean targetShieldedBefore = hasAnyShield(battle.getBossStatusEffects());
        boolean empowered = false;

        if (skill.isOffensive()) {
            HeroDamage heroDamage = calculateHeroDamage(run, battle, skill);
            empowered = heroDamage.empowered();
            battle.setLastEffectiveness(heroDamage.effectiveness());
            if ("SUPER_EFFECTIVE".equals(heroDamage.effectiveness())) {
                battle.getBattleLog().add("Super effective! " + battle.getBoss().getName() + " is weak to " + skill.getType().getLabel() + ".");
                addMomentum(battle, 18, "hitting a weakness");
            } else if ("NOT_VERY_EFFECTIVE".equals(heroDamage.effectiveness())) {
                battle.getBattleLog().add("Not very effective... " + battle.getBoss().getName() + " resists " + skill.getType().getLabel() + ".");
                addMomentum(battle, 8, "landing a resisted hit");
            } else {
                addMomentum(battle, 10, "attacking");
            }
            applyDamageToBoss(run, battle, heroDamage.damage(), "Hero", "Boss", "ignore_shield".equals(skill.getEffect()));
        } else {
            battle.setLastEffectiveness("NORMAL");
        }

        if ("momentum_hit".equals(skill.getEffect())) {
            addMomentum(battle, 3, "Iron Slash discipline");
        }
        if ("anti_shield".equals(skill.getEffect()) && targetShieldedBefore) {
            battle.getBattleLog().add("Piercing Strike exploits the enemy shield.");
        }
        if ("quick_combo".equals(skill.getEffect()) && consumeStatus(battle.getHeroStatusEffects(), COMBO_READY)) {
            battle.getBattleLog().add("Quick Slash combo triggers after Guard/Focus.");
        }

        return applySkillEffect(run, battle, skill, true, empowered);
    }

    private HeroDamage calculateHeroDamage(RunState run, BattleState battle, Skill skill) {
        boolean empowered = skill.isOffensive() && battle.getHeroMomentum() >= 100;
        if (empowered) {
            battle.setHeroMomentum(battle.getHeroMomentum() - 100);
            battle.getBattleLog().add("Momentum unleashed! " + skill.getName() + " is Empowered.");
        }

        double typeMultiplier = 1.0;
        String effectiveness = "NORMAL";
        boolean cracked = hasStatus(battle.getBossStatusEffects(), CRACKED_ARMOR);
        if (skill.getType() == battle.getBoss().getWeakness()) {
            typeMultiplier = isDragonPhaseTwo(battle) && skill.getType() == DamageType.POISON ? 1.75 : 1.5;
            effectiveness = "SUPER_EFFECTIVE";
        } else if (skill.getType() == battle.getBoss().getResistance() && !empowered && !cracked) {
            typeMultiplier = 0.65;
            effectiveness = "NOT_VERY_EFFECTIVE";
        }

        int masteryLevel = run.getUpgradeLevels().getOrDefault(masteryUpgradeId(skill.getType()), 0);
        double masteryMultiplier = 1.0 + masteryLevel * 0.05;
        double levelMultiplier = 1.0 + Math.max(0, run.getHero().getLevel() - 1) * 0.02;
        double outgoingModifier = consumeOutgoingDamageDebuffs(battle.getHeroStatusEffects(), "Hero", battle);

        if (run.getHero().getMaxHp() > 0 && battle.getHeroHp() <= run.getHero().getMaxHp() * 0.35) {
            int adrenaline = run.getUpgradeLevels().getOrDefault("ADRENALINE", 0);
            if (adrenaline > 0) {
                outgoingModifier *= 1.0 + adrenaline * 0.05;
                battle.getBattleLog().add("Adrenaline increases outgoing damage by " + (adrenaline * 5) + "%.");
            }
        }

        if ("quick_combo".equals(skill.getEffect()) && hasStatus(battle.getHeroStatusEffects(), COMBO_READY)) {
            outgoingModifier *= 1.40;
        }

        if ("anti_shield".equals(skill.getEffect()) && hasAnyShield(battle.getBossStatusEffects())) {
            outgoingModifier *= 1.20;
        }

        if ("finisher".equals(skill.getEffect()) && (hasStatus(battle.getBossStatusEffects(), POISON) || hasStatus(battle.getBossStatusEffects(), BURN))) {
            outgoingModifier *= 1.25;
            battle.getBattleLog().add("Dragon Claw punishes the active damage-over-time effect.");
        }

        Optional<StatusEffect> expose = findStatus(battle.getBossStatusEffects(), EXPOSE);
        if (expose.isPresent()) {
            double exposeBonus = skill.getType() == battle.getBoss().getWeakness() ? 1.35 : 1.25;
            outgoingModifier *= exposeBonus;
            battle.getBattleLog().add("Expose makes this hit deal +" + Math.round((exposeBonus - 1.0) * 100) + "% damage.");
            battle.getBossStatusEffects().remove(expose.get());
        }

        if (empowered) {
            outgoingModifier *= 1.35;
        }

        int damage = Math.max(0, (int) Math.round(skill.getDamage() * typeMultiplier * masteryMultiplier * levelMultiplier * outgoingModifier));
        return new HeroDamage(damage, effectiveness, empowered);
    }

    private boolean applySkillEffect(RunState run, BattleState battle, Skill skill, boolean heroCaster) {
        return applySkillEffect(run, battle, skill, heroCaster, false);
    }

    private boolean applySkillEffect(RunState run, BattleState battle, Skill skill, boolean heroCaster, boolean empowered) {
        String effect = skill.getEffect() == null ? "none" : skill.getEffect();
        String caster = heroCaster ? "Hero" : battle.getBoss().getName();
        String target = heroCaster ? battle.getBoss().getName() : "Hero";
        List<StatusEffect> targetStatuses = heroCaster ? battle.getBossStatusEffects() : battle.getHeroStatusEffects();
        List<StatusEffect> casterStatuses = heroCaster ? battle.getHeroStatusEffects() : battle.getBossStatusEffects();
        boolean targetIsHero = !heroCaster;
        boolean interrupt = false;

        switch (effect) {
            case "none", "momentum_hit", "anti_shield", "quick_combo", "finisher" -> {
            }
            case "shield" -> {
                int guardTraining = run.getUpgradeLevels().getOrDefault("GUARD_TRAINING", 0);
                int statusBlocks = guardTraining >= 3 ? 2 : 1;
                addOrRefreshStatus(casterStatuses, SHIELD, "Guard Shield", 1, caster + " braces behind a shield.", battle);
                addOrRefreshStatus(casterStatuses, STATUS_BLOCK, "Status Block", 1, "", battle);
                findStatus(casterStatuses, STATUS_BLOCK).ifPresent(effectStatus -> effectStatus.setStacks(statusBlocks));
                int before = battle.getHeroEnergy();
                if (heroCaster) {
                    battle.setHeroEnergy(Math.min(run.getHero().getMaxEnergy(), battle.getHeroEnergy() + 12));
                    battle.getBattleLog().add("Guard restores " + (battle.getHeroEnergy() - before) + " energy.");
                    addMomentum(battle, 3, "preparing a guard");
                    addOrRefreshStatus(casterStatuses, COMBO_READY, "Combo Ready", 1, "Hero is ready for a quick follow-up.", battle);
                }
            }
            case "shield_2_turns" -> addOrRefreshStatus(casterStatuses, STONE_SHIELD, "Stone Shield", 2, caster + " gains 2 Stone Shield stacks.", battle);
            case "shield_reflect" -> addOrRefreshStatus(casterStatuses, SHIELD_REFLECT, "Reflect Shield", 1, caster + " gains a reflective shield.", battle);
            case "restore_energy" -> {
                if (heroCaster) {
                    int before = battle.getHeroEnergy();
                    battle.setHeroEnergy(Math.min(run.getHero().getMaxEnergy(), battle.getHeroEnergy() + 30));
                    battle.getBattleLog().add("Hero restores " + (battle.getHeroEnergy() - before) + " energy.");
                    int heal = run.getUpgradeLevels().getOrDefault("BATTLE_FOCUS", 0) * 5;
                    if (heal > 0) {
                        int oldHp = battle.getHeroHp();
                        battle.setHeroHp(Math.min(run.getHero().getMaxHp(), battle.getHeroHp() + heal));
                        battle.getBattleLog().add("Battle Focus heals " + (battle.getHeroHp() - oldHp) + " HP.");
                    }
                    removeDebuff(battle.getHeroStatusEffects(), SLOW, "Slow", battle);
                    removeDebuff(battle.getHeroStatusEffects(), CHILLED, "Chilled", battle);
                    removeDebuff(battle.getHeroStatusEffects(), EXPOSE, "Expose", battle);
                    addMomentum(battle, 10, "focusing");
                    addOrRefreshStatus(casterStatuses, COMBO_READY, "Combo Ready", 1, "Hero is ready for a quick follow-up.", battle);
                }
            }
            case "poison" -> addPoison(targetStatuses, targetIsHero, target, battle);
            case "burn" -> {
                double chance = empowered && heroCaster ? 1.0 : 0.60;
                if (ThreadLocalRandom.current().nextDouble() < chance) {
                    addStatusToTarget(targetStatuses, targetIsHero, BURN, "Burn", 3, 1, target + " is burning.", battle);
                } else {
                    battle.getBattleLog().add(caster + " fails to ignite " + target + ".");
                }
            }
            case "burn_chance" -> {
                if (ThreadLocalRandom.current().nextDouble() < 0.35) {
                    addStatusToTarget(targetStatuses, targetIsHero, BURN, "Burn", 3, 1, target + " is burning.", battle);
                } else {
                    battle.getBattleLog().add(target + " avoids the burn.");
                }
            }
            case "stun_chance" -> {
                boolean heavy = battle.getBossIntent() != null && "HEAVY_ATTACK".equals(battle.getBossIntent().getIntentType());
                double chance = heavy ? 0.45 : 0.30;
                if (ThreadLocalRandom.current().nextDouble() < chance) {
                    addStatusToTarget(targetStatuses, targetIsHero, STUN, "Stun", 1, 1, target + " is stunned.", battle);
                    interrupt = heroCaster && battle.getBossIntent() != null && battle.getBossIntent().isInterruptible();
                } else {
                    battle.getBattleLog().add(target + " avoids the stun.");
                }
            }
            case "slow_chance" -> {
                boolean heavy = battle.getBossIntent() != null && "HEAVY_ATTACK".equals(battle.getBossIntent().getIntentType());
                double chance = heavy ? 1.0 : 0.35;
                if (ThreadLocalRandom.current().nextDouble() < chance) {
                    addStatusToTarget(targetStatuses, targetIsHero, SLOW, "Slow", 1, 1, target + " is slowed.", battle);
                    if (heavy && heroCaster && battle.getBossIntent().isInterruptible()) {
                        interrupt = true;
                        battle.getBattleLog().add("Slow delays the heavy attack.");
                    }
                } else {
                    battle.getBattleLog().add(target + " avoids the slow.");
                }
            }
            case "weakness_debuff" -> addStatusToTarget(targetStatuses, targetIsHero, EXPOSE, "Expose", 1, 1, target + " is exposed. The next hit against it will deal bonus damage.", battle);
            case "ignore_shield" -> {
                if (heroCaster && hasAnyShield(targetStatuses)) {
                    addStatusToTarget(targetStatuses, targetIsHero, CRACKED_ARMOR, "Cracked Armor", 2, 1, target + "'s armor is cracked and resistance is weakened.", battle);
                }
            }
            default -> battle.getBattleLog().add(caster + " triggers " + effect + ".");
        }
        return interrupt;
    }

    private void executeBossIntent(RunState run, BattleState battle) {
        if (battle.getBossHp() <= 0 || battle.getHeroHp() <= 0 || battle.getBossIntent() == null) {
            return;
        }
        if (consumeStatus(battle.getBossStatusEffects(), STUN)) {
            battle.getBattleLog().add(battle.getBoss().getName() + " is stunned and skips the intent.");
            return;
        }

        BossIntent intent = battle.getBossIntent();
        Skill skill = findBossSkill(battle.getBoss(), intent.getSkillId());
        DamageType intentDamageType = damageTypeFromLabel(intent.getDamageType(), skill.getType());
        battle.getBattleLog().add(battle.getBoss().getName() + " executes " + intent.getName() + ".");

        if (intent.getDamage() > 0 && intentDamageType.isOffensive()) {
            double outgoing = consumeOutgoingDamageDebuffs(battle.getBossStatusEffects(), battle.getBoss().getName(), battle);
            int baseDamage = Math.max(0, (int) Math.round(intent.getDamage() * outgoing));
            applyDamageToHero(run, battle, baseDamage, intentDamageType, "ignore_shield".equals(skill.getEffect()));
        }
        applySkillEffect(run, battle, skill, false);
    }

    private Skill findBossSkill(Boss boss, String skillId) {
        return data.getBossBattleSkills(boss.getId()).stream()
                .filter(skill -> skill.getId().equals(skillId))
                .findFirst()
                .orElse(new Skill(skillId, "Basic Attack", boss.getMainDamageTypes().isEmpty() ? DamageType.SLASH : boss.getMainDamageTypes().get(0),
                        basicDamageForBoss(boss.getId()), 0, 0, "none", "A basic attack."));
    }

    private void chooseNextBossIntent(RunState run, BattleState battle) {
        updatePhase(battle);
        Boss boss = battle.getBoss();
        int turn = battle.getTurnNumber();
        Skill skill;
        boolean heavy = false;
        String description = "Incoming action.";

        switch (boss.getId()) {
            case "FOREST_GOBLIN" -> {
                if (battle.isPhaseTwo() && turn % 4 == 0) {
                    skill = new Skill("GOBLIN_FRENZY", "Frenzy Slash", DamageType.SLASH, 18, 0, 0, "none", "An interruptible flurry in phase 2.");
                    heavy = true;
                    description = "Heavy phase-2 slash. Guard or interrupt this.";
                } else if (turn % 3 == 0) {
                    skill = findBossSkill(boss, "GOBLIN_DIRTY_TRICK");
                    description = "Applies Expose if not blocked.";
                } else {
                    skill = findBossSkill(boss, "GOBLIN_SLASH");
                }
            }
            case "VENOM_SPIDER" -> {
                if (turn % 4 == 0) {
                    skill = findBossSkill(boss, "SPIDER_WEB_TRAP");
                    heavy = true;
                    description = "Interruptible Web Trap can stun.";
                } else if (turn % 2 == 0) {
                    skill = findBossSkill(boss, "SPIDER_POISON_BITE");
                    description = battle.isPhaseTwo() ? "Toxic phase poison is stronger." : "Applies stacking poison.";
                } else {
                    skill = findBossSkill(boss, "SPIDER_FANG_STAB");
                }
            }
            case "STONE_GOLEM" -> {
                if (turn % 3 == 0) {
                    skill = new Skill("GOLEM_HEAVY_SLAM", "Heavy Slam", DamageType.PIERCE, battle.isPhaseTwo() ? 32 : 28, 0, 0, "none", "A telegraphed heavy slam.");
                    heavy = true;
                    description = "Very heavy interruptible attack. Guard, Slow, or Web Trap helps.";
                } else if (turn % 4 == 0) {
                    skill = findBossSkill(boss, "GOLEM_STONE_SKIN");
                    description = "Golem is preparing a shield.";
                } else {
                    skill = findBossSkill(boss, "GOLEM_ROCK_PIERCE");
                }
            }
            case "FROST_REVENANT" -> {
                if (battle.isPhaseTwo() && turn % 3 == 0) {
                    skill = findBossSkill(boss, "REVENANT_FROZEN_ARMOR");
                    description = "Ice Mirror: reflect shield is coming.";
                } else if (turn % 2 == 0) {
                    skill = findBossSkill(boss, "REVENANT_FROST_BOLT");
                    description = "Can Slow your next action.";
                } else {
                    skill = findBossSkill(boss, "REVENANT_ICE_CUT");
                }
            }
            case "DRAGON_KING" -> {
                if (battle.isPhaseTwo()) {
                    if (turn % 2 == 1) {
                        skill = findBossSkill(boss, "DRAGON_INFERNO_BREATH");
                        heavy = true;
                        description = "Royal Fury: huge fire breath. Poison weakness is stronger in phase 2.";
                    } else {
                        skill = findBossSkill(boss, "DRAGON_CLAW_BOSS");
                        description = "Royal Fury claw. Guard if low HP.";
                    }
                } else if (turn % 4 == 0) {
                    skill = findBossSkill(boss, "DRAGON_INFERNO_BREATH");
                    heavy = true;
                    description = "Telegraphed heavy fire attack. Guard or interrupt it.";
                } else if (turn % 3 == 0) {
                    skill = findBossSkill(boss, "DRAGON_FLAME_BITE");
                    description = "Can apply Burn.";
                } else {
                    skill = findBossSkill(boss, "DRAGON_CLAW_BOSS");
                }
            }
            default -> {
                skill = findBossSkill(boss, "BOSS_BASIC_ATTACK");
            }
        }

        int intentDamage = skill.getDamage();
        if (battle.isPhaseTwo() && boss.getId().equals("VENOM_SPIDER") && "SPIDER_POISON_BITE".equals(skill.getId())) {
            intentDamage += 2;
        }
        if (battle.isPhaseTwo() && boss.getId().equals("FOREST_GOBLIN") && skill.getType() == DamageType.SLASH) {
            intentDamage += 2;
        }

        battle.setBossIntent(new BossIntent(
                skill.getId(),
                skill.getName(),
                intentDamage,
                skill.getType().getLabel(),
                skill.getEffect(),
                heavy ? "HEAVY_ATTACK" : (skill.isOffensive() ? "ATTACK" : "DEFENSE"),
                heavy,
                description
        ));
        battle.getBattleLog().add("Intent: " + boss.getName() + " prepares " + skill.getName()
                + (skill.isOffensive() ? " (" + intentDamage + " " + skill.getType().getLabel() + " damage)." : "."));
    }

    private void updatePhase(BattleState battle) {
        boolean phaseTwoNow = battle.getBossHp() <= battle.getBoss().getMaxHp() / 2;
        if (phaseTwoNow && !battle.isPhaseTwo()) {
            battle.setPhaseTwo(true);
            String phaseMessage = switch (battle.getBoss().getId()) {
                case "FOREST_GOBLIN" -> "Forest Goblin enters Frenzy: more Slash damage, but it becomes easier to burst down.";
                case "VENOM_SPIDER" -> "Venom Spider enters Toxic Web: poison pressure increases.";
                case "STONE_GOLEM" -> "Stone Golem's core cracks: it deals more Pierce damage but suffers harder Frost punishment.";
                case "FROST_REVENANT" -> "Frost Revenant activates Ice Mirror: watch for reflect shields.";
                case "DRAGON_KING" -> "Dragon King enters Royal Fury: Inferno Breath becomes frequent, but Poison weakness grows.";
                default -> battle.getBoss().getName() + " enters phase 2.";
            };
            battle.getBattleLog().add(phaseMessage);
        }
    }

    private int basicDamageForBoss(String bossId) {
        return switch (bossId) {
            case "FOREST_GOBLIN" -> 10;
            case "VENOM_SPIDER" -> 13;
            case "STONE_GOLEM" -> 18;
            case "FROST_REVENANT" -> 17;
            case "DRAGON_KING" -> 24;
            default -> 12;
        };
    }

    private void applyDamageToBoss(RunState run, BattleState battle, int amount, String attacker, String target, boolean ignoreShield) {
        boolean shieldedBefore = hasAnyShield(battle.getBossStatusEffects());
        boolean reflectActive = hasStatus(battle.getBossStatusEffects(), SHIELD_REFLECT);
        int finalDamage = applyShieldReduction(run, battle.getBossStatusEffects(), amount, ignoreShield, target, battle, false);
        battle.setBossHp(battle.getBossHp() - finalDamage);
        battle.getBattleLog().add(target + " takes " + finalDamage + " damage.");
        if (shieldedBefore && ignoreShield) {
            addStatusToTarget(battle.getBossStatusEffects(), false, CRACKED_ARMOR, "Cracked Armor", 2, 1, target + "'s armor is cracked.", battle);
        }
        if (reflectActive && finalDamage > 0) {
            battle.setHeroHp(Math.max(0, battle.getHeroHp() - 10));
            battle.getBattleLog().add("Reflect shield deals 10 damage to " + attacker + ".");
        }
    }

    private void applyDamageToHero(RunState run, BattleState battle, int baseDamage, DamageType damageType, boolean ignoreShield) {
        double resistance = totalResistance(run, battle, damageType);
        int resistedDamage = Math.max(0, (int) Math.round(baseDamage * (1.0 - resistance)));
        Optional<StatusEffect> exposedHero = findStatus(battle.getHeroStatusEffects(), EXPOSE);
        if (exposedHero.isPresent()) {
            resistedDamage = (int) Math.round(resistedDamage * 1.25);
            battle.getHeroStatusEffects().remove(exposedHero.get());
            battle.getBattleLog().add("Expose makes the incoming hit deal +25% damage.");
        }
        if (baseDamage > 0 && resistedDamage == 0) {
            resistedDamage = 1;
        }
        if (resistance > 0) {
            battle.getBattleLog().add("Hero resists " + Math.round(resistance * 100) + "% of incoming " + damageType.getLabel() + " damage.");
        }
        boolean reflectActive = hasStatus(battle.getHeroStatusEffects(), SHIELD_REFLECT);
        int finalDamage = applyShieldReduction(run, battle.getHeroStatusEffects(), resistedDamage, ignoreShield, "Hero", battle, true);
        battle.setHeroHp(Math.max(0, battle.getHeroHp() - finalDamage));
        battle.getBattleLog().add("Hero takes " + finalDamage + " " + damageType.getLabel() + " damage.");
        if (reflectActive && finalDamage > 0) {
            battle.setBossHp(Math.max(0, battle.getBossHp() - 10));
            battle.getBattleLog().add("Reflect shield deals 10 damage to " + battle.getBoss().getName() + ".");
            addOrRefreshStatus(battle.getBossStatusEffects(), CHILLED, "Chilled", 1, battle.getBoss().getName() + " is chilled and will deal less damage.", battle);
        }
    }

    private int applyShieldReduction(RunState run, List<StatusEffect> statuses, int damage, boolean ignoreShield, String target, BattleState battle, boolean targetHero) {
        Optional<StatusEffect> shield = findShield(statuses);
        if (shield.isEmpty() || damage <= 0) {
            return damage;
        }

        double reduction = switch (shield.get().getId()) {
            case SHIELD -> Math.min(0.80, 0.65 + run.getUpgradeLevels().getOrDefault("GUARD_TRAINING", 0) * 0.05);
            case STONE_SHIELD -> 0.55;
            case SHIELD_REFLECT -> 0.50;
            default -> 0.50;
        };
        if (ignoreShield) {
            reduction *= 0.50;
        }
        int blocked = (int) Math.round(damage * reduction);
        int reducedDamage = Math.max(0, damage - blocked);
        if (damage > 0 && reducedDamage == 0) {
            reducedDamage = 1;
            blocked = damage - 1;
        }
        battle.getBattleLog().add("Shield blocks " + blocked + " damage.");
        decrementStackOrRemove(statuses, shield.get());
        if (targetHero && blocked > 0) {
            addMomentum(battle, 8, "blocking damage");
        }
        return reducedDamage;
    }

    private double totalResistance(RunState run, BattleState battle, DamageType damageType) {
        double generalResistance = run.getUpgradeLevels().getOrDefault("GENERAL_RESISTANCE", 0) * 0.02;
        double specificResistance = run.getUpgradeLevels().getOrDefault(resistanceUpgradeId(damageType), 0) * 0.06;
        Optional<StatusEffect> potionResistance = findStatus(battle.getHeroStatusEffects(), RESISTANCE_TONIC);
        double temporaryResistance = potionResistance.isPresent() ? 0.25 : 0.0;
        double cap = potionResistance.isPresent() ? 0.65 : 0.50;
        double total = Math.min(cap, generalResistance + specificResistance + temporaryResistance);
        potionResistance.ifPresent(effect -> {
            battle.getBattleLog().add("Resistance Potion cushions the hit.");
            battle.getHeroStatusEffects().remove(effect);
        });
        return total;
    }

    private void applyHeroEndOfTurn(RunState run, BattleState battle) {
        applyDamageOverTime(battle.getHeroStatusEffects(), true, run, battle);
    }

    private void applyBossEndOfTurn(RunState run, BattleState battle) {
        applyDamageOverTime(battle.getBossStatusEffects(), false, run, battle);
    }

    private void applyDamageOverTime(List<StatusEffect> statuses, boolean targetHero, RunState run, BattleState battle) {
        Iterator<StatusEffect> iterator = statuses.iterator();
        while (iterator.hasNext()) {
            StatusEffect effect = iterator.next();
            int damage = 0;
            String damageName = null;
            if (POISON.equals(effect.getId())) {
                int perStack = targetHero && "VENOM_SPIDER".equals(battle.getBoss().getId()) && battle.isPhaseTwo() ? 8 : 5;
                damage = perStack * effect.getStacks();
                damageName = "poison";
            } else if (BURN.equals(effect.getId())) {
                damage = 8;
                damageName = "burn";
            }

            if (damage > 0) {
                if (targetHero) {
                    battle.setHeroHp(Math.max(0, battle.getHeroHp() - damage));
                    battle.getBattleLog().add("Hero takes " + damage + " " + damageName + " damage.");
                } else {
                    battle.setBossHp(Math.max(0, battle.getBossHp() - damage));
                    battle.getBattleLog().add(battle.getBoss().getName() + " takes " + damage + " " + damageName + " damage.");
                }
                effect.decrement();
                if (effect.getRemainingTurns() <= 0) {
                    battle.getBattleLog().add((targetHero ? "Hero" : battle.getBoss().getName()) + " is no longer affected by " + effect.getName() + ".");
                    iterator.remove();
                }
            }
        }
    }

    private void finishRoundOrBattle(RunState run, BattleState battle) {
        if (finishIfNeeded(run, battle)) {
            trimBattleLog(battle);
            return;
        }
        decrementCooldowns(battle.getHeroCooldowns());
        decrementCooldowns(battle.getBossCooldowns());
        int before = battle.getHeroEnergy();
        battle.setHeroEnergy(Math.min(run.getHero().getMaxEnergy(), battle.getHeroEnergy() + 8));
        if (battle.getHeroEnergy() > before) {
            battle.getBattleLog().add("Hero regenerates " + (battle.getHeroEnergy() - before) + " energy.");
        }
        battle.setTurnNumber(battle.getTurnNumber() + 1);
        chooseNextBossIntent(run, battle);
        runService.syncHeroFromBattle(run);
        run.touch();
        trimBattleLog(battle);
    }

    private boolean finishIfNeeded(RunState run, BattleState battle) {
        if (battle.getBossHp() <= 0) {
            finishHeroWin(run, battle);
            return true;
        }
        if (battle.getHeroHp() <= 0) {
            finishHeroLose(run, battle);
            return true;
        }
        return false;
    }

    private void finishHeroWin(RunState run, BattleState battle) {
        if (battle.getResult() == BattleResult.HERO_WIN) {
            return;
        }
        battle.setBossHp(0);
        battle.setResult(BattleResult.HERO_WIN);
        battle.getBattleLog().add(battle.getBoss().getName() + " is defeated.");
        battle.getBattleLog().add("Hero wins the trial!");
        runService.syncHeroFromBattle(run);
        rewardService.applyVictoryRewards(run, battle);
        run.touch();
        trimBattleLog(battle);
    }

    private void finishHeroLose(RunState run, BattleState battle) {
        if (battle.getResult() == BattleResult.HERO_LOSE) {
            return;
        }
        battle.setHeroHp(0);
        battle.setResult(BattleResult.HERO_LOSE);
        battle.getBattleLog().add("Hero falls in battle.");
        battle.getBattleLog().add("Defeat is not the end. Upgrade, re-equip, and retry the current boss.");
        runService.syncHeroFromBattle(run);
        run.touch();
        trimBattleLog(battle);
    }

    private void decrementCooldowns(Map<String, Integer> cooldowns) {
        List<String> remove = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : cooldowns.entrySet()) {
            int next = Math.max(0, entry.getValue() - 1);
            if (next <= 0) {
                remove.add(entry.getKey());
            } else {
                entry.setValue(next);
            }
        }
        remove.forEach(cooldowns::remove);
    }

    private void addPoison(List<StatusEffect> statuses, boolean targetHero, String target, BattleState battle) {
        if (targetHero) {
            Optional<StatusEffect> block = findStatus(statuses, STATUS_BLOCK);
            if (block.isPresent()) {
                battle.getBattleLog().add("Guard blocks Poison.");
                decrementStackOrRemove(statuses, block.get());
                return;
            }
        }
        Optional<StatusEffect> existing = findStatus(statuses, POISON);
        if (existing.isPresent()) {
            existing.get().incrementStacks(3);
            existing.get().setRemainingTurns(4);
        } else {
            statuses.add(new StatusEffect(POISON, "Poison", 4, 1));
        }
        int stacks = findStatus(statuses, POISON).map(StatusEffect::getStacks).orElse(1);
        battle.getBattleLog().add(target + " is poisoned (" + stacks + "/3 stacks).");
    }

    private boolean addStatusToTarget(List<StatusEffect> statuses, boolean targetHero, String id, String name, int turns, int stacks, String logMessage, BattleState battle) {
        if (targetHero && (POISON.equals(id) || BURN.equals(id) || STUN.equals(id) || SLOW.equals(id) || EXPOSE.equals(id))) {
            Optional<StatusEffect> block = findStatus(statuses, STATUS_BLOCK);
            if (block.isPresent()) {
                battle.getBattleLog().add("Guard blocks " + name + ".");
                decrementStackOrRemove(statuses, block.get());
                return false;
            }
        }
        addOrRefreshStatus(statuses, id, name, turns, logMessage, battle);
        findStatus(statuses, id).ifPresent(effect -> effect.setStacks(Math.max(effect.getStacks(), stacks)));
        return true;
    }

    private void addOrRefreshStatus(List<StatusEffect> statuses, String id, String name, int turns, String logMessage, BattleState battle) {
        Optional<StatusEffect> existing = findStatus(statuses, id);
        if (existing.isPresent()) {
            existing.get().setRemainingTurns(Math.max(existing.get().getRemainingTurns(), turns));
            existing.get().setStacks(Math.max(existing.get().getStacks(), turns > 1 && (STONE_SHIELD.equals(id) || STATUS_BLOCK.equals(id)) ? turns : existing.get().getStacks()));
        } else {
            int stacks = STONE_SHIELD.equals(id) ? 2 : 1;
            statuses.add(new StatusEffect(id, name, turns, stacks));
        }
        if (logMessage != null && !logMessage.isBlank()) {
            battle.getBattleLog().add(logMessage);
        }
    }

    private void decrementStackOrRemove(List<StatusEffect> statuses, StatusEffect effect) {
        if (effect.getStacks() > 1) {
            effect.setStacks(effect.getStacks() - 1);
        } else {
            statuses.remove(effect);
        }
    }

    private void removeDebuff(List<StatusEffect> statuses, String id, String label, BattleState battle) {
        Optional<StatusEffect> effect = findStatus(statuses, id);
        if (effect.isPresent()) {
            statuses.remove(effect.get());
            battle.getBattleLog().add("Focus cleanses " + label + ".");
        }
    }

    private Optional<StatusEffect> findStatus(List<StatusEffect> statuses, String id) {
        return statuses.stream().filter(effect -> id.equals(effect.getId())).findFirst();
    }

    private Optional<StatusEffect> findShield(List<StatusEffect> statuses) {
        Optional<StatusEffect> shield = findStatus(statuses, SHIELD_REFLECT);
        if (shield.isEmpty()) shield = findStatus(statuses, STONE_SHIELD);
        if (shield.isEmpty()) shield = findStatus(statuses, SHIELD);
        return shield;
    }

    private boolean hasStatus(List<StatusEffect> statuses, String id) {
        return findStatus(statuses, id).isPresent();
    }

    private boolean hasAnyShield(List<StatusEffect> statuses) {
        return findShield(statuses).isPresent();
    }

    private boolean consumeStatus(List<StatusEffect> statuses, String id) {
        Optional<StatusEffect> found = findStatus(statuses, id);
        if (found.isEmpty()) {
            return false;
        }
        statuses.remove(found.get());
        return true;
    }

    private double consumeOutgoingDamageDebuffs(List<StatusEffect> statuses, String actorName, BattleState battle) {
        double multiplier = 1.0;
        if (consumeStatus(statuses, SLOW)) {
            multiplier *= 0.80;
            battle.getBattleLog().add(actorName + " is slowed. Outgoing damage is reduced by 20%.");
        }
        if (consumeStatus(statuses, CHILLED)) {
            multiplier *= 0.85;
            battle.getBattleLog().add(actorName + " is chilled. Outgoing damage is reduced by 15%.");
        }
        return multiplier;
    }

    private void addMomentum(BattleState battle, int amount, String reason) {
        int before = battle.getHeroMomentum();
        battle.setHeroMomentum(before + amount);
        int gained = battle.getHeroMomentum() - before;
        if (gained > 0) {
            battle.getBattleLog().add("Hero gains " + gained + " Momentum from " + reason + ".");
        }
    }

    private boolean isDragonPhaseTwo(BattleState battle) {
        return "DRAGON_KING".equals(battle.getBoss().getId()) && battle.isPhaseTwo();
    }

    private DamageType damageTypeFromLabel(String label, DamageType fallback) {
        if (label == null) {
            return fallback;
        }
        for (DamageType type : DamageType.values()) {
            if (type.getLabel().equalsIgnoreCase(label)) {
                return type;
            }
        }
        return fallback;
    }

    private String masteryUpgradeId(DamageType type) {
        if (type == null) return "";
        return switch (type) {
            case SLASH -> "SLASH_MASTERY";
            case PIERCE -> "PIERCE_MASTERY";
            case FROST -> "FROST_MASTERY";
            case POISON -> "POISON_MASTERY";
            case FIRE -> "FIRE_MASTERY";
            default -> "";
        };
    }

    private String resistanceUpgradeId(DamageType type) {
        if (type == null) return "";
        return switch (type) {
            case SLASH -> "SLASH_RESISTANCE";
            case PIERCE -> "PIERCE_RESISTANCE";
            case FROST -> "FROST_RESISTANCE";
            case POISON -> "POISON_RESISTANCE";
            case FIRE -> "FIRE_RESISTANCE";
            default -> "";
        };
    }

    private void trimBattleLog(BattleState battle) {
        List<String> log = battle.getBattleLog();
        int extra = log.size() - MAX_BATTLE_LOG_ENTRIES;
        if (extra > 0) {
            log.subList(0, extra).clear();
        }
    }

    private record HeroDamage(int damage, String effectiveness, boolean empowered) {
    }
}
