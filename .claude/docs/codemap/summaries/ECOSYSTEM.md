# CustomEnchantment - Ecosystem Context

## Position in Server Stack

```
Minecraft Server (Paper/Leaf 1.21.1)
  |-- BafFramework (core framework - module system, config, commands)
  |     |-- CustomEnchantment (this plugin - enchants, items, player stats)
  |     |-- CustomMenu (optional - external menu integration)
  |     |-- CustomShop (optional - shop integration)
  |     |-- CustomFarm (optional - farm integration)
  |-- PlaceholderAPI (optional - placeholder support)
  |-- mcMMO (optional - MMO skill integration)
  |-- Citizens (optional - NPC integration)
  |-- StackMob (optional - mob stacking compatibility)
```

## Plugin Scope

CustomEnchantment adds a YAML-driven enchantment system to Minecraft items.
Enchantments are defined by conditions (when they trigger) and effects (what they do).
The plugin also manages custom item types, player stat tracking, and inventory menus.

## Key Integration Points

- **BafFramework**: Module lifecycle, config system, command builder, hook registry
- **Bukkit Events**: 50+ event handlers across 15 listeners
- **YAML Config**: 15+ config files defining enchantments, items, menus
- **SQLite Database**: Player data persistence
- **PlaceholderAPI**: Player stat placeholders for other plugins
