package com.monstertrials.dto.response;

import com.monstertrials.data.GameData;
import com.monstertrials.model.BattleResult;
import com.monstertrials.model.BattleState;
import com.monstertrials.model.Boss;
import com.monstertrials.model.BossIntent;
import com.monstertrials.model.DamageType;
import com.monstertrials.model.Hero;
import com.monstertrials.model.Potion;
import com.monstertrials.model.RewardSummary;
import com.monstertrials.model.RunState;
import com.monstertrials.model.Skill;
import com.monstertrials.model.StatusEffect;
import com.monstertrials.model.Upgrade;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class ResponseMapper {
    private ResponseMapper() {
    }

    public static RunStateResponse toRunState(RunState run, GameData data) {
        Boss currentBoss = null;
        if (!run.isRunComplete() && run.getCurrentBossIndex() >= 0 && run.getCurrentBossIndex() < data.getBosses().size()) {
            currentBoss = data.getBosses().get(run.getCurrentBossIndex());
        }

        String phase = "BOSS_PREVIEW";
        if (run.getBattle() != null) {
            BattleResult result = run.getBattle().getResult();
            phase = result == BattleResult.ONGOING ? "BATTLE" : result.name();
        } else if (run.isRunComplete()) {
            phase = "RUN_COMPLETE";
        }

        return new RunStateResponse(
                run.getRunId(),
                toHero(run.getHero(), data, run),
                run.getCurrentBossIndex(),
                currentBoss == null ? null : toBoss(currentBoss, data, run),
                data.getBosses().stream().map(boss -> toBoss(boss, data, run)).toList(),
                toUpgrades(run, data),
                toPotions(run, data),
                run.getBattle() == null ? null : toBattle(run.getBattle(), run, data),
                run.getLastReward() == null ? null : toReward(run.getLastReward(), data),
                run.isRunComplete(),
                run.getTotalCoinsEarned(),
                Map.copyOf(run.getUpgradeLevels()),
                Map.copyOf(run.getBossWinCounts()),
                phase
        );
    }

    public static HeroResponse toHero(Hero hero, GameData data) {
        return toHero(hero, data, null);
    }

    public static HeroResponse toHero(Hero hero, GameData data, RunState run) {
        List<SkillResponse> learned = hero.getLearnedSkillIds().stream()
                .map(data::getSkill)
                .map(ResponseMapper::toSkill)
                .toList();
        List<SkillResponse> equipped = hero.getEquippedSkillIds().stream()
                .map(data::getSkill)
                .map(ResponseMapper::toSkill)
                .toList();
        return new HeroResponse(
                hero.getMaxHp(),
                hero.getCurrentHp(),
                hero.getMaxEnergy(),
                hero.getCurrentEnergy(),
                hero.getLevel(),
                hero.getXp(),
                data.getNextLevelXp(hero.getLevel()),
                hero.getCoins(),
                hero.getWeaponId(),
                weaponLabel(hero.getWeaponId()),
                hero.getMaxEquippedSkills(),
                Set.copyOf(hero.getLearnedSkillIds()),
                List.copyOf(hero.getEquippedSkillIds()),
                learned,
                equipped,
                toHeroCombatStats(hero, run)
        );
    }

    public static SkillResponse toSkill(Skill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getName(),
                label(skill.getType()),
                skill.getDamage(),
                skill.getEnergyCost(),
                skill.getCooldown(),
                skill.getEffect(),
                skill.getDescription(),
                skill.isOffensive()
        );
    }

    public static BossResponse toBoss(Boss boss, GameData data, RunState run) {
        int winCount = 0;
        if (run != null) {
            winCount = run.getBossWinCounts().getOrDefault(boss.getId(), 0);
        }
        boolean skillAvailable = false;
        if (run != null) {
            skillAvailable = boss.getLearnableSkillIds().stream()
                    .anyMatch(skillId -> !run.getHero().getLearnedSkillIds().contains(skillId));
        }
        List<SkillResponse> learnableSkills = boss.getLearnableSkillIds().stream()
                .map(data::getSkill)
                .map(ResponseMapper::toSkill)
                .toList();
        return new BossResponse(
                boss.getId(),
                boss.getName(),
                boss.getMaxHp(),
                label(boss.getWeakness()),
                label(boss.getResistance()),
                boss.getFirstWinCoins(),
                boss.getFirstWinXp(),
                boss.getMainDamageTypes().stream().map(ResponseMapper::label).toList(),
                boss.getDescription(),
                learnableSkills,
                winCount,
                winCount > 0,
                replayCoinsForBoss(boss, skillAvailable),
                replayXpForBoss(boss)
        );
    }

    public static UpgradeResponse toUpgrade(Upgrade upgrade, int currentLevel, int coins) {
        boolean maxed = currentLevel >= upgrade.getMaxLevel();
        int nextCost = upgrade.costForLevel(currentLevel);
        return new UpgradeResponse(
                upgrade.getId(),
                upgrade.getName(),
                upgrade.getType().name(),
                upgrade.getDamageType() == null ? null : label(upgrade.getDamageType()),
                upgrade.getMaxLevel(),
                currentLevel,
                nextCost,
                !maxed && coins >= nextCost,
                maxed,
                upgrade.getEffectDescription()
        );
    }

    public static List<UpgradeResponse> toUpgrades(RunState run, GameData data) {
        return data.getUpgrades().values().stream()
                .map(upgrade -> toUpgrade(upgrade, run.getUpgradeLevels().getOrDefault(upgrade.getId(), 0), run.getHero().getCoins()))
                .toList();
    }


    public static PotionResponse toPotion(Potion potion, RunState run) {
        int quantity = run == null ? 0 : run.getPotionInventory().getOrDefault(potion.getId(), 0);
        boolean usedThisBattle = run != null
                && run.getBattle() != null
                && run.getBattle().getUsedPotionIds().contains(potion.getId());
        boolean canUseInBattle = run != null
                && run.getBattle() != null
                && run.getBattle().getResult() == BattleResult.ONGOING
                && quantity > 0
                && (!potion.isOneUsePerBattle() || !usedThisBattle);
        return new PotionResponse(
                potion.getId(),
                potion.getName(),
                potion.getCost(),
                potion.getEffect(),
                potion.getEffectDescription(),
                quantity,
                run != null && run.getHero().getCoins() >= potion.getCost(),
                canUseInBattle,
                usedThisBattle,
                potion.isOneUsePerBattle(),
                potion.isConsumesAction()
        );
    }

    public static List<PotionResponse> toPotions(RunState run, GameData data) {
        return data.getPotions().values().stream()
                .map(potion -> toPotion(potion, run))
                .toList();
    }

    public static BattleStateResponse toBattle(BattleState battle, RunState run, GameData data) {
        return new BattleStateResponse(
                toBoss(battle.getBoss(), data, run),
                battle.getHeroHp(),
                run.getHero().getMaxHp(),
                battle.getHeroEnergy(),
                run.getHero().getMaxEnergy(),
                battle.getHeroMomentum(),
                battle.getHeroMaxMomentum(),
                battle.getBossHp(),
                battle.getBoss().getMaxHp(),
                Map.copyOf(battle.getHeroCooldowns()),
                Map.copyOf(battle.getBossCooldowns()),
                battle.getHeroStatusEffects().stream().map(ResponseMapper::toStatus).toList(),
                battle.getBossStatusEffects().stream().map(ResponseMapper::toStatus).toList(),
                List.copyOf(battle.getBattleLog()),
                battle.getResult().name(),
                battle.getLastEffectiveness(),
                battle.getTurnNumber(),
                toBossIntent(battle.getBossIntent()),
                battle.isPhaseTwo()
        );
    }

    public static StatusEffectResponse toStatus(StatusEffect effect) {
        return new StatusEffectResponse(effect.getId(), effect.getName(), effect.getRemainingTurns(), effect.getStacks());
    }

    public static BossIntentResponse toBossIntent(BossIntent intent) {
        if (intent == null) {
            return null;
        }
        return new BossIntentResponse(
                intent.getSkillId(),
                intent.getName(),
                intent.getDamage(),
                intent.getDamageType(),
                intent.getEffect(),
                intent.getIntentType(),
                intent.isInterruptible(),
                intent.getDescription()
        );
    }

    public static RewardResponse toReward(RewardSummary reward, GameData data) {
        return new RewardResponse(
                reward.getBossId(),
                reward.isFirstWin(),
                reward.getXpGained(),
                reward.getCoinsGained(),
                reward.getOldLevel(),
                reward.getNewLevel(),
                reward.getLearnedSkillIds().stream().map(data::getSkill).map(ResponseMapper::toSkill).toList(),
                reward.getAutoUnlockedSkillIds().stream().map(data::getSkill).map(ResponseMapper::toSkill).toList(),
                reward.getRewardType()
        );
    }

    public static List<SkillResponse> allSkills(GameData data) {
        return data.getSkills().values().stream()
                .map(ResponseMapper::toSkill)
                .toList();
    }

    public static List<BossResponse> allBosses(GameData data) {
        return data.getBosses().stream()
                .map(boss -> toBoss(boss, data, null))
                .toList();
    }

    public static List<UpgradeResponse> allUpgrades(GameData data) {
        return data.getUpgrades().values().stream()
                .map(upgrade -> toUpgrade(upgrade, 0, 0))
                .toList();
    }

    public static List<PotionResponse> allPotions(GameData data) {
        return data.getPotions().values().stream()
                .map(potion -> toPotion(potion, null))
                .toList();
    }

    public static HeroCombatStatsResponse toHeroCombatStats(Hero hero, RunState run) {
        int generalResistanceLevel = run == null ? 0 : run.getUpgradeLevels().getOrDefault("GENERAL_RESISTANCE", 0);
        int generalResistancePercent = generalResistanceLevel * 2;

        Map<String, Integer> mastery = Map.of(
                "Slash", percentageForUpgrade(run, "SLASH_MASTERY", 5),
                "Pierce", percentageForUpgrade(run, "PIERCE_MASTERY", 5),
                "Frost", percentageForUpgrade(run, "FROST_MASTERY", 5),
                "Poison", percentageForUpgrade(run, "POISON_MASTERY", 5),
                "Fire", percentageForUpgrade(run, "FIRE_MASTERY", 5)
        );

        Map<String, Integer> resistances = Map.of(
                "Slash", totalResistancePercent(run, "SLASH_RESISTANCE"),
                "Pierce", totalResistancePercent(run, "PIERCE_RESISTANCE"),
                "Frost", totalResistancePercent(run, "FROST_RESISTANCE"),
                "Poison", totalResistancePercent(run, "POISON_RESISTANCE"),
                "Fire", totalResistancePercent(run, "FIRE_RESISTANCE")
        );

        return new HeroCombatStatsResponse(
                Math.max(0, hero.getLevel() - 1) * 2,
                generalResistancePercent,
                mastery,
                resistances
        );
    }

    private static int percentageForUpgrade(RunState run, String upgradeId, int percentPerLevel) {
        if (run == null) {
            return 0;
        }
        return run.getUpgradeLevels().getOrDefault(upgradeId, 0) * percentPerLevel;
    }

    private static int totalResistancePercent(RunState run, String specificUpgradeId) {
        if (run == null) {
            return 0;
        }
        int general = run.getUpgradeLevels().getOrDefault("GENERAL_RESISTANCE", 0) * 2;
        int specific = run.getUpgradeLevels().getOrDefault(specificUpgradeId, 0) * 6;
        return Math.min(50, general + specific);
    }

    private static int replayXpForBoss(Boss boss) {
        return Math.max(8, (int) Math.round(boss.getFirstWinXp() * 0.40));
    }

    private static int replayCoinsForBoss(Boss boss, boolean skillAvailable) {
        double multiplier = skillAvailable ? 0.45 : 0.35;
        return Math.max(12, (int) Math.round(boss.getFirstWinCoins() * multiplier));
    }

    private static String weaponLabel(String weaponId) {
        if (weaponId == null) {
            return "Sword";
        }
        return switch (weaponId) {
            case "bow" -> "Bow";
            case "staff" -> "Staff";
            case "dagger" -> "Dagger";
            default -> "Sword";
        };
    }

    private static String label(DamageType type) {
        return type == null ? null : type.getLabel();
    }
}
