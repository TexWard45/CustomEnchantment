# Menu System

The menu system is the core of CustomMenu, providing the infrastructure for creating, registering, and displaying inventory-based GUIs.

## Core Components

### AbstractMenu

The base class for all menu types. Extends `AbstractStrategy` for type-based instantiation.

```java
public abstract class AbstractMenu<M extends MenuData, E extends ExtraData> extends AbstractStrategy {
    protected Map<Integer, AbstractItem> itemSlotMap;
    protected ItemRegister itemRegister;
    protected Inventory inventory;
    protected Player owner;
    protected PlayerData ownerData;
    protected M menuData;
    protected E extraData;
}
```

#### Key Methods

| Method | Description |
|--------|-------------|
| `getType()` | Returns the menu type identifier (abstract) |
| `registerItems()` | Registers item types for this menu (abstract) |
| `setupMenu(Player, MenuData)` | Initializes the menu for a player |
| `setupInventory()` | Creates the Bukkit inventory |
| `setupItems()` | Places all items in the inventory |
| `openInventory()` | Opens the inventory for the player |
| `reopenInventory()` | Refreshes and reopens the inventory |
| `handleClick(ClickData)` | Processes click events on menu items |
| `handlePlayerInventoryClick(ClickData)` | Processes click events on player inventory (default: no-op) |
| `handleClose()` | Called when menu is closed |

#### Lifecycle

```
1. Menu instantiated via MenuRegister.getMenu(type)
2. setExtraData(extraData) - optional extra data
3. setupMenu(player, menuData)
   ├── setOwner(player)
   ├── setMenuData(menuData)
   ├── setupInventory() - creates Bukkit inventory
   └── setupItems() - places items
4. openInventory() - shows to player
5. handleClick(clickData) - on user interaction
6. handleClose() - on inventory close
```

### MenuRegister

Registry for menu types. Uses Strategy Pattern for type-based instantiation.

```java
public class MenuRegister extends StrategyRegister<AbstractMenu> {
    private static MenuRegister instance;

    public static MenuRegister instance();

    public void register();  // Registers built-in menus
    public AbstractMenu getMenu(String type);  // Creates new instance by type
}
```

#### Registering Menu Types

```java
// In MenuRegister.register() or custom initialization
MenuRegister.instance().registerStrategy(DefaultMenu.class);
MenuRegister.instance().registerStrategy(PageMenu.class);
MenuRegister.instance().registerStrategy(CustomShopMenu.class);
```

#### Getting a Menu Instance

```java
// Creates a new instance each time
AbstractMenu menu = MenuRegister.instance().getMenu("default");
AbstractMenu pageMenu = MenuRegister.instance().getMenu("example-page");
```

### MenuManager

High-level menu operations and plugin coordination.

```java
public class MenuManager {
    private static MenuManager instance;

    public static MenuManager instance();

    public boolean hasMenu(Plugin plugin, String menuName);
    public boolean hasMenu(String pluginName, String menuName);
    public void reloadMenu(Plugin plugin);
}
```

#### Usage Examples

```java
// Check if menu exists
if (MenuManager.instance().hasMenu("BafFramework", "shop")) {
    // Menu exists
}

// Reload all menus for a plugin
MenuManager.instance().reloadMenu(myPlugin);
```

### MenuOpener

Builder for opening menus. Handles async loading.

```java
public class MenuOpener {
    private Player player;
    private MenuData menuData;
    private ExtraData extraData;
    private boolean async = true;  // Default async

    public static MenuOpener builder();

    public MenuOpener player(Player player);
    public MenuOpener menuData(MenuData menuData);
    public MenuOpener menuData(String pluginName, String menuName);
    public MenuOpener menuData(Plugin plugin, String menuName);
    public <T extends ExtraData> MenuOpener extraData(T extraData);
    public MenuOpener async(boolean async);
    public void build();  // Opens the menu
}
```

#### Opening Menus

```java
// Open by plugin and menu name
MenuOpener.builder()
    .player(player)
    .menuData("BafFramework", "example")
    .build();

// Open with extra data
MenuOpener.builder()
    .player(player)
    .menuData(plugin, "shop")
    .extraData(new ShopExtraData(shopId))
    .async(true)
    .build();

// Open synchronously (for already-loaded data)
MenuOpener.builder()
    .player(player)
    .menuData(menuData)
    .async(false)
    .build();
```

## Built-in Menu Types

### DefaultMenu

Basic menu with standard item support.

```java
public class DefaultMenu<M extends MenuData, E extends ExtraData> extends AbstractMenu<M, E> {
    @Override
    public String getType() {
        return "default";
    }

    @Override
    public void registerItems() {
        registerItem(DefaultItem.class);
    }
}
```

**YAML Configuration:**
```yaml
type: 'default'
title: '&8My Menu'
row: 3
items:
  # ... item definitions
```

### PageMenu

Menu with pagination support. See [Page Menus](PAGE_MENUS.md) for details.

```java
public abstract class PageMenu<M extends MenuData, E extends ExtraData> extends AbstractMenu<M, E> {
    protected int page = 1;
    protected int maxPage = 1;

    public void nextPage();
    public void previousPage();
}
```

## Creating Custom Menus

### Step 1: Extend AbstractMenu

```java
public class ShopMenu extends AbstractMenu<MenuData, ShopExtraData> {

    @Override
    public String getType() {
        return "shop";
    }

    @Override
    public void registerItems() {
        // Register items this menu can use
        registerItem(DefaultItem.class);
        registerItem(BuyItem.class);
        registerItem(SellItem.class);
    }

    // Optional: Override for custom behavior
    @Override
    public void setupMenu() {
        // Pre-setup logic
        super.setupMenu();
        // Post-setup logic
    }

    @Override
    public void handleClose() {
        // Cleanup when menu closes
        getExtraData().saveCart();
    }
}
```

### Step 2: Register the Menu

```java
// In your plugin's onEnable
MenuRegister.instance().registerStrategy(ShopMenu.class);
```

### Step 3: Create YAML Configuration

```yaml
# plugins/YourPlugin/menu/shop.yml
type: 'shop'
title: '&6Shop - {category}'
row: 6
data:
  category: 'weapons'
items:
  background:
    item:
      type: STAINED_GLASS_PANE
      damage: 15
      display: ' '
    slot: 0-8,45-53
  # ... more items
```

### Step 4: Open the Menu

```java
MenuOpener.builder()
    .player(player)
    .menuData(plugin, "shop")
    .extraData(new ShopExtraData(categoryId))
    .build();
```

## Menu Events

### Click Handling

Click events are routed through `AbstractMenu.handleClick(ClickData)`:

```java
public void handleClick(ClickData data) {
    // Find the item at clicked slot
    AbstractItem abstractItem = itemSlotMap.get(data.getClickedSlot());
    if (abstractItem != null) {
        abstractItem.handleClick(data);
    }
}
```

### Close Handling

Override `handleClose()` for cleanup:

```java
@Override
public void handleClose() {
    // Save data, cleanup resources
    savePlayerProgress();
}
```

### Player Inventory Click Handling

By default, clicks on the player's bottom inventory while a menu is open are cancelled but not handled. Override `handlePlayerInventoryClick(ClickData)` to implement custom behavior for player inventory interactions.

**Common use cases:**
- Storage menus - deposit items from player inventory
- Shop menus - sell items from player inventory
- Crafting menus - transfer materials from player inventory

```java
@Override
public void handlePlayerInventoryClick(ClickData data) {
    // data.isPlayerInventory() is always true
    // data.getClickedSlot() is relative to player inventory (0-35)

    ItemStack clickedItem = data.getEvent().getCurrentItem();
    Player player = data.getPlayer();

    // Example: Deposit item into storage
    if (data.getEvent().getClick() == ClickType.LEFT) {
        depositItemToStorage(player, clickedItem);
        player.getInventory().setItem(data.getClickedSlot(), null);
        reopenInventory();
    }

    // Example: Handle shift-click for bulk transfer
    if (data.getEvent().isShiftClick()) {
        depositAllSimilarItems(player, clickedItem.getType());
        reopenInventory();
    }
}
```

**Slot mapping:**
- Player inventory slots 0-35
  - 0-8: Hotbar (bottom row)
  - 9-35: Main inventory (3 rows)
- `data.getClickedSlot()` returns the **relative slot** (0-35), not raw slot
- `data.isPlayerInventory()` is `true` for player inventory clicks, `false` for menu clicks

**Example: Storage Menu**

```java
public class StorageMenu extends AbstractMenu<MenuData, ExtraData> {

    @Override
    public void handlePlayerInventoryClick(ClickData data) {
        ItemStack item = data.getEvent().getCurrentItem();

        // Validate item type
        if (!isAcceptableItem(item)) {
            data.getPlayer().sendMessage("§cThis storage only accepts certain items!");
            return;
        }

        // Add to storage
        boolean success = addToStorage(item);

        if (success) {
            // Remove from player inventory
            data.getPlayer().getInventory().setItem(data.getClickedSlot(), null);
            data.getPlayer().sendMessage("§aItem deposited!");

            // Refresh display
            reopenInventory();
        } else {
            data.getPlayer().sendMessage("§cStorage is full!");
        }
    }
}
```

**Note:** If you don't override `handlePlayerInventoryClick()`, player inventory clicks will be cancelled but won't trigger any action (default no-op behavior).

## Async Operations

### Opening Async

MenuOpener opens menus asynchronously by default:

```java
// Setup runs async, then opens on main thread
Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
    menu.setExtraData(extraData);
    menu.setupMenu(player, menuData);

    Bukkit.getScheduler().runTask(plugin, menu::openInventory);
});
```

### Reopening Async

```java
// Reopen with async setup
menu.reopenInventoryAsync();

// This is equivalent to:
Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
    setupMenu();
    Bukkit.getScheduler().runTask(plugin, this::openInventory);
});
```

## Best Practices

1. **Use async opening** for menus that load data from databases
2. **Register all item types** your menu uses in `registerItems()`
3. **Override handleClose()** to save any pending changes
4. **Use ExtraData** for menu-specific state
5. **Keep menu types focused** - one menu type per purpose
