import { useState } from 'react';
import CharacterAvatar from '../components/CharacterAvatar.jsx';
import { builderOptions, normalizeCharacterProfile } from '../data/characterOptions.js';
import { staticAssets } from '../assets/assetMap.js';

const titleByClass = {
  knight: 'Knight',
  ranger: 'Ranger',
  mage: 'Mage',
  rogue: 'Rogue'
};

const weaponByClass = {
  knight: 'sword',
  ranger: 'bow',
  mage: 'staff',
  rogue: 'dagger'
};

function OptionGroup({ label, options, value, onChange, colorSwatches = false }) {
  return (
    <div className="builder-group panel simple-builder-group">
      <h3>{label}</h3>
      <div className={`builder-options simple-builder-options ${colorSwatches ? 'simple-swatch-options' : ''}`}>
        {options.map((option) => {
          const selected = option.id === value;
          return (
            <button
              type="button"
              key={option.id}
              aria-pressed={selected}
              className={`builder-option simple-builder-option ${selected ? 'selected' : ''}`}
              onClick={() => onChange(option.id)}
            >
              {option.swatch && <span className="builder-swatch" style={{ background: option.swatch }} />}
              <span className="builder-option-copy">
                <strong>{option.label}</strong>
                {option.hint && <small>{option.hint}</small>}
              </span>
            </button>
          );
        })}
      </div>
    </div>
  );
}

function CharacterPreview({ profile }) {
  return (
    <div className="builder-preview panel simple-builder-preview">
      <div className="builder-preview-heading">
        <p className="eyebrow">Preview</p>
        <h2>{profile.name || 'Unnamed Hero'}</h2>
        <p>{profile.title}</p>
      </div>

      <div className="simple-builder-scene">
        <CharacterAvatar profile={profile} variant="creator" />
      </div>

      <div className="simple-preview-tags">
        <span className="stat-chip">{profile.title}</span>
        <span className="stat-chip">{profile.weapon}</span>
      </div>
      <p className="fine-print">Your weapon shapes your starter skills and opening stat bonus.</p>
    </div>
  );
}

export default function CharacterBuilderPage({ initialProfile, actions, loading }) {
  const [profile, setProfile] = useState(() => normalizeCharacterProfile(initialProfile));

  function updateField(field, value) {
    setProfile((current) => normalizeCharacterProfile({ ...current, [field]: value }));
  }

  function updateHeroClass(heroClass) {
    setProfile((current) => normalizeCharacterProfile({
      ...current,
      heroClass,
      title: titleByClass[heroClass],
      weapon: weaponByClass[heroClass]
    }));
  }

  async function beginRun() {
    await actions.startNewRunFromProfile(profile);
  }

  return (
    <main className="page character-builder-page" style={{ backgroundImage: `linear-gradient(180deg, rgba(8,8,18,.82), rgba(8,8,18,.94)), url(${staticAssets.backgrounds.characterCreator})` }}>
      <section className="page-header simple-character-header">
        <p className="eyebrow">Create hero</p>
        <h1>Create Your Hero</h1>
        <p>Shape your hero's look and starter combat style before entering the trials.</p>
      </section>

      <section className="character-builder-layout simple-character-builder-layout">
        <CharacterPreview profile={profile} />

        <div className="builder-controls simple-builder-controls">
          <div className="panel builder-group simple-builder-group identity-builder-group">
            <h3>Identity</h3>
            <label className="builder-label">
              Hero name
              <input
                className="builder-input"
                value={profile.name}
                maxLength={22}
                onChange={(event) => updateField('name', event.target.value)}
                placeholder="Enter hero name"
              />
            </label>
          </div>

          <OptionGroup label="Class" options={builderOptions.heroClasses} value={profile.heroClass} onChange={updateHeroClass} />
          <OptionGroup label="Weapon" options={builderOptions.weapons} value={profile.weapon} onChange={(value) => updateField('weapon', value)} />
          <OptionGroup label="Skin" options={builderOptions.skinTones} value={profile.skinTone} onChange={(value) => updateField('skinTone', value)} colorSwatches />
          <OptionGroup label="Hair" options={builderOptions.hairColors} value={profile.hairColor} onChange={(value) => updateField('hairColor', value)} colorSwatches />
          <OptionGroup label="Cloak" options={builderOptions.cloakColors} value={profile.cloakColor} onChange={(value) => updateField('cloakColor', value)} colorSwatches />
          <OptionGroup label="Eyes" options={builderOptions.eyeColors} value={profile.eyeColor} onChange={(value) => updateField('eyeColor', value)} colorSwatches />
        </div>
      </section>

      <div className="action-row sticky-actions">
        <button className="secondary-btn" onClick={() => actions.goTo('start')}>Back</button>
        <button className="primary-btn" disabled={loading || !profile.name.trim()} onClick={beginRun}>Enter the Trials</button>
      </div>
    </main>
  );
}
