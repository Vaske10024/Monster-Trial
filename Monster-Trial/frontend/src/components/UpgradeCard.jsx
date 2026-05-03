import DamageTypeBadge from './DamageTypeBadge.jsx';
import Sprite from './Sprite.jsx';
import { upgradeIcons } from '../assets/assetMap.js';

export default function UpgradeCard({ upgrade, onBuy }) {
  const disabled = upgrade.maxed || !upgrade.canAfford;

  return (
    <article className={`upgrade-card ${upgrade.maxed ? 'maxed' : ''}`}>
      <div className="upgrade-head">
        <Sprite src={upgradeIcons[upgrade.id]} alt={`${upgrade.name} icon`} fallbackLabel={upgrade.name} className="icon-sprite" size={56} />
        <div className="upgrade-main">
          <h3>{upgrade.name}</h3>
          <div className="upgrade-badges">
            <span className="tag-pill">{upgrade.type}</span>
            {upgrade.damageType && <DamageTypeBadge type={upgrade.damageType} />}
          </div>
        </div>
      </div>

      <p>{upgrade.effectDescription}</p>

      <div className="upgrade-meta upgrade-meta-grid">
        <span>Level <strong>{upgrade.currentLevel}</strong> / {upgrade.maxLevel}</span>
        <span>{upgrade.maxed ? 'MAX LEVEL' : `Next cost ${upgrade.nextCost} coins`}</span>
      </div>

      <button className="secondary-btn full" disabled={disabled} onClick={() => onBuy(upgrade.id)}>
        {upgrade.maxed ? 'Maxed' : upgrade.canAfford ? 'Upgrade' : 'Need coins'}
      </button>
    </article>
  );
}
