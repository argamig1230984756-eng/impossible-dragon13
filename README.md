# ImpossibleDragon (Paper 1.21.11)

A brutal, multi-phase Ender Dragon fight. Needs Java 21.

## Build
```
mvn clean package
```
Jar ends up in `target/ImpossibleDragon-1.0.0.jar` -> drop into `plugins/`.

## Features
- 800 HP dragon (configurable), damage reduction + per-hit damage cap
- 3 phases (100-66%, 66-33%, <33% ENRAGED with faster cooldowns)
- Explosive dragon fireballs, wither skull barrages
- Telegraphed lightning strikes
- Endermen / vex / phantom minion waves (no loot)
- Tail strike (telegraphed sweep with knockback)
- Lightning breath (charge at the mouth, dodgeable beam)
- Animated purple magic circles + spinning shield around every crystal
- Roar (darkness/nausea/slow) and levitation-drop attack
- Pillar crystals respawn; alive crystals heal the dragon extra
- Dragon lifesteal + damage multiplier
- Bonus XP and item rewards on death

## Commands (`impossibledragon.admin`, alias `/idragon`)
- `/idragon status` - HP, phase, crystals, minions
- `/idragon reload` - reload config and re-apply stats
- `/idragon retune` - re-apply max-health to active dragons

Everything is tweakable in `config.yml`.

## Bedrock (Geyser)
Bedrock players get action-bar warnings + a sound before tail strikes, lightning breath and ground lightning, since Java particles may not render for them. See `effects.warning-text` (`bedrock` | `everyone` | `off`).

## Shield + crystal timers (HEAL_LOCK mode, default)
- The dragon can ALWAYS be hit. The crystals are its healing network: while any crystal lives, it heals.
- Every destroyed crystal has its own 60s respawn timer (floating text that turns yellow/red + a ring that
  fills up). An action-bar "shield race" shows how many crystals are left and when the FIRST one returns.
- Destroy the LAST crystal before the FIRST one respawns -> the shield breaks and the dragon cannot heal at
  all (no crystal healing, no lifesteal) for `shield.broken-seconds` (0 = until the next phase).
- Otherwise crystals just come back one by one. Phase changes and the rebirth bring every crystal back.
- `shield.mode: IMMUNITY` restores the old "immune until the shield breaks, then a short vulnerable window" fight.

## Lightning breath
- Damage goes through the normal dragon damage multiplier, but one hit can never take more than
  `max-health-fraction` (45%) of the player's max HP, so it no longer one-shots. Beam core = full damage,
  rim = `edge-damage-factor`. A red lane shows exactly where the beam will go during the wind-up.

## Rebirth (final form)
The first time the dragon would die, it refuses: it becomes invulnerable for a few seconds while
its health refills to 50%, every crystal returns and the shield comes back. It then fights in
PHASE 4 (FINAL FORM) with faster attacks and +25% damage. The second death is real.

## Shield look (resource pack)
Each living crystal gets a translucent violet sphere with lightning inside and a rune circle on the ground. These are
models in `ThreeHeadsDragon-ResourcePack.zip` (`assets/impossibledragon`). Players need that pack enabled, otherwise
they only see the particle shield. Bedrock/Floodgate players are skipped. Turn it off with `effects.crystal-bubble.enabled: false`.

