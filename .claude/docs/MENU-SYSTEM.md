# CustomEnchantment Menu System Reference

This document provides comprehensive reference for the GUI menu system in the CustomEnchantment plugin.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Menu Types](#menu-types)
- [Menu Module](#menu-module)
- [Creating Custom Menus](#creating-custom-menus)
- [Configuration Files](#configuration-files)
- [CE Anvil System](#ce-anvil-system)
- [Integration](#integration)

---

## Overview

The menu system provides inventory-based GUIs for player interactions:

| Menu | Command | Purpose |
|------|---------|---------|
| CE Anvil | `/ceanvil` | Apply enchantments, gems, scrolls |
| Equipment | `/equipment` | Extra equipment slot management |
| Book Craft | `/bookcraft` | Combine enchantment books |
| Book Upgrade | `/bookupgrade` | Upgrade book levels |
| Artifact Upgrade | `/artifactupgrade` | Upgrade artifact levels |
| Tinkerer | `/tinkerer` | Exchange books for rewards |

---

## Architecture

### Dependencies

The menu system uses the external CustomMenu plugin for GUI rendering:

```java
// Check if CustomMenu is available
if (!Bukkit.getPluginManager().isPluginEnabled("CustomMenu")) {
    return;
}

// Register menu folder
CustomMenuAPI.registerPlugin(getPlugin(), getPlugin().getMenuFolder());
```

### Core Classes

| Class | Purpose |
|-------|---------|
| `MenuAbstract` | Base class for menu state |
| `MenuListenerAbstract` | Base class for menu events |
| `MenuModule` | Registers all menu listeners |
| `CustomMenuModule` | CustomMenu integration |

### Class Hierarchy

```
MenuAbstract
├── CEAnvilMenu      - Enchantment anvil
├── BookCraftMenu    - Book combining
├── BookUpgradeMenu  - Book upgrading
├── EquipmentMenu    - Extra slots
├── TinkererMenu     - Book exchange
└── ArtifactUpgradeMenu - Artifact upgrading

MenuListenerAbstract
├── CEAnvilMenuListener
├── BookCraftMenuListener
├── BookUpgradeMenuListener
├── EquipmentMenuListener
├── TinkererMenuListener
└── ArtifactUpgradeMenuListener
```

---

## Menu Types

### CE Anvil (`/ceanvil`)

Multi-purpose anvil for item modifications.

**Slot 1 Views** (Main Item):
| View | Item Type | Description |
|------|-----------|-------------|
| `Slot1CEWeaponView` | WEAPON | Weapon items |

**Slot 2 Views** (Modifier Item):
| View | Item Type | Description |
|------|-----------|-------------|
| `Slot2CEBookView` | BOOK | Enchantment books |
| `Slot2CEGemView` | GEM | Socket gems |
| `Slot2CEGemDrillView` | GEM_DRILL | Gem removal |
| `Slot2CERemoveEnchantView` | REMOVE_ENCHANT | Enchant removal |
| `Slot2CEEraseEnchantView` | ERASE_ENCHANT | Enchant erase |
| `Slot2CEEnchantPointView` | ENCHANT_POINT | Add enchant points |
| `Slot2CERemoveEnchantPointView` | REMOVE_ENCHANT_POINT | Remove points |
| `Slot2CEProtectDeadView` | PROTECT_DEAD | Soul protection |
| `Slot2CERemoveProtectDeadView` | REMOVE_PROTECT_DEAD | Remove protection |
| `Slot2CEProtectDestroyView` | PROTECT_DESTROY | Destroy protection |
| `Slot2CELoreFormatView` | LORE_FORMAT | Lore formatting |
| `Slot2CERemoveGemView` | REMOVE_GEM | Remove gems |
| `Slot2CEDefaultView` | DEFAULT | Default empty view |

### Equipment (`/equipment`)

Manages extra equipment slots (artifacts, sigils, outfits).

### Book Craft (`/bookcraft`)

Combines books of the same enchantment to increase level/success rate.

### Book Upgrade (`/bookupgrade`)

Upgrades enchantment books using experience and materials.

### Artifact Upgrade (`/artifactupgrade`)

Upgrades artifact items to higher levels.

### Tinkerer (`/tinkerer`)

Exchanges enchantment books for rewards (dust, items, etc.).

---

## Menu Module

### MenuModule

Registers all menu views and listeners:

```java
public class MenuModule extends PluginModule<CustomEnchantment> {
    public void onEnable() {
        // Register slot 1 views
        CEAnvilMenu.registerView1(CEItemType.WEAPON, Slot1CEWeaponView.class);

        // Register slot 2 views
        CEAnvilMenu.registerView2(CEItemType.BOOK, Slot2CEBookView.class);
        CEAnvilMenu.registerView2(CEItemType.GEM, Slot2CEGemView.class);
        // ... more views

        // Register menu listeners
        CMenuListener.registerMenuListener(new CEAnvilMenuListener());
        CMenuListener.registerMenuListener(new BookCraftMenuListener());
        CMenuListener.registerMenuListener(new TinkererMenuListener());
        CMenuListener.registerMenuListener(new EquipmentMenuListener());
        // ... more listeners
    }
}
```

### CustomMenuModule

Integrates with external CustomMenu plugin:

```java
public class CustomMenuModule extends PluginModule<CustomEnchantment> {
    public void onEnable() {
        // Register custom catalogs
        new CEBookCatalog().register();
        new CustomEnchantmentTradeItemCompare().register();
        new CustomEnchantmentItemDisplaySetup().register();

        // Register menu folder
        CustomMenuAPI.registerPlugin(getPlugin(), getPlugin().getMenuFolder());
    }

    public void onDisable() {
        CustomMenuAPI.unregisterPlugin(getPlugin());
    }
}
```

---

## Creating Custom Menus

### Menu State Class

Extend `MenuAbstract` to manage menu state:

```java
public class MyCustomMenu extends MenuAbstract {
    public static final String MENU_NAME = "my-menu";
    private static HashMap<String, MyCustomMenu> map = new HashMap<>();

    private ItemStack storedItem;

    public MyCustomMenu(CMenuView menuView, Player player) {
        super(menuView, player);
    }

    public static MyCustomMenu get(Player player) {
        return map.get(player.getName());
    }

    public static MyCustomMenu put(Player player, CMenuView view) {
        MyCustomMenu menu = new MyCustomMenu(view, player);
        map.put(player.getName(), menu);
        return menu;
    }

    public static void remove(Player player) {
        map.remove(player.getName());
    }

    public void update() {
        // Update slot display
        updateSlots("slot1", storedItem);
    }

    public void returnItems() {
        if (storedItem != null) {
            InventoryUtils.addItem(player, storedItem);
            storedItem = null;
        }
    }
}
```

### Menu Listener Class

Extend `MenuListenerAbstract` to handle events:

```java
public class MyCustomMenuListener extends MenuListenerAbstract {
    @Override
    public String getMenuName() {
        return MyCustomMenu.MENU_NAME;
    }

    @Override
    public void onMenuOpen(CustomMenuOpenEvent e) {
        Player player = e.getPlayer();
        CMenuView view = e.getCMenuView();
        MyCustomMenu.put(player, view);
    }

    @Override
    public void onMenuClose(CustomMenuCloseEvent e) {
        Player player = e.getPlayer();
        MyCustomMenu menu = MyCustomMenu.get(player);
        if (menu != null) {
            menu.returnItems();
        }
        MyCustomMenu.remove(player);
    }

    @Override
    public void onMenuClick(CustomMenuClickEvent e) {
        Player player = e.getPlayer();
        String itemName = e.getItemName();
        MyCustomMenu menu = MyCustomMenu.get(player);

        if (menu == null) return;

        if (itemName.equals("confirm")) {
            menu.confirm();
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack cursor = e.getCursor();
        MyCustomMenu menu = MyCustomMenu.get(player);

        if (menu != null && cursor != null) {
            // Handle item placement
            menu.addItem(cursor);
            e.setCancelled(true);
        }
    }
}
```

### Registering Custom Menu

```java
// In your module's onEnable()
CMenuListener.registerMenuListener(new MyCustomMenuListener());
```

---

## Configuration Files

### Menu Configuration Location

```
plugins/CustomEnchantment/menu/
├── ce-anvil.yml       # CE Anvil configuration
├── equipment.yml      # Equipment menu
├── book-craft.yml     # Book crafting
├── book-upgrade.yml   # Book upgrading
├── tinkerer.yml       # Tinkerer menu
└── artifact-upgrade.yml
```

### Example Menu Configuration

```yaml
# ce-anvil.yml
menu:
  title: "&6Custom Enchantment Anvil"
  size: 54

items:
  slot1:
    material: AIR
    slots: [20]
    name: "&eSlot 1 - Weapon"

  slot2:
    material: AIR
    slots: [24]
    name: "&eSlot 2 - Modifier"

  confirm:
    material: LIME_STAINED_GLASS_PANE
    slots: [31]
    name: "&aConfirm"

  preview1:
    material: AIR
    slots: [11]

  preview2:
    material: AIR
    slots: [12]

  # ... more items
```

### Slot Configuration

Each menu item can have:
- `material` - Display material
- `slots` - Inventory slot positions
- `name` - Display name
- `lore` - Item lore lines
- `custom-model-data` - Custom model for resource packs

---

## CE Anvil System

### View Registration

The CE Anvil uses a view-based system for different item interactions:

```java
// Register view for slot 1 (main item)
CEAnvilMenu.registerView1(CEItemType.WEAPON, Slot1CEWeaponView.class);

// Register view for slot 2 (modifier item)
CEAnvilMenu.registerView2(CEItemType.BOOK, Slot2CEBookView.class);
```

### Creating Slot 2 View

```java
public class MyCustomView extends AnvilSlot2View {
    public MyCustomView(CEAnvilMenu menu) {
        super(menu);
    }

    @Override
    public boolean isSuitable(CEItem ceItem) {
        // Return true if this view handles this item type
        return ceItem != null && ceItem.getType().equals("MY_TYPE");
    }

    @Override
    public AnvilSlot2View instance(CEAnvilMenu menu) {
        return new MyCustomView(menu);
    }

    @Override
    public void updateView() {
        // Update preview slots
        ItemStack preview = createPreview();
        updateSlots("preview1", preview);
    }

    @Override
    public void updateConfirm() {
        // Update confirm button
        if (canApply()) {
            ItemStack confirmItem = createConfirmItem();
            updateSlots("confirm", confirmItem);
        } else {
            updateSlots("confirm", null);
        }
    }

    @Override
    public ApplyReason apply(CEItem item1, CEItem item2) {
        // Apply the modification
        ApplyReason reason = ApplyReason.builder()
            .result(ApplyResult.SUCCESS)
            .reason("APPLIED")
            .build();

        // Modify item1 based on item2
        doApply(item1, item2);

        return reason;
    }
}
```

### Apply Results

| Result | Description |
|--------|-------------|
| `SUCCESS` | Modification successful |
| `FAIL` | Failed, consume modifier |
| `FAIL_AND_UPDATE` | Failed, update main item |
| `DESTROY` | Main item destroyed |
| `CANCEL` | Operation cancelled |
| `NOTHING` | No action taken |

---

## Integration

### Opening Menus

```java
import com.bafmc.custommenu.api.CustomMenuAPI;

// Open menu by name
CustomMenuAPI.open(player, "ce-anvil");

// Open with page
CustomMenuAPI.open(player, "equipment", 1);
```

### Menu Data Access

```java
// Get menu instance for player
CEAnvilMenu menu = CEAnvilMenu.getCEAnvilMenu(player);

// Get stored items
CEAnvilMenu.ItemData slot1Data = menu.getItemData1();
CEAnvilMenu.ItemData slot2Data = menu.getItemData2();

// Get CE item from slot
if (slot1Data != null) {
    CEItem ceItem = slot1Data.getCEItem();
    ItemStack itemStack = slot1Data.getItemStack();
}
```

### Updating Menu Display

```java
// Update specific named slot
menu.updateSlots("slot1", itemStack);

// Update multiple slots by name
menu.updateSlots("preview1", previewItem);
menu.updateSlots("preview2", null); // Clear slot

// Get slots by item name
List<Integer> slots = menu.getSlots("confirm");
```

---

## Best Practices

### State Management

1. **Always store menu per-player**: Use `HashMap<String, Menu>` with player name
2. **Return items on close**: Implement `returnItems()` method
3. **Clean up on close**: Remove player from map in `onMenuClose`

### Performance

1. **Minimize updates**: Only update changed slots
2. **Cache computed values**: Pre-calculate preview items
3. **Batch updates**: Update multiple slots in single method call

### Error Handling

```java
@Override
public void onMenuClick(CustomMenuClickEvent e) {
    try {
        handleClick(e);
    } catch (Exception ex) {
        ex.printStackTrace();
        e.getPlayer().sendMessage("An error occurred");
    }
}
```

### Thread Safety

- Menu operations run on main thread
- Use async for heavy calculations
- Sync back to main for Bukkit API calls

---

## See Also

- [ARCHITECTURE.md](ARCHITECTURE.md) - System architecture
- [COMMANDS.md](COMMANDS.md) - Menu-related commands
- [ITEM-SYSTEM.md](ITEM-SYSTEM.md) - Item handling
