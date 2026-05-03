package com.monstertrials.dto.response;

import java.util.List;

public record RewardResponse(
        String bossId,
        boolean firstWin,
        int xpGained,
        int coinsGained,
        int oldLevel,
        int newLevel,
        List<SkillResponse> learnedSkills,
        List<SkillResponse> autoUnlockedSkills,
        String rewardType
) {
}
