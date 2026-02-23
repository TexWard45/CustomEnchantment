# CustomEnchantment - Plugin Summary

**Type:** Bukkit Plugin (Paper/Leaf 1.21.1) | **Java:** 21 | **Framework:** BafFramework
**Classes:** 541 | **Modules:** 16

## What It Does

Custom enchantment system for Minecraft. Adds config-driven enchantments with
conditions (when to trigger) and effects (what happens). Supports custom items
(artifacts, outfits, sigils, gems), player stat expansions, and menu-based UIs.

## Architecture

Three-phase initialization:
1. **Phase 1 - Hooks:** Register ConditionHook/EffectHook strategies (EnchantModule, AttributeModule, FilterModule, ExecuteModule, FeatureModule)
2. **Phase 2 - Config:** Load YAML configs that reference registered hooks (ConfigModule)
3. **Phase 3 - Features:** Start listeners, commands, menus, tasks (remaining 10 modules)

## Module Overview

| Module | Classes | Purpose |
|--------|---------|---------|
| [FeatureModule](feature.md) | 5 | Item strategy registration (CustomEnchantmentItem) |
| [EnchantModule](enchant.md) | 141 | 28 conditions + 77 effects hook registration |
| [AttributeModule](attribute.md) | 4 | Custom attribute types and attribute map registration |
| [FilterModule](filter.md) | 3 | Item/entity filtering with FilterRegister |
| [ExecuteModule](execute.md) | 3 | Give/use item execute hooks |
| [ConfigModule](config.md) | 18 | YAML config loading for all 15+ config classes |
| [CustomMenuModule](custommenu.md) | 4 | External CustomMenu plugin integration |
| [CommandModule](command.md) | 31 | 9 root commands, 20+ subcommands |
| [ItemModule](item.md) | 129 | 22 item factories for all CE item types |
| [PlayerModule](player.md) | 41 | 14 player expansions, 6 mining strategies, PlayerStorage |
| [TaskModule](task.md) | 24 | 16 sync + 6 async scheduled tasks |
| [GuardModule](guard.md) | 5 | Guard mob system (spawning, targeting, management) |
| [DatabaseModule](database.md) | 2 | SQLite persistence for player data |
| [MenuModule](menu.md) | 76 | 6 menu strategies, 12 anvil handlers |
| [PlaceholderModule](placeholder.md) | 2 | PlaceholderAPI integration |
| [ListenerModule](listener.md) | 15 | 9 core + 5 conditional event listeners (50+ events) |

## Key Data Flows

**Event Processing:** Bukkit Event -> ListenerModule -> CECaller -> EnchantModule (conditions + effects) -> PlayerModule (CEPlayer) -> TaskModule (scheduled updates)

**Item Creation:** ConfigModule (YAML) -> CEEnchantMap/CEItemConfig -> ItemModule (CEItemFactory) -> CEItem/CEWeapon instances -> MenuModule (display/trade) -> Player inventory

**Player Data:** PlayerListener (join) -> PlayerModule (create CEPlayer) -> PlayerExpansions (14 systems) -> TaskModule (periodic updates) -> DatabaseModule (persist via SaveTask)

## External Dependencies

**Required:** BafFramework (compileOnly)
**Optional:** CustomMenu, StackMob, Vouchers, ItemDropManager, mcMMO, PlaceholderAPI, CustomShop, DamageIndicator, CustomFarm, Citizens
