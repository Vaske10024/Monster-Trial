import UpgradeCard from '../components/UpgradeCard.jsx';
import PotionCard from '../components/PotionCard.jsx';
import PlayAgainBossPanel from '../components/PlayAgainBossPanel.jsx';

const groupLabels = {
  DEFENSE: 'Defense Upgrades',
  DAMAGE: 'Damage Masteries',
  UTILITY: 'Utility Training'
};

export default function ShopPage({ run, actions }) {
  const groups = run.upgrades.reduce((acc, upgrade) => {
    acc[upgrade.type] = acc[upgrade.type] || [];
    acc[upgrade.type].push(upgrade);
    return acc;
  }, {});

  const boss = run.currentBoss;
  const incoming = boss?.mainDamageTypes?.join(', ') || 'Mixed';

  return (
    <main className="page shop-page">
      <section className="page-header">
        <p className="eyebrow">Permanent upgrades</p>
        <h1>Monster-Trial Shop</h1>
        <p>Coins: <strong>{run.hero.coins}</strong>. Upgrades stay active for the rest of this run.</p>
      </section>

      <section className="panel hint-panel">
        <h2>Suggested for {boss.name}</h2>
        <div className="prep-grid">
          <div className="prep-card">
            <h3>Best damage type</h3>
            <p>Boost <strong>{boss.weakness}</strong> skills for extra value in this fight.</p>
          </div>
          <div className="prep-card">
            <h3>Important defenses</h3>
            <p>This boss mainly deals <strong>{incoming}</strong> damage, so those resistances are the best defensive buys.</p>
          </div>
          <div className="prep-card">
            <h3>Run economy</h3>
            <p>If you have already beaten this boss, play it again to earn more coins before continuing.</p>
          </div>
        </div>
      </section>

      <PlayAgainBossPanel run={run} actions={actions} />

      <section className="panel shop-section">
        <h2>Battle Potions</h2>
        <p className="fine-print">Potions are powerful consumables. Healing and cleansing use your action; Resistance is quick-use so it can be paired with an attack. Each type is once per battle.</p>
        <div className="upgrade-grid">
          {(run.potions || []).map((potion) => (
            <PotionCard key={potion.id} potion={potion} onBuy={actions.buyPotion} />
          ))}
        </div>
      </section>

      {['DEFENSE', 'DAMAGE', 'UTILITY'].map((group) => (
        <section key={group} className="panel shop-section">
          <h2>{groupLabels[group]}</h2>
          <div className="upgrade-grid">
            {(groups[group] || []).map((upgrade) => (
              <UpgradeCard key={upgrade.id} upgrade={upgrade} onBuy={actions.buyUpgrade} />
            ))}
          </div>
        </section>
      ))}

      <div className="action-row sticky-actions">
        <button className="secondary-btn" onClick={actions.goBack}>Back</button>
        <button className="primary-btn" onClick={() => actions.goTo('equip')}>Equip Skills</button>
      </div>
    </main>
  );
}
