const sprite = (src, frameWidth = 256, frameHeight = 256, frameCount = 6, fps = 8, loop = true) => ({
  src,
  frameWidth,
  frameHeight,
  frameCount,
  fps,
  loop
});

export const animationMap = {
  hero: {
    idle: sprite('/assets/spritesheets/hero_idle.png', 256, 256, 6, 8, true),
    slashAttack: sprite('/assets/spritesheets/hero_attack_slash.png', 256, 256, 6, 12, false),
    pierceAttack: sprite('/assets/spritesheets/hero_attack_pierce.png', 256, 256, 6, 12, false),
    frostAttack: sprite('/assets/spritesheets/hero_attack_frost.png', 256, 256, 6, 12, false),
    poisonAttack: sprite('/assets/spritesheets/hero_attack_poison.png', 256, 256, 6, 12, false),
    fireAttack: sprite('/assets/spritesheets/hero_attack_fire.png', 256, 256, 6, 12, false),
    guard: sprite('/assets/spritesheets/hero_guard.png', 256, 256, 5, 10, false),
    focus: sprite('/assets/spritesheets/hero_focus.png', 256, 256, 5, 10, false),
    hit: sprite('/assets/spritesheets/hero_hit.png', 256, 256, 4, 10, false),
    defeat: sprite('/assets/spritesheets/hero_defeat.png', 256, 256, 6, 8, false),
    victory: sprite('/assets/spritesheets/hero_victory.png', 256, 256, 6, 8, true)
  },
  bosses: {
    FOREST_GOBLIN: {
      idle: sprite('/assets/spritesheets/forest_goblin_idle.png', 512, 512, 6, 8, true),
      attack: sprite('/assets/spritesheets/forest_goblin_attack.png', 512, 512, 6, 6, false),
      hit: sprite('/assets/spritesheets/forest_goblin_hit.png', 512, 512, 6, 4, false),
      defeat: sprite('/assets/spritesheets/forest_goblin_defeat.png', 512, 512, 6, 4, false)
    },
    VENOM_SPIDER: {
      idle: sprite('/assets/spritesheets/venom_spider_idle.png', 256, 256, 6, 8, true),
      attack: sprite('/assets/spritesheets/venom_spider_attack.png', 256, 256, 6, 6, false),
      poisonBite: sprite('/assets/spritesheets/venom_spider_poison_bite.png', 256, 256, 6, 6, false),
      webTrap: sprite('/assets/spritesheets/venom_spider_web_trap.png', 256, 256, 6, 6, false),
      hit: sprite('/assets/spritesheets/venom_spider_hit.png', 256, 256, 6, 6, false),
      defeat: sprite('/assets/spritesheets/venom_spider_defeat.png', 256, 256, 6, 6, false)
    },
    STONE_GOLEM: {
      idle: sprite('/assets/spritesheets/stone_golem_idle.png', 384, 384, 6, 7, true),
      attack: sprite('/assets/spritesheets/stone_golem_attack.png', 384, 384, 7, 10, false),
      stoneSkin: sprite('/assets/spritesheets/stone_golem_stone_skin.png', 384, 384, 6, 10, false),
      hit: sprite('/assets/spritesheets/stone_golem_hit.png', 384, 384, 4, 8, false),
      defeat: sprite('/assets/spritesheets/stone_golem_defeat.png', 384, 384, 7, 8, false)
    },
    FROST_REVENANT: {
      idle: sprite('/assets/spritesheets/frost_revenant_idle.png', 256, 256, 6, 8, true),
      attack: sprite('/assets/spritesheets/frost_revenant_attack.png', 256, 256, 6, 12, false),
      castFrost: sprite('/assets/spritesheets/frost_revenant_cast_frost.png', 256, 256, 6, 12, false),
      frozenArmor: sprite('/assets/spritesheets/frost_revenant_frozen_armor.png', 256, 256, 6, 10, false),
      hit: sprite('/assets/spritesheets/frost_revenant_hit.png', 256, 256, 4, 10, false),
      defeat: sprite('/assets/spritesheets/frost_revenant_defeat.png', 256, 256, 6, 8, false)
    },
    DRAGON_KING: {
      idle: sprite('/assets/spritesheets/dragon_king_idle.png', 384, 384, 6, 7, true),
      attack: sprite('/assets/spritesheets/dragon_king_claw_attack.png', 384, 384, 7, 10, false),
      infernoBreath: sprite('/assets/spritesheets/dragon_king_inferno_breath.png', 384, 384, 8, 12, false),
      hit: sprite('/assets/spritesheets/dragon_king_hit.png', 384, 384, 4, 8, false),
      defeat: sprite('/assets/spritesheets/dragon_king_defeat.png', 384, 384, 8, 8, false)
    }
  },
  effects: {
    slash: sprite('/assets/effects/effect_slash.png', 128, 128, 5, 14, false),
    pierce: sprite('/assets/effects/effect_pierce.png', 128, 128, 5, 14, false),
    frostArrow: sprite('/assets/effects/effect_frost_arrow.png', 128, 128, 6, 14, false),
    poison: sprite('/assets/effects/effect_poison.png', 128, 128, 6, 10, false),
    fireBurst: sprite('/assets/effects/effect_fire_burst.png', 128, 128, 6, 14, false),
    burn: sprite('/assets/effects/effect_burn.png', 128, 128, 6, 10, true),
    shield: sprite('/assets/effects/effect_shield.png', 128, 128, 6, 10, true),
    stun: sprite('/assets/effects/effect_stun.png', 128, 128, 6, 8, true),
    levelUp: sprite('/assets/effects/effect_level_up.png', 128, 128, 8, 12, false),
    coinReward: sprite('/assets/effects/effect_coin_reward.png', 128, 128, 8, 12, false)
  }
};

export function heroAnimationForSkill(skill) {
  if (!skill) return animationMap.hero.idle;
  if (skill.effect === 'shield' || skill.effect === 'shield_2_turns' || skill.effect === 'shield_reflect') return animationMap.hero.guard;
  if (skill.effect === 'restore_energy') return animationMap.hero.focus;
  switch (skill.type) {
    case 'Slash': return animationMap.hero.slashAttack;
    case 'Pierce': return animationMap.hero.pierceAttack;
    case 'Frost': return animationMap.hero.frostAttack;
    case 'Poison': return animationMap.hero.poisonAttack;
    case 'Fire': return animationMap.hero.fireAttack;
    default: return animationMap.hero.idle;
  }
}
