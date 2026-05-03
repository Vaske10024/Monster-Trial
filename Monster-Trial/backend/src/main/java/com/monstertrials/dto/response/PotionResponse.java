package com.monstertrials.dto.response;

public record PotionResponse(
        String id,
        String name,
        int cost,
        String effect,
        String effectDescription,
        int quantity,
        boolean canAfford,
        boolean canUseInBattle,
        boolean usedThisBattle,
        boolean oneUsePerBattle,
        boolean consumesAction
) {
}
