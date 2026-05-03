package com.monstertrials.model;

public class Skill {
    private String id;
    private String name;
    private DamageType type;
    private int damage;
    private int energyCost;
    private int cooldown;
    private String effect;
    private String description;

    public Skill() {
    }

    public Skill(String id, String name, DamageType type, int damage, int energyCost, int cooldown, String effect, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.energyCost = energyCost;
        this.cooldown = cooldown;
        this.effect = effect;
        this.description = description;
    }

    public boolean isOffensive() {
        return type != null && type.isOffensive() && damage > 0;
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

    public DamageType getType() {
        return type;
    }

    public void setType(DamageType type) {
        this.type = type;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public void setEnergyCost(int energyCost) {
        this.energyCost = energyCost;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
