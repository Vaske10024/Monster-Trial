# Skillbound Monster Trials

Skillbound Monster Trials is a small turn-based boss rush RPG that runs in the browser. You make a hero, choose a starter weapon, learn skills, buy upgrades, and try to beat a chain of five monsters.

It is not trying to be a huge open-world game or anything like that. The main idea is simple: prepare for each boss, read what the boss is about to do, and pick the right skill at the right time.

## What the game is about

You play as a custom hero entering a set of monster trials. Each trial is a one-on-one fight against a boss. Every boss has its own weakness, resistance, attack style, and rewards.

The current boss path is:

1. **Forest Goblin** - a fast starter boss that mainly teaches the basics.
2. **Venom Spider** - uses poison and damage over time.
3. **Stone Golem** - has more health and defensive tools.
4. **Frost Revenant** - uses frost, slow effects, and shields.
5. **Dragon King** - the final boss, with heavy fire damage and burn effects.

After beating bosses, you earn XP, coins, and sometimes new skills. You can then spend coins in the shop, change your loadout, and continue to the next monster.

## Main features

- Create a hero with a name, class, weapon, colors, and basic appearance options.
- Fight turn-based battles against five bosses.
- Use skills with different damage types like Slash, Pierce, Frost, Poison, and Fire.
- Check boss weaknesses and resistances before battle.
- Buy upgrades for damage, defense, health, energy, and other bonuses.
- Equip up to four learned skills before fighting.
- Build Momentum during combat and use it for stronger attacks.
- Replay beaten bosses to farm extra coins and XP.
- Keep going even after losing. A defeat does not delete the run.

## How to play

### 1. Start a new run

Click **Start**, then build your hero. You can pick a class, weapon, name, and colors.

The weapon matters a bit:

- **Sword** gives extra HP and starts with a balanced Slash skill.
- **Bow** gives extra Energy and starts with a Pierce skill.
- **Staff** gives some HP and Energy and starts with Frost access.
- **Dagger** gives a cheaper Slash combo style.

### 2. Preview the boss

Before every battle, the game shows the next boss. This is important because each boss has:

- a weakness,
- a resistance,
- main damage types,
- possible learnable skills,
- first win rewards.

Try to use skills that match the boss weakness. Also try to buy resistance upgrades against the type of damage the boss mostly uses.

### 3. Use the shop

The shop lets you spend coins on upgrades. Upgrades stay active for the rest of the run.

There are three upgrade groups:

- **Defense Upgrades** - reduce incoming damage from specific types or all types.
- **Damage Masteries** - improve your damage with certain damage types.
- **Utility Training** - improves HP, Energy, Guard, Focus, Momentum, and other useful things.

You do not need to buy everything. It is usually better to buy what helps against the next boss.

### 4. Equip skills

You can equip up to four learned skills. You must have at least one offensive skill equipped before starting battle.

A decent beginner loadout usually has:

- one or two damage skills,
- **Guard** for defense,
- **Focus** for energy recovery,
- one special skill that matches the boss weakness.

### 5. Battle the boss

During battle, you and the boss take turns. On your turn, choose one of your equipped skills.

Pay attention to these things:

- **HP** - if this reaches 0, you lose the fight.
- **Energy** - attacks cost Energy, so do not waste it all too early.
- **Momentum** - builds up as you attack, block, interrupt, or hit weaknesses.
- **Boss Intent** - shows what the boss is planning to do next.
- **Cooldowns** - some stronger skills need a few turns before they can be used again.

The boss intent is one of the most useful parts of the game. If the boss is about to use a heavy attack, you can Guard, interrupt it, or prepare instead of blindly attacking.

## Combat tips

- Use the boss weakness whenever possible. Weakness hits do extra damage.
- Do not spam resisted damage types unless you have no better option.
- Use **Guard** before big incoming attacks.
- Use **Focus** when your Energy is low or when you need to clear certain debuffs.
- If a boss intent says it is interruptible, skills like Web Trap or Slow effects can be very useful.
- When Momentum reaches 100, your next offensive skill becomes Empowered.
- Replaying a boss can help if the next boss feels too hard.
- Losing is not the end. You can go back, upgrade, change skills, and retry.

## Running the game locally

This project has two parts:

- `frontend/` - React + Vite browser app
- `backend/` - Java 17 + Spring Boot API

