# Command System

BafFramework provides `AdvancedCommandBuilder` for fluent command tree construction.

## AdvancedCommandBuilder

Package: `com.bafmc.bukkit.command.AdvancedCommandBuilder`

### Basic Usage

```java
import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.Argument;

AdvancedCommandBuilder.builder()
    .plugin(plugin)
    .rootCommand("myplugin")
    .permission("myplugin.use")

    // /myplugin help
    .subCommand("help")
        .commandExecutor((sender, args) -> {
            sender.sendMessage("Help message here");
        })
    .end()

    // /myplugin reload
    .subCommand("reload")
        .permission("myplugin.admin")
        .commandExecutor((sender, args) -> {
            plugin.onReload();
            sender.sendMessage("Config reloaded.");
        })
    .end()

    .build();  // Registers with Bukkit
```

The command name (`"myplugin"`) must be declared in `plugin.yml`.

### Builder Methods

| Method | Description |
|--------|-------------|
| `.builder()` | Create new builder (static) |
| `.plugin(Plugin)` | Set plugin instance (needed for async) |
| `.rootCommand(String)` | Set the root command name |
| `.subCommand(String)` | Add a literal sub-command |
| `.subCommand(String...)` | Add multiple sub-commands at the same level |
| `.subOptionalCommand(String)` | Add a variable argument (wrapped in `<>`) |
| `.commandExecutor(executor)` | Set the command handler for current node |
| `.tabCompleter(completer)` | Set tab completion for current node |
| `.permission(String)` | Set permission for current node |
| `.permission(AbstractPermission)` | Set dynamic permission |
| `.async(boolean)` | Run executor asynchronously |
| `.apply(CommandRegistrar)` | Apply a modular command registrar |
| `.end()` | Return to parent node |
| `.root()` | Return to root node |
| `.build()` | Register with Bukkit |

### Variable Arguments

Use `<>` brackets for variable arguments. These match any input and are available by name in the `Argument` object.

```java
// /myplugin give <player> <amount>
.subCommand("give")
    .subOptionalCommand("<player>")
        .subOptionalCommand("<amount>")
            .commandExecutor((sender, args) -> {
                String playerName = args.get("<player>");
                String amount = args.get("<amount>");
            })
        .end()
    .end()
.end()
```

`ArgumentType.PLAYER` (`"<player>"`) triggers automatic online player tab-completion.

### Async Commands

For commands that perform I/O (database, network), set `.async(true)` to run off the main thread:

```java
.subCommand("lookup")
    .subOptionalCommand("<player>")
        .async(true)
        .commandExecutor((sender, args) -> {
            // Runs on async thread -- safe for database queries
            PlayerData data = database.loadPlayer(args.get("<player>"));
            // Schedule back to main thread for Bukkit API calls
            Bukkit.getScheduler().runTask(plugin, () -> {
                sender.sendMessage("Found: " + data);
            });
        })
    .end()
.end()
```

## Argument

Package: `com.bafmc.bukkit.command.Argument`

Parsed command arguments, accessible by index or by name.

| Method | Description |
|--------|-------------|
| `args.get(int index)` | Get argument by position (0-based) |
| `args.get(String name)` | Get argument by name (e.g., `"<player>"`) |
| `args.getPlayer()` | Get `Player` from `<player>` argument |
| `args.getToEnd(int from)` | Join all args from index to end |
| `args.get(int from, int to)` | Join args in range |
| `args.has(int index)` | Check if index exists |
| `args.has(String name)` | Check if named argument exists |
| `args.size()` | Total argument count |

## CommandRegistrar

Package: `com.bafmc.bukkit.command.CommandRegistrar`

Modularize commands into separate classes.

```java
import com.bafmc.bukkit.command.CommandRegistrar;
import com.bafmc.bukkit.command.AdvancedCommandBuilder;

public class ReloadCommand implements CommandRegistrar {
    @Override
    public void onRegister(AdvancedCommandBuilder builder) {
        builder
            .subCommand("reload")
                .permission("myplugin.admin")
                .commandExecutor((sender, args) -> {
                    // reload logic
                })
            .end();
    }
}
```

Apply it to the builder:

```java
AdvancedCommandBuilder.builder()
    .plugin(plugin)
    .rootCommand("myplugin")
    .apply(new ReloadCommand())
    .apply(new HelpCommand())
    .build();
```

## Full Example

```java
// In module onEnable():
AdvancedCommandBuilder.builder()
    .plugin(getPlugin())
    .rootCommand("shop")
    .permission("shop.use")
    .commandExecutor((sender, args) -> {
        sender.sendMessage("Usage: /shop open | /shop reload");
    })

    .subCommand("open")
        .commandExecutor((sender, args) -> {
            if (sender instanceof Player player) {
                openShopMenu(player);
            }
        })
    .end()

    .subCommand("reload")
        .permission("shop.admin")
        .commandExecutor((sender, args) -> {
            getPlugin().onReload();
            sender.sendMessage("Shop config reloaded.");
        })
    .end()

    .subCommand("give")
        .permission("shop.admin")
        .subOptionalCommand("<player>")
            .subOptionalCommand("<item>")
                .tabCompleter((sender, args) -> List.of("sword", "shield", "potion"))
                .subOptionalCommand("<amount>")
                    .commandExecutor((sender, args) -> {
                        Player target = args.getPlayer();
                        String item = args.get("<item>");
                        String amount = args.get("<amount>", "1");
                        giveItem(target, item, Integer.parseInt(amount));
                    })
                .end()
            .end()
        .end()
    .end()

    .build();
```
