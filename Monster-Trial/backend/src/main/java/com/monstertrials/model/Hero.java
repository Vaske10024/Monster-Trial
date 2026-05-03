package com.monstertrials.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Hero {
    public static final int BASE_MAX_HP = 100;
    public static final int BASE_MAX_ENERGY = 100;

    private int maxHp = BASE_MAX_HP;
    private int currentHp = BASE_MAX_HP;
    private int maxEnergy = BASE_MAX_ENERGY;
    private int currentEnergy = BASE_MAX_ENERGY;
    private int level = 1;
    private int xp = 0;
    private int coins = 0;
    private String weaponId = "sword";
    private int maxEquippedSkills = 4;
    private Set<String> learnedSkillIds = new LinkedHashSet<>();
    private List<String> equippedSkillIds = new ArrayList<>();

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.max(0, Math.min(currentHp, maxHp));
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public void setCurrentEnergy(int currentEnergy) {
        this.currentEnergy = Math.max(0, Math.min(currentEnergy, maxEnergy));
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = Math.max(0, xp);
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = Math.max(0, coins);
    }

    public String getWeaponId() {
        return weaponId;
    }

    public void setWeaponId(String weaponId) {
        this.weaponId = weaponId == null || weaponId.isBlank() ? "sword" : weaponId;
    }

    public int getMaxEquippedSkills() {
        return maxEquippedSkills;
    }

    public void setMaxEquippedSkills(int maxEquippedSkills) {
        this.maxEquippedSkills = maxEquippedSkills;
    }

    public Set<String> getLearnedSkillIds() {
        return learnedSkillIds;
    }

    public void setLearnedSkillIds(Set<String> learnedSkillIds) {
        this.learnedSkillIds = learnedSkillIds;
    }

    public List<String> getEquippedSkillIds() {
        return equippedSkillIds;
    }

    public void setEquippedSkillIds(List<String> equippedSkillIds) {
        this.equippedSkillIds = equippedSkillIds;
    }
}
