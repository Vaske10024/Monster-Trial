package com.monstertrials.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Set;

public class BattleState {
    private Boss boss;
    private int heroHp;
    private int heroEnergy;
    private int heroMomentum;
    private int heroMaxMomentum = 100;
    private int bossHp;
    private Map<String, Integer> heroCooldowns = new LinkedHashMap<>();
    private Map<String, Integer> bossCooldowns = new LinkedHashMap<>();
    private Set<String> usedPotionIds = new LinkedHashSet<>();
    private List<StatusEffect> heroStatusEffects = new ArrayList<>();
    private List<StatusEffect> bossStatusEffects = new ArrayList<>();
    private List<String> battleLog = new ArrayList<>();
    private BattleResult result = BattleResult.ONGOING;
    private String lastEffectiveness = "NORMAL";
    private boolean rewardApplied = false;
    private int turnNumber = 1;
    private BossIntent bossIntent;
    private boolean phaseTwo;

    public Boss getBoss() { return boss; }
    public void setBoss(Boss boss) { this.boss = boss; }
    public int getHeroHp() { return heroHp; }
    public void setHeroHp(int heroHp) { this.heroHp = Math.max(0, heroHp); }
    public int getHeroEnergy() { return heroEnergy; }
    public void setHeroEnergy(int heroEnergy) { this.heroEnergy = Math.max(0, heroEnergy); }
    public int getHeroMomentum() { return heroMomentum; }
    public void setHeroMomentum(int heroMomentum) { this.heroMomentum = Math.max(0, Math.min(heroMomentum, heroMaxMomentum)); }
    public int getHeroMaxMomentum() { return heroMaxMomentum; }
    public void setHeroMaxMomentum(int heroMaxMomentum) { this.heroMaxMomentum = Math.max(1, heroMaxMomentum); }
    public int getBossHp() { return bossHp; }
    public void setBossHp(int bossHp) { this.bossHp = Math.max(0, bossHp); }
    public Map<String, Integer> getHeroCooldowns() { return heroCooldowns; }
    public void setHeroCooldowns(Map<String, Integer> heroCooldowns) { this.heroCooldowns = heroCooldowns; }
    public Map<String, Integer> getBossCooldowns() { return bossCooldowns; }
    public void setBossCooldowns(Map<String, Integer> bossCooldowns) { this.bossCooldowns = bossCooldowns; }
    public Set<String> getUsedPotionIds() { return usedPotionIds; }
    public void setUsedPotionIds(Set<String> usedPotionIds) { this.usedPotionIds = usedPotionIds == null ? new LinkedHashSet<>() : usedPotionIds; }
    public List<StatusEffect> getHeroStatusEffects() { return heroStatusEffects; }
    public void setHeroStatusEffects(List<StatusEffect> heroStatusEffects) { this.heroStatusEffects = heroStatusEffects; }
    public List<StatusEffect> getBossStatusEffects() { return bossStatusEffects; }
    public void setBossStatusEffects(List<StatusEffect> bossStatusEffects) { this.bossStatusEffects = bossStatusEffects; }
    public List<String> getBattleLog() { return battleLog; }
    public void setBattleLog(List<String> battleLog) { this.battleLog = battleLog; }
    public BattleResult getResult() { return result; }
    public void setResult(BattleResult result) { this.result = result; }
    public String getLastEffectiveness() { return lastEffectiveness; }
    public void setLastEffectiveness(String lastEffectiveness) { this.lastEffectiveness = lastEffectiveness; }
    public boolean isRewardApplied() { return rewardApplied; }
    public void setRewardApplied(boolean rewardApplied) { this.rewardApplied = rewardApplied; }
    public int getTurnNumber() { return turnNumber; }
    public void setTurnNumber(int turnNumber) { this.turnNumber = turnNumber; }
    public BossIntent getBossIntent() { return bossIntent; }
    public void setBossIntent(BossIntent bossIntent) { this.bossIntent = bossIntent; }
    public boolean isPhaseTwo() { return phaseTwo; }
    public void setPhaseTwo(boolean phaseTwo) { this.phaseTwo = phaseTwo; }
}
