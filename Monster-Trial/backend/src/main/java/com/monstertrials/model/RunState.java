package com.monstertrials.model;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class RunState {
    private UUID runId;
    private Hero hero;
    private int currentBossIndex;
    private Map<String, Integer> upgradeLevels = new LinkedHashMap<>();
    private Map<String, Integer> potionInventory = new LinkedHashMap<>();
    private Map<String, Integer> bossWinCounts = new LinkedHashMap<>();
    private BattleState battle;
    private RewardSummary lastReward;
    private boolean runComplete;
    private int totalCoinsEarned;
    private Instant createdAt = Instant.now();
    private Instant lastAccessedAt = createdAt;

    public UUID getRunId() {
        return runId;
    }

    public void setRunId(UUID runId) {
        this.runId = runId;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public int getCurrentBossIndex() {
        return currentBossIndex;
    }

    public void setCurrentBossIndex(int currentBossIndex) {
        this.currentBossIndex = currentBossIndex;
    }

    public Map<String, Integer> getUpgradeLevels() {
        return upgradeLevels;
    }

    public void setUpgradeLevels(Map<String, Integer> upgradeLevels) {
        this.upgradeLevels = upgradeLevels;
    }

    public Map<String, Integer> getPotionInventory() {
        return potionInventory;
    }

    public void setPotionInventory(Map<String, Integer> potionInventory) {
        this.potionInventory = potionInventory == null ? new LinkedHashMap<>() : potionInventory;
    }

    public Map<String, Integer> getBossWinCounts() {
        return bossWinCounts;
    }

    public void setBossWinCounts(Map<String, Integer> bossWinCounts) {
        this.bossWinCounts = bossWinCounts;
    }

    public BattleState getBattle() {
        return battle;
    }

    public void setBattle(BattleState battle) {
        this.battle = battle;
    }

    public RewardSummary getLastReward() {
        return lastReward;
    }

    public void setLastReward(RewardSummary lastReward) {
        this.lastReward = lastReward;
    }

    public boolean isRunComplete() {
        return runComplete;
    }

    public void setRunComplete(boolean runComplete) {
        this.runComplete = runComplete;
    }

    public int getTotalCoinsEarned() {
        return totalCoinsEarned;
    }

    public void setTotalCoinsEarned(int totalCoinsEarned) {
        this.totalCoinsEarned = totalCoinsEarned;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
    }

    public Instant getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(Instant lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt == null ? Instant.now() : lastAccessedAt;
    }

    public void touch() {
        this.lastAccessedAt = Instant.now();
    }
}
