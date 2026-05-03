package com.monstertrials.dto.response;

import java.util.List;
import java.util.Map;

public record BattleStateResponse(
        BossResponse boss,
        int heroHp,
        int heroMaxHp,
        int heroEnergy,
        int heroMaxEnergy,
        int heroMomentum,
        int heroMaxMomentum,
        int bossHp,
        int bossMaxHp,
        Map<String, Integer> heroCooldowns,
        Map<String, Integer> bossCooldowns,
        List<StatusEffectResponse> heroStatusEffects,
        List<StatusEffectResponse> bossStatusEffects,
        List<String> battleLog,
        String result,
        String lastEffectiveness,
        int turnNumber,
        BossIntentResponse bossIntent,
        boolean phaseTwo
) {
}
