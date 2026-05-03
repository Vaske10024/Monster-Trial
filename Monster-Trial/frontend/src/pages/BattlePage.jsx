import { useEffect, useMemo, useState } from 'react';
import AnimatedSprite, { preloadSprite } from '../components/AnimatedSprite.jsx';
import CharacterAvatar from '../components/CharacterAvatar.jsx';
import BattleLog from '../components/BattleLog.jsx';
import DamageTypeBadge from '../components/DamageTypeBadge.jsx';
import EnergyBar from '../components/EnergyBar.jsx';
import HpBar from '../components/HpBar.jsx';
import SkillCard from '../components/SkillCard.jsx';
import PotionCard from '../components/PotionCard.jsx';
import StatusBadge from '../components/StatusBadge.jsx';
import { animationMap, heroAnimationForSkill } from '../assets/animationMap.js';
import { staticAssets } from '../assets/assetMap.js';

const sleep = (ms) => new Promise((resolve) => window.setTimeout(resolve, ms));
const BOSS_HIT_RECOVERY_DELAY_MS = 1000;
const BOSS_DAMAGE_FLOAT_MS = 2000;


function poseForHeroSprite(spriteSrc = '') {
  if (spriteSrc.includes('defeat')) return 'pose-defeat';
  if (spriteSrc.includes('victory')) return 'pose-victory';
  if (spriteSrc.includes('hit')) return 'pose-hit';
  if (spriteSrc.includes('guard')) return 'pose-guard';
  if (spriteSrc.includes('focus')) return 'pose-focus';
  if (spriteSrc.includes('attack')) return 'pose-attack';
  return 'pose-idle';
}

function MomentumBar({ current = 0, max = 100 }) {
  const percent = Math.max(0, Math.min(100, (current / Math.max(1, max)) * 100));
  return (
    <div className="bar-block">
      <div className="bar-label"><span>Momentum</span><span>{current} / {max}</span></div>
      <div className="meter momentum-meter"><div style={{ width: `${percent}%` }} /></div>
    </div>
  );
}

function BossIntentPanel({ intent, phaseTwo }) {
  if (!intent) return null;
  return (
    <div className={`boss-intent-card ${intent.interruptible ? 'danger' : ''}`}>
      <div>
        <span className="intent-label">{phaseTwo ? 'Phase 2 Intent' : 'Boss Intent'}</span>
        <strong>{intent.name}</strong>
      </div>
      <div className="intent-details">
        {intent.damage > 0 && <span>{intent.damage} {intent.damageType}</span>}
        {intent.effect && intent.effect !== 'none' && <span>Effect: {intent.effect}</span>}
        {intent.interruptible && <span>Interruptible</span>}
      </div>
      <p>{intent.description}</p>
    </div>
  );
}

export default function BattlePage({ run, actions, loading, characterProfile }) {
  const battle = run.battle;
  const boss = battle?.boss;
  const [heroAnim, setHeroAnim] = useState(animationMap.hero.idle);
  const [bossAnimKey, setBossAnimKey] = useState('idle');
  const [bossDamageFloats, setBossDamageFloats] = useState([]);
  const [busy, setBusy] = useState(false);
  const [animNonce, setAnimNonce] = useState(0);

  const bossAnimations = useMemo(() => animationMap.bosses[boss?.id] || {}, [boss?.id]);
  const background = staticAssets.backgrounds[boss?.id];
  const heroAnimations = animationMap.hero;

  useEffect(() => {
    const heroSources = Object.values(heroAnimations)
      .map((animation) => animation?.src)
      .filter(Boolean);
    const bossSources = Object.values(bossAnimations)
      .map((animation) => animation?.src)
      .filter(Boolean);
    heroSources.forEach(preloadSprite);
    bossSources.forEach(preloadSprite);
  }, [bossAnimations, heroAnimations]);

  useEffect(() => {
    if (!battle) return;
    if (battle.result === 'HERO_WIN') {
      setHeroAnim(animationMap.hero.victory);
      setBossAnimKey('defeat');
    } else if (battle.result === 'HERO_LOSE') {
      setHeroAnim(animationMap.hero.defeat);
      setBossAnimKey('idle');
    } else {
      setHeroAnim(animationMap.hero.idle);
      setBossAnimKey('idle');
    }
  }, [battle?.result]);

  function triggerBossHitFeedback() {
    setBossAnimKey('hit');
  }

  function showBossDamageFloat(damage) {
    if (!damage || damage <= 0) return;
    const id = `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
    setBossDamageFloats((current) => [...current, { id, damage }]);
    window.setTimeout(() => {
      setBossDamageFloats((current) => current.filter((entry) => entry.id !== id));
    }, BOSS_DAMAGE_FLOAT_MS);
  }

  if (!battle || !boss) {
    return (
      <main className="page">
        <section className="panel empty-state">
          <h1>No active battle</h1>
          <button className="primary-btn" onClick={() => actions.goTo('preview')}>Return to Preview</button>
        </section>
      </main>
    );
  }

  async function handleSkill(skill) {
    if (busy || loading) return;
    setBusy(true);
    setAnimNonce((value) => value + 1);
    setHeroAnim(heroAnimationForSkill(skill));
    await sleep(180);
    triggerBossHitFeedback();
    try {
      const previousBossHp = battle.bossHp;
      const updated = await actions.useSkill(skill.id);
      if (!updated?.battle) {
        setHeroAnim(animationMap.hero.idle);
        setBossAnimKey('idle');
        return;
      }
      const bossDamageTaken = Math.max(0, previousBossHp - updated.battle.bossHp);
      showBossDamageFloat(bossDamageTaken);
      const result = updated.battle?.result;
      if (result === 'ONGOING') {
        await sleep(350);
        await sleep(BOSS_HIT_RECOVERY_DELAY_MS);
        setBossAnimKey('attack');
        await sleep(300);
        setHeroAnim(animationMap.hero.hit);
        await sleep(450);
        setHeroAnim(animationMap.hero.idle);
        setBossAnimKey('idle');
      } else if (result === 'HERO_WIN') {
        setHeroAnim(animationMap.hero.victory);
        setBossAnimKey('defeat');
        await sleep(700);
        actions.routeAfterBattle(updated);
      } else if (result === 'HERO_LOSE') {
        setHeroAnim(animationMap.hero.defeat);
        setBossAnimKey('idle');
        await sleep(700);
        actions.routeAfterBattle(updated);
      }
    } catch (error) {
      setHeroAnim(animationMap.hero.idle);
      setBossAnimKey('idle');
    } finally {
      setBusy(false);
    }
  }


  async function handlePotion(potion) {
    if (busy || loading) return;
    setBusy(true);
    setAnimNonce((value) => value + 1);
    setHeroAnim(potion.id === 'RESISTANCE_POTION' ? animationMap.hero.guard : animationMap.hero.focus);
    setBossAnimKey('idle');
    try {
      const updated = await actions.usePotion(potion.id);
      if (!updated?.battle) {
        setHeroAnim(animationMap.hero.idle);
        setBossAnimKey('idle');
        return;
      }
      const result = updated.battle?.result;
      if (result === 'ONGOING' && !potion.consumesAction) {
        await sleep(350);
        setHeroAnim(animationMap.hero.idle);
        setBossAnimKey('idle');
      } else if (result === 'ONGOING') {
        await sleep(350);
        await sleep(BOSS_HIT_RECOVERY_DELAY_MS);
        setBossAnimKey('attack');
        await sleep(300);
        setHeroAnim(animationMap.hero.hit);
        await sleep(450);
        setHeroAnim(animationMap.hero.idle);
        setBossAnimKey('idle');
      } else if (result === 'HERO_WIN') {
        setHeroAnim(animationMap.hero.victory);
        setBossAnimKey('defeat');
        await sleep(700);
        actions.routeAfterBattle(updated);
      } else if (result === 'HERO_LOSE') {
        setHeroAnim(animationMap.hero.defeat);
        setBossAnimKey('idle');
        await sleep(700);
        actions.routeAfterBattle(updated);
      }
    } catch (error) {
      setHeroAnim(animationMap.hero.idle);
      setBossAnimKey('idle');
    } finally {
      setBusy(false);
    }
  }

  const effectivenessText = battle.lastEffectiveness === 'SUPER_EFFECTIVE'
    ? 'Super effective!'
    : battle.lastEffectiveness === 'NOT_VERY_EFFECTIVE'
      ? 'Not very effective...'
      : battle.heroMomentum >= 100
        ? 'Empowered attack ready!'
        : 'Read the intent, then choose.';

  return (
    <main className="page battle-page" style={{ backgroundImage: `linear-gradient(180deg, rgba(6,7,12,.80), rgba(6,7,12,.96)), url(${background})` }}>
      <section className="battle-top panel">
        <div>
          <p className="eyebrow">Turn {battle.turnNumber}</p>
          <h1>{boss.name}</h1>
        </div>
        <div className="effectiveness-callout">
          <span>{effectivenessText}</span>
          <small>Weak: {boss.weakness} | Resists: {boss.resistance}</small>
        </div>
      </section>

      <section className="battle-arena panel">
        <div className="combatant hero-combatant">
          <CharacterAvatar key={`hero-avatar-${heroAnim.src}-${animNonce}`} profile={characterProfile} variant="battle" className={`hero-facing ${poseForHeroSprite(heroAnim.src)}`} ariaLabel={`${characterProfile?.name || 'Hero'} battle avatar`} />
          <h2>{characterProfile?.name || 'Hero'}</h2>
          <HpBar current={battle.heroHp} max={battle.heroMaxHp} label="HP" />
          <EnergyBar current={battle.heroEnergy} max={battle.heroMaxEnergy} />
          <MomentumBar current={battle.heroMomentum} max={battle.heroMaxMomentum} />
          <div className="status-row">
            {battle.heroStatusEffects.map((status) => <StatusBadge key={`${status.id}-${status.name}`} status={status} />)}
          </div>
        </div>

        <div className="battle-center-column">
          <div className="versus-mark">VS</div>
          <BossIntentPanel intent={battle.bossIntent} phaseTwo={battle.phaseTwo} />
        </div>

        <div className="combatant boss-combatant">
          <div className="boss-sprite-shell">
            <div className="boss-damage-float-layer" aria-hidden="true">
              {bossDamageFloats.map((entry) => (
                <span key={entry.id} className="boss-damage-float">-{entry.damage}</span>
              ))}
            </div>
            <AnimatedSprite
              key={`boss-${bossAnimKey}-${animNonce}`}
              {...(bossAnimations[bossAnimKey] || bossAnimations.idle)}
              fallbackLabel={boss.name}
              scale={
                boss.id === 'VENOM_SPIDER'
                  ? 1.23
                  : boss.id === 'STONE_GOLEM' || boss.id === 'DRAGON_KING'
                    ? 0.62
                    : 0.82
              }
            />
          </div>
          <h2>{boss.name}</h2>
          <HpBar current={battle.bossHp} max={battle.bossMaxHp} label="HP" />
          <div className="weak-res-inline">
            <DamageTypeBadge type={boss.weakness} />
            <DamageTypeBadge type={boss.resistance} />
            {battle.phaseTwo && <span className="phase-pill">Phase 2</span>}
          </div>
          <div className="status-row">
            {battle.bossStatusEffects.map((status) => <StatusBadge key={`${status.id}-${status.name}`} status={status} />)}
          </div>
        </div>
      </section>

      <section className="battle-bottom">
        <div className="command-stack">
          <div className="panel skill-command-panel">
            <h2>Skills</h2>
          <p className="fine-print">Use Guard and Focus to control tempo, interrupt heavy intents, build Momentum, and set up an Empowered attack.</p>
          <div className="skill-grid command-grid">
            {run.hero.equippedSkills.map((skill) => {
              const cooldown = battle.heroCooldowns[skill.id] || 0;
              const notEnoughEnergy = battle.heroEnergy < skill.energyCost;
              return (
                <SkillCard
                  key={skill.id}
                  skill={skill}
                  cooldown={cooldown}
                  compact
                  actionLabel={notEnoughEnergy ? 'No Energy' : 'Use'}
                  disabled={busy || loading || notEnoughEnergy || battle.result !== 'ONGOING'}
                  onClick={() => handleSkill(skill)}
                  tooltipContext={{ boss, battle }}
                />
              );
            })}
          </div>
          </div>
          <div className="panel skill-command-panel potion-command-panel">
            <h2>Potions</h2>
          <p className="fine-print">Healing and cleansing use your action. Resistance is quick-use, so you can still attack after drinking it. Each potion type is once per battle.</p>
          <div className="upgrade-grid compact-grid">
            {(run.potions || []).map((potion) => (
              <PotionCard
                key={potion.id}
                potion={potion}
                mode="battle"
                disabled={busy || loading || battle.result !== 'ONGOING'}
                onUse={() => handlePotion(potion)}
              />
            ))}
          </div>
          </div>
        </div>
        <BattleLog entries={battle.battleLog} />
      </section>
    </main>
  );
}
