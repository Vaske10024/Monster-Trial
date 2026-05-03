package com.monstertrials.service;

import com.monstertrials.data.GameData;
import com.monstertrials.model.RunState;
import com.monstertrials.model.Skill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class SkillService {
    private final RunService runService;
    private final GameData data;

    public SkillService(RunService runService, GameData data) {
        this.runService = runService;
        this.data = data;
    }

    public RunState equipSkills(UUID runId, List<String> skillIds) {
        RunState run = runService.getRun(runId);
        synchronized (run) {
            runService.ensureNoActiveBattle(run);
            if (skillIds == null) {
                throw new IllegalArgumentException("skillIds is required.");
            }
            if (skillIds.size() > run.getHero().getMaxEquippedSkills()) {
                throw new IllegalArgumentException("You can equip at most " + run.getHero().getMaxEquippedSkills() + " skills.");
            }

            Set<String> deduped = new LinkedHashSet<>(skillIds);
            if (deduped.size() != skillIds.size()) {
                throw new IllegalArgumentException("Duplicate equipped skills are not allowed.");
            }

            for (String skillId : skillIds) {
                data.getSkill(skillId);
                if (!run.getHero().getLearnedSkillIds().contains(skillId)) {
                    throw new IllegalArgumentException("Cannot equip unknown skill: " + skillId);
                }
            }

            run.getHero().setEquippedSkillIds(new ArrayList<>(skillIds));
            run.touch();
            return run;
        }
    }

    public boolean hasOffensiveEquipped(RunState run) {
        return run.getHero().getEquippedSkillIds().stream()
                .map(data::getSkill)
                .anyMatch(Skill::isOffensive);
    }
}
