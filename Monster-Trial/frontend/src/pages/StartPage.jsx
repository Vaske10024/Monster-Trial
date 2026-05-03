import { staticAssets } from '../assets/assetMap.js';

export default function StartPage({ onStart, characterProfile }) {
  return (
    <main className="start-page" style={{ backgroundImage: `linear-gradient(90deg, rgba(10,8,20,.88), rgba(10,8,20,.56)), url(${staticAssets.backgrounds.start})` }}>
      <section className="start-panel panel">
        <p className="eyebrow">Turn-based boss rush RPG</p>
        <h1>Monster-Trial</h1>
        <p className="hero-copy">
          Defeat five monsters, learn their skills, spend coins on permanent upgrades, and adapt your loadout to every weakness, resistance, and boss intent.
        </p>
        <div className="start-summary-row">
          <div className="start-summary-card">
            <strong>Read the boss</strong>
            <span>Check intent, weakness, and resistance before every fight.</span>
          </div>
          <div className="start-summary-card">
            <strong>Build your hero</strong>
            <span>Choose a weapon, class style, colors, and starter kit.</span>
          </div>
          <div className="start-summary-card">
            <strong>Last hero</strong>
            <span>{characterProfile?.name || 'Eira'} the {characterProfile?.title || 'Rune Warden'}</span>
          </div>
        </div>
        <button className="primary-btn" onClick={onStart}>Create Character</button>
        <p className="fine-print">Create your hero, read each boss, and swap skills before every trial.</p>
      </section>
    </main>
  );
}
