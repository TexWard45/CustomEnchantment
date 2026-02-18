# Command Migration Guide

## Overview

Each interactive menu needs a command to open it. During migration, we'll create NEW commands using the new MenuOpener API while keeping legacy commands working.

## Old Pattern (Legacy CustomMenu API)

```java
// In CommandModule.onEnable()
AdvancedCommandBuilder tinkererBuilder = AdvancedCommandBuilder.builder()
    .plugin(getPlugin())
    .rootCommand("tinkerer");

tinkererBuilder.commandExecutor(new AdvancedCommandExecutor() {
    public boolean onCommand(CommandSender sender, Argument arg) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        // OLD API - opens menu via CustomMenuAPI
        CustomMenuAPI.getCPlayer(player).openCustomMenu("tinkerer", true);
        return true;
    }
}).end();

tinkererBuilder.build();
```

## New Pattern (BafFramework CustomMenu API)

```java
// Create dedicated command class
public class TinkererCommand implements AdvancedCommandExecutor {
    private final CustomEnchantment plugin;

    public TinkererCommand(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Argument arg) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // Create ExtraData with necessary state
        TinkererExtraData extraData = new TinkererExtraData();
        extraData.setSettings(TinkererCustomMenu.getSettings());

        // NEW API - open menu via MenuOpener builder
        MenuOpener.builder()
                .player(player)
                .menuData(plugin, TinkererCustomMenu.MENU_NAME)  // Plugin + menu name
                .extraData(extraData)  // Runtime state
                .async(false)  // Sync for immediate response
                .build();

        return true;
    }
}

// In CommandModule.onEnable()
AdvancedCommandBuilder.builder()
    .plugin(getPlugin())
    .rootCommand("tinkerer")
    .commandExecutor(new TinkererCommand(getPlugin()))
    .end()
    .build();
```

## Migration Strategy

### During Development (Phases 1-6)

Register BOTH commands:
- `/tinkerer` - Old command (legacy API) - keep working
- `/tinkerer-new` - New command (new API) - for testing

### After All Menus Migrated (Phase 7)

Replace old commands with new ones:
- Remove old command registration
- Update `/tinkerer` to use new API

## Command Migration Checklist

For each menu, create:

- [ ] New `*Command.java` class implementing `AdvancedCommandExecutor`
- [ ] Proper `ExtraData` initialization with required settings
- [ ] `MenuOpener.builder()` call with correct parameters
- [ ] Register command in `CommandModule.onEnable()`
- [ ] Test command opens menu correctly
- [ ] Test player-inventory interaction works

## Per-Menu Command Details

### Phase 1: Tinkerer

**Command:** `/tinkerer` (eventually replaces old)
**ExtraData:** `TinkererExtraData` with `TinkererSettings`
**Menu Name:** `"tinkerer"`

```java
TinkererExtraData extraData = new TinkererExtraData();
extraData.setSettings(TinkererCustomMenu.getSettings());

MenuOpener.builder()
    .player(player)
    .menuData(plugin, "tinkerer")
    .extraData(extraData)
    .async(false)
    .build();
```

### Phase 2: Book Craft

**Command:** `/bookcraft`
**ExtraData:** `BookCraftExtraData` with `FastCraft` instance
**Menu Name:** `"book-craft"`

```java
BookCraftExtraData extraData = new BookCraftExtraData();
extraData.setSettings(BookCraftCustomMenu.getSettings());

MenuOpener.builder()
    .player(player)
    .menuData(plugin, "book-craft")
    .extraData(extraData)
    .async(false)
    .build();
```

### Phase 3: CE Anvil

**Command:** `/ceanvil`
**ExtraData:** `CEAnvilExtraData` (initially empty)
**Menu Name:** `"ce-anvil"`

```java
CEAnvilExtraData extraData = new CEAnvilExtraData();

MenuOpener.builder()
    .player(player)
    .menuData(plugin, "ce-anvil")
    .extraData(extraData)
    .async(false)
    .build();
```

### Phase 4: Book Upgrade

**Command:** `/bookupgrade`
**ExtraData:** `BookUpgradeExtraData`
**Menu Name:** `"book-upgrade"`

```java
BookUpgradeExtraData extraData = new BookUpgradeExtraData();

MenuOpener.builder()
    .player(player)
    .menuData(plugin, "book-upgrade")
    .extraData(extraData)
    .async(false)
    .build();
```

### Phase 4: Artifact Upgrade

**Command:** `/artifactupgrade`
**ExtraData:** `ArtifactUpgradeExtraData`
**Menu Name:** `"artifact-upgrade"`

```java
ArtifactUpgradeExtraData extraData = new ArtifactUpgradeExtraData();

MenuOpener.builder()
    .player(player)
    .menuData(plugin, "artifact-upgrade")
    .extraData(extraData)
    .async(false)
    .build();
```

### Phase 5: Equipment

**Command:** `/equipment`
**ExtraData:** `EquipmentExtraData` (with initialization)
**Menu Name:** `"equipment"`

```java
EquipmentExtraData extraData = new EquipmentExtraData();
// Initialize maps for cooldowns, etc.

MenuOpener.builder()
    .player(player)
    .menuData(plugin, "equipment")
    .extraData(extraData)
    .async(false)
    .build();
```

## Testing Commands

1. **Register command** - Add to `CommandModule.onEnable()`
2. **Reload plugin** - `/reload confirm` or restart server
3. **Test command** - Run `/tinkerer` (or new command name)
4. **Verify menu opens** - Check GUI displays correctly
5. **Test interactions:**
   - Click items in menu
   - Click items in player inventory (should add to menu)
   - Close menu (should return items)
   - Confirm action (should execute rewards)

## Common Issues

### Menu doesn't open

- Check `MenuRegister.instance().registerStrategy()` was called
- Check menu YAML file exists and has correct `type` field
- Check ExtraData is properly initialized

### Player-inventory clicks don't work

- Verify `handleClick()` override checks `clickedInventory`
- Check `data.getClickedInventory() == player.getInventory()`

### Items not returned on close

- Verify `handleClose()` is overridden
- Check `returnItems()` is called in `handleClose()`

## Phase 7: Final Cleanup

In Phase 7, replace all old commands:

```java
// REMOVE old inline commands
// AdvancedCommandBuilder tinkererBuilder = ...
// CustomMenuAPI.getCPlayer(player).openCustomMenu("tinkerer", true);

// KEEP new command classes
AdvancedCommandBuilder.builder()
    .plugin(getPlugin())
    .rootCommand("tinkerer")
    .commandExecutor(new TinkererCommand(getPlugin()))
    .end()
    .build();
```

Update `CommandModule.java` imports:
- Remove `import com.bafmc.custommenu.api.CustomMenuAPI;`
- Add `import com.bafmc.bukkit.bafframework.custommenu.menu.builder.MenuOpener;`
