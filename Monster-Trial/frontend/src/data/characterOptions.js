export const builderOptions = {
  genders: [
    { id: 'female', label: 'Female', hint: 'Softer silhouette and longer hair.' },
    { id: 'male', label: 'Male', hint: 'Broader silhouette and shorter hair.' }
  ],
  heroClasses: [
    { id: 'knight', label: 'Knight', hint: 'Simple armored hero.' },
    { id: 'ranger', label: 'Ranger', hint: 'Light cloak and bow.' },
    { id: 'mage', label: 'Mage', hint: 'Arcane robe and staff.' },
    { id: 'rogue', label: 'Rogue', hint: 'Dark cloak and daggers.' }
  ],
  weapons: [
    { id: 'sword', label: 'Sword', hint: '+5 HP, balanced Slash starter.' },
    { id: 'bow', label: 'Bow', hint: '+5 Energy, steady Pierce starter.' },
    { id: 'staff', label: 'Staff', hint: '+3 HP/Energy, safe Frost starter.' },
    { id: 'dagger', label: 'Dagger', hint: 'Low-cost Slash combo starter.' }
  ],
  skinTones: [
    { id: '#f3c9aa', label: 'Fair', swatch: '#f3c9aa' },
    { id: '#d3a780', label: 'Warm', swatch: '#d3a780' },
    { id: '#b77a58', label: 'Bronze', swatch: '#b77a58' },
    { id: '#7b4f3c', label: 'Deep', swatch: '#7b4f3c' }
  ],
  hairColors: [
    { id: '#e6d1a2', label: 'Blonde', swatch: '#e6d1a2' },
    { id: '#b66d3f', label: 'Auburn', swatch: '#b66d3f' },
    { id: '#453531', label: 'Dark', swatch: '#453531' },
    { id: '#b9c1d4', label: 'Silver', swatch: '#b9c1d4' }
  ],
  hairStyles: [
    { id: 'layered', label: 'Layered', hint: 'Soft volume around the face.' },
    { id: 'ponytail', label: 'Ponytail', hint: 'Pulled back with a long tail.' },
    { id: 'braided', label: 'Braided', hint: 'Structured side braid.' },
    { id: 'cropped', label: 'Cropped', hint: 'Short and practical.' }
  ],
  cloakColors: [
    { id: '#8b2f3b', label: 'Crimson', swatch: '#8b2f3b' },
    { id: '#2e6b59', label: 'Green', swatch: '#2e6b59' },
    { id: '#385e89', label: 'Blue', swatch: '#385e89' },
    { id: '#4a4658', label: 'Dark', swatch: '#4a4658' }
  ],
  eyeColors: [
    { id: '#8fdcff', label: 'Blue', swatch: '#8fdcff' },
    { id: '#7bf3b0', label: 'Green', swatch: '#7bf3b0' },
    { id: '#ffd28d', label: 'Amber', swatch: '#ffd28d' },
    { id: '#cbb2ff', label: 'Violet', swatch: '#cbb2ff' }
  ],
  accessories: [
    { id: 'circlet', label: 'Circlet', hint: 'Polished headpiece.' },
    { id: 'ribbon', label: 'Ribbon', hint: 'Tied hair accent.' },
    { id: 'earring', label: 'Earring', hint: 'Small gold drop earring.' },
    { id: 'none', label: 'None', hint: 'No accessory.' }
  ]
};

export const defaultCharacterProfile = {
  name: 'Eira',
  gender: 'female',
  title: 'Knight',
  heroClass: 'knight',
  weapon: 'sword',
  hairColor: builderOptions.hairColors[0].id,
  hairStyle: 'layered',
  skinTone: builderOptions.skinTones[1].id,
  cloakColor: builderOptions.cloakColors[0].id,
  eyeColor: builderOptions.eyeColors[0].id,
  accessory: 'circlet'
};

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

function optionExists(group, value) {
  return builderOptions[group]?.some((option) => option.id === value);
}

function optionOrDefault(group, value) {
  return optionExists(group, value) ? value : builderOptions[group]?.[0]?.id;
}

function sanitizeName(value) {
  const normalized = String(value || defaultCharacterProfile.name)
    .replace(/[\u0000-\u001f\u007f]/g, '')
    .replace(/\s+/g, ' ')
    .trim()
    .slice(0, 22);
  return normalized || defaultCharacterProfile.name;
}

export function normalizeCharacterProfile(profile = {}) {
  const merged = { ...defaultCharacterProfile, ...profile };
  const gender = optionExists('genders', merged.gender) ? merged.gender : defaultCharacterProfile.gender;
  const heroClass = optionExists('heroClasses', merged.heroClass) ? merged.heroClass : defaultCharacterProfile.heroClass;
  const weapon = optionExists('weapons', merged.weapon) ? merged.weapon : weaponByClass[heroClass];
  const requestedHairStyle = optionOrDefault('hairStyles', merged.hairStyle) || defaultCharacterProfile.hairStyle;
  const hairStyle = gender === 'male' && requestedHairStyle === 'layered'
    ? 'cropped'
    : requestedHairStyle;

  return {
    ...merged,
    name: sanitizeName(merged.name),
    gender,
    heroClass,
    weapon,
    title: titleByClass[heroClass] || defaultCharacterProfile.title,
    skinTone: optionOrDefault('skinTones', merged.skinTone) || defaultCharacterProfile.skinTone,
    hairColor: optionOrDefault('hairColors', merged.hairColor) || defaultCharacterProfile.hairColor,
    hairStyle,
    cloakColor: optionOrDefault('cloakColors', merged.cloakColor) || defaultCharacterProfile.cloakColor,
    eyeColor: optionOrDefault('eyeColors', merged.eyeColor) || defaultCharacterProfile.eyeColor,
    accessory: optionOrDefault('accessories', merged.accessory) || defaultCharacterProfile.accessory
  };
}
