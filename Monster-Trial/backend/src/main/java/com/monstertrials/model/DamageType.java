package com.monstertrials.model;

public enum DamageType {
    SLASH("Slash"),
    PIERCE("Pierce"),
    FROST("Frost"),
    POISON("Poison"),
    FIRE("Fire"),
    DEFENSE("Defense"),
    UTILITY("Utility");

    private final String label;

    DamageType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isOffensive() {
        return this == SLASH || this == PIERCE || this == FROST || this == POISON || this == FIRE;
    }

    public static DamageType fromId(String id) {
        return DamageType.valueOf(id.trim().toUpperCase());
    }
}
