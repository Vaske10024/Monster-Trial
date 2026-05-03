package com.monstertrials.service;

import com.monstertrials.data.GameData;
import com.monstertrials.model.BattleState;
import com.monstertrials.model.Boss;
import com.monstertrials.model.Hero;
import com.monstertrials.model.RewardSummary;
import com.monstertrials.model.RunState;
import com.monstertrials.model.Skill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RewardService {
    private final GameData data;
    private final RunService runService;

    public RewardService(GameData data, RunService runService) {
        this.data = data;
        this.runService = runService;
    }


    public int replayXpForBoss(Boss boss) {
        return Math.max(8, (int) Math.round(boss.getFirstWinXp() * 0.40));
    }

    public int replayCoinsForBoss(Boss boss, boolean skillAvailable) {
        double multiplier = skillAvailable ? 0.45 : 0.35;
        return Math.max(12, (int) Math.round(boss.getFirstWinCoins() * multiplier));
    }

    public RewardSummary applyVictoryRewards(RunState run, BattleState battle) {
        if (battle.isRewardApplied()) {
            return run.getLastReward();
        }

        Boss boss = battle.getBoss();
        Hero hero = run.getHero();
        int previousWins = run.getBossWinCounts().getOrDefault(boss.getId(), 0);
        boolean firstWin = previousWins == 0;

        List<String> unlearnedBossSkills = boss.getLearnableSkillIds().stream()
                .filter(skillId -> !hero.getLearnedSkillIds().contains(skillId))
                .toList();

        int xpGained;
        int coinsGained;
        String rewardType;
        if (firstWin) {
            xpGained = boss.getFirstWinXp();
            coinsGained = boss.getFirstWinCoins();
            rewardType = "FIRST_WIN";
        } else if (!unlearnedBossSkills.isEmpty()) {
            xpGained = replayXpForBoss(boss);
            coinsGained = replayCoinsForBoss(boss, true);
            rewardType = "PLAY_AGAIN_SKILL_AVAILABLE";
        } else {
            xpGained = replayXpForBoss(boss);
            coinsGained = replayCoinsForBoss(boss, false);
            rewardType = "PLAY_AGAIN_BONUS";
        }

        RewardSummary reward = new RewardSummary();
        reward.setBossId(boss.getId());
        reward.setFirstWin(firstWin);
        reward.setXpGained(xpGained);
        reward.setCoinsGained(coinsGained);
        reward.setRewardType(rewardType);
        reward.setOldLevel(hero.getLevel());

        hero.setXp(hero.getXp() + xpGained);
        hero.setCoins(hero.getCoins() + coinsGained);
        run.setTotalCoinsEarned(run.getTotalCoinsEarned() + coinsGained);
        runService.recalculateHeroStats(run, true);
        reward.setNewLevel(hero.getLevel());

        if (!unlearnedBossSkills.isEmpty()) {
            List<String> choices = new ArrayList<>(unlearnedBossSkills);
            String learnedSkillId = choices.get(ThreadLocalRandom.current().nextInt(choices.size()));
            hero.getLearnedSkillIds().add(learnedSkillId);
            reward.getLearnedSkillIds().add(learnedSkillId);
        }

        if (firstWin && "VENOM_SPIDER".equals(boss.getId()) && !hero.getLearnedSkillIds().contains("ICE_ARROW")) {
            hero.getLearnedSkillIds().add("ICE_ARROW");
            reward.getAutoUnlockedSkillIds().add("ICE_ARROW");
        }

        run.getBossWinCounts().put(boss.getId(), previousWins + 1);
        battle.setRewardApplied(true);
        run.setLastReward(reward);

        battle.getBattleLog().add("Victory rewards: +" + xpGained + " XP, +" + coinsGained + " coins.");
        if (reward.getNewLevel() > reward.getOldLevel()) {
            battle.getBattleLog().add("Hero reached level " + reward.getNewLevel() + "! Max HP and Energy increased.");
        }
        for (String skillId : reward.getLearnedSkillIds()) {
            Skill skill = data.getSkill(skillId);
            battle.getBattleLog().add("Hero learned " + skill.getName() + ".");
        }
        for (String skillId : reward.getAutoUnlockedSkillIds()) {
            Skill skill = data.getSkill(skillId);
            battle.getBattleLog().add(skill.getName() + " unlocked automatically for the next trial.");
        }

        return reward;
    }
}
