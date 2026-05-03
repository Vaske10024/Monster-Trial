import DamageTypeBadge from './DamageTypeBadge.jsx';
import Sprite from './Sprite.jsx';
import { skillIcons } from '../assets/assetMap.js';
import { getSkillTooltip } from '../utils/skillTips.js';

export default function SkillCard({
  skill,
  equipped = false,
  selected = false,
  cooldown = 0,
  disabled = false,
  actionLabel,
  onClick,
  compact = false,
  tooltipContext
}) {
  if (!skill) return null;
  const isBlocked = disabled || cooldown > 0;
  const tooltip = getSkillTooltip(skill, tooltipContext);

  return (
    <article className={`skill-card ${equipped ? 'equipped' : ''} ${selected ? 'selected' : ''} ${compact ? 'compact' : ''}`}>
      <div className="skill-card-top">
        <Sprite src={skillIcons[skill.id]} alt={`${skill.name} icon`} fallbackLabel={skill.name} className="icon-sprite" size={56} />
        <div>
          <h3>{skill.name}</h3>
          <DamageTypeBadge type={skill.type} />
        </div>
        {tooltip && (
          <div className="skill-tooltip-anchor" aria-label={`Skill help for ${skill.name}`}>
            <span className="skill-help-chip">?</span>
            <div className="skill-tooltip" role="tooltip">
              <h4>{skill.name}</h4>
              <p><strong>Role:</strong> {tooltip.role}</p>
              <p><strong>When to use:</strong> {tooltip.timing}</p>
              <p><strong>Why it works:</strong> {tooltip.synergy}</p>
              <p><strong>Watch out:</strong> {tooltip.caution}</p>
              {tooltip.notes?.length > 0 && (
                <div className="skill-tooltip-notes">
                  <strong>Right now</strong>
                  <ul>
                    {tooltip.notes.map((note) => <li key={note}>{note}</li>)}
                  </ul>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
      <div className="skill-stats">
        <span>DMG <strong>{skill.damage}</strong></span>
        <span>ENG <strong>{skill.energyCost}</strong></span>
        <span>CD <strong>{skill.cooldown}</strong></span>
      </div>
      {!compact && <p>{skill.description}</p>}
      {!compact && <p className="effect-line">Effect: <strong>{skill.effect}</strong></p>}
      {cooldown > 0 && <div className="cooldown-pill">Cooldown: {cooldown}</div>}
      {actionLabel && (
        <button className="secondary-btn full" disabled={isBlocked} onClick={onClick}>
          {cooldown > 0 ? `Cooldown ${cooldown}` : actionLabel}
        </button>
      )}
    </article>
  );
}
