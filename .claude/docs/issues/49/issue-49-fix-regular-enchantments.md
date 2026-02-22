# Issue #49: Fix Regular Enchantments (Phase 1)

## Summary

Implemented effect definitions for 4 regular enchantments that had empty/missing levels: miner, lumberjack, hasten, planter.

## Mistakes & Lessons Learned

### Mistake 1: Missing potion level in detail-description

**What happened:** Hasten's `detail-description` listed potion name, chance, and duration but omitted the potion effect level (I/II/III) per enchantment level.

**Why it happened:** Focused on the effect values and trigger mechanics, didn't cross-reference with enchantments that have potion effects (e.g., helmet.yml's nightvision, aghast's confusion/blindness).

**Fix:** Added `&eCấp hiệu ứng: I/I/II/II/III` line.

**Lesson:** When an enchantment has scaling potion effects, the detail-description MUST include:
- `&eHiệu ứng thuốc: <Vietnamese name>`
- `&eCấp hiệu ứng: <level per enchant level>`
- `&eTỉ lệ kích hoạt: <chance per level>`
- `&eThời gian hiệu ứng: <duration per level>`
- `&eThời gian hồi: <cooldown>`

### Mistake 2: Wrong potion API name

**What happened:** Used `HASTE` (modern Paper 1.21 name) instead of `FAST_DIGGING` (legacy Bukkit name) in `ADD_POTION` effects.

**Why it happened:** Assumed modern Bukkit/Paper name without checking existing convention in the codebase.

**Fix:** Changed all `ADD_POTION:HASTE:` to `ADD_POTION:FAST_DIGGING:`.

**Lesson:** The codebase uses `PotionEffectType.getByName()` which accepts legacy Bukkit names. Existing configs consistently use legacy names:
- `CONFUSION` (not NAUSEA)
- `SLOW` (not SLOWNESS)
- `SPEED` (not SWIFTNESS)
- `FAST_DIGGING` (not HASTE)
- `BLINDNESS`, `POISON`, `WEAKNESS`, `FIRE_RESISTANCE`, `SATURATION` (same in both)

**Rule:** Always use legacy Bukkit PotionEffectType names in YAML configs.

## Patterns Established

### Tool-group enchantments
- Use `HOLD/CHANGE_HAND` pattern (NOT `ARMOR_EQUIP`)
- Condition: `EQUIP_SLOT:MAINHAND`
- Do NOT have `detail-description` (use `custom-display-lore` instead)
- `enchant-point: 0` (unlike regular enchantments which use 1)

### MINING trigger type
- `type: MINING` triggers on block break while holding tool
- Works with `chance` and `cooldown` for proc-based effects
- Condition: `EQUIP_SLOT:MAINHAND`

### ADD_BLOCK_BONUS pattern
- Money: `ADD_BLOCK_BONUS:MONEY:<key>:<block_group>:<amount>`
- EXP: `ADD_BLOCK_BONUS:EXP:<key>:<block_group>:<amount>:<min>:<chance>`
- Remove: `REMOVE_BLOCK_BONUS:MONEY:<key>` / `REMOVE_BLOCK_BONUS:EXP:<key>`

### ADD_EXPLOSION_MINING pattern
- Format: `ADD_EXPLOSION_MINING:<key>:<size>:<chance>:<replant>:<drop>:<block_group>`
- Remove: `REMOVE_EXPLOSION_MINING:<key>`

### Material groups (config.yml)
- `ALL_ORE_BLOCK` - all ore blocks
- `ALL_LOG_BLOCK` - all log blocks (ADDED in this phase)
- `ALL_FARM_FULL_STAGE` - all farmable crops at full growth
- `CE_HARVEST_BLOCK` - harvestable crop blocks

## Review Checklist for Future Phases

Before submitting any enchantment YAML:

- [ ] **detail-description** includes ALL scaling values per level
- [ ] Potion effects use **legacy Bukkit names** (FAST_DIGGING, CONFUSION, SLOW, SPEED)
- [ ] Effect key names are unique and follow `<enchantname>_<stat>` convention
- [ ] REMOVE effects in unequip/change_hand match ALL ADD effects in equip/hold
- [ ] MESSAGE effects use existing color pattern: `&color&l***Name: &a&lEffect&color&l***`
- [ ] Trigger type matches tool type (HOLD for tools, ARMOR_EQUIP for armor, EXTRA_SLOT_EQUIP for artifacts)
- [ ] Values scale reasonably across levels per tier framework
- [ ] detail-description format: use `X x Cấp` for linear scaling, `v1/v2/v3/v4/v5` for non-linear
