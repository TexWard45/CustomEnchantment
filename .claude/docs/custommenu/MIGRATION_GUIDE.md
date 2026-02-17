# CustomMenu Migration Guide

This guide covers migrating from legacy CustomMenu API to BafFramework CustomMenu system, based on real-world migration experience.

## Table of Contents

- [Registration Issues](#registration-issues)
- [YAML Format Differences](#yaml-format-differences)
- [Player Inventory vs Menu GUI Clicks](#player-inventory-vs-menu-gui-clicks)
- [Dynamic Item Updates](#dynamic-item-updates)
- [Common Pitfalls](#common-pitfalls)
- [Migration Checklist](#migration-checklist)

---

## Registration Issues

### Problem: Menu Won't Open

**Symptom:** Menu creation succeeds but inventory doesn't open for the player.

**Root Cause:** Plugin not registered with `MenuPluginRegister`, so YAML files aren't loaded.

**Solution:**

```java
public class MenuModule extends PluginModule<YourPlugin> {

    @Override
    public void onEnable() {
        // CRITICAL: Register plugin folder with MenuPluginRegister
        registerFolder();

        // Register menu classes
        registerMenu();

        // Load menus from folder
        onReload();
    }

    private void registerFolder() {
        // Option 1: Use default menu folder
        MenuPluginRegister.instance().registerMenuPlugin(getPlugin());

        // Option 2: Use custom folder (e.g., menu-new instead of menu)
        AbstractMenuPlugin menuPlugin = new AbstractMenuPlugin<YourPlugin>(getPlugin()) {
            @Override
            public File getMenuFolder() {
                return new File(getPlugin().getDataFolder(), "menu-new");
            }
        };
        MenuPluginRegister.instance().registerMenuPlugin(menuPlugin);
    }

    private void registerMenu() {
        MenuRegister.instance().registerStrategy(YourCustomMenu.class);
    }

    @Override
    public void onReload() {
        // Get menu plugin and reload YAML files
        Map<Plugin, AbstractMenuPlugin> menuMap = MenuPluginRegister.instance().getMap();
        AbstractMenuPlugin menuPlugin = menuMap.get(getPlugin());

        if (menuPlugin != null) {
            menuPlugin.clearMenu();
            menuPlugin.registerMenu(menuPlugin.getMenuFolder());
        }
    }
}
```

**Key Points:**
- Must call `registerMenuPlugin()` before opening menus
- Must call `onReload()` to actually load YAML files
- Menu YAML must have matching `type` field

---

## YAML Format Differences

### Legacy Format

```yaml
settings:
  title: '&9&lMenu Title'
  rows: 6
  sound: ENTITY_EXPERIENCE_ORB_PICKUP
  actions:
    '1':
      inventory-type: CHEST
      slots: 0-53
      cancel-drag: true
      cancel-click-type:
      - LEFT
      - RIGHT

# Character-grid layout
layout:
- 'aooobOOOa'
- 'oooobOOOO'

items:
  border:
    item:
      type: WHITE_STAINED_GLASS_PANE
    slots: 'b'  # References layout character
```

### New BafFramework Format

```yaml
type: 'your-menu-type'  # CRITICAL: Must match YourMenu.getType()
title: '&9&lMenu Title'
row: 6  # Note: 'row' not 'rows'

items:
  border:
    type: default  # Item type (registered via registerItem())
    item:
      type: WHITE_STAINED_GLASS_PANE
      amount: 1
      display: '&7'
    slot: 4,13,22,31,40  # Direct slot numbers, not layout characters
```

### Key Differences

| Legacy | New BafFramework |
|--------|------------------|
| `settings.title` | `title` (root level) |
| `rows: 6` | `row: 6` |
| `layout` grid | Direct `slot` numbers |
| `slots: 'b'` | `slot: 4,13,22,31,40` |
| No `type` field | `type: 'menu-type'` **required** |
| `settings.actions` | Not needed (framework handles) |

### Slot Number Conversion

6-row inventory (54 slots):
```
 0  1  2  3  4  5  6  7  8
 9 10 11 12 13 14 15 16 17
18 19 20 21 22 23 24 25 26
27 28 29 30 31 32 33 34 35
36 37 38 39 40 41 42 43 44
45 46 47 48 49 50 51 52 53
```

Legacy layout `'aooobOOOa'` → New slots `0,1,2,3,4,5,6,7,8`

---

## Player Inventory vs Menu GUI Clicks

### The Framework Automatically Separates Click Events

The BafFramework calls **different methods** for different click types:

| Click Location | Method Called | data.isPlayerInventory() |
|----------------|---------------|-------------------------|
| Menu GUI (top inventory) | `handleClick(ClickData)` | `false` |
| Player inventory (bottom) | `handlePlayerInventoryClick(ClickData)` | `true` |

### Correct Implementation

```java
public class StorageMenu extends AbstractMenu<MenuData, ExtraData> {

    @Override
    public void handleClick(ClickData data) {
        // Clicks on menu GUI items only
        // Framework routes to AbstractItem.handleClick()
        super.handleClick(data);
    }

    @Override
    public void handlePlayerInventoryClick(ClickData data) {
        // Clicks on player's inventory (bottom)
        ItemStack item = data.getEvent().getCurrentItem();

        if (item == null || item.getType().isAir()) {
            return;
        }

        // Handle deposit/withdraw logic
        boolean success = addToStorage(item);

        if (success) {
            data.getPlayer().getInventory().setItem(data.getClickedSlot(), null);
        }
    }
}
```

### Common Mistake (DON'T DO THIS)

```java
// WRONG: Checking inventory type in handleClick()
@Override
public void handleClick(ClickData data) {
    Inventory clickedInventory = data.getClickedInventory();

    // This check is unnecessary - framework already separates events!
    if (clickedInventory.getType() == InventoryType.PLAYER) {
        handlePlayerInventoryClick(data);  // Framework already calls this!
        return;
    }

    super.handleClick(data);
}
```

**Key Points:**
- Don't check `InventoryType` - framework handles it
- Override `handlePlayerInventoryClick()` for player inventory interactions
- `data.getClickedSlot()` is relative (0-35 for player inventory)

---

## Dynamic Item Updates

### ⚠️ IMPORTANT: Avoid Glitchy Menu Reopening

**DO NOT use `reopenInventory()` for simple item updates!**

Calling `reopenInventory()` closes and reopens the menu, causing:
- **Visible flicker** - menu closes then reopens
- **Annoying user experience** - interrupts player interaction
- **Performance overhead** - recreates entire inventory

**✅ PREFER: Update items directly in-place**

```java
@Override
public void handlePlayerInventoryClick(ClickData data) {
    // Add item
    addItem(data.getEvent().getCurrentItem());

    // ✅ GOOD: Update directly (smooth, instant)
    updateMenu();  // Calls inventory.setItem() directly

    // ❌ BAD: Reopen menu (glitchy, slow)
    // reopenInventory();
}
```

**Result:** Items appear **instantly** without any menu flicker! 🎉

---

### Problem: Items Disappear After Menu Refresh

**Symptom:** You set items with `inventory.setItem()`, but they disappear after `reopenInventory()`.

**Root Cause:** `reopenInventory()` calls `setupItems()`, which loads YAML items and overwrites dynamic items.

**Solution 1: Override setupItems() (Recommended)**

```java
@Override
public void setupItems() {
    // First, load YAML items (borders, buttons, placeholders)
    super.setupItems();

    // Then, overlay dynamic items on top
    if (extraData != null) {
        updateDynamicItems();
    }
}

private void updateDynamicItems() {
    // Set your dynamic items here
    for (int i = 0; i < items.size(); i++) {
        inventory.setItem(slot, items.get(i));
    }
}
```

**Solution 2: Don't Call reopenInventory() (For In-Place Updates)**

```java
@Override
public void handlePlayerInventoryClick(ClickData data) {
    // Add item to storage
    addItemToStorage(data.getEvent().getCurrentItem());

    // Update display in-place (no flicker!)
    updateDynamicItems();

    // DON'T call reopenInventory() - causes annoying refresh
    // reopenInventory();  // ❌
}
```

**When to Use Each:**
- **Override setupItems()**: When you need to reopen (pagination, filter changes, etc.)
- **In-place updates**: When adding/removing items (smooth, no flicker)

---

## Common Pitfalls

### 1. Reward Amount Always Shows 1

**Problem:**
```java
inventory.setItem(rewardSlot, reward.getItemStack());  // Always amount=1
```

**Solution:** Clone and set amount
```java
ItemStack rewardItem = reward.getItemStack().clone();
rewardItem.setAmount(inputItem.getAmount());  // Match input amount
inventory.setItem(rewardSlot, rewardItem);
```

### 2. Taking Entire Stack Instead of 1 Item

**Problem:** Clicking a stack of 64 adds all 64 items.

**Solution:** Clone with amount=1
```java
@Override
public void handlePlayerInventoryClick(ClickData data) {
    ItemStack clickedItem = data.getEvent().getCurrentItem();

    // Take only 1 from stack
    ItemStack singleItem = clickedItem.clone();
    singleItem.setAmount(1);

    addItem(singleItem);

    // Decrease stack by 1
    int newAmount = clickedItem.getAmount() - 1;
    if (newAmount <= 0) {
        data.getEvent().setCurrentItem(null);
    } else {
        clickedItem.setAmount(newAmount);
    }
}
```

### 3. Hardcoded Messages Instead of Message Keys

**Bad:**
```java
player.sendMessage("§cThis item is not supported!");
```

**Good:**
```java
YourPluginMessage.send(player, "menu.your-menu.not-support-item");
```

### 4. Forgetting to Return Items on Close

```java
@Override
public void handleClose() {
    // Always return items to prevent loss!
    returnAllItemsToPlayer();
}
```

---

## Migration Checklist

### Pre-Migration

- [ ] Read BafFramework CustomMenu documentation
- [ ] Understand player inventory click handling
- [ ] Plan YAML format conversion strategy
- [ ] Create backup of legacy menus

### MenuModule Setup

- [ ] Register plugin with `MenuPluginRegister`
- [ ] Register custom menu classes with `MenuRegister`
- [ ] Implement `onReload()` to load YAML files
- [ ] Use custom folder (e.g., `menu-new`) to avoid loading legacy menus

### Command Registration

- [ ] Create command class implementing `AdvancedCommandExecutor`
- [ ] Register command in `CommandModule.onEnable()`
- [ ] **IMPORTANT: Add command to `plugin.yml`** (e.g., `bookcraft-new`)
- [ ] Initialize settings in config module (call `initializeSettings()`)

### Menu Class

- [ ] Extend `AbstractMenu<MenuData, ExtraData>`
- [ ] Implement `getType()` to return menu identifier
- [ ] Implement `registerItems()` to register item types
- [ ] Override `handlePlayerInventoryClick()` for player inventory interactions
- [ ] Override `setupItems()` if using dynamic items
- [ ] Override `handleClose()` to return items

### YAML Conversion

- [ ] Add `type` field matching `getType()`
- [ ] Move `settings.title` to root `title`
- [ ] Change `rows` to `row`
- [ ] Convert character-grid layout to direct slot numbers
- [ ] Add `type` field to each item (matching registered item types)
- [ ] Remove `settings.actions` (framework handles)

### Testing

- [ ] Menu opens successfully
- [ ] Items display correctly
- [ ] Player inventory clicks work
- [ ] Dynamic items persist across refreshes
- [ ] Items return on close
- [ ] Reward amounts match input amounts
- [ ] Messages use message keys (not hardcoded)

### Cleanup

- [ ] Remove debug logging
- [ ] Write unit tests
- [ ] Update documentation
- [ ] Delete legacy files

---

## Example: Complete Migration

### Before (Legacy)

**Menu Class:**
```java
public class TinkererMenu extends MenuAbstract {
    private static HashMap<String, Menu> menuMap = new HashMap<>();

    public void open(Player player) {
        CPlayer cPlayer = CustomMenuAPI.getCPlayer(player);
        CMenuView view = cPlayer.openCustomMenu("tinkerer");
        // ...
    }
}
```

**Listener:**
```java
public class TinkererMenuListener extends MenuListenerAbstract {
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        // Manual click handling
    }
}
```

**YAML:**
```yaml
settings:
  title: '&9&lTinkerer'
  rows: 6
layout:
- 'aooobOOOa'
items:
  border:
    slots: 'b'
```

### After (BafFramework)

**Menu Class:**
```java
public class TinkererCustomMenu extends AbstractMenu<MenuData, TinkererExtraData> {

    @Override
    public String getType() {
        return "tinkerer";
    }

    @Override
    public void registerItems() {
        registerItem(DefaultItem.class);
        registerItem(TinkerSlotItem.class);
        registerItem(TinkerAcceptItem.class);
    }

    @Override
    public void setupItems() {
        super.setupItems();
        if (extraData != null) {
            updateMenu();
        }
    }

    @Override
    public void handlePlayerInventoryClick(ClickData data) {
        ItemStack clickedItem = data.getEvent().getCurrentItem();

        if (clickedItem == null) return;

        // Take 1 from stack
        ItemStack singleItem = clickedItem.clone();
        singleItem.setAmount(1);

        addItem(singleItem);

        // Decrease stack
        int newAmount = clickedItem.getAmount() - 1;
        if (newAmount <= 0) {
            data.getEvent().setCurrentItem(null);
        } else {
            clickedItem.setAmount(newAmount);
        }
    }

    @Override
    public void handleClose() {
        returnItems();
    }
}
```

**Item Class (replaces listener):**
```java
public class TinkerSlotItem extends AbstractItem<TinkererCustomMenu> {

    @Override
    public String getType() {
        return "tinker";
    }

    @Override
    public void handleClick(ClickData data) {
        menu.returnItem(data.getClickedSlot());
    }
}
```

**MenuModule:**
```java
public class MenuModule extends PluginModule<YourPlugin> {

    @Override
    public void onEnable() {
        registerFolder();
        registerMenu();
        onReload();
    }

    private void registerFolder() {
        AbstractMenuPlugin menuPlugin = new AbstractMenuPlugin<YourPlugin>(getPlugin()) {
            @Override
            public File getMenuFolder() {
                return new File(getPlugin().getDataFolder(), "menu-new");
            }
        };
        MenuPluginRegister.instance().registerMenuPlugin(menuPlugin);
    }

    private void registerMenu() {
        MenuRegister.instance().registerStrategy(TinkererCustomMenu.class);
    }

    @Override
    public void onReload() {
        Map<Plugin, AbstractMenuPlugin> menuMap = MenuPluginRegister.instance().getMap();
        AbstractMenuPlugin menuPlugin = menuMap.get(getPlugin());

        if (menuPlugin != null) {
            menuPlugin.clearMenu();
            menuPlugin.registerMenu(menuPlugin.getMenuFolder());
        }
    }
}
```

**YAML:**
```yaml
type: 'tinkerer'
title: '&9&lTinkerer'
row: 6

items:
  border:
    type: default
    item:
      type: WHITE_STAINED_GLASS_PANE
      display: '&7'
    slot: 4,13,22,31,40

  tinker:
    type: tinker
    item:
      type: LIGHT_GRAY_STAINED_GLASS_PANE
      display: '&7Tinker Slot'
    slot: 1,2,3,9,10,11,12
```

---

## Benefits of New System

1. **No manual event dispatching** - Framework routes clicks automatically
2. **Type-safe item handling** - Item classes handle their own clicks
3. **Cleaner code** - No static state maps or manual player tracking
4. **Better reusability** - Menu types can be reused across plugins
5. **Player inventory support** - Built-in `handlePlayerInventoryClick()`
6. **Async loading** - MenuOpener handles async by default

---

## Troubleshooting

### Menu doesn't open
- Check if plugin is registered with `MenuPluginRegister`
- Verify YAML has correct `type` field
- Check if YAML file is in correct folder
- Ensure `onReload()` is called in `onEnable()`

### Player inventory clicks don't work
- Override `handlePlayerInventoryClick(ClickData)` (not `handleClick()`)
- Don't check inventory type - framework already separates events

### Items disappear after refresh
- Override `setupItems()` and call `updateDynamicItems()` after `super.setupItems()`
- Or don't call `reopenInventory()` for in-place updates

### Reward shows wrong amount
- Clone reward ItemStack and set amount: `reward.clone().setAmount(amount)`

### Takes entire stack instead of 1 item
- Clone item with amount=1: `item.clone().setAmount(1)`
- Decrease original stack by 1

---

For more details, see:
- [MENU_SYSTEM.md](MENU_SYSTEM.md) - Core menu system
- [MENU_ITEMS.md](MENU_ITEMS.md) - Custom item types
- [CONFIGURATION.md](CONFIGURATION.md) - YAML format reference
- [ADVANCED_PATTERNS.md](ADVANCED_PATTERNS.md) - Handler strategy, template items, state machines, paginated lists (for complex menus like CE Anvil)
