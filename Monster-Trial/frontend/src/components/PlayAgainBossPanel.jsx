export default function PlayAgainBossPanel({ run, actions, compact = false }) {
  if (!run || run.battle?.result === 'ONGOING') return null;

  const defeatedBosses = (run.bosses || []).filter((boss) => boss.defeated);
  if (defeatedBosses.length === 0) return null;

  return (
    <section className={`panel play-again-panel ${compact ? 'compact-play-again-panel' : ''}`}>
      <div className="play-again-hero">
        <div>
          <p className="eyebrow">Play Again</p>
          <h2>Replay cleared bosses</h2>
          <p>Jump back into any victory fight to earn bonus coins, XP, and remaining skill unlocks.</p>
        </div>
        <span className="play-again-count">{defeatedBosses.length} ready</span>
      </div>

      <div className="play-again-boss-grid">
        {defeatedBosses.map((boss) => (
          <article key={boss.id} className="play-again-boss-card">
            <div className="play-again-card-top">
              <div>
                <strong>{boss.name}</strong>
                <span>{boss.winCount} win{boss.winCount === 1 ? '' : 's'}</span>
              </div>
              <span className="play-again-sigil" aria-hidden="true">+</span>
            </div>

            <div className="play-again-rewards" aria-label={`${boss.name} replay rewards`}>
              <span><strong>{boss.replayCoins}</strong><small>coins</small></span>
              <span><strong>{boss.replayXp}</strong><small>XP</small></span>
            </div>

            <button className="secondary-btn play-again-btn" onClick={() => actions.replayBoss(boss.id)}>
              Play Again
            </button>
          </article>
        ))}
      </div>
    </section>
  );
}
