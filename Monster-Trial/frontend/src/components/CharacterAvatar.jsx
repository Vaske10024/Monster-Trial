import { normalizeCharacterProfile } from '../data/characterOptions.js';

const titleToClass = {
  Knight: 'knight',
  Ranger: 'ranger',
  Mage: 'mage',
  Rogue: 'rogue'
};

function safeClass(value, fallback = 'hero') {
  return String(value || fallback).toLowerCase().replace(/[^a-z0-9]+/g, '-');
}

export default function CharacterAvatar({ profile, variant = 'creator', className = '', ariaLabel }) {
  const normalized = normalizeCharacterProfile(profile);
  const heroClass = normalized.heroClass || titleToClass[normalized.title] || 'knight';

  const style = {
    '--skin-tone': normalized.skinTone,
    '--hair-color': normalized.hairColor,
    '--cloak-color': normalized.cloakColor,
    '--eye-color': normalized.eyeColor
  };

  const classes = [
    'character-avatar',
    'simple-character-avatar',
    `avatar-${variant}`,
    `simple-gender-${safeClass(normalized.gender, 'female')}`,
    `simple-class-${safeClass(heroClass, 'knight')}`,
    `simple-hair-${safeClass(normalized.hairStyle, 'layered')}`,
    `simple-accessory-${safeClass(normalized.accessory, 'circlet')}`,
    `simple-weapon-${safeClass(normalized.weapon, 'sword')}`,
    className
  ].filter(Boolean).join(' ');

  return (
    <div className={classes} style={style} role="img" aria-label={ariaLabel || `${normalized.name || 'Hero'} character portrait`}>
      <div className="simple-avatar-card">
        <div className="simple-avatar-glow" />
        <div className="simple-avatar-weapon" />
        <div className="simple-avatar-shadow" />
        <div className="simple-avatar-cloak" />
        <div className="simple-avatar-body">
          <div className="simple-avatar-belt" />
          <div className="simple-avatar-emblem" />
        </div>
        <div className="simple-avatar-arm simple-avatar-arm-left" />
        <div className="simple-avatar-arm simple-avatar-arm-right" />
        <div className="simple-avatar-neck" />
        <div className="simple-avatar-back-hair" />
        <div className="simple-avatar-head">
          <div className="simple-avatar-hair" />
          <div className="simple-avatar-face-lock simple-avatar-face-lock-left" />
          <div className="simple-avatar-face-lock simple-avatar-face-lock-right" />
          <div className="simple-avatar-accessory" />
          <div className="simple-avatar-eye simple-avatar-eye-left" />
          <div className="simple-avatar-eye simple-avatar-eye-right" />
          <div className="simple-avatar-mouth" />
        </div>
      </div>
    </div>
  );
}
