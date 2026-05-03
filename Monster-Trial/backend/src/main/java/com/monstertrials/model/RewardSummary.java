package com.monstertrials.model;

import java.util.ArrayList;
import java.util.List;

public class RewardSummary {
    private String bossId;
    private boolean firstWin;
    private int xpGained;
    private int coinsGained;
    private int oldLevel;
    private int newLevel;
    private List<String> learnedSkillIds = new ArrayList<>();
    private List<String> autoUnlockedSkillIds = new ArrayList<>();
    private String rewardType;

    public RewardSummary() {
    }

    public String getBossId() {
        return bossId;
    }

    public void setBossId(String bossId) {
        this.bossId = bossId;
    }

    public boolean isFirstWin() {
        return firstWin;
    }

    public void setFirstWin(boolean firstWin) {
        this.firstWin = firstWin;
    }

    public int getXpGained() {
        return xpGained;
    }

    public void setXpGained(int xpGained) {
        this.xpGained = xpGained;
    }

    public int getCoinsGained() {
        return coinsGained;
    }

    public void setCoinsGained(int coinsGained) {
        this.coinsGained = coinsGained;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public void setOldLevel(int oldLevel) {
        this.oldLevel = oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public void setNewLevel(int newLevel) {
        this.newLevel = newLevel;
    }

    public List<String> getLearnedSkillIds() {
        return learnedSkillIds;
    }

    public void setLearnedSkillIds(List<String> learnedSkillIds) {
        this.learnedSkillIds = learnedSkillIds;
    }

    public List<String> getAutoUnlockedSkillIds() {
        return autoUnlockedSkillIds;
    }

    public void setAutoUnlockedSkillIds(List<String> autoUnlockedSkillIds) {
        this.autoUnlockedSkillIds = autoUnlockedSkillIds;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }
}
