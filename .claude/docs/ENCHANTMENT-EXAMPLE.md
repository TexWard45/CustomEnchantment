# Complex Enchantment Example: Tempest Wrath

This document demonstrates a complex enchantment showcasing most features of the CustomEnchantment system.

## Enchantment Overview

**Tempest Wrath** - A supreme sword enchantment with multiple mechanics:

1. **Passive Bonus**: Movement speed and attack damage when held
2. **Combo System**: Build stacks on attack (max 5 stacks)
3. **Tempest Strike**: At 5 stacks, deal massive AoE damage + lightning
4. **Storm Shield**: When hit at low HP, gain damage reduction and counter-attack
5. **Rage Mode**: Below 30% HP, all effects are enhanced

---

## Full Configuration

```yaml
# ============================================================================
# TEMPEST WRATH - Supreme Sword Enchantment
# A complex enchantment demonstrating advanced CE mechanics
# ============================================================================
tempestwrath:
  group: "supreme"
  display: "TempestWrath"
  description:
    - "&6Harness the power of the storm."
    - "&6Build combo stacks to unleash devastation."
  detail-description:
    - "&6Harness the power of the storm."
    - "&6Build combo stacks to unleash devastation."
    - ""
    - "&e&lPassive Bonuses (when held):"
    - "&7- Movement Speed: +5%/8%/12%/15%/20%"
    - "&7- Attack Damage: +3%/5%/8%/10%/15%"
    - ""
    - "&e&lCombo System:"
    - "&7- Each attack builds 1 stack (max 5)"
    - "&7- At 5 stacks: Tempest Strike activated"
    - "&7- Tempest Strike: +50% ATK + Lightning + AoE"
    - "&7- Stacks reset after 5 seconds of no attacks"
    - ""
    - "&e&lStorm Shield (below 50% HP):"
    - "&7- 30% chance to block 25% damage"
    - "&7- Counter-attack deals 5 true damage"
    - "&7- Cooldown: 3s"
    - ""
    - "&e&lRage Mode (below 30% HP):"
    - "&7- All damage bonuses doubled"
    - "&7- Storm Shield always activates"
    - "&7- Gain Resistance I for 2s on hit"
  applies-description:
    - "Sword"
  applies:
    - "ALL_SWORD"
  max-level: 5
  enchant-point: 3
  valuable: 5000
  levels:
    # ========================================================================
    # LEVEL 1
    # ========================================================================
    "1":
      # ----------------------------------------------------------------------
      # PASSIVE: Activate when holding the sword
      # ----------------------------------------------------------------------
      passive_hold:
        type: HOLD
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
        effects:
          # Add passive attributes
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.05:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.03:1"
          # Initialize combo storage
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "NUMBER_STORAGE:SET:tempest_last_attack:0"
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "MESSAGE:&6&l*** Tempest Wrath: &a&lActivated &6&l***"

      # ----------------------------------------------------------------------
      # PASSIVE: Deactivate when switching away
      # ----------------------------------------------------------------------
      passive_unhold:
        type: CHANGE_HAND
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
        effects:
          # Remove passive attributes
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          # Cancel any pending tasks
          - "REMOVE_TASK:tempest_combo_reset"
          - "REMOVE_TASK:tempest_rage_end"
          # Reset storage
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "MESSAGE:&6&l*** Tempest Wrath: &7&lDeactivated &6&l***"

      # ----------------------------------------------------------------------
      # COMBO: Check for combo timeout (reset after 5 seconds)
      # This runs on every attack to check if combo should reset
      # ----------------------------------------------------------------------
      combo_timeout_check:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>:0"
          - "NUMBER_STORAGE:%time%-tempest_last_attack:>:5000"
        effects:
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "MESSAGE:&6&l*** Combo Reset (timeout) ***"

      # ----------------------------------------------------------------------
      # COMBO: Build stacks on attack (when under 5 stacks)
      # ----------------------------------------------------------------------
      combo_build:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:<:5"
        effects:
          # Increment combo counter
          - "NUMBER_STORAGE:ADD:tempest_combo:1"
          # Update last attack time
          - "NUMBER_STORAGE:SET:tempest_last_attack:%time%"
          # Cancel previous reset task
          - "REMOVE_TASK:tempest_combo_reset"
          # Schedule new reset task (5 seconds = 100 ticks)
          - "NAME=tempest_combo_reset,DELAY=100 | NUMBER_STORAGE:SET:tempest_combo:0"
          - "NAME=tempest_combo_reset_msg,DELAY=100 | MESSAGE:&6&l*** Combo Reset ***"
          # Visual feedback
          - "PLAY_SOUND:ENTITY_EXPERIENCE_ORB_PICKUP:1.0:1.2"

      # ----------------------------------------------------------------------
      # COMBO: Display stack count message
      # Separate function to show stack count after incrementing
      # ----------------------------------------------------------------------
      combo_display_1:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:=:1"
        effects:
          - "MESSAGE:&e&l[Tempest] &7Stack: &e1&7/5"

      combo_display_2:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:=:2"
        effects:
          - "MESSAGE:&e&l[Tempest] &7Stack: &e2&7/5"

      combo_display_3:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:=:3"
        effects:
          - "MESSAGE:&e&l[Tempest] &7Stack: &e3&7/5"

      combo_display_4:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:=:4"
        effects:
          - "MESSAGE:&e&l[Tempest] &7Stack: &6&l4&7/5 &c(Almost ready!)"

      # ----------------------------------------------------------------------
      # TEMPEST STRIKE: Unleash at 5 stacks (Normal Mode)
      # Only triggers when NOT in rage mode
      # ----------------------------------------------------------------------
      tempest_strike_normal:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>=:5"
          - "NUMBER_STORAGE:tempest_rage:=:0"
        options:
          # +50% total damage
          - "ATTACK:0.5:2"
        effects:
          # Reset combo
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "REMOVE_TASK:tempest_combo_reset"
          - "REMOVE_TASK:tempest_combo_reset_msg"
          # Lightning strike on enemy
          - "TARGET=ENEMY | LIGHTNING:~_xP_yP_zP"
          # Slow enemy
          - "TARGET=ENEMY | ADD_POTION:SLOW:1:40"
          # Visual and audio feedback
          - "PLAY_SOUND:ENTITY_LIGHTNING_BOLT_THUNDER:1.0:1.0"
          - "MESSAGE:&6&l*** TEMPEST STRIKE! &e+50% ATK &6***"
        # Find nearby enemies for AoE damage
        target-filter:
          target: ENEMY
          except-player: true
          except-enemy: true
          min-distance: 0
          max-distance: 4
        target-conditions:
          - "NEGATIVE=true | FACTION_RELATION:MEMBER"
          - "NEGATIVE=true | FACTION_RELATION:ALLY"
        target-effects:
          - "TARGET=ENEMY | DEAL_DAMAGE:5"
          - "TARGET=ENEMY | ADD_POTION:SLOW:0:20"
        true-condition-break: true

      # ----------------------------------------------------------------------
      # TEMPEST STRIKE: Unleash at 5 stacks (Rage Mode - Enhanced)
      # Triggers when IN rage mode (below 30% HP)
      # ----------------------------------------------------------------------
      tempest_strike_rage:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>=:5"
          - "NUMBER_STORAGE:tempest_rage:=:1"
        options:
          # +100% total damage in rage mode (doubled)
          - "ATTACK:1.0:2"
        effects:
          # Reset combo
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "REMOVE_TASK:tempest_combo_reset"
          - "REMOVE_TASK:tempest_combo_reset_msg"
          # Double lightning strike
          - "TARGET=ENEMY | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY,DELAY=5 | LIGHTNING:~_xP_yP_zP"
          # Stronger slow
          - "TARGET=ENEMY | ADD_POTION:SLOW:2:60"
          # Visual and audio feedback
          - "PLAY_SOUND:ENTITY_LIGHTNING_BOLT_THUNDER:1.0:0.8"
          - "MESSAGE:&c&l*** RAGING TEMPEST STRIKE! &4+100% ATK &c***"
        # Enhanced AoE in rage mode
        target-filter:
          target: ENEMY
          except-player: true
          except-enemy: true
          min-distance: 0
          max-distance: 6
        target-conditions:
          - "NEGATIVE=true | FACTION_RELATION:MEMBER"
          - "NEGATIVE=true | FACTION_RELATION:ALLY"
        target-effects:
          - "TARGET=ENEMY | DEAL_DAMAGE:10"
          - "TARGET=ENEMY | ADD_POTION:SLOW:1:40"
        true-condition-break: true

      # ----------------------------------------------------------------------
      # RAGE MODE: Activate when HP drops below 30%
      # ----------------------------------------------------------------------
      rage_activate:
        type: DEFENSE
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:<=:30"
          - "NUMBER_STORAGE:tempest_rage:=:0"
        effects:
          - "NUMBER_STORAGE:SET:tempest_rage:1"
          # Boost attributes in rage mode
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.10:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.06:1"
          # Visual indicator
          - "ADD_POTION:GLOWING:0:100"
          - "PLAY_SOUND:ENTITY_WITHER_SPAWN:0.5:1.5"
          - "MESSAGE:&c&l*** RAGE MODE ACTIVATED! ***"

      # ----------------------------------------------------------------------
      # RAGE MODE: Deactivate when HP goes above 50%
      # ----------------------------------------------------------------------
      rage_deactivate:
        type: DEFENSE
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:>:50"
          - "NUMBER_STORAGE:tempest_rage:=:1"
        effects:
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          # Reset to normal attributes
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.05:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.03:1"
          - "MESSAGE:&6&l*** Rage Mode Ended ***"

      # ----------------------------------------------------------------------
      # STORM SHIELD: Defensive proc (Normal Mode - 30% chance)
      # Triggers when below 50% HP but not in rage
      # ----------------------------------------------------------------------
      storm_shield_normal:
        type: DEFENSE
        chance: 30
        cooldown: 3000
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:<=:50"
          - "HEALTH_PERCENT:>:30"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
        options:
          # Block 25% of incoming damage
          - "DEFENSE:0.25:2"
        effects:
          # Counter-attack with true damage
          - "TARGET=ENEMY | TRUE_DAMAGE:5"
          # Brief resistance
          - "ADD_POTION:DAMAGE_RESISTANCE:0:30"
          # Visual feedback
          - "PLAY_SOUND:ITEM_SHIELD_BLOCK:1.0:1.0"
          - "MESSAGE:&b&l*** Storm Shield! &7-25% DMG, Counter: 5 ***"

      # ----------------------------------------------------------------------
      # STORM SHIELD: Defensive proc (Rage Mode - 100% chance)
      # Always triggers when in rage mode
      # ----------------------------------------------------------------------
      storm_shield_rage:
        type: DEFENSE
        chance: 100
        cooldown: 3000
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "NUMBER_STORAGE:tempest_rage:=:1"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
        options:
          # Block 40% of incoming damage in rage
          - "DEFENSE:0.40:2"
        effects:
          # Enhanced counter-attack
          - "TARGET=ENEMY | TRUE_DAMAGE:10"
          # Stronger resistance
          - "ADD_POTION:DAMAGE_RESISTANCE:1:40"
          # Life steal effect
          - "HEALTH:ADD:2"
          # Visual feedback
          - "PLAY_SOUND:ITEM_SHIELD_BLOCK:1.0:0.8"
          - "PACKET_REDSTONE_PARTICLE:~_~_1_~:255:50:50:1.5"
          - "MESSAGE:&c&l*** Raging Storm Shield! &4-40% DMG, Counter: 10, +2 HP ***"
        true-condition-break: true

      # ----------------------------------------------------------------------
      # DAMAGE SCALING: Enhanced damage when enemy is low HP
      # Execute-style bonus damage
      # ----------------------------------------------------------------------
      execute_bonus:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "TARGET=ENEMY | HEALTH_PERCENT:<=:25"
        options:
          # +20% damage to low HP enemies
          - "ATTACK:0.2:2"
        effects:
          - "MESSAGE:&4&l*** Execute Bonus: +20% ATK ***"

    # ========================================================================
    # LEVEL 2 - Enhanced values
    # ========================================================================
    "2":
      passive_hold:
        type: HOLD
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
        effects:
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.08:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.05:1"
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "NUMBER_STORAGE:SET:tempest_last_attack:0"
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "MESSAGE:&6&l*** Tempest Wrath II: &a&lActivated &6&l***"

      passive_unhold:
        type: CHANGE_HAND
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
        effects:
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "REMOVE_TASK:tempest_combo_reset"
          - "REMOVE_TASK:tempest_rage_end"
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "MESSAGE:&6&l*** Tempest Wrath II: &7&lDeactivated &6&l***"

      # ... (same structure as level 1 with enhanced values)
      # Tempest Strike: +60% normal, +120% rage
      # Storm Shield: 35% chance normal, block 30%
      # Execute bonus: +25% to low HP
      combo_timeout_check:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>:0"
          - "NUMBER_STORAGE:%time%-tempest_last_attack:>:5000"
        effects:
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "MESSAGE:&6&l*** Combo Reset (timeout) ***"

      combo_build:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:<:5"
        effects:
          - "NUMBER_STORAGE:ADD:tempest_combo:1"
          - "NUMBER_STORAGE:SET:tempest_last_attack:%time%"
          - "REMOVE_TASK:tempest_combo_reset"
          - "NAME=tempest_combo_reset,DELAY=100 | NUMBER_STORAGE:SET:tempest_combo:0"
          - "NAME=tempest_combo_reset_msg,DELAY=100 | MESSAGE:&6&l*** Combo Reset ***"
          - "PLAY_SOUND:ENTITY_EXPERIENCE_ORB_PICKUP:1.0:1.2"

      combo_display_1:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:=:1"
        effects:
          - "MESSAGE:&e&l[Tempest II] &7Stack: &e1&7/5"

      combo_display_2:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:=:2"
        effects:
          - "MESSAGE:&e&l[Tempest II] &7Stack: &e2&7/5"

      combo_display_3:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:=:3"
        effects:
          - "MESSAGE:&e&l[Tempest II] &7Stack: &e3&7/5"

      combo_display_4:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:=:4"
        effects:
          - "MESSAGE:&e&l[Tempest II] &7Stack: &6&l4&7/5 &c(Almost ready!)"

      tempest_strike_normal:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>=:5"
          - "NUMBER_STORAGE:tempest_rage:=:0"
        options:
          - "ATTACK:0.6:2"
        effects:
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "REMOVE_TASK:tempest_combo_reset"
          - "REMOVE_TASK:tempest_combo_reset_msg"
          - "TARGET=ENEMY | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY | ADD_POTION:SLOW:1:50"
          - "PLAY_SOUND:ENTITY_LIGHTNING_BOLT_THUNDER:1.0:1.0"
          - "MESSAGE:&6&l*** TEMPEST STRIKE II! &e+60% ATK &6***"
        target-filter:
          target: ENEMY
          except-player: true
          except-enemy: true
          min-distance: 0
          max-distance: 4
        target-conditions:
          - "NEGATIVE=true | FACTION_RELATION:MEMBER"
          - "NEGATIVE=true | FACTION_RELATION:ALLY"
        target-effects:
          - "TARGET=ENEMY | DEAL_DAMAGE:7"
          - "TARGET=ENEMY | ADD_POTION:SLOW:0:30"
        true-condition-break: true

      tempest_strike_rage:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>=:5"
          - "NUMBER_STORAGE:tempest_rage:=:1"
        options:
          - "ATTACK:1.2:2"
        effects:
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "REMOVE_TASK:tempest_combo_reset"
          - "REMOVE_TASK:tempest_combo_reset_msg"
          - "TARGET=ENEMY | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY,DELAY=5 | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY | ADD_POTION:SLOW:2:70"
          - "PLAY_SOUND:ENTITY_LIGHTNING_BOLT_THUNDER:1.0:0.8"
          - "MESSAGE:&c&l*** RAGING TEMPEST STRIKE II! &4+120% ATK &c***"
        target-filter:
          target: ENEMY
          except-player: true
          except-enemy: true
          min-distance: 0
          max-distance: 6
        target-conditions:
          - "NEGATIVE=true | FACTION_RELATION:MEMBER"
          - "NEGATIVE=true | FACTION_RELATION:ALLY"
        target-effects:
          - "TARGET=ENEMY | DEAL_DAMAGE:14"
          - "TARGET=ENEMY | ADD_POTION:SLOW:1:50"
        true-condition-break: true

      rage_activate:
        type: DEFENSE
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:<=:30"
          - "NUMBER_STORAGE:tempest_rage:=:0"
        effects:
          - "NUMBER_STORAGE:SET:tempest_rage:1"
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.16:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.10:1"
          - "ADD_POTION:GLOWING:0:100"
          - "PLAY_SOUND:ENTITY_WITHER_SPAWN:0.5:1.5"
          - "MESSAGE:&c&l*** RAGE MODE II ACTIVATED! ***"

      rage_deactivate:
        type: DEFENSE
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:>:50"
          - "NUMBER_STORAGE:tempest_rage:=:1"
        effects:
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.08:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.05:1"
          - "MESSAGE:&6&l*** Rage Mode Ended ***"

      storm_shield_normal:
        type: DEFENSE
        chance: 35
        cooldown: 3000
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:<=:50"
          - "HEALTH_PERCENT:>:30"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
        options:
          - "DEFENSE:0.30:2"
        effects:
          - "TARGET=ENEMY | TRUE_DAMAGE:7"
          - "ADD_POTION:DAMAGE_RESISTANCE:0:35"
          - "PLAY_SOUND:ITEM_SHIELD_BLOCK:1.0:1.0"
          - "MESSAGE:&b&l*** Storm Shield II! &7-30% DMG, Counter: 7 ***"

      storm_shield_rage:
        type: DEFENSE
        chance: 100
        cooldown: 3000
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "NUMBER_STORAGE:tempest_rage:=:1"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
        options:
          - "DEFENSE:0.45:2"
        effects:
          - "TARGET=ENEMY | TRUE_DAMAGE:14"
          - "ADD_POTION:DAMAGE_RESISTANCE:1:45"
          - "HEALTH:ADD:3"
          - "PLAY_SOUND:ITEM_SHIELD_BLOCK:1.0:0.8"
          - "PACKET_REDSTONE_PARTICLE:~_~_1_~:255:50:50:1.5"
          - "MESSAGE:&c&l*** Raging Storm Shield II! &4-45% DMG, Counter: 14, +3 HP ***"
        true-condition-break: true

      execute_bonus:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "TARGET=ENEMY | HEALTH_PERCENT:<=:25"
        options:
          - "ATTACK:0.25:2"
        effects:
          - "MESSAGE:&4&l*** Execute Bonus II: +25% ATK ***"

    # ========================================================================
    # LEVEL 3, 4, 5 would follow same pattern with scaling values:
    #
    # Level 3:
    #   - Passive: +12% MS, +8% ATK
    #   - Tempest Strike: +70% normal, +140% rage
    #   - Storm Shield: 40% chance, block 35%
    #   - Counter damage: 9 normal, 18 rage
    #   - Execute: +30%
    #
    # Level 4:
    #   - Passive: +15% MS, +10% ATK
    #   - Tempest Strike: +80% normal, +160% rage
    #   - Storm Shield: 45% chance, block 40%
    #   - Counter damage: 12 normal, 24 rage
    #   - Execute: +35%
    #
    # Level 5:
    #   - Passive: +20% MS, +15% ATK
    #   - Tempest Strike: +100% normal, +200% rage
    #   - Storm Shield: 50% chance, block 50%
    #   - Counter damage: 15 normal, 30 rage
    #   - Execute: +40%
    #   - Special: Lightning chains to 2 additional enemies
    # ========================================================================
    "3":
      passive_hold:
        type: HOLD
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
        effects:
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.12:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.08:1"
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "NUMBER_STORAGE:SET:tempest_last_attack:0"
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "MESSAGE:&6&l*** Tempest Wrath III: &a&lActivated &6&l***"

      passive_unhold:
        type: CHANGE_HAND
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
        effects:
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "REMOVE_TASK:tempest_combo_reset"
          - "REMOVE_TASK:tempest_rage_end"
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "MESSAGE:&6&l*** Tempest Wrath III: &7&lDeactivated &6&l***"

      combo_timeout_check:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>:0"
          - "NUMBER_STORAGE:%time%-tempest_last_attack:>:5000"
        effects:
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "MESSAGE:&6&l*** Combo Reset (timeout) ***"

      combo_build:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:<:5"
        effects:
          - "NUMBER_STORAGE:ADD:tempest_combo:1"
          - "NUMBER_STORAGE:SET:tempest_last_attack:%time%"
          - "REMOVE_TASK:tempest_combo_reset"
          - "NAME=tempest_combo_reset,DELAY=100 | NUMBER_STORAGE:SET:tempest_combo:0"
          - "NAME=tempest_combo_reset_msg,DELAY=100 | MESSAGE:&6&l*** Combo Reset ***"
          - "PLAY_SOUND:ENTITY_EXPERIENCE_ORB_PICKUP:1.0:1.2"

      combo_display_1:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:=:1"
        effects:
          - "MESSAGE:&e&l[Tempest III] &7Stack: &e1&7/5"

      combo_display_2:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:=:2"
        effects:
          - "MESSAGE:&e&l[Tempest III] &7Stack: &e2&7/5"

      combo_display_3:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:=:3"
        effects:
          - "MESSAGE:&e&l[Tempest III] &7Stack: &e3&7/5"

      combo_display_4:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:=:4"
        effects:
          - "MESSAGE:&e&l[Tempest III] &7Stack: &6&l4&7/5 &c(Almost ready!)"

      tempest_strike_normal:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>=:5"
          - "NUMBER_STORAGE:tempest_rage:=:0"
        options:
          - "ATTACK:0.7:2"
        effects:
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "REMOVE_TASK:tempest_combo_reset"
          - "REMOVE_TASK:tempest_combo_reset_msg"
          - "TARGET=ENEMY | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY | ADD_POTION:SLOW:1:60"
          - "PLAY_SOUND:ENTITY_LIGHTNING_BOLT_THUNDER:1.0:1.0"
          - "MESSAGE:&6&l*** TEMPEST STRIKE III! &e+70% ATK &6***"
        target-filter:
          target: ENEMY
          except-player: true
          except-enemy: true
          min-distance: 0
          max-distance: 5
        target-conditions:
          - "NEGATIVE=true | FACTION_RELATION:MEMBER"
          - "NEGATIVE=true | FACTION_RELATION:ALLY"
        target-effects:
          - "TARGET=ENEMY | DEAL_DAMAGE:9"
          - "TARGET=ENEMY | ADD_POTION:SLOW:0:40"
        true-condition-break: true

      tempest_strike_rage:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>=:5"
          - "NUMBER_STORAGE:tempest_rage:=:1"
        options:
          - "ATTACK:1.4:2"
        effects:
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "REMOVE_TASK:tempest_combo_reset"
          - "REMOVE_TASK:tempest_combo_reset_msg"
          - "TARGET=ENEMY | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY,DELAY=5 | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY | ADD_POTION:SLOW:2:80"
          - "PLAY_SOUND:ENTITY_LIGHTNING_BOLT_THUNDER:1.0:0.8"
          - "MESSAGE:&c&l*** RAGING TEMPEST STRIKE III! &4+140% ATK &c***"
        target-filter:
          target: ENEMY
          except-player: true
          except-enemy: true
          min-distance: 0
          max-distance: 7
        target-conditions:
          - "NEGATIVE=true | FACTION_RELATION:MEMBER"
          - "NEGATIVE=true | FACTION_RELATION:ALLY"
        target-effects:
          - "TARGET=ENEMY | DEAL_DAMAGE:18"
          - "TARGET=ENEMY | ADD_POTION:SLOW:1:60"
        true-condition-break: true

      rage_activate:
        type: DEFENSE
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:<=:30"
          - "NUMBER_STORAGE:tempest_rage:=:0"
        effects:
          - "NUMBER_STORAGE:SET:tempest_rage:1"
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.24:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.16:1"
          - "ADD_POTION:GLOWING:0:100"
          - "PLAY_SOUND:ENTITY_WITHER_SPAWN:0.5:1.5"
          - "MESSAGE:&c&l*** RAGE MODE III ACTIVATED! ***"

      rage_deactivate:
        type: DEFENSE
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:>:50"
          - "NUMBER_STORAGE:tempest_rage:=:1"
        effects:
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.12:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.08:1"
          - "MESSAGE:&6&l*** Rage Mode Ended ***"

      storm_shield_normal:
        type: DEFENSE
        chance: 40
        cooldown: 3000
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:<=:50"
          - "HEALTH_PERCENT:>:30"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
        options:
          - "DEFENSE:0.35:2"
        effects:
          - "TARGET=ENEMY | TRUE_DAMAGE:9"
          - "ADD_POTION:DAMAGE_RESISTANCE:0:40"
          - "PLAY_SOUND:ITEM_SHIELD_BLOCK:1.0:1.0"
          - "MESSAGE:&b&l*** Storm Shield III! &7-35% DMG, Counter: 9 ***"

      storm_shield_rage:
        type: DEFENSE
        chance: 100
        cooldown: 3000
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "NUMBER_STORAGE:tempest_rage:=:1"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
        options:
          - "DEFENSE:0.50:2"
        effects:
          - "TARGET=ENEMY | TRUE_DAMAGE:18"
          - "ADD_POTION:DAMAGE_RESISTANCE:1:50"
          - "HEALTH:ADD:4"
          - "PLAY_SOUND:ITEM_SHIELD_BLOCK:1.0:0.8"
          - "PACKET_REDSTONE_PARTICLE:~_~_1_~:255:50:50:1.5"
          - "MESSAGE:&c&l*** Raging Storm Shield III! &4-50% DMG, Counter: 18, +4 HP ***"
        true-condition-break: true

      execute_bonus:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "TARGET=ENEMY | HEALTH_PERCENT:<=:25"
        options:
          - "ATTACK:0.30:2"
        effects:
          - "MESSAGE:&4&l*** Execute Bonus III: +30% ATK ***"

    # Level 4 and 5 follow same pattern...
    "4":
      passive_hold:
        type: HOLD
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
        effects:
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.15:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.10:1"
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "NUMBER_STORAGE:SET:tempest_last_attack:0"
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "MESSAGE:&6&l*** Tempest Wrath IV: &a&lActivated &6&l***"

      passive_unhold:
        type: CHANGE_HAND
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
        effects:
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "REMOVE_TASK:tempest_combo_reset"
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "MESSAGE:&6&l*** Tempest Wrath IV: &7&lDeactivated &6&l***"

      combo_timeout_check:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>:0"
          - "NUMBER_STORAGE:%time%-tempest_last_attack:>:5000"
        effects:
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "MESSAGE:&6&l*** Combo Reset (timeout) ***"

      combo_build:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:<:5"
        effects:
          - "NUMBER_STORAGE:ADD:tempest_combo:1"
          - "NUMBER_STORAGE:SET:tempest_last_attack:%time%"
          - "REMOVE_TASK:tempest_combo_reset"
          - "NAME=tempest_combo_reset,DELAY=100 | NUMBER_STORAGE:SET:tempest_combo:0"
          - "PLAY_SOUND:ENTITY_EXPERIENCE_ORB_PICKUP:1.0:1.2"

      tempest_strike_normal:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>=:5"
          - "NUMBER_STORAGE:tempest_rage:=:0"
        options:
          - "ATTACK:0.8:2"
        effects:
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "REMOVE_TASK:tempest_combo_reset"
          - "TARGET=ENEMY | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY | ADD_POTION:SLOW:2:60"
          - "PLAY_SOUND:ENTITY_LIGHTNING_BOLT_THUNDER:1.0:1.0"
          - "MESSAGE:&6&l*** TEMPEST STRIKE IV! &e+80% ATK &6***"
        target-filter:
          target: ENEMY
          except-player: true
          except-enemy: true
          min-distance: 0
          max-distance: 5
        target-conditions:
          - "NEGATIVE=true | FACTION_RELATION:MEMBER"
        target-effects:
          - "TARGET=ENEMY | DEAL_DAMAGE:12"
          - "TARGET=ENEMY | ADD_POTION:SLOW:1:40"
        true-condition-break: true

      tempest_strike_rage:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>=:5"
          - "NUMBER_STORAGE:tempest_rage:=:1"
        options:
          - "ATTACK:1.6:2"
        effects:
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "REMOVE_TASK:tempest_combo_reset"
          - "TARGET=ENEMY | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY,DELAY=5 | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY | ADD_POTION:SLOW:3:80"
          - "PLAY_SOUND:ENTITY_LIGHTNING_BOLT_THUNDER:1.0:0.7"
          - "MESSAGE:&c&l*** RAGING TEMPEST STRIKE IV! &4+160% ATK &c***"
        target-filter:
          target: ENEMY
          except-player: true
          except-enemy: true
          min-distance: 0
          max-distance: 7
        target-conditions:
          - "NEGATIVE=true | FACTION_RELATION:MEMBER"
        target-effects:
          - "TARGET=ENEMY | DEAL_DAMAGE:24"
          - "TARGET=ENEMY | ADD_POTION:SLOW:2:60"
        true-condition-break: true

      rage_activate:
        type: DEFENSE
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:<=:30"
          - "NUMBER_STORAGE:tempest_rage:=:0"
        effects:
          - "NUMBER_STORAGE:SET:tempest_rage:1"
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.30:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.20:1"
          - "ADD_POTION:GLOWING:0:100"
          - "PLAY_SOUND:ENTITY_WITHER_SPAWN:0.5:1.4"
          - "MESSAGE:&c&l*** RAGE MODE IV ACTIVATED! ***"

      rage_deactivate:
        type: DEFENSE
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:>:50"
          - "NUMBER_STORAGE:tempest_rage:=:1"
        effects:
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.15:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.10:1"
          - "MESSAGE:&6&l*** Rage Mode Ended ***"

      storm_shield_normal:
        type: DEFENSE
        chance: 45
        cooldown: 2500
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:<=:50"
          - "HEALTH_PERCENT:>:30"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
        options:
          - "DEFENSE:0.40:2"
        effects:
          - "TARGET=ENEMY | TRUE_DAMAGE:12"
          - "ADD_POTION:DAMAGE_RESISTANCE:0:45"
          - "PLAY_SOUND:ITEM_SHIELD_BLOCK:1.0:0.9"
          - "MESSAGE:&b&l*** Storm Shield IV! &7-40% DMG, Counter: 12 ***"

      storm_shield_rage:
        type: DEFENSE
        chance: 100
        cooldown: 2500
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "NUMBER_STORAGE:tempest_rage:=:1"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
        options:
          - "DEFENSE:0.55:2"
        effects:
          - "TARGET=ENEMY | TRUE_DAMAGE:24"
          - "ADD_POTION:DAMAGE_RESISTANCE:1:55"
          - "HEALTH:ADD:5"
          - "PLAY_SOUND:ITEM_SHIELD_BLOCK:1.0:0.7"
          - "MESSAGE:&c&l*** Raging Storm Shield IV! &4-55% DMG, Counter: 24, +5 HP ***"
        true-condition-break: true

      execute_bonus:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "TARGET=ENEMY | HEALTH_PERCENT:<=:25"
        options:
          - "ATTACK:0.35:2"
        effects:
          - "MESSAGE:&4&l*** Execute Bonus IV: +35% ATK ***"

    "5":
      passive_hold:
        type: HOLD
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
        effects:
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.20:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.15:1"
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "NUMBER_STORAGE:SET:tempest_last_attack:0"
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "MESSAGE:&6&l*** Tempest Wrath V: &a&lActivated &6&l***"

      passive_unhold:
        type: CHANGE_HAND
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
        effects:
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "REMOVE_TASK:tempest_combo_reset"
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "MESSAGE:&6&l*** Tempest Wrath V: &7&lDeactivated &6&l***"

      combo_timeout_check:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>:0"
          - "NUMBER_STORAGE:%time%-tempest_last_attack:>:5000"
        effects:
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "MESSAGE:&6&l*** Combo Reset (timeout) ***"

      combo_build:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:<:5"
        effects:
          - "NUMBER_STORAGE:ADD:tempest_combo:1"
          - "NUMBER_STORAGE:SET:tempest_last_attack:%time%"
          - "REMOVE_TASK:tempest_combo_reset"
          - "NAME=tempest_combo_reset,DELAY=100 | NUMBER_STORAGE:SET:tempest_combo:0"
          - "PLAY_SOUND:ENTITY_EXPERIENCE_ORB_PICKUP:1.0:1.3"

      # LEVEL 5 SPECIAL: Chain lightning to nearby enemies
      tempest_strike_normal:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>=:5"
          - "NUMBER_STORAGE:tempest_rage:=:0"
        options:
          - "ATTACK:1.0:2"
        effects:
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "REMOVE_TASK:tempest_combo_reset"
          - "TARGET=ENEMY | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY | ADD_POTION:SLOW:2:80"
          - "PLAY_SOUND:ENTITY_LIGHTNING_BOLT_THUNDER:1.0:1.0"
          - "MESSAGE:&6&l*** TEMPEST STRIKE V! &e+100% ATK + CHAIN LIGHTNING &6***"
        target-filter:
          target: ENEMY
          except-player: true
          except-enemy: true
          min-distance: 0
          max-distance: 6
        target-conditions:
          - "NEGATIVE=true | FACTION_RELATION:MEMBER"
        target-effects:
          - "TARGET=ENEMY | DEAL_DAMAGE:15"
          - "TARGET=ENEMY | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY | ADD_POTION:SLOW:1:40"
        true-condition-break: true

      tempest_strike_rage:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "NUMBER_STORAGE:tempest_combo:>=:5"
          - "NUMBER_STORAGE:tempest_rage:=:1"
        options:
          - "ATTACK:2.0:2"
        effects:
          - "NUMBER_STORAGE:SET:tempest_combo:0"
          - "REMOVE_TASK:tempest_combo_reset"
          - "TARGET=ENEMY | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY,DELAY=3 | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY,DELAY=6 | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY | ADD_POTION:SLOW:3:100"
          - "PLAY_SOUND:ENTITY_LIGHTNING_BOLT_THUNDER:1.0:0.6"
          - "MESSAGE:&c&l*** ULTIMATE RAGING TEMPEST! &4+200% ATK + TRIPLE CHAIN &c***"
        target-filter:
          target: ENEMY
          except-player: true
          except-enemy: true
          min-distance: 0
          max-distance: 8
        target-conditions:
          - "NEGATIVE=true | FACTION_RELATION:MEMBER"
        target-effects:
          - "TARGET=ENEMY | DEAL_DAMAGE:30"
          - "TARGET=ENEMY | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY,DELAY=5 | LIGHTNING:~_xP_yP_zP"
          - "TARGET=ENEMY | ADD_POTION:SLOW:2:80"
        true-condition-break: true

      rage_activate:
        type: DEFENSE
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:<=:30"
          - "NUMBER_STORAGE:tempest_rage:=:0"
        effects:
          - "NUMBER_STORAGE:SET:tempest_rage:1"
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.40:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.30:1"
          - "ADD_POTION:GLOWING:0:100"
          - "ADD_POTION:FIRE_RESISTANCE:0:100"
          - "PLAY_SOUND:ENTITY_WITHER_SPAWN:0.6:1.3"
          - "MESSAGE:&c&l*** ULTIMATE RAGE MODE V ACTIVATED! ***"

      rage_deactivate:
        type: DEFENSE
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:>:50"
          - "NUMBER_STORAGE:tempest_rage:=:1"
        effects:
          - "NUMBER_STORAGE:SET:tempest_rage:0"
          - "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
          - "REMOVE_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk"
          - "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.20:1"
          - "ADD_ATTRIBUTE:ATTACK_DAMAGE:tempest_atk:0.15:1"
          - "MESSAGE:&6&l*** Rage Mode Ended ***"

      storm_shield_normal:
        type: DEFENSE
        chance: 50
        cooldown: 2000
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "HEALTH_PERCENT:<=:50"
          - "HEALTH_PERCENT:>:30"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
        options:
          - "DEFENSE:0.50:2"
        effects:
          - "TARGET=ENEMY | TRUE_DAMAGE:15"
          - "ADD_POTION:DAMAGE_RESISTANCE:1:50"
          - "PLAY_SOUND:ITEM_SHIELD_BLOCK:1.0:0.8"
          - "MESSAGE:&b&l*** Storm Shield V! &7-50% DMG, Counter: 15 ***"

      storm_shield_rage:
        type: DEFENSE
        chance: 100
        cooldown: 2000
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "NUMBER_STORAGE:tempest_rage:=:1"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
        options:
          - "DEFENSE:0.60:2"
        effects:
          - "TARGET=ENEMY | TRUE_DAMAGE:30"
          - "TARGET=ENEMY | ADD_POTION:SLOW:2:40"
          - "ADD_POTION:DAMAGE_RESISTANCE:2:60"
          - "HEALTH:ADD:8"
          - "PLAY_SOUND:ITEM_SHIELD_BLOCK:1.0:0.6"
          - "MESSAGE:&c&l*** ULTIMATE Raging Storm Shield V! &4-60% DMG, Counter: 30, +8 HP ***"
        true-condition-break: true

      execute_bonus:
        type: ATTACK
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
          - "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
          - "TARGET=ENEMY | HEALTH_PERCENT:<=:25"
        options:
          - "ATTACK:0.40:2"
        effects:
          - "MESSAGE:&4&l*** Execute Bonus V: +40% ATK ***"

      # LEVEL 5 SPECIAL: True damage on kill
      kill_bonus:
        type: KILL_PLAYER
        chance: 100
        cooldown: 0
        conditions:
          - "EQUIP_SLOT:MAINHAND"
        effects:
          - "HEALTH:ADD:10"
          - "ADD_POTION:REGENERATION:1:60"
          - "ADD_POTION:ABSORPTION:1:200"
          - "MESSAGE:&6&l*** Storm Harvest: +10 HP, Regen II, Absorption II ***"
```

---

## Features Demonstrated

### 1. Multiple Trigger Types
- `HOLD` / `CHANGE_HAND` - Passive activation/deactivation
- `ATTACK` - Combo system and damage bonuses
- `DEFENSE` - Rage mode and storm shield
- `KILL_PLAYER` - Kill bonus (Level 5 only)

### 2. Number Storage State Machine
```yaml
# Initialize state
- "NUMBER_STORAGE:SET:tempest_combo:0"
- "NUMBER_STORAGE:SET:tempest_rage:0"
- "NUMBER_STORAGE:SET:tempest_last_attack:0"

# Increment combo
- "NUMBER_STORAGE:ADD:tempest_combo:1"

# Check time elapsed
- "NUMBER_STORAGE:%time%-tempest_last_attack:>:5000"

# Check state
- "NUMBER_STORAGE:tempest_rage:=:1"
```

### 3. Named Delayed Tasks
```yaml
# Schedule task with name
- "NAME=tempest_combo_reset,DELAY=100 | NUMBER_STORAGE:SET:tempest_combo:0"

# Cancel task before scheduling new one
- "REMOVE_TASK:tempest_combo_reset"
```

### 4. Conditional Branches
```yaml
# Different behavior based on state
tempest_strike_normal:
  conditions:
    - "NUMBER_STORAGE:tempest_rage:=:0"  # Not in rage
  true-condition-break: true

tempest_strike_rage:
  conditions:
    - "NUMBER_STORAGE:tempest_rage:=:1"  # In rage mode
  true-condition-break: true
```

### 5. Target Filters (AoE)
```yaml
target-filter:
  target: ENEMY
  except-player: true    # Don't include caster
  except-enemy: true     # Don't include primary target (already hit)
  min-distance: 0
  max-distance: 6
target-conditions:
  - "NEGATIVE=true | FACTION_RELATION:MEMBER"  # Exclude faction members
target-effects:
  - "TARGET=ENEMY | DEAL_DAMAGE:15"
```

### 6. Damage/Defense Modifiers
```yaml
options:
  - "ATTACK:0.5:2"    # +50% damage (multiply)
  - "ATTACK:1.0:2"    # +100% damage
  - "DEFENSE:0.25:2"  # Reduce damage taken by 25%
```

### 7. Multiple Effects with Targets and Delays
```yaml
effects:
  - "TARGET=ENEMY | LIGHTNING:~_xP_yP_zP"           # Lightning on enemy
  - "TARGET=ENEMY,DELAY=5 | LIGHTNING:~_xP_yP_zP"   # Delayed lightning
  - "ADD_POTION:DAMAGE_RESISTANCE:1:40"             # Potion on self
  - "HEALTH:ADD:2"                                   # Heal self
```

### 8. Health-Based Conditions
```yaml
# Storm shield only below 50% HP
- "HEALTH_PERCENT:<=:50"
- "HEALTH_PERCENT:>:30"

# Rage mode below 30% HP
- "HEALTH_PERCENT:<=:30"
```

### 9. Attribute Management
```yaml
# Remove old attributes before adding new (for state changes)
- "REMOVE_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms"
- "ADD_ATTRIBUTE:MOVEMENT_SPEED:tempest_ms:0.20:1"
```

### 10. Sound and Visual Feedback
```yaml
- "PLAY_SOUND:ENTITY_LIGHTNING_BOLT_THUNDER:1.0:1.0"
- "PACKET_REDSTONE_PARTICLE:~_~_1_~:255:50:50:1.5"
- "ADD_POTION:GLOWING:0:100"  # Visual indicator
```

---

## Scaling Table

| Level | MS Bonus | ATK Bonus | Tempest Strike | Rage Strike | Shield Chance | Shield Block | Counter |
|-------|----------|-----------|----------------|-------------|---------------|--------------|---------|
| 1 | +5% | +3% | +50% | +100% | 30% | 25% | 5 |
| 2 | +8% | +5% | +60% | +120% | 35% | 30% | 7 |
| 3 | +12% | +8% | +70% | +140% | 40% | 35% | 9 |
| 4 | +15% | +10% | +80% | +160% | 45% | 40% | 12 |
| 5 | +20% | +15% | +100% | +200% | 50% | 50% | 15 |

**Rage Mode bonuses are doubled for all values.**

---

## Tips for Complex Enchantments

1. **Use unique IDs** - Prefix storage keys and attribute IDs with enchantment name
2. **Clean up on unequip** - Remove all attributes, cancel all tasks
3. **Order functions carefully** - Use `true-condition-break` to prevent multiple triggers
4. **Test edge cases** - What happens when switching weapons mid-combo?
5. **Balance cooldowns** - Prevent exploit stacking
6. **Provide feedback** - Messages, sounds, particles help players understand mechanics
7. **Scale appropriately** - Higher levels should feel impactful but not broken
