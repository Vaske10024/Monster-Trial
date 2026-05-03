package com.monstertrials.dto.response;

public record UpgradeResponse(
        String id,
        String name,
        String type,
        String damageType,
        int maxLevel,
        int currentLevel,
        int nextCost,
        boolean canAfford,
        boolean maxed,
        String effectDescription
) {
}
