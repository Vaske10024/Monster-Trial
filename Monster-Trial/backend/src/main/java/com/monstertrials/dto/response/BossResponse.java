package com.monstertrials.dto.response;

import java.util.List;

public record BossResponse(
        String id,
        String name,
        int maxHp,
        String weakness,
        String resistance,
        int firstWinCoins,
        int firstWinXp,
        List<String> mainDamageTypes,
        String description,
        List<SkillResponse> learnableSkills,
        int winCount,
        boolean defeated,
        int replayCoins,
        int replayXp
) {
}
