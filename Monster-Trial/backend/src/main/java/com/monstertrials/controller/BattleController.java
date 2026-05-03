package com.monstertrials.controller;

import com.monstertrials.data.GameData;
import com.monstertrials.dto.request.UseSkillRequest;
import com.monstertrials.dto.request.PotionRequest;
import com.monstertrials.dto.response.ResponseMapper;
import com.monstertrials.dto.response.RunStateResponse;
import com.monstertrials.service.BattleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/run/{runId}/battle")
public class BattleController {
    private final BattleService battleService;
    private final GameData data;

    public BattleController(BattleService battleService, GameData data) {
        this.battleService = battleService;
        this.data = data;
    }

    @PostMapping("/start")
    public RunStateResponse startBattle(@PathVariable UUID runId) {
        return ResponseMapper.toRunState(battleService.startBattle(runId), data);
    }

    @PostMapping("/use-skill")
    public RunStateResponse useSkill(@PathVariable UUID runId, @Valid @RequestBody UseSkillRequest request) {
        return ResponseMapper.toRunState(battleService.useSkill(runId, request.skillId()), data);
    }

    @PostMapping("/use-potion")
    public RunStateResponse usePotion(@PathVariable UUID runId, @Valid @RequestBody PotionRequest request) {
        return ResponseMapper.toRunState(battleService.usePotion(runId, request.potionId()), data);
    }

    @PostMapping("/retry")
    public RunStateResponse retry(@PathVariable UUID runId) {
        return ResponseMapper.toRunState(battleService.retryBattle(runId), data);
    }

    @PostMapping("/clear")
    public RunStateResponse clearCompletedBattle(@PathVariable UUID runId) {
        return ResponseMapper.toRunState(battleService.clearCompletedBattle(runId), data);
    }
}
