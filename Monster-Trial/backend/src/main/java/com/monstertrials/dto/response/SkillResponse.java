package com.monstertrials.dto.response;

public record SkillResponse(
        String id,
        String name,
        String type,
        int damage,
        int energyCost,
        int cooldown,
        String effect,
        String description,
        boolean offensive
) {
}
