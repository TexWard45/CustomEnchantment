# Commands

CustomMenu provides admin commands for managing menus at runtime.

## Command Overview

Base command: `/3fcustommenu` (alias: `/3fcm`)

| Command | Permission | Description |
|---------|------------|-------------|
| `/3fcustommenu` | `custommenu.command` | Show help |
| `/3fcustommenu help` | `custommenu.command.help` | Show help |
| `/3fcustommenu reload` | `custommenu.command.reload` | Reload all configs |
| `/3fcustommenu open <player> <plugin> <menu>` | `custommenu.command.open` | Open menu for player |

## Command Registration

Commands are registered in `AdminCommandRegister`:

```java
public class AdminCommandRegister {
    public static void register(CustomMenu plugin, AdvancedCommandBuilder builder) {
        HelpCommand helpExecutor = new HelpCommand(plugin);
        ReloadCommand reloadExecutor = new ReloadCommand(plugin);
        OpenMenuCommand openExecutor = new OpenMenuCommand(plugin);

        MenuPluginTabCompleter menuPluginTabCompleter = new MenuPluginTabCompleter();
        MenuTabCompleter menuTabCompleter = new MenuTabCompleter();

        builder
            .permission("custommenu.command")
            .commandExecutor(helpExecutor)
            .subCommand("help")
                .permission("custommenu.command.help")
                .commandExecutor(helpExecutor)
            .end()
            .subCommand("reload")
                .permission("custommenu.command.reload")
                .commandExecutor(reloadExecutor)
            .end()
            .subCommand("open")
                .permission("custommenu.command.open")
                .subOptionalCommand(ArgumentType.PLAYER)
                    .subOptionalCommand("plugin")
                        .tabCompleter(menuPluginTabCompleter)
                        .subOptionalCommand("menu")
                            .tabCompleter(menuTabCompleter)
                            .commandExecutor(openExecutor)
                        .end()
                    .end()
                .end()
            .end()
        ;

        builder.build();
    }
}
```

## Help Command

Shows available commands to the player.

```java
public class HelpCommand implements AdvancedCommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Argument argument) {
        ConfigModule.instance().getMessageConfig()
            .sendMessage(commandSender, "command.help");
        return true;
    }
}
```

**Message Configuration:**
```yaml
command:
  help:
    - "&6======== &eCustomMenu Commands &6========"
    - "&f/custommenu reload &e- Reload configuration"
    - "&f/custommenu open <player> <plugin> <menu> &e- Open menu"
```

## Reload Command

Reloads all CustomMenu configurations.

```java
public class ReloadCommand implements AdvancedCommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Argument argument) {
        plugin.onReload();
        ConfigModule.instance().getMessageConfig()
            .sendMessage(commandSender, "command.reload");
        return false;
    }
}
```

**What gets reloaded:**
- `config.yml` - Main configuration
- `message.yml` - Messages
- All menu YAML files for all registered plugins

**Message Configuration:**
```yaml
command:
  reload: '&a[!] Configuration reloaded!'
```

## Open Menu Command

Opens a menu for a specific player.

```java
public class OpenMenuCommand implements AdvancedCommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Argument argument) {
        Player player = argument.getPlayer();
        if (player == null) {
            ConfigModule.instance().getMessageConfig()
                .sendMessage(commandSender, "command.player-not-found",
                    PlaceholderBuilder.builder()
                        .put("{player}", argument.get("player"))
                        .build());
            return true;
        }

        String pluginName = argument.get("plugin");
        if (pluginName == null) {
            // Handle missing plugin
            return true;
        }

        String menuName = argument.get("menu");
        if (menuName == null) {
            // Handle missing menu
            return true;
        }

        MenuOpener.builder()
            .player(player)
            .menuData(pluginName, menuName)
            .async(true)
            .build();

        return true;
    }
}
```

**Usage:**
```
/3fcustommenu open Notch BafFramework example
/3fcustommenu open @p MyPlugin shop
```

**Message Configuration:**
```yaml
command:
  player-not-found: '&c[!] Player {player} not found!'
  plugin-not-found: '&c[!] Plugin {plugin} not found!'
  menu-not-found: '&c[!] Menu {menu} not found!'
```

## Tab Completers

### MenuPluginTabCompleter

Provides plugin name suggestions:

```java
public class MenuPluginTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
                                       String label, String[] args) {
        return MenuPluginRegister.instance().getPluginNameList();
    }
}
```

### MenuTabCompleter

Provides menu name suggestions based on selected plugin:

```java
public class MenuTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
                                       String label, String[] args) {
        // Get plugin name from previous argument
        String pluginName = args[args.length - 2];
        AbstractMenuPlugin menuPlugin =
            MenuPluginRegister.instance().getMenuPlugin(pluginName);

        if (menuPlugin == null) {
            return Collections.emptyList();
        }

        return menuPlugin.getMenuNameList();
    }
}
```

### MenuTabPluginCompleter

Alternative completer for plugin-specific menus:

```java
public class MenuTabPluginCompleter implements TabCompleter {
    // Implementation for specific plugin contexts
}
```

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `custommenu.command` | Base command access | op |
| `custommenu.command.help` | Help command | op |
| `custommenu.command.reload` | Reload command | op |
| `custommenu.command.open` | Open menu command | op |

## Adding Custom Commands

### Extend AdminCommandRegister

```java
// In your plugin's onEnable
AdminCommandRegister.register(customMenu, builder);

// Add your commands
builder
    .subCommand("custom")
        .permission("custommenu.command.custom")
        .commandExecutor(new CustomCommand())
    .end()
;
```

### Create Separate Command

```java
public class MyMenuCommand implements AdvancedCommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Argument argument) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only!");
            return true;
        }

        Player player = (Player) sender;
        String menuName = argument.get("menu");

        MenuOpener.builder()
            .player(player)
            .menuData("MyPlugin", menuName)
            .build();

        return true;
    }
}

// Register
AdvancedCommandBuilder.builder()
    .plugin(myPlugin)
    .rootCommand("menu")
    .permission("myplugin.menu")
    .subOptionalCommand("menu")
        .tabCompleter(new MyMenuTabCompleter())
        .commandExecutor(new MyMenuCommand())
    .end()
    .build();
```

## Console Support

Commands can be executed from console:

```
> 3fcustommenu reload
[CustomMenu] Configuration reloaded!

> 3fcustommenu open Notch BafFramework example
[CustomMenu] Opening menu for Notch
```

## Error Handling

Commands provide clear error messages:

```yaml
command:
  player-not-found: '&c[!] Player {player} not found!'
  plugin-not-found: '&c[!] Plugin {plugin} not found!'
  menu-not-found: '&c[!] Menu {menu} not found!'
```

Example error flow:
```
> /3fcustommenu open InvalidPlayer BafFramework example
[!] Player InvalidPlayer not found!

> /3fcustommenu open Notch InvalidPlugin example
[!] Plugin InvalidPlugin not found!

> /3fcustommenu open Notch BafFramework invalid-menu
[!] Menu invalid-menu not found!
```

## Best Practices

1. **Use tab completion** for better UX
2. **Validate all inputs** before processing
3. **Provide clear error messages** with context
4. **Check permissions** at each level
5. **Support console** when appropriate
6. **Use async operations** for menu opening
