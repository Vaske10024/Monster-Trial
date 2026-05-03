package com.monstertrials.data;

import com.monstertrials.model.Boss;
import com.monstertrials.model.DamageType;
import com.monstertrials.model.Skill;
import com.monstertrials.model.Potion;
import com.monstertrials.model.Upgrade;
import com.monstertrials.model.UpgradeType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GameData {
    public static final List<String> INITIAL_SKILLS = List.of("IRON_SLASH", "PIERCING_STRIKE", "GUARD", "FOCUS");

    private final Map<String, Skill> skills = new LinkedHashMap<>();
    private final List<Boss> bosses = new ArrayList<>();
    private final Map<String, Upgrade> upgrades = new LinkedHashMap<>();
    private final Map<String, Potion> potions = new LinkedHashMap<>();
    private final Map<String, List<Skill>> bossBattleSkills = new LinkedHashMap<>();
    private final Map<Integer, Integer> xpThresholds = new LinkedHashMap<>();

    public GameData() {
        initXpThresholds();
        initSkills();
        initBosses();
        initUpgrades();
        initPotions();
        initBossBattleSkills();
    }

    private void initXpThresholds() {
        xpThresholds.put(1, 0);
        xpThresholds.put(2, 50);
        xpThresholds.put(3, 120);
        xpThresholds.put(4, 210);
        xpThresholds.put(5, 330);
        xpThresholds.put(6, 480);
    }

    private void initSkills() {
        addSkill(new Skill("IRON_SLASH", "Iron Slash", DamageType.SLASH, 14, 10, 0, "momentum_hit", "Reliable sword attack that gains a little Momentum."));
        addSkill(new Skill("PIERCING_STRIKE", "Piercing Strike", DamageType.PIERCE, 12, 9, 0, "anti_shield", "Precise piercing attack. Deals bonus damage against shielded targets."));
        addSkill(new Skill("AIMED_SHOT", "Aimed Shot", DamageType.PIERCE, 13, 10, 0, "momentum_hit", "Bow attack with steady damage and a little Momentum gain."));
        addSkill(new Skill("ARCANE_BOLT", "Arcane Bolt", DamageType.FROST, 12, 9, 0, "none", "Staff attack that gives early access to Frost damage without high burst."));
        addSkill(new Skill("DAGGER_FLURRY", "Dagger Flurry", DamageType.SLASH, 9, 6, 0, "quick_combo", "Low-cost dagger combo. Stronger after Guard or Focus."));
        addSkill(new Skill("GUARD", "Guard", DamageType.DEFENSE, 0, 0, 1, "shield", "Reduces next incoming damage by 65%, blocks one status effect, restores 12 energy, and gains Momentum when it blocks damage."));
        addSkill(new Skill("FOCUS", "Focus", DamageType.UTILITY, 0, 0, 2, "restore_energy", "Restores 30 energy, cleanses Slow/Weakness Debuff, and gains 10 Momentum."));

        addSkill(new Skill("QUICK_SLASH", "Quick Slash", DamageType.SLASH, 10, 7, 0, "quick_combo", "Cheap slash attack. Stronger after Guard or Focus."));
        addSkill(new Skill("DIRTY_TRICK", "Dirty Trick", DamageType.SLASH, 6, 12, 2, "weakness_debuff", "Deals low damage and Exposes the enemy for your next hit."));
        addSkill(new Skill("POISON_BITE", "Poison Bite", DamageType.POISON, 5, 15, 1, "poison", "Deals poison damage and applies a stacking poison for 4 turns."));
        addSkill(new Skill("WEB_TRAP", "Web Trap", DamageType.PIERCE, 4, 14, 3, "stun_chance", "Small damage with a high chance to interrupt dangerous boss intents."));
        addSkill(new Skill("ICE_ARROW", "Ice Arrow", DamageType.FROST, 15, 16, 1, "slow_chance", "Frost arrow that can Slow. Guaranteed Slow against heavy boss intents."));
        addSkill(new Skill("STONE_SKIN", "Stone Skin", DamageType.DEFENSE, 0, 15, 3, "shield_2_turns", "Grants two Stone Shield stacks, each reducing incoming damage."));
        addSkill(new Skill("ROCK_PIERCE", "Rock Pierce", DamageType.PIERCE, 18, 20, 1, "ignore_shield", "Strong pierce attack that ignores shield and cracks armor if target is shielded."));
        addSkill(new Skill("FROZEN_ARMOR", "Frozen Armor", DamageType.FROST, 0, 18, 3, "shield_reflect", "Gains shield, reflects damage, and chills attackers."));
        addSkill(new Skill("INFERNO_BREATH", "Inferno Breath", DamageType.FIRE, 25, 32, 3, "burn", "Heavy fire damage with burn chance. Empowered casts guarantee burn."));
        addSkill(new Skill("DRAGON_CLAW", "Dragon Claw", DamageType.SLASH, 20, 20, 1, "finisher", "Powerful slash attack that hits harder against burning or poisoned targets."));
    }

    private void initBosses() {
        bosses.add(new Boss(
                "FOREST_GOBLIN",
                "Forest Goblin",
                95,
                DamageType.SLASH,
                DamageType.POISON,
                40,
                35,
                List.of(DamageType.SLASH),
                "A fast but weak forest creature. Intro boss.",
                List.of("QUICK_SLASH", "DIRTY_TRICK")
        ));

        bosses.add(new Boss(
                "VENOM_SPIDER",
                "Venom Spider",
                130,
                DamageType.FIRE,
                DamageType.POISON,
                60,
                50,
                List.of(DamageType.POISON, DamageType.PIERCE),
                "Poison-based boss that introduces damage over time.",
                List.of("POISON_BITE", "WEB_TRAP")
        ));

        bosses.add(new Boss(
                "STONE_GOLEM",
                "Stone Golem",
                180,
                DamageType.FROST,
                DamageType.SLASH,
                85,
                75,
                List.of(DamageType.SLASH, DamageType.PIERCE),
                "Slow defensive boss with high HP.",
                List.of("STONE_SKIN", "ROCK_PIERCE")
        ));

        bosses.add(new Boss(
                "FROST_REVENANT",
                "Frost Revenant",
                155,
                DamageType.PIERCE,
                DamageType.FROST,
                110,
                95,
                List.of(DamageType.FROST),
                "Magical frost boss that uses slow and shields.",
                List.of("ICE_ARROW", "FROZEN_ARMOR")
        ));

        bosses.add(new Boss(
                "DRAGON_KING",
                "Dragon King",
                240,
                DamageType.POISON,
                DamageType.FIRE,
                150,
                130,
                List.of(DamageType.FIRE, DamageType.SLASH),
                "Final boss with heavy damage and burn effects.",
                List.of("INFERNO_BREATH", "DRAGON_CLAW")
        ));
    }

    private void initUpgrades() {
        addUpgrade(new Upgrade("GENERAL_RESISTANCE", "General Resistance", UpgradeType.DEFENSE, null, 10, 50, 35, "+2% resistance to all damage types per level."));
        addUpgrade(new Upgrade("FIRE_RESISTANCE", "Fire Resistance", UpgradeType.DEFENSE, DamageType.FIRE, 5, 50, 35, "+6% Fire resistance per level."));
        addUpgrade(new Upgrade("FROST_RESISTANCE", "Frost Resistance", UpgradeType.DEFENSE, DamageType.FROST, 5, 50, 35, "+6% Frost resistance per level."));
        addUpgrade(new Upgrade("POISON_RESISTANCE", "Poison Resistance", UpgradeType.DEFENSE, DamageType.POISON, 5, 50, 35, "+6% Poison resistance per level."));
        addUpgrade(new Upgrade("SLASH_RESISTANCE", "Slash Resistance", UpgradeType.DEFENSE, DamageType.SLASH, 5, 50, 35, "+6% Slash resistance per level."));
        addUpgrade(new Upgrade("PIERCE_RESISTANCE", "Pierce Resistance", UpgradeType.DEFENSE, DamageType.PIERCE, 5, 50, 35, "+6% Pierce resistance per level."));

        addUpgrade(new Upgrade("SLASH_MASTERY", "Slash Mastery", UpgradeType.DAMAGE, DamageType.SLASH, 5, 60, 40, "+5% Slash damage per level."));
        addUpgrade(new Upgrade("PIERCE_MASTERY", "Pierce Mastery", UpgradeType.DAMAGE, DamageType.PIERCE, 5, 60, 40, "+5% Pierce damage per level."));
        addUpgrade(new Upgrade("FROST_MASTERY", "Frost Mastery", UpgradeType.DAMAGE, DamageType.FROST, 5, 60, 40, "+5% Frost damage per level."));
        addUpgrade(new Upgrade("POISON_MASTERY", "Poison Mastery", UpgradeType.DAMAGE, DamageType.POISON, 5, 60, 40, "+5% Poison damage per level."));
        addUpgrade(new Upgrade("FIRE_MASTERY", "Fire Mastery", UpgradeType.DAMAGE, DamageType.FIRE, 5, 60, 40, "+5% Fire damage per level."));

        addUpgrade(new Upgrade("VITALITY_TRAINING", "Vitality Training", UpgradeType.UTILITY, null, 5, 70, 45, "+10 Max HP per level."));
        addUpgrade(new Upgrade("ENERGY_CORE", "Energy Core", UpgradeType.UTILITY, null, 5, 65, 40, "+8 Max Energy per level."));
        addUpgrade(new Upgrade("GUARD_TRAINING", "Guard Training", UpgradeType.UTILITY, null, 3, 55, 35, "Guard blocks +5% more damage per level. Level 3 blocks two status effects."));
        addUpgrade(new Upgrade("MOMENTUM_DISCIPLINE", "Momentum Discipline", UpgradeType.UTILITY, null, 3, 60, 40, "+5 max Momentum overflow per level. Level 3 starts battles with 15 Momentum."));
        addUpgrade(new Upgrade("BATTLE_FOCUS", "Battle Focus", UpgradeType.UTILITY, null, 2, 65, 45, "Focus also heals 5 HP per level."));
        addUpgrade(new Upgrade("ADRENALINE", "Adrenaline", UpgradeType.UTILITY, null, 3, 75, 45, "When below 35% HP, gain +5% outgoing damage per level."));
    }


    private void initPotions() {
        addPotion(new Potion(
                "HEALING_POTION",
                "Healing Potion",
                100,
                "heal",
                "Heals 28 HP. Uses your action and can be used once per battle.",
                true,
                true
        ));
        addPotion(new Potion(
                "RESISTANCE_POTION",
                "Resistance Potion",
                100,
                "resist_next_hit",
                "Quick use: the next incoming hit gets +25% resistance, up to a temporary 65% cap. Does not end your turn; can be used once per battle.",
                true,
                false
        ));
        addPotion(new Potion(
                "CLEANSING_POTION",
                "Cleansing Potion",
                80,
                "cleanse",
                "Removes Poison, Burn, Slow, Chilled, and Expose. Uses your action and can be used once per battle.",
                true,
                true
        ));
    }

    private void initBossBattleSkills() {
        bossBattleSkills.put("FOREST_GOBLIN", List.of(
                new Skill("GOBLIN_SLASH", "Goblin Slash", DamageType.SLASH, 12, 0, 0, "none", "A quick clawing slash."),
                new Skill("GOBLIN_DIRTY_TRICK", "Dirty Trick", DamageType.SLASH, 8, 0, 2, "weakness_debuff", "A distracting low blow.")
        ));

        bossBattleSkills.put("VENOM_SPIDER", List.of(
                new Skill("SPIDER_POISON_BITE", "Poison Bite", DamageType.POISON, 13, 0, 2, "poison", "A venomous bite."),
                new Skill("SPIDER_FANG_STAB", "Fang Stab", DamageType.PIERCE, 15, 0, 0, "none", "Piercing spider fangs."),
                new Skill("SPIDER_WEB_TRAP", "Web Trap", DamageType.PIERCE, 6, 0, 3, "stun_chance", "Sticky webbing that can stun.")
        ));

        bossBattleSkills.put("STONE_GOLEM", List.of(
                new Skill("GOLEM_STONE_SLAM", "Stone Slam", DamageType.SLASH, 20, 0, 0, "none", "A heavy sweeping stone strike."),
                new Skill("GOLEM_ROCK_PIERCE", "Rock Pierce", DamageType.PIERCE, 22, 0, 1, "ignore_shield", "Jagged stone shards pierce defenses."),
                new Skill("GOLEM_STONE_SKIN", "Stone Skin", DamageType.DEFENSE, 0, 0, 3, "shield_2_turns", "The golem hardens its rocky hide.")
        ));

        bossBattleSkills.put("FROST_REVENANT", List.of(
                new Skill("REVENANT_FROST_BOLT", "Frost Bolt", DamageType.FROST, 20, 0, 1, "slow_chance", "A freezing bolt that can slow."),
                new Skill("REVENANT_FROZEN_ARMOR", "Frozen Armor", DamageType.FROST, 0, 0, 3, "shield_reflect", "Ice armor that reflects small damage."),
                new Skill("REVENANT_ICE_CUT", "Ice Cut", DamageType.FROST, 16, 0, 0, "none", "A sharp blade of ice.")
        ));

        bossBattleSkills.put("DRAGON_KING", List.of(
                new Skill("DRAGON_CLAW_BOSS", "Dragon Claw", DamageType.SLASH, 28, 0, 1, "none", "A massive claw swipe."),
                new Skill("DRAGON_INFERNO_BREATH", "Inferno Breath", DamageType.FIRE, 32, 0, 3, "burn", "A torrent of dragonfire."),
                new Skill("DRAGON_FLAME_BITE", "Flame Bite", DamageType.FIRE, 24, 0, 0, "burn_chance", "A blazing bite that can burn.")
        ));
    }

    private void addSkill(Skill skill) {
        skills.put(skill.getId(), skill);
    }

    private void addUpgrade(Upgrade upgrade) {
        upgrades.put(upgrade.getId(), upgrade);
    }

    private void addPotion(Potion potion) {
        potions.put(potion.getId(), potion);
    }

    public Map<String, Skill> getSkills() {
        return Collections.unmodifiableMap(skills);
    }

    public Skill getSkill(String id) {
        Skill skill = skills.get(id);
        if (skill == null) {
            throw new IllegalArgumentException("Unknown skill: " + id);
        }
        return skill;
    }

    public List<Boss> getBosses() {
        return Collections.unmodifiableList(bosses);
    }

    public Boss getBoss(String id) {
        return bosses.stream()
                .filter(boss -> boss.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown boss: " + id));
    }

    public Map<String, Upgrade> getUpgrades() {
        return Collections.unmodifiableMap(upgrades);
    }

    public Upgrade getUpgrade(String id) {
        Upgrade upgrade = upgrades.get(id);
        if (upgrade == null) {
            throw new IllegalArgumentException("Unknown upgrade: " + id);
        }
        return upgrade;
    }

    public Map<String, Potion> getPotions() {
        return Collections.unmodifiableMap(potions);
    }

    public Potion getPotion(String id) {
        Potion potion = potions.get(id);
        if (potion == null) {
            throw new IllegalArgumentException("Unknown potion: " + id);
        }
        return potion;
    }

    public List<Skill> getBossBattleSkills(String bossId) {
        return bossBattleSkills.getOrDefault(bossId, List.of());
    }

    public Map<Integer, Integer> getXpThresholds() {
        return Collections.unmodifiableMap(xpThresholds);
    }

    public int getLevelForXp(int xp) {
        int level = 1;
        for (Map.Entry<Integer, Integer> entry : xpThresholds.entrySet()) {
            if (xp >= entry.getValue()) {
                level = entry.getKey();
            }
        }
        return level;
    }

    public int getNextLevelXp(int currentLevel) {
        return xpThresholds.getOrDefault(currentLevel + 1, 0);
    }
}
