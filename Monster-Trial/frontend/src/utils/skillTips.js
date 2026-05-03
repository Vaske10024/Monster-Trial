const interruptSkills = new Set(['WEB_TRAP', 'ICE_ARROW', 'DIRTY_TRICK']);

const tipsBySkillId = {
  IRON_SLASH: {
    role: 'Reliable damage tool.',
    timing: 'Use when you want steady damage with no setup, especially against Slash-weak bosses.',
    synergy: 'Great filler between setup turns and a safe way to spend momentum-ready turns if Slash matches weakness.',
    caution: 'Offers no utility, so do not mindlessly spam it into Slash resistance.'
  },
  PIERCING_STRIKE: {
    role: 'Efficient Pierce strike.',
    timing: 'Best when the boss is weak to Pierce or when you need consistent low-cost pressure.',
    synergy: 'Pairs well after setup skills because it is cheap and easy to fit into most turns.',
    caution: 'If the boss resists Pierce, use it as a filler only when better options are unavailable.'
  },
  GUARD: {
    role: 'Tempo defense skill.',
    timing: 'Use when the boss intent looks dangerous, when you expect a status effect, or when you need to recover energy safely.',
    synergy: 'Excellent before heavy attacks. It also buys time to reach an Empowered burst turn.',
    caution: 'Do not throw it out every turn; use it reactively when a scary boss move is coming.'
  },
  FOCUS: {
    role: 'Resource reset and cleanse.',
    timing: 'Best when your energy is low, when you are debuffed, or when you want momentum without taking a risk.',
    synergy: 'Strong after defending because it prepares a future burst turn while keeping your rotation alive.',
    caution: 'Using it at full energy is usually wasteful unless you specifically need the cleanse or momentum.'
  },
  QUICK_SLASH: {
    role: 'Cheap combo slash.',
    timing: 'Use it to keep pressure up without draining too much energy.',
    synergy: 'Feels best after a setup turn such as Guard or Focus, because it converts the tempo into damage.',
    caution: 'Its base damage is low, so it shines more in Slash matchups and combo turns than as a raw finisher.'
  },
  DIRTY_TRICK: {
    role: 'Setup and disruption.',
    timing: 'Use it before a big hit or when you want to weaken the enemy damage output next turn.',
    synergy: 'Sets up follow-up burst turns and can help soften dangerous boss intents.',
    caution: 'Low raw damage, so use it for the setup value rather than for immediate damage.'
  },
  POISON_BITE: {
    role: 'Damage over time setup.',
    timing: 'Use early in longer battles or before phase 2 so poison ticks can stack value over time.',
    synergy: 'Excellent against bosses with large HP pools and especially strong into Poison-weak targets.',
    caution: 'If the fight will end in a turn or two, direct damage may be better than reapplying poison.'
  },
  WEB_TRAP: {
    role: 'Control / interrupt skill.',
    timing: 'Best when the boss is charging an interruptible heavy move or when you need to fish for a stun.',
    synergy: 'Use it to stop burst turns from the boss and swing tempo back in your favor.',
    caution: 'Damage is low and control is chance-based, so avoid using it as generic filler.'
  },
  ICE_ARROW: {
    role: 'Control plus Frost damage.',
    timing: 'Very good into Frost weakness and especially good when the boss is preparing a heavy attack because Slow is more valuable there.',
    synergy: 'Lets you soften incoming damage while still dealing real damage, making it one of the best tactical attacks.',
    caution: 'Costs more energy than your starter attacks, so plan your energy before relying on it repeatedly.'
  },
  STONE_SKIN: {
    role: 'Extended defense.',
    timing: 'Use it before bosses that hit hard for multiple turns or when you expect a dangerous phase transition.',
    synergy: 'Pairs well with slow, attrition-heavy fights where staying alive matters more than bursting immediately.',
    caution: 'It is expensive, so do not cast it in easy turns where Guard would already be enough.'
  },
  ROCK_PIERCE: {
    role: 'Heavy Pierce burst.',
    timing: 'Best when you need a strong single hit or when the target is shielded.',
    synergy: 'Very strong after setup or on Empowered turns because its base hit is already high.',
    caution: 'Watch your energy; it can leave you dry if used back to back.'
  },
  FROZEN_ARMOR: {
    role: 'Counter-defense skill.',
    timing: 'Use before you expect to be hit so the shield and reflect both get value.',
    synergy: 'Strong against aggressive bosses and useful when you want a safer, attrition-based turn.',
    caution: 'If the boss is unlikely to attack soon, you may waste part of its value.'
  },
  INFERNO_BREATH: {
    role: 'Big fire nuke.',
    timing: 'Use as a burst finisher, on Empowered turns, or when Fire is currently favored in the matchup.',
    synergy: 'Great once you have tempo advantage and enough energy banked to afford a costly cast.',
    caution: 'Very expensive. Avoid firing it if it will leave you unable to react next turn.'
  },
  DRAGON_CLAW: {
    role: 'Heavy Slash finisher.',
    timing: 'Best as a closing hit or when the enemy is already softened by statuses or setup.',
    synergy: 'Excellent in burst windows because it turns stored momentum into immediate damage.',
    caution: 'It is not as flexible as your cheaper starter attacks, so make sure the hit matters.'
  }
};

export function getSkillTooltip(skill, context = {}) {
  if (!skill) return null;
  const base = tipsBySkillId[skill.id] || {
    role: skill.offensive ? 'Offensive skill.' : 'Support skill.',
    timing: 'Use it when its damage type or effect matches the current situation.',
    synergy: 'Try to combine it with the boss weakness, visible intent, and your available energy.',
    caution: 'Think about the current boss resistance and your cooldown rotation before committing.'
  };

  const notes = [];
  const { boss, battle } = context;

  if (boss?.weakness && boss.weakness === skill.type) {
    notes.push(`Great matchup: ${boss.name} is weak to ${skill.type}, so this is one of your best damage options right now.`);
  }
  if (boss?.resistance && boss.resistance === skill.type) {
    notes.push(`Caution: ${boss.name} resists ${skill.type}, so this will underperform unless the utility matters more than raw damage.`);
  }
  if (battle?.bossIntent?.interruptible && interruptSkills.has(skill.id)) {
    notes.push(`Boss intent is interruptible right now. ${skill.name} is a strong answer when you need to stop the incoming heavy action.`);
  }
  if (battle?.heroEnergy < skill.energyCost) {
    notes.push('You currently do not have enough energy to cast this skill.');
  } else if (skill.energyCost === 0) {
    notes.push('Zero energy cost makes this easy to fit into a recovery or setup turn.');
  }
  const cooldownLeft = battle?.heroCooldowns?.[skill.id] || 0;
  if (cooldownLeft > 0) {
    notes.push(`Currently on cooldown for ${cooldownLeft} more turn${cooldownLeft === 1 ? '' : 's'}.`);
  }
  if (battle?.heroMomentum >= 100 && skill.offensive) {
    notes.push('Your Momentum is full. Using this offensive skill now will consume the Empowered attack bonus.');
  }

  return {
    ...base,
    notes
  };
}
