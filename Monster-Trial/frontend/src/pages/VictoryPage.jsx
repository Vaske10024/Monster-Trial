import SkillCard from '../components/SkillCard.jsx';
import PlayAgainBossPanel from '../components/PlayAgainBossPanel.jsx';

const rewardLabels = {
  FIRST_WIN: 'First Win',
  PLAY_AGAIN_SKILL_AVAILABLE: 'Play Again Skill Bonus',
  PLAY_AGAIN_BONUS: 'Play Again Bonus'
};

export default function VictoryPage({ run, actions }) {
  const reward = run.lastReward;
  const defeatedBoss = run.battle?.boss || run.currentBoss;
  const defeatedCurrentBoss = defeatedBoss?.id === run.currentBoss?.id;
  const finalBoss = defeatedCurrentBoss && run.currentBossIndex >= run.bosses.length - 1;
  const canContinueProgress = defeatedCurrentBoss && !run.runComplete;

  return (
    <main className="page result-page victory-page">
      <section className="panel result-panel">
        <p className="eyebrow">Victory</p>
        <h1>{defeatedBoss?.name} defeated</h1>
        {reward ? (
          <div className="reward-grid">
            <div><span>XP gained</span><strong>{reward.xpGained}</strong></div>
            <div><span>Coins gained</span><strong>{reward.coinsGained}</strong></div>
            <div><span>Reward</span><strong>{rewardLabels[reward.rewardType] || 'Bonus'}</strong></div>
            <div><span>Level</span><strong>{reward.oldLevel} → {reward.newLevel}</strong></div>
          </div>
        ) : <p>Rewards were already claimed.</p>}

        {reward?.learnedSkills?.length > 0 && (
          <section className="result-subsection">
            <h2>Skill Learned</h2>
            <div className="skill-grid compact-grid">
              {reward.learnedSkills.map((skill) => <SkillCard key={skill.id} skill={skill} compact equipped tooltipContext={{ boss: defeatedBoss }} />)}
            </div>
          </section>
        )}

        {reward?.autoUnlockedSkills?.length > 0 && (
          <section className="result-subsection">
            <h2>Automatic Unlock</h2>
            <div className="skill-grid compact-grid">
              {reward.autoUnlockedSkills.map((skill) => <SkillCard key={skill.id} skill={skill} compact equipped tooltipContext={{ boss: defeatedBoss }} />)}
            </div>
          </section>
        )}

        <PlayAgainBossPanel run={run} actions={actions} compact />

        <div className="action-row">
          <button className="secondary-btn" onClick={() => actions.replayBoss(defeatedBoss?.id)}>Play Again</button>
          <button className="secondary-btn" onClick={() => actions.goTo('shop')}>Shop</button>
          <button className="secondary-btn" onClick={() => actions.goTo('equip')}>Equip Skills</button>
          {canContinueProgress ? (
            <button className="primary-btn" onClick={actions.nextBoss}>{finalBoss ? 'Finish Run' : 'Continue'}</button>
          ) : (
            <button className="primary-btn" onClick={actions.clearBattle}>{run.runComplete ? 'Back to Run Summary' : 'Back to Current Trial'}</button>
          )}
        </div>
      </section>
    </main>
  );
}
