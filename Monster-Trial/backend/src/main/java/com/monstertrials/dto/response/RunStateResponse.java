package com.monstertrials.dto.response;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record RunStateResponse(
        UUID runId,
        HeroResponse hero,
        int currentBossIndex,
        BossResponse currentBoss,
        List<BossResponse> bosses,
        List<UpgradeResponse> upgrades,
        List<PotionResponse> potions,
        BattleStateResponse battle,
        RewardResponse lastReward,
        boolean runComplete,
        int totalCoinsEarned,
        Map<String, Integer> upgradeLevels,
        Map<String, Integer> bossWinCounts,
        String phase
) {
}
