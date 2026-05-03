package com.monstertrials.controller;

import com.monstertrials.data.GameData;
import com.monstertrials.dto.request.UpgradeRequest;
import com.monstertrials.dto.request.PotionRequest;
import com.monstertrials.dto.response.ResponseMapper;
import com.monstertrials.dto.response.RunStateResponse;
import com.monstertrials.service.ShopService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/run/{runId}/shop")
public class ShopController {
    private final ShopService shopService;
    private final GameData data;

    public ShopController(ShopService shopService, GameData data) {
        this.shopService = shopService;
        this.data = data;
    }

    @PostMapping("/upgrade")
    public RunStateResponse upgrade(@PathVariable UUID runId, @Valid @RequestBody UpgradeRequest request) {
        return ResponseMapper.toRunState(shopService.buyUpgrade(runId, request.upgradeId()), data);
    }

    @PostMapping("/potion")
    public RunStateResponse potion(@PathVariable UUID runId, @Valid @RequestBody PotionRequest request) {
        return ResponseMapper.toRunState(shopService.buyPotion(runId, request.potionId()), data);
    }
}
