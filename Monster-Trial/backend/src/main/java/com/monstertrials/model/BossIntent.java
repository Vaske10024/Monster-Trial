package com.monstertrials.model;

public class BossIntent {
    private String skillId;
    private String name;
    private int damage;
    private String damageType;
    private String effect;
    private String intentType;
    private boolean interruptible;
    private String description;

    public BossIntent() {
    }

    public BossIntent(String skillId, String name, int damage, String damageType, String effect,
                      String intentType, boolean interruptible, String description) {
        this.skillId = skillId;
        this.name = name;
        this.damage = damage;
        this.damageType = damageType;
        this.effect = effect;
        this.intentType = intentType;
        this.interruptible = interruptible;
        this.description = description;
    }

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
    public String getDamageType() { return damageType; }
    public void setDamageType(String damageType) { this.damageType = damageType; }
    public String getEffect() { return effect; }
    public void setEffect(String effect) { this.effect = effect; }
    public String getIntentType() { return intentType; }
    public void setIntentType(String intentType) { this.intentType = intentType; }
    public boolean isInterruptible() { return interruptible; }
    public void setInterruptible(boolean interruptible) { this.interruptible = interruptible; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
