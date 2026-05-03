package com.monstertrials.dto.response;

public record BossIntentResponse(
        String skillId,
        String name,
        int damage,
        String damageType,
        String effect,
        String intentType,
        boolean interruptible,
        String description
) {
}
