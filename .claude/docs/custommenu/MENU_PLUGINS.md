# Menu Plugins

The Menu Plugin system allows external Bukkit plugins to integrate with CustomMenu, registering their own menus that can be managed through the CustomMenu framework.

## Overview

```
MenuPluginRegister
├── Plugin A → AbstractMenuPlugin A
│   └── menu/ folder with YAML files
├── Plugin B → AbstractMenuPlugin B
│   └── menu/ folder with YAML files
└── BafFramework → DefaultMenuPlugin
    └── CustomMenu/menu/ folder
```

## AbstractMenuPlugin

Base class for menu plugin integrations.

```java
@Getter
public abstract class AbstractMenuPlugin<T extends Plugin> {
    private T plugin;
    private Map<String, MenuData> menuDataMap = new LinkedHashMap<>();

    public AbstractMenuPlugin(T plugin) {
        this.plugin = plugin;
    }

    public File getMenuFolder() {
        return new File(plugin.getDataFolder(), "menu");
    }

    public void registerMenu(File file);
    public MenuData getMenuData(String name);
    public void clearMenu();
    public boolean hasMenu(String menuName);
    public List<String> getMenuNameList();
}
```

### Key Methods

| Method | Description |
|--------|-------------|
| `getPlugin()` | Returns the associated Bukkit plugin |
| `getMenuFolder()` | Returns the folder containing menu YAML files |
| `registerMenu(File)` | Loads a menu from file (recursive for directories) |
| `getMenuData(String)` | Gets a loaded menu by name |
| `clearMenu()` | Clears all loaded menus |
| `hasMenu(String)` | Checks if a menu exists |
| `getMenuNameList()` | Returns list of all menu names |

### Menu Loading

Menus are loaded automatically from the plugin's menu folder:

```java
public void registerMenu(File file) {
    if (file.isDirectory()) {
        // Recursively load subfolders
        for (File subFile : file.listFiles()) {
            registerMenu(subFile);
        }
    } else {
        // Load YAML file as menu
        String menuId = FileUtils.removeExtension(file.getName());
        AdvancedFileConfiguration config = new AdvancedFileConfiguration(file);
        MenuData menuData = config.get(MenuData.class);
        menuData.setId(menuId);
        menuDataMap.put(menuId, menuData);
    }
}
```

## MenuPluginRegister

Global registry for all menu plugins.

```java
public class MenuPluginRegister {
    private static MenuPluginRegister instance;
    private Map<Plugin, AbstractMenuPlugin> map = new LinkedHashMap<>();
    private Map<String, Plugin> pluginMap = new LinkedHashMap<>();

    public static MenuPluginRegister instance();
    public static AbstractMenuPlugin of(Plugin plugin);

    public void registerMenuPlugin(AbstractMenuPlugin menuPlugin);
    public void registerMenuPlugin(Plugin plugin);
    public void unregisterMenuPlugin(Plugin plugin);

    public Plugin getPlugin(String name);
    public AbstractMenuPlugin getMenuPlugin(String name);
    public AbstractMenuPlugin getMenuPlugin(Plugin plugin);

    public boolean hasPlugin(Plugin plugin);
    public boolean hasPlugin(String name);
    public boolean hasMenu(Plugin plugin, String menuName);
    public boolean hasMenu(String pluginName, String menuName);

    public Map<Plugin, AbstractMenuPlugin> getMap();
    public List<String> getPluginNameList();
}
```

## DefaultMenuPlugin

Simple implementation for plugins that just need basic menu loading.

```java
public class DefaultMenuPlugin extends AbstractMenuPlugin<Plugin> {
    public DefaultMenuPlugin(Plugin plugin) {
        super(plugin);
    }
}
```

## Integrating Your Plugin

### Option 1: Simple Registration

For basic menu support, just register your plugin:

```java
public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Register with CustomMenu
        MenuPluginRegister.instance().registerMenuPlugin(this);
    }

    @Override
    public void onDisable() {
        // Unregister from CustomMenu
        MenuPluginRegister.instance().unregisterMenuPlugin(this);
    }
}
```

This creates a `DefaultMenuPlugin` for your plugin, loading menus from:
```
plugins/MyPlugin/menu/
├── shop.yml
├── inventory.yml
└── settings.yml
```

### Option 2: Custom Menu Plugin

For more control, create a custom AbstractMenuPlugin:

```java
public class MyMenuPlugin extends AbstractMenuPlugin<MyPlugin> {

    public MyMenuPlugin(MyPlugin plugin) {
        super(plugin);
    }

    // Custom menu folder location
    @Override
    public File getMenuFolder() {
        return new File(getPlugin().getDataFolder(), "guis");
    }

    // Custom loading behavior
    @Override
    public void registerMenu(File file) {
        // Skip backup files
        if (file.getName().endsWith(".bak")) {
            return;
        }
        super.registerMenu(file);
    }
}
```

Register:
```java
@Override
public void onEnable() {
    MenuPluginRegister.instance().registerMenuPlugin(new MyMenuPlugin(this));
}
```

### Option 3: Programmatic Menu Registration

Register menus programmatically without YAML files:

```java
public class MyMenuPlugin extends AbstractMenuPlugin<MyPlugin> {

    @Override
    public void registerMenu(File file) {
        super.registerMenu(file);

        // Also register programmatic menus
        registerProgrammaticMenus();
    }

    private void registerProgrammaticMenus() {
        // Create menu data programmatically
        MenuData confirmMenu = createConfirmMenu();
        getMenuDataMap().put("confirm", confirmMenu);
    }

    private MenuData createConfirmMenu() {
        // Build MenuData programmatically
        // ...
    }
}
```

## Opening Plugin Menus

### By Plugin Instance

```java
// Check if menu exists
if (MenuPluginRegister.instance().hasMenu(plugin, "shop")) {
    // Open the menu
    MenuOpener.builder()
        .player(player)
        .menuData(plugin, "shop")
        .build();
}
```

### By Plugin Name

```java
// Check if menu exists
if (MenuPluginRegister.instance().hasMenu("MyPlugin", "shop")) {
    // Open the menu
    MenuOpener.builder()
        .player(player)
        .menuData("MyPlugin", "shop")
        .build();
}
```

### Direct MenuData Access

```java
AbstractMenuPlugin menuPlugin = MenuPluginRegister.of(plugin);
if (menuPlugin != null) {
    MenuData menuData = menuPlugin.getMenuData("shop");
    if (menuData != null) {
        MenuOpener.builder()
            .player(player)
            .menuData(menuData)
            .build();
    }
}
```

## Reloading Menus

Reload all menus for a plugin:

```java
// Using MenuManager
MenuManager.instance().reloadMenu(plugin);

// This calls MenuModule.onReload(plugin, menuPlugin) which:
// 1. Clears existing menus: menuPlugin.clearMenu()
// 2. Reloads from folder: menuPlugin.registerMenu(file)
```

## Command Integration

CustomMenu's commands support plugin menus:

```
/3fcustommenu open <player> <plugin> <menu>
```

Tab completion provides:
- Plugin names from `MenuPluginRegister.getPluginNameList()`
- Menu names from plugin's `getMenuNameList()`

## Directory Structure

```
plugins/
├── BafFramework/
│   └── CustomMenu/
│       ├── config.yml
│       ├── message.yml
│       └── menu/
│           └── example.yml
├── MyPlugin/
│   └── menu/
│       ├── shop.yml
│       ├── inventory/
│       │   ├── main.yml
│       │   └── categories.yml
│       └── settings.yml
└── AnotherPlugin/
    └── menu/
        └── admin.yml
```

## API Reference

### Get All Registered Plugins

```java
Map<Plugin, AbstractMenuPlugin> allPlugins = MenuPluginRegister.instance().getMap();
for (Map.Entry<Plugin, AbstractMenuPlugin> entry : allPlugins.entrySet()) {
    Plugin plugin = entry.getKey();
    AbstractMenuPlugin menuPlugin = entry.getValue();
    System.out.println(plugin.getName() + ": " + menuPlugin.getMenuNameList());
}
```

### List All Menus

```java
List<String> pluginNames = MenuPluginRegister.instance().getPluginNameList();
for (String pluginName : pluginNames) {
    AbstractMenuPlugin menuPlugin = MenuPluginRegister.instance().getMenuPlugin(pluginName);
    List<String> menus = menuPlugin.getMenuNameList();
    System.out.println(pluginName + " menus: " + menus);
}
```

### Check Menu Existence

```java
// By plugin instance
boolean exists = MenuPluginRegister.instance().hasMenu(plugin, "shop");

// By plugin name
boolean exists = MenuPluginRegister.instance().hasMenu("MyPlugin", "shop");
```

## Best Practices

1. **Register early** in `onEnable()` to ensure menus are available
2. **Unregister** in `onDisable()` to clean up
3. **Use subfolders** to organize many menus
4. **Provide default menus** by copying resources on first run
5. **Handle missing menus** gracefully with existence checks
6. **Use consistent naming** for menu files (lowercase, hyphenated)
