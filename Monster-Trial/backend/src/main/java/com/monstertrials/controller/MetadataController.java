package com.monstertrials.controller;

import com.monstertrials.data.GameData;
import com.monstertrials.dto.response.BossResponse;
import com.monstertrials.dto.response.ResponseMapper;
import com.monstertrials.dto.response.SkillResponse;
import com.monstertrials.dto.response.PotionResponse;
import com.monstertrials.dto.response.UpgradeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/metadata")
public class MetadataController {
    private final GameData data;

    public MetadataController(GameData data) {
        this.data = data;
    }

    @GetMapping("/skills")
    public List<SkillResponse> skills() {
        return ResponseMapper.allSkills(data);
    }

    @GetMapping("/bosses")
    public List<BossResponse> bosses() {
        return ResponseMapper.allBosses(data);
    }

    @GetMapping("/upgrades")
    public List<UpgradeResponse> upgrades() {
        return ResponseMapper.allUpgrades(data);
    }

    @GetMapping("/potions")
    public List<PotionResponse> potions() {
        return ResponseMapper.allPotions(data);
    }
}
