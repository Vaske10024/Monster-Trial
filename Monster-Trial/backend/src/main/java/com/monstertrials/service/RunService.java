package com.monstertrials.service;

import com.monstertrials.data.GameData;
import com.monstertrials.model.BattleResult;
import com.monstertrials.model.Boss;
import com.monstertrials.model.Hero;
import com.monstertrials.model.RunState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RunService {
    private final GameData data;
    private final Map<UUID, RunState> runs = new ConcurrentHashMap<>();
    private final int maxActiveRuns;
    private final Duration runTtl;

    public RunService(
            GameData data,
            @Value("${monster-trial.runs.max-active:5000}") int maxActiveRuns,
            @Value("${monster-trial.runs.ttl-hours:24}") long ttlHours
    ) {
        this.data = data;
        this.maxActiveRuns = Math.max(100, maxActiveRuns);
        this.runTtl = Duration.ofHours(Math.max(1, ttlHours));
    }

    public RunState startNewRun() {
        return startNewRun("sword");
    }

    public synchronized RunState startNewRun(String weaponId) {
        purgeExpiredRuns();
        enforceCapacity();

        RunState run = new RunState();
        run.setRunId(UUID.randomUUID());
        run.setHero(new Hero());
        run.getHero().setWeaponId(normalizeWeaponId(weaponId));
        run.setCurrentBossIndex(0);
        run.setUpgradeLevels(new LinkedHashMap<>());
        run.setBossWinCounts(new LinkedHashMap<>());
        run.setCreatedAt(Instant.now());
        run.touch();

        data.getUpgrades().keySet().forEach(id -> run.getUpgradeLevels().put(id, 0));
        data.getBosses().forEach(boss -> run.getBossWinCounts().put(boss.getId(), 0));

        List<String> starterSkills = starterSkillsForWeapon(run.getHero().getWeaponId());
        run.getHero().getLearnedSkillIds().addAll(starterSkills);
        run.getHero().getEquippedSkillIds().addAll(starterSkills);
        recalculateHeroStats(run, true);
        run.getHero().setCurrentHp(run.getHero().getMaxHp());
        run.getHero().setCurrentEnergy(run.getHero().getMaxEnergy());

        runs.put(run.getRunId(), run);
        return run;
    }

    public RunState getRun(UUID runId) {
        RunState run = runs.get(runId);
        if (run == null) {
            throw new IllegalArgumentException("Run not found.");
        }
        if (isExpired(run, Instant.now())) {
            runs.remove(runId);
            throw new IllegalArgumentException("Run expired. Start a new run.");
        }
        run.touch();
        return run;
    }

    public Boss getCurrentBoss(RunState run) {
        if (run.isRunComplete()) {
            throw new IllegalStateException("The run is already complete.");
        }
        int index = run.getCurrentBossIndex();
        if (index < 0 || index >= data.getBosses().size()) {
            throw new IllegalStateException("No current boss is available.");
        }
        return data.getBosses().get(index);
    }

    public RunState moveToNextBoss(UUID runId) {
        RunState run = getRun(runId);
        synchronized (run) {
            ensureNoActiveBattle(run);
            Boss current = getCurrentBoss(run);
            int wins = run.getBossWinCounts().getOrDefault(current.getId(), 0);
            if (wins <= 0) {
                throw new IllegalStateException("Defeat the current boss before continuing.");
            }

            run.setBattle(null);
            if (run.getCurrentBossIndex() >= data.getBosses().size() - 1) {
                run.setRunComplete(true);
            } else {
                run.setCurrentBossIndex(run.getCurrentBossIndex() + 1);
            }
            run.touch();
            return run;
        }
    }

    public void recalculateHeroStats(RunState run, boolean restoreGainedStats) {
        Hero hero = run.getHero();
        int oldMaxHp = hero.getMaxHp();
        int oldMaxEnergy = hero.getMaxEnergy();

        int newLevel = data.getLevelForXp(hero.getXp());
        hero.setLevel(newLevel);

        int vitalityLevel = run.getUpgradeLevels().getOrDefault("VITALITY_TRAINING", 0);
        int energyCoreLevel = run.getUpgradeLevels().getOrDefault("ENERGY_CORE", 0);

        int newMaxHp = Hero.BASE_MAX_HP + weaponHpBonus(hero.getWeaponId()) + (newLevel - 1) * 6 + vitalityLevel * 10;
        int newMaxEnergy = Hero.BASE_MAX_ENERGY + weaponEnergyBonus(hero.getWeaponId()) + (newLevel - 1) * 4 + energyCoreLevel * 8;

        hero.setMaxHp(newMaxHp);
        hero.setMaxEnergy(newMaxEnergy);

        if (restoreGainedStats) {
            int hpDelta = Math.max(0, newMaxHp - oldMaxHp);
            int energyDelta = Math.max(0, newMaxEnergy - oldMaxEnergy);
            hero.setCurrentHp(Math.min(newMaxHp, hero.getCurrentHp() + hpDelta));
            hero.setCurrentEnergy(Math.min(newMaxEnergy, hero.getCurrentEnergy() + energyDelta));
        } else {
            hero.setCurrentHp(Math.min(hero.getCurrentHp(), newMaxHp));
            hero.setCurrentEnergy(Math.min(hero.getCurrentEnergy(), newMaxEnergy));
        }
    }

    public RunState clearCompletedBattle(UUID runId) {
        RunState run = getRun(runId);
        synchronized (run) {
            if (run.getBattle() == null) {
                return run;
            }
            if (run.getBattle().getResult() == BattleResult.ONGOING) {
                throw new IllegalStateException("Finish the current battle before leaving it.");
            }
            run.setBattle(null);
            run.touch();
            return run;
        }
    }

    public void ensureNoActiveBattle(RunState run) {
        if (run.getBattle() != null && run.getBattle().getResult() == BattleResult.ONGOING) {
            throw new IllegalStateException("Finish the current battle before starting another one.");
        }
    }

    public String normalizeWeaponId(String weaponId) {
        if (weaponId == null || weaponId.isBlank()) {
            return "sword";
        }
        String normalized = weaponId.trim().toLowerCase();
        return switch (normalized) {
            case "sword", "bow", "staff", "dagger" -> normalized;
            default -> "sword";
        };
    }

    public String weaponLabel(String weaponId) {
        return switch (normalizeWeaponId(weaponId)) {
            case "bow" -> "Bow";
            case "staff" -> "Staff";
            case "dagger" -> "Dagger";
            default -> "Sword";
        };
    }

    private List<String> starterSkillsForWeapon(String weaponId) {
        return switch (normalizeWeaponId(weaponId)) {
            case "bow" -> List.of("AIMED_SHOT", "PIERCING_STRIKE", "GUARD", "FOCUS");
            case "staff" -> List.of("ARCANE_BOLT", "PIERCING_STRIKE", "GUARD", "FOCUS");
            case "dagger" -> List.of("DAGGER_FLURRY", "PIERCING_STRIKE", "GUARD", "FOCUS");
            default -> GameData.INITIAL_SKILLS;
        };
    }

    private int weaponHpBonus(String weaponId) {
        return switch (normalizeWeaponId(weaponId)) {
            case "sword" -> 5;
            case "staff" -> 3;
            default -> 0;
        };
    }

    private int weaponEnergyBonus(String weaponId) {
        return switch (normalizeWeaponId(weaponId)) {
            case "bow" -> 5;
            case "staff" -> 3;
            default -> 0;
        };
    }

    public void syncHeroFromBattle(RunState run) {
        if (run.getBattle() == null) {
            return;
        }
        run.getHero().setCurrentHp(run.getBattle().getHeroHp());
        run.getHero().setCurrentEnergy(run.getBattle().getHeroEnergy());
    }

    private boolean isExpired(RunState run, Instant now) {
        Instant lastAccess = run.getLastAccessedAt() == null ? run.getCreatedAt() : run.getLastAccessedAt();
        return lastAccess != null && lastAccess.plus(runTtl).isBefore(now);
    }

    private void purgeExpiredRuns() {
        Instant now = Instant.now();
        runs.entrySet().removeIf(entry -> isExpired(entry.getValue(), now));
    }

    private void enforceCapacity() {
        while (runs.size() >= maxActiveRuns) {
            UUID leastRecentRunId = runs.entrySet().stream()
                    .min(Comparator.comparing(entry -> entry.getValue().getLastAccessedAt()))
                    .map(Map.Entry::getKey)
                    .orElse(null);
            if (leastRecentRunId == null) {
                return;
            }
            runs.remove(leastRecentRunId);
        }
    }
}
