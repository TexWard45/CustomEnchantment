# Issue #50: Common Artifact Enchantments (Phase 2)

## Summary

Implemented 13 common artifact enchantment effect definitions in `artifact-common.yml`. All are simple single-stat passive boosts using EXTRA_SLOT_EQUIP/UNEQUIP pattern.

## Previous Phases

- Phase 1 (#49): Fix Regular Enchantments - Lessons applied below

## Lessons Applied from Phase 1

From Phase 1 (#49 - see `.claude/docs/issues/49/`):
- Applied: Complete detail-descriptions with scaling values using `X x Cấp` format
- Applied: Correct custom attribute type names (DODGE_CHANCE not DODGE, LIFE_STEAL not LIFESTEAL)
- Applied: Unique effect key names following `<enchantname>_<stat>` convention
- Applied: Every ADD effect has matching REMOVE in unequip

## Decisions Made

### No ARROW_DAMAGE or ARROW_DAMAGE_REDUCTION custom attribute exists

**Problem:** ironarrow needs "bow damage" and chainchestplate needs "arrow damage reduction", but `CustomAttributeType.java` has neither type.

**Decision:** Used closest available attributes:
- ironarrow: `ATTACK_DAMAGE` (general attack damage, affects both melee and ranged)
- chainchestplate: `DAMAGE_REDUCTION` (general damage reduction, not arrow-specific)

**Lesson for future:** If bow-specific stats are needed, new `CustomAttributeType` entries must be added in Java first.

### HEALTH_REGENERATION display value vs internal value

**Pattern:** freshgrass (existing) uses description "0.4 x Cấp" but internal HEALTH_REGENERATION values are 1.0/2.0/3.0/4.0/5.0. This is the established convention - display units differ from internal attribute units.

**Applied:** herballeaves follows the same pattern as freshgrass.

### Duplicate stat enchantments are by design

Multiple common artifacts share the same stat:
- ATTACK_DAMAGE: deadtwig, rustyblade, ironarrow (+ existing ironbeak)
- ATTACK_SPEED: bentblade, feather
- HEALTH_REGENERATION: herballeaves (+ existing freshgrass)
- CRITICAL_CHANCE: crackedglassshard (+ existing eggshell)

This is intentional - common tier provides variety of themed items with same basic stats.

### eggshell enchantment exists but has no matching item

**Observation:** `artifact-common-attack.yml` has `eggshell` (CRITICAL_CHANCE), but no artifact item definition references `enchant: "eggshell"`. The `_1common_attack.yml` has `crackedglassshard` instead. These appear to be the same enchantment type (both CRITICAL_CHANCE) but with different keys.

**Action:** Created `crackedglassshard` as a new enchantment to match the item definition. Did not modify/remove `eggshell` as it may still be referenced elsewhere.

## Enchantment Summary

| Name | Attribute | Type | Base/lvl | Scaling |
|------|-----------|------|----------|---------|
| deadtwig | ATTACK_DAMAGE | ADD_ATTRIBUTE | 0.5 | Linear |
| rustyblade | ATTACK_DAMAGE | ADD_ATTRIBUTE | 0.5 | Linear |
| crackedglassshard | CRITICAL_CHANCE | ADD_CUSTOM_ATTRIBUTE | 1.0% | Linear |
| tatteredboots | MOVEMENT_SPEED | ADD_ATTRIBUTE | 0.005 | Linear |
| rabbithide | DODGE_CHANCE | ADD_CUSTOM_ATTRIBUTE | 1.0% | Linear |
| fang | LIFE_STEAL | ADD_CUSTOM_ATTRIBUTE | 1.0% | Linear |
| ironarrow | ATTACK_DAMAGE | ADD_ATTRIBUTE | 0.5 | Linear |
| yellowsand | MAX_ABSORPTION | ADD_ATTRIBUTE | 2 | Linear |
| wornstone | ARMOR | ADD_ATTRIBUTE | 1 | Linear |
| bentblade | ATTACK_SPEED | ADD_ATTRIBUTE | 0.02 | Linear |
| chainchestplate | DAMAGE_REDUCTION | ADD_CUSTOM_ATTRIBUTE | 2.0% | Linear |
| herballeaves | HEALTH_REGENERATION | ADD_CUSTOM_ATTRIBUTE | 1.0 | Linear |
| feather | ATTACK_SPEED | ADD_ATTRIBUTE | 0.02 | Linear |

## Patterns Confirmed for Future Phases

### Common artifact enchantment pattern
- group: artifact
- Trigger: EXTRA_SLOT_EQUIP / EXTRA_SLOT_UNEQUIP
- Condition: EQUIP_SLOT:EXTRA_SLOT
- Single stat boost (no active effects, no MESSAGE effects)
- Linear scaling: value = base * level
- max-level: 5, enchant-point: 1, valuable: 0

### Vanilla vs Custom attribute decision tree
- Vanilla (ADD_ATTRIBUTE): ATTACK_DAMAGE, MAX_HEALTH, MOVEMENT_SPEED, ATTACK_SPEED, ARMOR, MAX_ABSORPTION, ARMOR_TOUGHNESS, KNOCKBACK_RESISTANCE, SCALE
- Custom (ADD_CUSTOM_ATTRIBUTE): DODGE_CHANCE, CRITICAL_CHANCE, CRITICAL_DAMAGE, LIFE_STEAL, HEALTH_REGENERATION, HEALTH_REGENERATION_PERCENT, DAMAGE_REDUCTION, ARMOR_PENETRATION, SLOW_RESISTANCE, MAGIC_RESISTANCE, VULNERABILITY, GRIEVOUS_WOUNDS, AOE_RANGE, AOE_DAMAGE_RATIO, ACCURACY_CHANCE, MINING_POWER

### Format difference
- ADD_ATTRIBUTE: `ADD_ATTRIBUTE:TYPE:key:value:operation`
- ADD_CUSTOM_ATTRIBUTE: `ADD_CUSTOM_ATTRIBUTE:key:TYPE:value:operation` (key and TYPE swapped!)

## Review Checklist (Updated from Phase 1)

Before submitting any artifact enchantment YAML:

- [ ] All enchantment keys match artifact item `enchant:` values
- [ ] detail-description includes ALL scaling values per level
- [ ] Effect key names are unique and follow `<enchantname>_<stat>` convention
- [ ] REMOVE effects in unequip match ALL ADD effects in equip
- [ ] Trigger type correct: EXTRA_SLOT_EQUIP/UNEQUIP for artifacts
- [ ] Values scale correctly across all 5 levels
- [ ] Attribute type names match CustomAttributeType.java exactly
- [ ] ADD_ATTRIBUTE vs ADD_CUSTOM_ATTRIBUTE format correct (key/TYPE position differs!)
- [ ] Build succeeds (./gradlew build)
