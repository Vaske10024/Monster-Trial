import SkillCard from '../components/SkillCard.jsx';
import PlayAgainBossPanel from '../components/PlayAgainBossPanel.jsx';

export default function RunCompletePage({ run, actions }) {
  return (
    <main className="page result-page complete-page">
      <section className="panel result-panel wide">
        <p className="eyebrow">Run Complete</p>
        <h1>Monster-Trial Conquered</h1>
        <p>The hero defeated all five bosses and mastered the trial path.</p>

        <div className="reward-grid">
          <div><span>Final level</span><strong>{run.hero.level}</strong></div>
          <div><span>Total XP</span><strong>{run.hero.xp}</strong></div>
          <div><span>Total coins earned</span><strong>{run.totalCoinsEarned}</strong></div>
          <div><span>Learned skills</span><strong>{run.hero.learnedSkills.length}</strong></div>
        </div>

        <section className="result-subsection">
          <h2>Defeated Bosses</h2>
          <div className="boss-complete-list">
            {run.bosses.map((boss) => (
              <div key={boss.id} className="complete-boss-row">
                <strong>{boss.name}</strong>
                <span>{boss.winCount} win{boss.winCount === 1 ? '' : 's'}</span>
              </div>
            ))}
          </div>
        </section>

        <PlayAgainBossPanel run={run} actions={actions} />

        <section className="result-subsection">
          <h2>Learned Skills</h2>
          <div className="skill-grid compact-grid">
            {run.hero.learnedSkills.map((skill) => <SkillCard key={skill.id} skill={skill} compact equipped tooltipContext={{ boss: run.currentBoss }} />)}
          </div>
        </section>

        <div className="action-row">
          <button className="primary-btn" onClick={actions.restartAtBuilder}>Start New Run</button>
        </div>
      </section>
    </main>
  );
}
