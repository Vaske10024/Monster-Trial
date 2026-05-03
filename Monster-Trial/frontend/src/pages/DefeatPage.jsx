import PlayAgainBossPanel from '../components/PlayAgainBossPanel.jsx';

export default function DefeatPage({ run, actions }) {
  const defeatedBy = run.battle?.boss || run.currentBoss;
  const retryLabel = defeatedBy?.id === run.currentBoss?.id ? 'Retry' : 'Try Again';

  return (
    <main className="page result-page defeat-page">
      <section className="panel result-panel">
        <p className="eyebrow">Defeat</p>
        <h1>{defeatedBy?.name} overcame the hero</h1>
        <p>The run is not reset. Buy upgrades, change skills, or retry the same boss immediately.</p>
        <div className="defeat-stats">
          <span>Hero Level <strong>{run.hero.level}</strong></span>
          <span>Coins <strong>{run.hero.coins}</strong></span>
          <span>Current Trial <strong>{run.currentBossIndex + 1}</strong> / {run.bosses.length}</span>
        </div>
        <PlayAgainBossPanel run={run} actions={actions} compact />

        <div className="action-row">
          <button className="secondary-btn" onClick={() => actions.goTo('shop')}>Shop</button>
          <button className="secondary-btn" onClick={() => actions.goTo('equip')}>Equip Skills</button>
          <button className="secondary-btn" onClick={actions.clearBattle}>{run.runComplete ? 'Back to Run Summary' : 'Back to Current Trial'}</button>
          <button className="primary-btn" onClick={actions.retryBattle}>{retryLabel}</button>
        </div>
      </section>
    </main>
  );
}
