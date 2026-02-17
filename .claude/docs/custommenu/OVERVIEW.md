# CustomMenu Framework Overview

CustomMenu is a comprehensive GUI menu framework for Bukkit plugins built on top of BafFramework. It provides a flexible, configuration-driven approach to creating interactive inventory menus with support for pagination, conditions, executes, and plugin extensions.

## Architecture

CustomMenu follows a modular architecture that separates concerns into distinct modules:

```
CustomMenu (BafChildPlugin)
├── FileStorage          - File path management
├── CoreModule           - Commands and scheduled tasks
├── ConfigModule         - Configuration loading
├── PlayerModule         - Player data and session management
└── MenuModule           - Menu system and plugin registry
```

## Module Overview

### FileStorage

Manages file paths for the CustomMenu plugin:
- `config.yml` - Main configuration
- `message.yml` - Localized messages
- `menu/` - Menu definition folder

```java
FileStorage fileStorage = FileStorage.instance();
File configFile = fileStorage.getConfigFile();
File menuFolder = fileStorage.getMenuFolder();
```

### CoreModule

Handles core functionality:
- **Command Registration**: Registers `/3fcustommenu` admin commands
- **Auto-Save Task**: Runs every 5 minutes to save player data

```java
// Commands registered via AdvancedCommandBuilder
AdvancedCommandBuilder.builder()
    .plugin(BafFramework.instance())
    .rootCommand("3fcustommenu")
    // ... command tree
```

### ConfigModule

Loads and provides access to configuration:
- `MainConfig` - Main plugin settings
- `MessageConfig` - Localized message strings

```java
ConfigModule.instance().getMainConfig();
ConfigModule.instance().getMessageConfig();
```

### PlayerModule

Manages player-specific data:
- `PlayerStorage` - Persistence layer for player data
- `PlayerManager` - Runtime player data management
- `PlayerListener` - Join/quit event handling

```java
PlayerData playerData = PlayerManager.instance().getPlayerData(player);
AbstractMenu currentMenu = playerData.getMenu();
```

### MenuModule

The heart of CustomMenu - manages menus and menu plugins:
- **MenuRegister**: Registry for menu types (default, page menus, custom)
- **MenuManager**: Menu lookup and reload operations
- **MenuPluginRegister**: Registry for external plugin menu integrations
- **InventoryListener**: Handles click and close events

```java
// Get a menu by type
AbstractMenu menu = MenuRegister.instance().getMenu("default");

// Check if a menu exists
boolean exists = MenuManager.instance().hasMenu(plugin, "shop");

// Reload menus for a plugin
MenuManager.instance().reloadMenu(plugin);
```

## Plugin Lifecycle

```java
public class CustomMenu extends BafChildPlugin {
    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();  // Calls registerModules()
    }

    public void registerModules() {
        registerModule(new FileStorage(this));
        registerModule(new CoreModule(this));
        registerModule(new ConfigModule(this));
        registerModule(new PlayerModule(this));
        registerModule(new MenuModule(this));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        super.onSave();
    }
}
```

## Key Concepts

### Strategy Pattern

CustomMenu uses the Strategy Pattern extensively:
- **AbstractMenu** extends `AbstractStrategy` - each menu type is a strategy
- **AbstractItem** extends `AbstractStrategy` - each item type is a strategy
- **MenuRegister** and **ItemRegister** extend `StrategyRegister`

This allows for:
- Dynamic menu/item type registration
- Runtime instantiation by type string
- Easy extension with custom types

### Data-Driven Configuration

Menus are defined in YAML files and loaded into data classes:
- `MenuData` - Menu configuration (title, rows, items)
- `ItemData` - Item configuration (material, slots, conditions, executes)

### Plugin Extension System

External plugins can register their own menus:

```java
// Register a plugin with CustomMenu
MenuPluginRegister.instance().registerMenuPlugin(myPlugin);

// Or with custom menu plugin implementation
MenuPluginRegister.instance().registerMenuPlugin(new MyCustomMenuPlugin(myPlugin));
```

## Directory Structure

```
plugins/BafFramework/CustomMenu/
├── config.yml           # Main configuration
├── message.yml          # Localized messages
└── menu/                # Menu definitions
    ├── example.yml      # Example menu
    ├── shop.yml         # Custom menus
    └── ...
```

## Quick Start

### Opening a Menu

```java
// Using MenuOpener builder
MenuOpener.builder()
    .player(player)
    .menuData("BafFramework", "example")  // plugin name, menu name
    .async(true)
    .build();

// Or with direct MenuData reference
MenuData menuData = MenuPluginRegister.of(plugin).getMenuData("example");
MenuOpener.builder()
    .player(player)
    .menuData(menuData)
    .build();
```

### Creating a Custom Menu Type

```java
public class ShopMenu extends AbstractMenu<MenuData, ExtraData> {
    @Override
    public String getType() {
        return "shop";
    }

    @Override
    public void registerItems() {
        registerItem(DefaultItem.class);
        registerItem(BuyItem.class);  // Custom item
    }
}

// Register in MenuRegister
MenuRegister.instance().registerStrategy(ShopMenu.class);
```

## Related Documentation

- [Menu System](MENU_SYSTEM.md) - AbstractMenu, MenuRegister, MenuManager
- [Menu Items](MENU_ITEMS.md) - AbstractItem and built-in items
- [Menu Data](MENU_DATA.md) - Data structures (MenuData, ItemData, etc.)
- [Page Menus](PAGE_MENUS.md) - Pagination system
- [Menu Plugins](MENU_PLUGINS.md) - Extending with plugins
- [Configuration](CONFIGURATION.md) - YAML configuration format
- [Commands](COMMANDS.md) - Admin commands
- [Creating Menus Tutorial](TUTORIAL_MENUS.md)
- [Creating Items Tutorial](TUTORIAL_ITEMS.md)
- [Advanced Patterns](ADVANCED_PATTERNS.md) - Handler strategy, template items, state machines, paginated lists
- [Migration Guide](MIGRATION_GUIDE.md) - Migrating from legacy CustomMenu to BafFramework
- [FastCraft Refactoring](FASTCRAFT_REFACTORING.md) - FastCraft mode implementation patterns
