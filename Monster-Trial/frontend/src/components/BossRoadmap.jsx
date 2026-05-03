import { useEffect, useMemo, useState } from 'react';
import { staticAssets } from '../assets/assetMap.js';

function initialsFor(name = 'Boss') {
  return name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join('') || 'B';
}

function BossMapIcon({ boss }) {
  const [loaded, setLoaded] = useState(false);
  const [failed, setFailed] = useState(false);
  const src = staticAssets.roadmap?.bossIcons?.[boss.id] || staticAssets.bosses?.[boss.id]?.portrait;

  useEffect(() => {
    setLoaded(false);
    setFailed(false);
  }, [src]);

  return (
    <span className="roadmap-node-icon" aria-hidden="true">
      {src && !failed && (
        <img
          src={src}
          alt=""
          onLoad={() => setLoaded(true)}
          onError={() => setFailed(true)}
        />
      )}
      {(!src || failed || !loaded) && <span>{initialsFor(boss.name)}</span>}
    </span>
  );
}

function statusForBoss(run, boss, index) {
  const ongoingBossId = run.battle?.result === 'ONGOING' ? run.battle?.boss?.id : null;
  const nextBossCanOpen = !run.runComplete && run.currentBoss?.defeated && index === run.currentBossIndex + 1;

  if (ongoingBossId === boss.id) return 'fighting';
  if (boss.defeated) return 'cleared';
  if (run.runComplete) return 'cleared';
  if (nextBossCanOpen) return 'next-ready';
  if (index === run.currentBossIndex) return 'current';
  return 'locked';
}

function actionLabelFor(status, run, boss, index) {
  if (run.battle?.result === 'ONGOING') return status === 'fighting' ? 'In Battle' : 'Finish Battle';
  if (status === 'locked') return 'Locked';
  if (status === 'next-ready') return 'Start Next';
  if (boss.defeated || run.runComplete) return index === run.currentBossIndex && !run.runComplete ? 'Play Again' : 'Play Again';
  return 'Start Fight';
}

function helperTextFor(status, boss, index, run) {
  if (status === 'fighting') return 'Current active fight.';
  if (status === 'cleared') return `${boss.winCount || 1} win${boss.winCount === 1 ? '' : 's'} recorded.`;
  if (status === 'next-ready') return 'Unlocked by your last victory.';
  if (status === 'current') return 'Current required trial.';
  const previous = run.bosses?.[Math.max(0, index - 1)];
  return previous ? `Defeat ${previous.name} first.` : 'Locked.';
}

export default function BossRoadmap({ run, actions }) {
  const [open, setOpen] = useState(false);
  const [busyBossId, setBusyBossId] = useState('');
  const bosses = run?.bosses || [];
  const isBattleOngoing = run?.battle?.result === 'ONGOING';

  const progressText = useMemo(() => {
    if (!run) return '';
    const defeated = bosses.filter((boss) => boss.defeated).length;
    if (run.runComplete) return 'All bosses cleared';
    return `${defeated} / ${bosses.length} bosses defeated`;
  }, [bosses, run]);

  useEffect(() => {
    if (!open) return undefined;
    function handleKeyDown(event) {
      if (event.key === 'Escape') {
        setOpen(false);
      }
    }
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [open]);

  if (!run || bosses.length === 0) return null;

  const panelStyle = staticAssets.roadmap?.panel
    ? {
        backgroundImage: `radial-gradient(circle at 85% 8%, rgba(244, 199, 107, .18), transparent 16rem), radial-gradient(circle at 10% 90%, rgba(98, 196, 107, .10), transparent 15rem), linear-gradient(145deg, rgba(38, 30, 50, .97), rgba(12, 13, 22, .97)), url(${staticAssets.roadmap.panel})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center'
      }
    : undefined;

  async function chooseBoss(boss, index, status) {
    if (isBattleOngoing || busyBossId || status === 'locked') return;

    setBusyBossId(boss.id);
    try {
      let updated = null;
      if (status === 'next-ready') {
        updated = await actions.nextBoss();
        if (!updated) return;
        updated = await actions.startBattle();
      } else if (boss.defeated || run.runComplete) {
        updated = await actions.replayBoss(boss.id);
      } else if (index === run.currentBossIndex) {
        updated = await actions.startBattle();
      }

      if (updated) {
        setOpen(false);
      }
    } finally {
      setBusyBossId('');
    }
  }

  return (
    <div className={`boss-roadmap ${open ? 'is-open' : ''}`}>
      <button
        type="button"
        className="roadmap-toggle"
        onClick={() => setOpen((value) => !value)}
        aria-expanded={open}
        aria-controls="boss-roadmap-panel"
      >
        <span className="roadmap-toggle-icon">✦</span>
        <span>Boss Map</span>
      </button>

      {open && (
        <section id="boss-roadmap-panel" className="roadmap-panel panel" aria-label="Boss roadmap" style={panelStyle}>
          <div className="roadmap-header">
            <div>
              <p className="eyebrow">Roadmap</p>
              <h2>Boss Route</h2>
              <p>{progressText}</p>
            </div>
            <button type="button" className="ghost-btn" onClick={() => setOpen(false)} aria-label="Close boss roadmap">×</button>
          </div>

          <div className="roadmap-track" role="list">
            {bosses.map((boss, index) => {
              const status = statusForBoss(run, boss, index);
              const disabled = isBattleOngoing || busyBossId || status === 'locked';
              const label = actionLabelFor(status, run, boss, index);

              return (
                <article key={boss.id} className={`roadmap-node roadmap-node-${status}`} role="listitem">
                  {index > 0 && <span className={`roadmap-connector roadmap-connector-${status}`} aria-hidden="true" />}
                  <button
                    type="button"
                    className="roadmap-node-button"
                    disabled={disabled}
                    onClick={() => chooseBoss(boss, index, status)}
                  >
                    <BossMapIcon boss={boss} />
                    <span className="roadmap-node-copy">
                      <strong>{boss.name}</strong>
                      <small>Trial {index + 1} · {helperTextFor(status, boss, index, run)}</small>
                    </span>
                    <span className="roadmap-node-action">{busyBossId === boss.id ? 'Opening...' : label}</span>
                  </button>
                </article>
              );
            })}
          </div>

          <div className="roadmap-legend">
            <span><i className="legend-dot legend-current" /> Current</span>
            <span><i className="legend-dot legend-cleared" /> Cleared</span>
            <span><i className="legend-dot legend-locked" /> Locked</span>
          </div>
        </section>
      )}
    </div>
  );
}
