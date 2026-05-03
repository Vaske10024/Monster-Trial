import EnergyBar from './EnergyBar.jsx';
import HpBar from './HpBar.jsx';
import CharacterAvatar from './CharacterAvatar.jsx';

const offenseTypes = ['Slash', 'Pierce', 'Frost', 'Poison', 'Fire'];

export default function HeroCard({ hero, battle, profile }) {
  if (!hero) return null;

  const nextLevel = hero.nextLevelXp > 0 ? hero.nextLevelXp : 'MAX';
  const combatStats = hero.combatStats || {};
  const mastery = combatStats.damageMasteryPercentByType || {};
  const resistances = combatStats.totalResistancePercentByType || {};
  const heroName = profile?.name || 'Trial Hero';
  const heroTitle = profile?.title || 'Monster Hunter';

  return (
      <article className="hero-card panel">
        <div className="card-visual-col hero-visual">
          <div className="hero-portrait-frame">
            <CharacterAvatar
                profile={profile}
                variant="portrait"
                className="hero-card-avatar"
                ariaLabel={`${heroName} portrait`}
            />
          </div>
        </div>

        <div className="hero-info">
          <h2>{heroName}</h2>
          <p className="fine-print hero-subtitle">{heroTitle}</p>

          <div className="hero-stats-grid">
            <span>Level <strong>{hero.level}</strong></span>
            <span>XP <strong>{hero.xp}</strong> / {nextLevel}</span>
            <span>Coins <strong>{hero.coins}</strong></span>
            <span>Weapon <strong>{hero.weaponLabel || profile?.weapon || 'Sword'}</strong></span>
            <span>Skills <strong>{hero.equippedSkillIds.length}</strong> / {hero.maxEquippedSkills}</span>
          </div>

          <HpBar current={battle?.heroHp ?? hero.currentHp} max={hero.maxHp} label="Hero HP" />
          <EnergyBar current={battle?.heroEnergy ?? hero.currentEnergy} max={hero.maxEnergy} />

          <div className="hero-bonus-grid">
            <div>
              <span>Global damage bonus</span>
              <strong>+{combatStats.globalDamageBonusPercent || 0}%</strong>
            </div>
            <div>
              <span>General resistance</span>
              <strong>{combatStats.generalResistancePercent || 0}%</strong>
            </div>
          </div>

          <div className="hero-detail-grid">
            <div>
              <h3>Damage Mastery</h3>
              <div className="mini-chip-grid">
                {offenseTypes.map((type) => (
                    <span key={`mastery-${type}`} className="stat-chip">{type} +{mastery[type] || 0}%</span>
                ))}
              </div>
            </div>

            <div>
              <h3>Resistances</h3>
              <div className="mini-chip-grid">
                {offenseTypes.map((type) => (
                    <span key={`res-${type}`} className="stat-chip">{type} {resistances[type] || 0}%</span>
                ))}
              </div>
            </div>
          </div>
        </div>
      </article>
  );
}
