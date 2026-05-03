package com.monstertrials.dto.response;

import java.util.List;
import java.util.Set;

public record HeroResponse(
        int maxHp,
        int currentHp,
        int maxEnergy,
        int currentEnergy,
        int level,
        int xp,
        int nextLevelXp,
        int coins,
        String weaponId,
        String weaponLabel,
        int maxEquippedSkills,
        Set<String> learnedSkillIds,
        List<String> equippedSkillIds,
        List<SkillResponse> learnedSkills,
        List<SkillResponse> equippedSkills,
        HeroCombatStatsResponse combatStats
) {
}
