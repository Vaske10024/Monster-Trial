package com.monstertrials.service;

import com.monstertrials.data.GameData;
import com.monstertrials.model.RunState;
import com.monstertrials.model.Upgrade;
import com.monstertrials.model.Potion;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShopService {
    private final RunService runService;
    private final GameData data;

    public ShopService(RunService runService, GameData data) {
        this.runService = runService;
        this.data = data;
    }

    public RunState buyUpgrade(UUID runId, String upgradeId) {
        RunState run = runService.getRun(runId);
        synchronized (run) {
            runService.ensureNoActiveBattle(run);
            Upgrade upgrade = data.getUpgrade(upgradeId);
            int currentLevel = run.getUpgradeLevels().getOrDefault(upgradeId, 0);
            if (currentLevel >= upgrade.getMaxLevel()) {
                throw new IllegalStateException(upgrade.getName() + " is already at max level.");
            }
            int cost = upgrade.costForLevel(currentLevel);
            if (run.getHero().getCoins() < cost) {
                throw new IllegalStateException("Not enough coins. Need " + cost + " coins.");
            }

            run.getHero().setCoins(run.getHero().getCoins() - cost);
            run.getUpgradeLevels().put(upgradeId, currentLevel + 1);
            runService.recalculateHeroStats(run, true);
            run.touch();
            return run;
        }
    }

    public RunState buyPotion(UUID runId, String potionId) {
        RunState run = runService.getRun(runId);
        synchronized (run) {
            runService.ensureNoActiveBattle(run);
            Potion potion = data.getPotion(potionId);
            if (run.getHero().getCoins() < potion.getCost()) {
                throw new IllegalStateException("Not enough coins. Need " + potion.getCost() + " coins.");
            }

            run.getHero().setCoins(run.getHero().getCoins() - potion.getCost());
            run.getPotionInventory().merge(potion.getId(), 1, Integer::sum);
            run.touch();
            return run;
        }
    }
}
