import Sprite from './Sprite.jsx';
import { potionIcons } from '../assets/assetMap.js';

export default function PotionCard({ potion, mode = 'shop', disabled = false, onBuy, onUse }) {
  if (!potion) return null;

  const isBattle = mode === 'battle';
  const quantity = potion.quantity || 0;
  const shopDisabled = !potion.canAfford;
  const battleDisabled = disabled || quantity <= 0 || !potion.canUseInBattle;
  const buttonLabel = isBattle
    ? potion.usedThisBattle
      ? 'Used this battle'
      : quantity <= 0
        ? 'None owned'
        : 'Use Potion'
    : potion.canAfford
      ? 'Buy Potion'
      : 'Need coins';

  return (
    <article className={`upgrade-card potion-card ${potion.usedThisBattle ? 'maxed' : ''}`}>
      <div className="upgrade-head">
        <Sprite src={potionIcons[potion.id]} alt={`${potion.name} icon`} fallbackLabel={potion.name} className="icon-sprite" size={56} />
        <div className="upgrade-main">
          <h3>{potion.name}</h3>
          <div className="upgrade-badges">
            <span className="tag-pill">Potion</span>
            <span className="tag-pill">Owned {quantity}</span>
          </div>
        </div>
      </div>

      <p>{potion.effectDescription}</p>

      <div className="upgrade-meta upgrade-meta-grid">
        <span>Cost <strong>{potion.cost}</strong> coins</span>
        <span>{potion.consumesAction ? 'Uses action' : 'Quick use'}</span>
        <span>{potion.oneUsePerBattle ? 'Once per battle' : 'Consumable'}</span>
      </div>

      <button
        className="secondary-btn full"
        disabled={isBattle ? battleDisabled : shopDisabled}
        onClick={() => isBattle ? onUse?.(potion.id) : onBuy?.(potion.id)}
      >
        {buttonLabel}
      </button>
    </article>
  );
}
