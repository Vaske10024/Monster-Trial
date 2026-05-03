package com.monstertrials.controller;

import com.monstertrials.data.GameData;
import com.monstertrials.dto.request.StartRunRequest;
import com.monstertrials.dto.response.ResponseMapper;
import com.monstertrials.dto.response.RunStateResponse;
import com.monstertrials.service.BattleService;
import com.monstertrials.service.RunService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/run")
public class RunController {
    private final RunService runService;
    private final BattleService battleService;
    private final GameData data;

    public RunController(RunService runService, BattleService battleService, GameData data) {
        this.runService = runService;
        this.battleService = battleService;
        this.data = data;
    }

    @PostMapping("/start")
    public RunStateResponse startRun(@RequestBody(required = false) StartRunRequest request) {
        String weapon = request == null ? "sword" : request.weapon();
        return ResponseMapper.toRunState(runService.startNewRun(weapon), data);
    }

    @GetMapping("/{runId}")
    public RunStateResponse getRun(@PathVariable UUID runId) {
        return ResponseMapper.toRunState(runService.getRun(runId), data);
    }

    @PostMapping("/{runId}/boss/replay")
    public RunStateResponse replayBoss(@PathVariable UUID runId) {
        return ResponseMapper.toRunState(battleService.replayBoss(runId), data);
    }

    @PostMapping("/{runId}/boss/{bossId}/replay")
    public RunStateResponse replayDefeatedBoss(@PathVariable UUID runId, @PathVariable String bossId) {
        return ResponseMapper.toRunState(battleService.replayBoss(runId, bossId), data);
    }

    @PostMapping("/{runId}/boss/next")
    public RunStateResponse nextBoss(@PathVariable UUID runId) {
        return ResponseMapper.toRunState(runService.moveToNextBoss(runId), data);
    }
}
