import BossCard from '../components/BossCard.jsx';
import DamageTypeBadge from '../components/DamageTypeBadge.jsx';
import HeroCard from '../components/HeroCard.jsx';
import SkillCard from '../components/SkillCard.jsx';
import { staticAssets } from '../assets/assetMap.js';
import PlayAgainBossPanel from '../components/PlayAgainBossPanel.jsx';

export default function BossPreviewPage({ run, actions, characterProfile }) {
  const boss = run.currentBoss;
  const background = staticAssets.backgrounds[boss?.id];
  const learnedIds = new Set(run.hero.learnedSkillIds);
  const learnable = boss?.learnableSkills || [];
  const combatStats = run.hero?.combatStats || {};
  const mastery = combatStats.damageMasteryPercentByType || {};
  const resistances = combatStats.totalResistancePercentByType || {};
  const mainDamageTypes = boss?.mainDamageTypes || [];

  return (
    <main className="page arena-page" style={{ backgroundImage: `linear-gradient(180deg, rgba(9,10,18,.88), rgba(9,10,18,.94)), url(${background})` }}>
      <div className="top-nav">
        <button className="ghost-btn" onClick={actions.clearRun}>New Run</button>
      </div>
      <section className="page-header">
        <p className="eyebrow">Trial {run.currentBossIndex + 1} of {run.bosses.length}</p>
        <h1>{boss.name}</h1>
        <p>Preview the monster, check the type matchup, upgrade in the shop, equip up to four learned skills, then start the battle.</p>
      </section>

      <div className="two-column">
        <HeroCard hero={run.hero} profile={characterProfile} />
        <BossCard boss={boss} />
      </div>

      <section className="panel hint-panel">
        <h2>Preparation Guide</h2>
        <div className="prep-grid">
          <div className="prep-card">
            <h3>Exploit Weakness</h3>
            <p>Use <strong>{boss.weakness}</strong> skills for 1.5x damage. Avoid overusing <strong>{boss.resistance}</strong> into this boss.</p>
            <div className="learnable-row">
              <DamageTypeBadge type={boss.weakness} />
              <span className="stat-chip">Current {boss.weakness} mastery +{mastery[boss.weakness] || 0}%</span>
              <DamageTypeBadge type={boss.resistance} />
            </div>
          </div>

          <div className="prep-card">
            <h3>Incoming Damage</h3>
            <p>{boss.name} mainly deals the following damage types. Upgrade these resistances if you want a safer fight.</p>
            <div className="mini-chip-grid">
              {mainDamageTypes.map((type) => (
                <span key={type} className="stat-chip">{type} resist {resistances[type] || 0}%</span>
              ))}
            </div>
          </div>

          <div className="prep-card">
            <h3>Play Again Rewards</h3>
            <p>First clears give the full reward. After a clear, play again to earn extra coins and XP before moving on.</p>
            <div className="mini-chip-grid">
              <span className="stat-chip">First win: {boss.firstWinCoins} coins</span>
              <span className="stat-chip">First win: {boss.firstWinXp} XP</span>
              {boss.defeated && <span className="stat-chip">Beaten {boss.winCount} time{boss.winCount === 1 ? '' : 's'}</span>}
            </div>
          </div>
        </div>

        <div className="learnable-row">
          {learnable.map((skill) => (
            <div key={skill.id} className="learnable-chip">
              {skill.name} {learnedIds.has(skill.id) ? <span>learned</span> : <span>learnable</span>}
            </div>
          ))}
        </div>
      </section>

      <PlayAgainBossPanel run={run} actions={actions} compact />

      <section className="panel equipped-panel">
        <div className="section-title-row">
          <h2>Equipped Skills</h2>
          <span>{run.hero.equippedSkillIds.length} / {run.hero.maxEquippedSkills}</span>
        </div>
        <div className="skill-grid compact-grid">
          {run.hero.equippedSkills.map((skill) => <SkillCard key={skill.id} skill={skill} compact equipped tooltipContext={{ boss: run.currentBoss }} />)}
        </div>
      </section>

      <div className="action-row sticky-actions">
        <button className="secondary-btn" onClick={() => actions.goTo('shop')}>Shop</button>
        <button className="secondary-btn" onClick={() => actions.goTo('equip')}>Equip Skills</button>
        {boss.defeated && <button className="secondary-btn" onClick={() => actions.replayBoss(boss.id)}>Play Again</button>}
        <button className="primary-btn" onClick={actions.startBattle}>{boss.defeated ? 'Battle Again' : 'Start Battle'}</button>
      </div>
    </main>
  );
}
