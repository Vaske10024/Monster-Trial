import DamageTypeBadge from './DamageTypeBadge.jsx';
import HpBar from './HpBar.jsx';
import Sprite from './Sprite.jsx';
import { staticAssets } from '../assets/assetMap.js';
import { safeClassSuffix } from '../utils/cssClass.js';

export default function BossCard({ boss, currentHp }) {
  if (!boss) return null;

  const art = staticAssets.bosses[boss.id] || {};
  const bossImage = art.fullbody || art.portrait;
  const bossClass = safeClassSuffix(boss.id);

  return (
    <article className="boss-card panel">
      <div className="card-visual-col boss-visual">
        <div className={`boss-portrait-frame boss-portrait-frame-${bossClass}`}>
          <Sprite
            src={bossImage}
            alt={`${boss.name} art`}
            fallbackLabel={boss.name}
            className={`boss-portrait-img boss-portrait-img-${bossClass}`}
          />
        </div>
      </div>

      <div className="boss-info">
        <h2>{boss.name}</h2>
        <p>{boss.description}</p>
        <HpBar current={currentHp ?? boss.maxHp} max={boss.maxHp} label="Boss HP" />

        <div className="weak-res-grid">
          <div>
            <span>Weakness</span>
            <DamageTypeBadge type={boss.weakness} />
          </div>
          <div>
            <span>Resistance</span>
            <DamageTypeBadge type={boss.resistance} />
          </div>
        </div>

        <div className="hero-detail-grid">
          <div>
            <h3>Main damage types</h3>
            <div className="mini-chip-grid">
              {boss.mainDamageTypes?.map((type) => (
                <DamageTypeBadge key={type} type={type} />
              ))}
            </div>
          </div>
          <div>
            <h3>Rewards</h3>
            <div className="mini-chip-grid">
              <span className="stat-chip">{boss.firstWinXp} XP first clear</span>
              <span className="stat-chip">{boss.firstWinCoins} coins first clear</span>
            </div>
          </div>
        </div>

        {boss.defeated && (
          <div className="success-banner">
            Defeated {boss.winCount} time{boss.winCount === 1 ? '' : 's'} — playing again still gives bonus rewards.
          </div>
        )}
      </div>
    </article>
  );
}
