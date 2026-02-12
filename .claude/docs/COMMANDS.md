# CustomEnchantment Commands Reference

This document provides comprehensive reference for all admin commands in the CustomEnchantment plugin.

## Table of Contents

- [Overview](#overview)
- [Main Command](#main-command)
- [Item Management](#item-management)
- [Enchantment Management](#enchantment-management)
- [Gem Management](#gem-management)
- [Player Management](#player-management)
- [Debug Commands](#debug-commands)
- [Menu Commands](#menu-commands)
- [Utility Commands](#utility-commands)

---

## Overview

All main commands use the base command `/customenchantment` (aliases may be configured).

### Permission Structure

| Permission | Description |
|------------|-------------|
| `customenchantment.reload` | Reload configuration |
| `customenchantment.admin.target` | Toggle admin mode for players |
| `customenchantment.debug.target` | Toggle debug mode for players |
| `customenchantment.info.other` | View player attribute info |
| `customenchantment.addenchant.other` | Add enchantments to items |
| `customenchantment.removeenchant.other` | Remove enchantments from items |
| `customenchantment.addgem.other` | Add gems to items |
| `customenchantment.open.other` | Open item storage GUIs |
| `customenchantment.updateitem.other` | Update item lore/data |

---

## Main Command

### /customenchantment reload

Reloads all configuration files asynchronously.

**Permission:** `customenchantment.reload`

**Usage:**
```
/customenchantment reload
```

**Behavior:**
- Runs asynchronously to prevent server lag
- Reloads all enchantment configurations
- Reloads group definitions
- Reloads item storage
- Triggers power recalculation

---

## Item Management

### /customenchantment give

Gives items to players.

**Permission:** `customenchantment.reload`

**Usage:**
```
/customenchantment give <player> <type> <name> [parameters...]
```

**Item Types:**

| Type | Parameters | Description |
|------|------------|-------------|
| `book` | `<name> <level> <success> <destroy> <xp> <amount>` | Enchantment book |
| `protectdead` | `<name> <amount>` | Soul protection item |
| `removeprotectdead` | `<name> <amount>` | Remove soul protection |
| `removeenchantpoint` | `<name> <amount>` | Remove enchant points |
| `loreformat` | `<name> <amount>` | Lore formatting item |
| `protectdestroy` | `<name> <amount>` | Destroy protection |
| `gem` | `<name> <level> <amount>` | Socket gem |
| `nametag` | `<name> <amount>` | Name tag |
| `enchantpoint` | `<name> <amount>` | Enchant point item |
| `increaseratebook` | `<name> <success> <destroy> <amount>` | Success rate book |
| `randombook` | `<name> <amount>` | Random enchant book |
| `voucher` | `<name> <amount>` | Voucher item |
| `storage` | `<name> <amount>` | Stored item |
| `removeenchant` | `<name> <amount>` | Enchant removal scroll |
| `removegem` | `<name> <amount>` | Gem removal tool |
| `eraseenchant` | `<name> <amount>` | Enchant erase scroll |
| `mask` | `<name> <amount>` | Mask item |
| `weapon` | `<name> <amount>` | Custom weapon |
| `artifact` | `<name> <level> <amount>` | Artifact item |
| `sigil` | `<name> <level> <amount>` | Sigil item |
| `outfit` | `<name> <level> <amount>` | Outfit item |
| `gemdrill` | `<name> <amount>` | Gem drill tool |
| `banner` | `<name> <amount>` | Banner item |
| `skin` | `<name> <amount>` | Skin item |

**Examples:**
```bash
# Give enchantment book
/customenchantment give Player1 book lifesteal 3 100 0 0 1

# Give artifact with level
/customenchantment give Player1 artifact dragon_heart 5 1

# Give gem
/customenchantment give Player1 gem ruby 3 1

# Give weapon
/customenchantment give Player1 weapon excalibur 1
```

---

### /customenchantment additem

Adds an item to storage.

**Permission:** `customenchantment.reload`

**Usage:**
```
/customenchantment additem <player>
```

Adds the item in the player's main hand to the item storage.

---

### /customenchantment removeitem

Removes an item from storage.

**Permission:** `customenchantment.reload`

**Usage:**
```
/customenchantment removeitem <player>
```

---

### /customenchantment updateitem

Updates the item in a player's hand (refreshes lore, stats, etc.).

**Permission:** `customenchantment.updateitem.other`

**Usage:**
```
/customenchantment updateitem <player>
```

**Use Cases:**
- After configuration changes
- To fix corrupted item data
- To update item display after enchantment changes

---

### /customenchantment useitem

Uses/activates an item.

**Permission:** `customenchantment.reload`

**Usage:**
```
/customenchantment useitem <player>
```

---

## Enchantment Management

### /customenchantment addenchant

Adds a custom enchantment to the held item.

**Permission:** `customenchantment.addenchant.other`

**Usage:**
```
/customenchantment addenchant <player> <enchant> <level>
```

**Parameters:**
| Parameter | Description |
|-----------|-------------|
| `player` | Target player |
| `enchant` | Enchantment name |
| `level` | Enchantment level |

**Example:**
```bash
/customenchantment addenchant Player1 lifesteal 5
```

**Behavior:**
- Adds enchantment to item in main hand
- Supports unified items (items with combined weapon/book)
- Updates item lore automatically

---

### /customenchantment removeenchant

Removes a custom enchantment from the held item.

**Permission:** `customenchantment.removeenchant.other`

**Usage:**
```
/customenchantment removeenchant <player> <enchant>
```

**Example:**
```bash
/customenchantment removeenchant Player1 lifesteal
```

---

## Gem Management

### /customenchantment addgem

Adds a gem to the held weapon.

**Permission:** `customenchantment.addgem.other`

**Usage:**
```
/customenchantment addgem <player> <gem> <level>
```

**Parameters:**
| Parameter | Description |
|-----------|-------------|
| `player` | Target player |
| `gem` | Gem name |
| `level` | Gem level |

**Example:**
```bash
/customenchantment addgem Player1 ruby 3
```

---

### /customenchantment removegem

Removes a gem from the held weapon.

**Permission:** `customenchantment.addgem.other` (inferred)

**Usage:**
```
/customenchantment removegem <player> <gem>
```

---

## Player Management

### /customenchantment admin

Toggles admin mode for a player.

**Permission:** `customenchantment.admin.target`

**Usage:**
```
/customenchantment admin <player> <on|off>
```

**Admin Mode Effects:**
- Enchantments always trigger (100% chance)
- Bypasses cooldowns
- Useful for testing enchantments

**Example:**
```bash
/customenchantment admin Player1 on
/customenchantment admin Player1 off
```

---

### /customenchantment debug

Toggles debug mode for a player.

**Permission:** `customenchantment.debug.target`

**Usage:**
```
/customenchantment debug <player> <on|off>
```

**Debug Mode Effects:**
- Shows debug messages for player actions
- Useful for troubleshooting enchantment issues

---

### /customenchantment fullchance

Toggles full chance mode (100% activation rate).

**Permission:** (Admin)

**Usage:**
```
/customenchantment fullchance <player> <on|off>
```

---

### /customenchantment info

Displays attribute information for a player.

**Permission:** `customenchantment.info.other`

**Usage:**
```
/customenchantment info <player>
```

**Output Includes:**
- Vanilla attribute modifiers (health, speed, damage, armor, etc.)
- Custom attribute values (dodge chance, critical chance, life steal, etc.)

**Example Output:**
```
== Info Player1 ==
GENERIC_MAX_HEALTH ce-bonus_hp 4.0 ADD_NUMBER
GENERIC_MOVEMENT_SPEED ce-swift 0.1 ADD_SCALAR
custom:player.dodge_chance DODGE_CHANCE 15.0
custom:player.critical_chance CRITICAL_CHANCE 25.0
== End ==
```

---

### /customenchantment cleartime

Clears time-based data for a player.

**Permission:** (Admin)

**Usage:**
```
/customenchantment cleartime <player>
```

---

### /customenchantment disablehelmet

Disables helmet slot effects for a player.

**Permission:** (Admin)

**Usage:**
```
/customenchantment disablehelmet <player>
```

---

### /customenchantment enablehelmet

Enables helmet slot effects for a player.

**Permission:** (Admin)

**Usage:**
```
/customenchantment enablehelmet <player>
```

---

## Debug Commands

### /customenchantment debugall

Enables debug output for all systems.

**Permission:** (Admin)

**Usage:**
```
/customenchantment debugall
```

---

### /customenchantment debugce

Toggles enchantment call debug logging.

**Permission:** (Admin)

**Usage:**
```
/customenchantment debugce <player>
```

**Behavior:**
- Logs every enchantment call for the player
- Shows enchantment name, level, function, and slot
- Useful for debugging complex enchantment interactions

---

### /customenchantment test

Testing command for development.

**Permission:** (Admin)

**Usage:**
```
/customenchantment test
```

---

## Menu Commands

### /customenchantment open

Opens item storage GUIs.

**Permission:** `customenchantment.open.other`

**Usage:**
```
/customenchantment open <player> <type> <page> [level]
```

**Menu Types:**

| Type | Description |
|------|-------------|
| `storage` | General item storage |
| `head` | Skull/head storage |
| `mask` | Mask items |
| `weapon` | Custom weapons |
| `artifact` | Artifacts (with level) |
| `sigil` | Sigils (with level) |
| `book` | Enchantment books by group |

**Examples:**
```bash
# Open storage page 1
/customenchantment open Player1 storage 1

# Open weapon storage
/customenchantment open Player1 weapon 1

# Open artifacts at level 5
/customenchantment open Player1 artifact 1 5

# Open books by group
/customenchantment open Player1 book legendary
```

---

## Standalone Commands

These commands are registered as separate root commands:

### /bookcraft

Opens the book crafting menu.

**Usage:**
```
/bookcraft
```

Opens `BookCraftMenu` for combining enchantment books.

---

### /bookupgrade

Opens the book upgrade menu.

**Usage:**
```
/bookupgrade
```

Opens `BookUpgradeMenu` for upgrading enchantment books.

---

### /artifactupgrade

Opens the artifact upgrade menu.

**Usage:**
```
/artifactupgrade
```

Opens `ArtifactUpgradeMenu` for upgrading artifacts.

---

### /tinkerer

Opens the tinkerer menu.

**Usage:**
```
/tinkerer
```

Opens the tinkerer GUI for exchanging enchantment books.

---

### /ceanvil

Opens the custom enchantment anvil.

**Usage:**
```
/ceanvil
```

Opens `ce-anvil` menu for applying enchantments.

---

### /equipment

Opens the equipment menu.

**Usage:**
```
/equipment
```

Opens the equipment GUI for managing extra slots.

---

### /nametag

Name tag management commands.

**Usage:**
```
/nametag <subcommand>
```

See `CommandNameTag.java` for subcommand details.

---

### /cefilter

Enchantment filter commands.

**Usage:**
```
/cefilter <subcommand>
```

Used for filtering enchantments in menus/displays.

---

## Command Examples

### Complete Item Setup

```bash
# Give a weapon to player
/customenchantment give Player1 weapon excalibur 1

# Add enchantments
/customenchantment addenchant Player1 lifesteal 5
/customenchantment addenchant Player1 critical 3
/customenchantment addenchant Player1 speed 2

# Add gems
/customenchantment addgem Player1 ruby 3
/customenchantment addgem Player1 diamond 2
```

### Testing Enchantments

```bash
# Enable admin mode (100% trigger, no cooldown)
/customenchantment admin Player1 on

# Enable debug logging
/customenchantment debugce Player1

# Test enchantments...

# Disable modes
/customenchantment admin Player1 off
/customenchantment debugce Player1
```

### Configuration Reload

```bash
# Edit configuration files...

# Reload
/customenchantment reload

# Update existing items
/customenchantment updateitem Player1
```

---

## Tab Completion

Most commands support tab completion for:
- Player names
- Enchantment names (from `CEEnchantMap`)
- Item names (from storage)
- Level values (based on max level)
- Group names
- Page numbers

---

## Error Handling

Commands typically fail silently when:
- Player not found
- No item in hand
- Invalid enchantment/item name
- Invalid level number

Check console for detailed errors when commands don't work as expected.

---

## See Also

- [ARCHITECTURE.md](ARCHITECTURE.md) - System architecture
- [ENCHANTMENT-CONFIG.md](ENCHANTMENT-CONFIG.md) - Enchantment configuration
- [ITEM-SYSTEM.md](ITEM-SYSTEM.md) - Item system documentation
