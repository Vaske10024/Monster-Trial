package com.monstertrials.model;

public class Upgrade {
    private String id;
    private String name;
    private UpgradeType type;
    private DamageType damageType;
    private int maxLevel;
    private int baseCost;
    private int costPerLevel;
    private String effectDescription;

    public Upgrade() {
    }

    public Upgrade(String id, String name, UpgradeType type, DamageType damageType, int maxLevel,
                   int baseCost, int costPerLevel, String effectDescription) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.damageType = damageType;
        this.maxLevel = maxLevel;
        this.baseCost = baseCost;
        this.costPerLevel = costPerLevel;
        this.effectDescription = effectDescription;
    }

    public int costForLevel(int currentLevel) {
        if (currentLevel >= maxLevel) {
            return 0;
        }
        return baseCost + costPerLevel * currentLevel;
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

    public UpgradeType getType() {
        return type;
    }

    public void setType(UpgradeType type) {
        this.type = type;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(int baseCost) {
        this.baseCost = baseCost;
    }

    public int getCostPerLevel() {
        return costPerLevel;
    }

    public void setCostPerLevel(int costPerLevel) {
        this.costPerLevel = costPerLevel;
    }

    public String getEffectDescription() {
        return effectDescription;
    }

    public void setEffectDescription(String effectDescription) {
        this.effectDescription = effectDescription;
    }
}
