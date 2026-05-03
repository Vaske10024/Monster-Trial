package com.monstertrials.controller;

import com.monstertrials.data.GameData;
import com.monstertrials.dto.request.EquipSkillsRequest;
import com.monstertrials.dto.response.ResponseMapper;
import com.monstertrials.dto.response.RunStateResponse;
import com.monstertrials.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/run/{runId}/skills")
public class SkillController {
    private final SkillService skillService;
    private final GameData data;

    public SkillController(SkillService skillService, GameData data) {
        this.skillService = skillService;
        this.data = data;
    }

    @PostMapping("/equip")
    public RunStateResponse equip(@PathVariable UUID runId, @Valid @RequestBody EquipSkillsRequest request) {
        return ResponseMapper.toRunState(skillService.equipSkills(runId, request.skillIds()), data);
    }
}
