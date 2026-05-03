package com.monstertrials.model;

import java.util.ArrayList;
import java.util.List;

public class Boss {
    private String id;
    private String name;
    private int maxHp;
    private DamageType weakness;
    private DamageType resistance;
    private int firstWinCoins;
    private int firstWinXp;
    private List<DamageType> mainDamageTypes = new ArrayList<>();
    private String description;
    private List<String> learnableSkillIds = new ArrayList<>();

    public Boss() {
    }

    public Boss(String id, String name, int maxHp, DamageType weakness, DamageType resistance,
                int firstWinCoins, int firstWinXp, List<DamageType> mainDamageTypes,
                String description, List<String> learnableSkillIds) {
        this.id = id;
        this.name = name;
        this.maxHp = maxHp;
        this.weakness = weakness;
        this.resistance = resistance;
        this.firstWinCoins = firstWinCoins;
        this.firstWinXp = firstWinXp;
        this.mainDamageTypes = new ArrayList<>(mainDamageTypes);
        this.description = description;
        this.learnableSkillIds = new ArrayList<>(learnableSkillIds);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public DamageType getWeakness() {
        return weakness;
    }

    public void setWeakness(DamageType weakness) {
        this.weakness = weakness;
    }

    public DamageType getResistance() {
        return resistance;
    }

    public void setResistance(DamageType resistance) {
        this.resistance = resistance;
    }

    public int getFirstWinCoins() {
        return firstWinCoins;
    }

    public void setFirstWinCoins(int firstWinCoins) {
        this.firstWinCoins = firstWinCoins;
    }

    public int getFirstWinXp() {
        return firstWinXp;
    }

    public void setFirstWinXp(int firstWinXp) {
        this.firstWinXp = firstWinXp;
    }

    public List<DamageType> getMainDamageTypes() {
        return mainDamageTypes;
    }

    public void setMainDamageTypes(List<DamageType> mainDamageTypes) {
        this.mainDamageTypes = mainDamageTypes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLearnableSkillIds() {
        return learnableSkillIds;
    }

    public void setLearnableSkillIds(List<String> learnableSkillIds) {
        this.learnableSkillIds = learnableSkillIds;
    }
}
