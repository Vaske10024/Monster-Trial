package com.monstertrials.dto.response;

import java.util.Map;

public record HeroCombatStatsResponse(
        int globalDamageBonusPercent,
        int generalResistancePercent,
        Map<String, Integer> damageMasteryPercentByType,
        Map<String, Integer> totalResistancePercentByType
) {
}
