# Issue #34: CE Anvil Menu Migration - Phase 3

## Parent Issue
**Parent:** #30 - CustomMenu Migration (Master Epic)

## Phase Information
**Current Phase:** Phase 3 of 8 - **COMPLETED**
**Previous Phases:**
- Phase 0 (#31): Migration Preparation - Completed
- Phase 1 (#32): Tinkerer Menu (Prototype) - Completed (on branch `30-feat-custommenu-migration`)
- Phase 2 (#33): BookCraft Menu + FastCraft - Completed (on branch `30-feat-custommenu-migration`)

**Next Phases:**
- Phase 4 (#35): Book Upgrade + Artifact Upgrade
- Phase 5 (#36): Equipment Menu
- Phase 6 (#37): Declarative YAML Menus (34 files)
- Phase 7 (#38): Cleanup + Finalization

## Phase 3 Documentation

- [PHASE3_RETROSPECTIVE.md](PHASE3_RETROSPECTIVE.md) — Bugs found, fixes applied, lessons learned
- [ARCHITECTURE_COMPARISON.md](ARCHITECTURE_COMPARISON.md) — Legacy vs new architecture comparison
- [VIEW_CATALOG.md](VIEW_CATALOG.md) — All 13 legacy views cataloged
- [YAML_CONVERSION.md](YAML_CONVERSION.md) — YAML format conversion details
- [NO_LEGACY_TOUCH_STRATEGY.md](NO_LEGACY_TOUCH_STRATEGY.md) — How legacy code remains untouched
- [IMPLEMENTATION_CHECKLIST.md](IMPLEMENTATION_CHECKLIST.md) — Implementation tracking

## Advanced Patterns Guide

The patterns from this phase are documented for reuse in future phases:
- [Advanced CustomMenu Patterns](../../custommenu/ADVANCED_PATTERNS.md) — Handler strategy, template items, state machines, paginated lists

---

## Table of Contents

1. [Understanding the Current CEAnvilMenu](#1-understanding-the-current-ceanvilmenu)
2. [Why CEAnvilMenu Is the Most Complex Migration](#2-why-ceanvilmenu-is-the-most-complex-migration)
3. [BafFramework CustomMenu API Deep Dive](#3-bafframework-custommenu-api-deep-dive)
4. [Migration Strategy](#4-migration-strategy)
5. [YAML Conversion Plan](#5-yaml-conversion-plan)
6. [Lessons Applied from Previous Phases](#6-lessons-applied-from-previous-phases)
7. [State Machine & Transition Diagram](#7-state-machine--transition-diagram)
8. [Implementation Plan](#8-implementation-plan)
9. [Testing Strategy](#9-testing-strategy)
10. [Risk Assessment](#10-risk-assessment)

---

## CRITICAL CONSTRAINTS

### 1. DO NOT TOUCH LEGACY CODE

All legacy files MUST remain **completely untouched**:
- `CEAnvilMenu.java` - NO modifications
- `CEAnvilMenuListener.java` - NO modifications
- `MenuAbstract.java` - NO modifications
- `MenuListenerAbstract.java` - NO modifications
- `AnvilSlot1View.java` - NO modifications
- `AnvilSlot2View.java` - NO modifications
- `AnvilSlot2ListView.java` - NO modifications
- `Slot1CEWeaponView.java` - NO modifications
- All 13 `Slot2CE*.java` files - NO modifications
- Legacy `ce-anvil.yml` - NO modifications

The legacy `/ceanvil` command and all legacy code runs in parallel with the new code.
Both old and new menus coexist until Phase 7 cleanup.

### 2. Register `/ceanvil-new` Command

Following the established pattern from Phase 1 (`/tinkerer-new`) and Phase 2 (`/bookcraft-new`):

**plugin.yml** - Add new command entry:
```yaml
  ceanvil-new:
    description: Anvil CustomEnchantment item (new API)
    usage: /<command>
```

**CommandModule.java** - Register new command using `CEAnvilCommand`:
```java
// NEW: Migrated ceanvil command using new CustomMenu BafFramework API
getPlugin().getLogger().info("[CommandModule] Registering /ceanvil-new command...");
AdvancedCommandBuilder ceanvilNewBuilder = AdvancedCommandBuilder.builder()
        .plugin(getPlugin())
        .rootCommand("ceanvil-new");
ceanvilNewBuilder.commandExecutor(new CEAnvilCommand(getPlugin())).end();
ceanvilNewBuilder.build();
getPlugin().getLogger().info("[CommandModule] /ceanvil-new command registered successfully");
```

**CEAnvilCommand.java** - New command class (follows BookCraftCommand pattern):
```java
public class CEAnvilCommand implements AdvancedCommandExecutor {
    private final CustomEnchantment plugin;

    public CEAnvilCommand(CustomEnchantment plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Argument arg) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        CEAnvilExtraData extraData = new CEAnvilExtraData();

        try {
            MenuOpener.builder()
                    .player(player)
                    .menuData(plugin, CEAnvilCustomMenu.MENU_NAME)
                    .extraData(extraData)
                    .async(false)
                    .build();
        } catch (Exception e) {
            plugin.getLogger().severe("[CEAnvilCommand] Exception opening menu: " + e.getMessage());
            player.sendMessage("§cError opening menu: " + e.getMessage());
        }
        return true;
    }
}
```

### 3. Implication: Views Must Be Duplicated/Re-implemented

Since we **cannot modify** the existing view classes (they reference `CEAnvilMenu` directly),
the new `CEAnvilCustomMenu` must **re-implement the view delegation logic** internally
or create **new view-compatible classes** that work with the new menu.

Two approaches:

**Option A: Compose with legacy CEAnvilMenu internally** (NOT recommended - couples to legacy)

**Option B: Re-implement the view logic in the new menu** (Recommended)
- The core business logic (`isSuitable()`, `apply()`, `testApplyTo()`) lives in `CEItem` subclasses (CEBook, CEGem, etc.), NOT in the views
- The views are mostly **display adapters** that call CEItem methods and place results in slots
- The new menu can replicate this display logic using AbstractItem subclasses + config-driven slots
- This results in cleaner code without legacy coupling

**Option C: Create new view interfaces + new implementations** (Recommended for complex views)
- Create new `AbstractAnvilSlot2Handler` interface for the new menu
- Implement handlers that call the same CEItem business logic
- Keep the same strategy pattern (register by CEItemType) but with new handler interface

---

## 1. Understanding the Current CEAnvilMenu

### What Is the CE Anvil?

The CE Anvil ("Lo ren" / Blacksmith) is an interactive GUI menu where players combine **CE items** (custom enchantment items). A player places:
- **Slot 1**: A weapon/armor piece (CEWeapon)
- **Slot 2**: A modifier item (book, gem, scroll, etc.)

The menu then shows a **preview** of the result and a **confirm** button. The player clicks confirm to apply the modification.

### Architecture Overview

```
CEAnvilMenuListener (legacy event handler)
  |
  v
CEAnvilMenu (state controller, extends MenuAbstract)
  |
  +--> AnvilSlot1View (1 implementation)
  |      +-- Slot1CEWeaponView (accepts CEWeapon)
  |
  +--> AnvilSlot2View (13 implementations)
         +-- Slot2CEDefaultView      (no item in slot2 - shows removable enchants)
         +-- Slot2CEBookView         (apply enchant book)
         +-- Slot2CEEnchantPointView (apply enchant points)
         +-- Slot2CEGemView          (apply gem)
         +-- Slot2CEGemDrillView     (drill gem slot)
         +-- Slot2CEProtectDeadView  (apply death protection)
         +-- Slot2CEProtectDestroyView (apply scroll protection)
         +-- Slot2CELoreFormatView   (format item lore)
         +-- Slot2CEEraseEnchantView (erase enchants)
         +-- Slot2CERemoveEnchantView     (remove specific enchant - LIST)
         +-- Slot2CERemoveEnchantPointView (remove enchant point - LIST)
         +-- Slot2CERemoveGemView          (remove gem - LIST)
         +-- Slot2CERemoveProtectDeadView  (remove death protection - LIST)
```

### How It Currently Works

#### 1. Legacy Event Flow

```
Player clicks item in their inventory
  --> InventoryClickEvent
    --> CEAnvilMenuListener.onInventoryClick(e)
      --> CEAnvilMenu.addItem(itemStack, ceItem)
        --> Finds matching AnvilSlot1View or AnvilSlot2View
        --> Sets view + itemData
        --> updateMenu() refreshes display

Player clicks menu GUI item
  --> CustomMenuClickEvent
    --> CEAnvilMenuListener.onMenuClick(e)
      --> e.getClickedCItem().getName() --> "slot1", "confirm", "preview3", etc.
      --> CEAnvilMenu.clickProcess(name)
        --> Routes to view1.clickProcess(name) and view2.clickProcess(name)
```

#### 2. Slot Update Mechanism (Legacy)

The legacy system updates slots **by item name**, not by slot number:

```java
// MenuAbstract.updateSlots(String itemName, ItemStack itemStack)
// Looks up CItem by name from CMenuView, gets its slot list, sets temporary items

menu.updateSlots("slot1", weaponItemStack);         // Sets weapon display
menu.updateSlots("preview3", previewItemStack);      // Sets center preview
menu.updateSlots("confirm", confirmItemStack);       // Sets confirm button
menu.updateSlots("confirm", null);                   // Resets to YAML default
```

When `null` is passed, `removeTemporaryItem(slot)` is called, which reverts to the YAML-defined item at that slot.

#### 3. The Priority System (Legacy)

In the legacy YAML, **13 confirm-* items** all map to the same slot (`"a"` = slot 31):

```yaml
confirm:               slots: "a"   # priority: default (0)
confirm-book:          slots: "a"   priority: -1
confirm-enchant-point: slots: "a"   priority: -1
confirm-remove-enchant: slots: "a"  priority: -1
# ... 10 more confirm variants
```

The legacy system uses `priority` to decide which item displays by default. But the **views override the slot dynamically** by calling:

```java
menu.updateSlots("confirm", menu.getItemStack(null, "confirm-book"));
```

This fetches the `confirm-book` item's configured ItemStack and places it at the confirm slot. The priority system is only relevant for the **default display** (when no view is active).

#### 4. View State Management

Each view manages its own state:
- **Simple views** (Slot2CEBookView, Slot2CEGemView, etc.): Stateless - just show preview and apply
- **List views** (AnvilSlot2ListView subclasses): Stateful - page number, selected index, item list
- **Default view** (Slot2CEDefaultView): Has its own page/list state (duplicates ListView logic)

#### 5. Slot Layout (5-row = 45 slots)

```
Row 0:  0  1  2  3  4  5  6  7  8   --> border
Row 1:  9 10 11 12 13 14 15 16 17   --> border + slot1(12) + slot2(14)
Row 2: 18 19 20 21 22 23 24 25 26   --> border + prev(19) + preview1-5(20-24) + next(25)
Row 3: 27 28 29 30 31 32 33 34 35   --> border + confirm(31)
Row 4: 36 37 38 39 40 41 42 43 44   --> border + return(40)
```

Preview slots are indexed non-sequentially in views:
```
preview3 (center) = slot 22  --> display index 0 (first shown)
preview2           = slot 21  --> display index 1
preview4           = slot 23  --> display index 2
preview1           = slot 20  --> display index 3
preview5           = slot 24  --> display index 4
```

---

## 2. Why CEAnvilMenu Is the Most Complex Migration

### Problem 1: Views Are NOT AbstractItems

In BafFramework, each slot has an `AbstractItem` that handles its click. But in CEAnvilMenu, the **views** control **multiple slots across the menu**:

- A `Slot2CEBookView` controls: `preview3` (center preview) + `confirm` button
- A `Slot2CERemoveEnchantView` controls: `preview1-5` (paginated list) + `confirm` + `previous-page` + `next-page`
- `Slot2CEDefaultView` controls the same slots as ListView views

**Challenge:** The view's `updateView()`, `updateConfirm()`, `clickProcess()` methods update slots that belong to different `AbstractItem` instances.

### Problem 2: Dynamic Confirm Button (13 Variants)

The confirm button's **appearance and behavior** changes based on which Slot2 view is active:

| View | Confirm Item Name | Display |
|------|-------------------|---------|
| No slot2 item | `confirm` | "Select items for both slots" |
| Slot2CEBookView | `confirm-book` | "Confirm enchant book" |
| Slot2CEGemDrillView | `confirm-gem-drill` | "Confirm drill gem slot" |
| Slot2CEGemDrillView (max) | `confirm-gem-drill-max` | "Max gem slots reached" |
| Slot2CEGemDrillView (chance) | `confirm-gem-drill-with-chance` | "Confirm drill ({chance}%)" |
| ... | ... | ... |

In legacy: views call `menu.getItemStack(null, "confirm-book")` to get the configured appearance.
In BafFramework: we need a way to store these ItemStack templates and let views access them.

### Problem 3: Preview Slots With Non-Linear Mapping

The 5 preview slots use a non-linear index-to-slot mapping:

```java
// index 0 → preview3 (center, slot 22) - shown first
// index 1 → preview2 (left of center, slot 21)
// index 2 → preview4 (right of center, slot 23)
// index 3 → preview1 (far left, slot 20)
// index 4 → preview5 (far right, slot 24)
```

This spiral-outward pattern means center is most important. In the new system, this mapping must come from config, not hardcoded.

### Problem 4: Dual Pagination Systems

- `Slot2CEDefaultView` has its OWN page/enchant state (duplicated from ListView)
- `AnvilSlot2ListView` subclasses have generic page/list state
- Both update `previous-page` / `next-page` slots dynamically

### Problem 5: View Lifecycle Is Coupled to Menu State

```
addItem(weapon)
  --> view1 = Slot1CEWeaponView
  --> view2 = Slot2CEDefaultView (auto-created, shows removable enchants)
  --> updateMenu()

addItem(book)
  --> view2.updateAllPreviewNull() (clear old view)
  --> view2 = Slot2CEBookView (replaces default view)
  --> updateMenu()

returnItem("slot2")
  --> view2.updateAllPreviewNull()
  --> view2 = Slot2CEDefaultView (reverts to default)
  --> updateMenu()

returnItem("slot1")
  --> view2.updateAllPreviewNull()
  --> view1 = null, itemData1 = null
  --> updateMenu()
```

---

## 3. BafFramework CustomMenu API Deep Dive

### AbstractMenu Lifecycle

```java
public abstract class AbstractMenu<M extends MenuData, E extends ExtraData> {
    // Fields available to subclass
    protected Inventory inventory;      // Bukkit inventory
    protected Player owner;             // Menu owner
    protected M menuData;               // YAML config data
    protected E extraData;              // Runtime state
    protected Map<Integer, AbstractItem> itemSlotMap;  // Slot → Item routing

    // Must implement
    abstract String getType();          // Matches YAML "type" field
    abstract void registerItems();      // Register AbstractItem classes

    // Lifecycle
    void setupMenu(Player, MenuData);   // Init menu
    void setupInventory();              // Create Bukkit Inventory
    void setupItems();                  // Place items from YAML + dynamic
    void openInventory();               // Show to player
    void handleClick(ClickData);        // Route to AbstractItem.handleClick()
    void handlePlayerInventoryClick(ClickData);  // Player inventory clicks
    void handleClose();                 // Cleanup

    // Utility
    void reopenInventory();             // Refresh display (causes flicker!)
}
```

### AbstractItem Lifecycle

```java
public abstract class AbstractItem<M extends AbstractMenu> {
    protected M menu;               // Parent menu reference
    protected ItemData itemData;    // YAML config for this item

    // Must implement
    abstract String getType();          // Matches YAML item "type" field
    void handleClick(ClickData data);   // Click handler

    // Optional overrides
    boolean canLoadItem();              // Display condition (default: true)
    ItemStack setupItemStack();         // Build display ItemStack
    void displayItemStack();            // Place in inventory
    void setItem(int slot, ItemStack);  // Set specific slot
}
```

### ItemData (From YAML)

```java
public class ItemData {
    String type;                        // Item type identifier
    ItemStackBuilder itemStackBuilder;  // Appearance builder
    String slotFormat;                  // "13" or "0,1,2" or "0-8"
    List<Integer> slots;               // Parsed slot numbers
    Condition condition;                // Display condition
    Condition executeCondition;         // Click condition
    Execute trueExecute;               // Actions if condition true
    Execute falseExecute;              // Actions if condition false
    AdvancedConfigurationSection dataConfig;  // Custom "data:" section
}
```

### MenuData (From YAML root)

```java
public class MenuData {
    String type;                        // Menu type
    String title;                       // Title with color codes
    int row;                            // Row count (1-6)
    Map<String, ItemData> itemMap;      // item-id → ItemData
    AdvancedConfigurationSection dataConfig;  // "data:" section
}
```

### Key Differences: Legacy vs BafFramework

| Feature | Legacy (CMenuView) | BafFramework (AbstractMenu) |
|---------|--------------------|-----------------------------|
| Slot updates | `menuView.setTemporaryItem(slot, item)` | `inventory.setItem(slot, item)` |
| Item lookup | `getCItemByName("confirm")` → slots | `menuData.getItemMap().get("confirm")` → ItemData |
| Click routing | Event name string → `clickProcess(name)` | Slot → `AbstractItem.handleClick(ClickData)` |
| Player clicks | Manual InventoryClickEvent check | `handlePlayerInventoryClick(ClickData)` |
| State | Static HashMap per player | ExtraData per menu instance |
| Slot revert | `removeTemporaryItem(slot)` → YAML default | Must manually set YAML default ItemStack |
| Priority | Multiple items per slot, priority decides | One item per slot, dynamic in code |

### Critical: No `removeTemporaryItem()` Equivalent

In legacy, calling `updateSlots("preview3", null)` reverts to the YAML-configured item.
In BafFramework, setting `inventory.setItem(slot, null)` makes the slot **empty**.

**Solution:** We must store the "default" ItemStack for each slot (from YAML) and restore it explicitly:

```java
// Store defaults during setupItems()
Map<String, ItemStack> defaultItemStacks = new HashMap<>();

// In setupItems(), capture YAML items
ItemData previewData = menuData.getItemMap().get("preview3");
defaultItemStacks.put("preview3", previewData.getItemStackBuilder().getItemStack());

// When clearing preview:
inventory.setItem(previewSlot, defaultItemStacks.get("preview3")); // Restore YAML default
```

---

## 4. Migration Strategy

### Core Decision: Keep Views, Replace the Shell

The 15 view strategy classes (`AnvilSlot1View`, `AnvilSlot2View`, and all subclasses) contain the **core business logic** (item validation, apply logic, preview generation). They should NOT be rewritten.

**Strategy:** Replace only the integration layer:

| Component | Action |
|-----------|--------|
| `MenuAbstract` (CMenuView wrapper) | **Replace** with `AbstractMenu` |
| `CEAnvilMenuListener` | **Replace** with `handleClick()` / `handlePlayerInventoryClick()` / `handleClose()` |
| `CEAnvilMenu.updateSlots(name, item)` | **Replace** with direct `inventory.setItem(slot, item)` using slot mappings from config |
| `CEAnvilMenu.getItemStack(null, name)` | **Replace** with `menuData.getItemMap().get(name).getItemStackBuilder().getItemStack()` |
| Static `HashMap<String, CEAnvilMenu>` | **Replace** with `ExtraData` (framework manages per-instance) |
| View classes | **Adapt** - change only the menu reference type |

### Architecture After Migration

```
CEAnvilCustomMenu (extends AbstractMenu<MenuData, CEAnvilExtraData>)
  |
  +--> registerItems()
  |      +-- DefaultItem (borders, static items)
  |      +-- AnvilSlotItem (slot1, slot2 - click returns item)
  |      +-- AnvilConfirmItem (confirm button - delegates to view2)
  |      +-- AnvilPreviewItem (preview1-5 - delegates to view2)
  |      +-- AnvilPageItem (previous-page, next-page - delegates to view2)
  |
  +--> CEAnvilExtraData (extends ExtraData)
  |      +-- AnvilSlot1View view1
  |      +-- AnvilSlot2View view2
  |      +-- ItemData itemData1, itemData2
  |      +-- CEAnvilSettings settings (slot config from YAML)
  |
  +--> handlePlayerInventoryClick(ClickData)
  |      +-- CEAPI.getCEItem(clickedItem)
  |      +-- addItem(itemStack, ceItem) (same logic as current)
  |
  +--> handleClose()
         +-- returnItems() to player
```

### View Adaptation Strategy

The views currently reference `CEAnvilMenu` for two purposes:
1. **State access**: `menu.getItemData1()`, `menu.getItemData2()`
2. **Slot updates**: `menu.updateSlots("preview3", itemStack)`, `menu.getItemStack(null, "confirm-book")`

We need to create an **interface** that both old and new menu implementations satisfy:

```java
public interface AnvilMenuAccess {
    // State access
    CEAnvilMenu.ItemData getItemData1();
    CEAnvilMenu.ItemData getItemData2();

    // Slot update (config-driven)
    void updateSlots(String itemName, ItemStack itemStack);
    ItemStack getItemStack(Player player, String itemName);
    List<Integer> getSlots(String itemName);
}
```

The `CEAnvilCustomMenu` implements this interface, delegating slot operations to:

```java
@Override
public void updateSlots(String itemName, ItemStack itemStack) {
    CEAnvilSettings settings = extraData.getSettings();
    List<Integer> slots = settings.getSlots(itemName);

    for (int slot : slots) {
        if (itemStack == null) {
            // Restore YAML default (NOT null!)
            inventory.setItem(slot, getDefaultItemStack(itemName));
        } else {
            inventory.setItem(slot, itemStack);
        }
    }
}
```

### Config-Driven Slot Mapping

**All slot numbers come from the YAML `data:` section or from `ItemData.slots`:**

```yaml
type: 'ce-anvil'
title: '&8&lBlacksmith'
row: 5
data:
  default-view:
    enchant-group:
      - "common"
      - "rare"
      - "epic"
      - "legendary"
      - "supreme"
      - "ultimate"
      - "event"
  # Preview display order: center-first spiral
  preview-index-order: [3, 2, 4, 1, 5]
items:
  # Each item defines its own slot - no hardcoding in Java
  slot1:
    type: anvil-slot
    item: ...
    slot: 12
    data:
      slot-name: slot1
  slot2:
    type: anvil-slot
    item: ...
    slot: 14
    data:
      slot-name: slot2
  preview1:
    type: anvil-preview
    item: ...
    slot: 20
  preview2:
    type: anvil-preview
    item: ...
    slot: 21
  preview3:
    type: anvil-preview
    item: ...
    slot: 22
  preview4:
    type: anvil-preview
    item: ...
    slot: 23
  preview5:
    type: anvil-preview
    item: ...
    slot: 24
  confirm:
    type: anvil-confirm
    item: ...
    slot: 31
  # Confirm variant templates (no slot - templates only)
  confirm-book:
    type: default
    item:
      type: ANVIL
      display: '&eConfirm enchant book'
      lore: ['', '&fClick to apply']
  confirm-remove-enchant:
    type: default
    item:
      type: ANVIL
      display: '&eConfirm remove enchant'
      lore: ['', '&fClick to remove']
  # ... other confirm variants (stored as templates, not displayed)
```

**Key insight:** Confirm variant items are defined in YAML for their ItemStack appearance, but they have NO slot assignment. They serve as **templates** that the views fetch via `menuData.getItemMap().get("confirm-book")`.

---

## 5. YAML Conversion Plan

### Legacy → BafFramework Conversion Table

| Legacy | BafFramework |
|--------|-------------|
| `settings.title: "&8&lLo ren"` | `title: '&8&lLo ren'` |
| `settings.rows: 5` | `row: 5` |
| `settings.sound: ...` | `data.sound: ...` (handle in code) |
| `settings.actions: ...` | Remove (framework handles) |
| `layout: [grid]` | Remove (use direct slot numbers) |
| `items.border.slots: "o"` | `items.border.slot: 0,1,2,3,...` (resolved from grid) |
| `items.confirm-book.slots: "a" / priority: -1` | `items.confirm-book.slot:` (no slot - template only) |

### Slot Resolution from Layout Grid

```
Legacy layout:
  Row 0: "ooooooooo"  →  0,1,2,3,4,5,6,7,8
  Row 1: "ooosoSooo"  →  9,10,11, s=12, 13, S=14, 15,16,17
  Row 2: "opxxxxxno"  →  18, p=19, x=20,21,22,23,24, n=25, 26
  Row 3: "ooooaoooo"  →  27,28,29,30, a=31, 32,33,34,35
  Row 4: "ooooRoooo"  →  36,37,38,39, R=40, 41,42,43,44
```

Border ("o") slots: `0,1,2,3,4,5,6,7,8,9,10,11,13,15,16,17,18,26,27,28,29,30,32,33,34,35,36,37,38,39,41,42,43,44`

### Handling Confirm Variants Without Priority

**Legacy approach:** 13 items all at slot "a" (31), priority decides default.

**New approach:**
1. The `anvil-confirm` item type occupies slot 31
2. Confirm variant items have NO slot - they are **template definitions**
3. When a view calls `updateConfirm()`, it fetches the template and places it at the confirm slot
4. When no view is active, the default confirm item (from `anvil-confirm` type) shows

```java
// In CEAnvilCustomMenu
public ItemStack getTemplateItemStack(String templateName) {
    ItemData templateData = menuData.getItemMap().get(templateName);
    if (templateData == null) return null;
    return templateData.getItemStackBuilder().getItemStack();
}

public ItemStack getTemplateItemStack(String templateName, Placeholder placeholder) {
    ItemData templateData = menuData.getItemMap().get(templateName);
    if (templateData == null) return null;
    return templateData.getItemStackBuilder().getItemStack(placeholder);
}
```

---

## 6. Lessons Applied from Previous Phases

### From Phase 1 (#32 - Tinkerer)

| Lesson | Application to CEAnvil |
|--------|----------------------|
| ExtraData replaces static HashMap | `CEAnvilExtraData` holds view1, view2, itemData1, itemData2 |
| Config-driven slots via `@Configuration` | `CEAnvilSettings` reads slot positions from YAML `data:` |
| `handlePlayerInventoryClick()` for bottom inv | Same pattern: detect CEItem, call `addItem()` |
| `handleClose()` returns items | Same: return itemData1 + itemData2 to player |
| `super.setupItems()` then dynamic overlay | Same: YAML items first, then update based on view state |

### From Phase 2 (#33 - BookCraft)

| Lesson | Application to CEAnvil |
|--------|----------------------|
| **Mode Router Pattern** | View dispatch IS the mode router: `view2.getClass()` determines mode |
| **State cleanup on transitions** | When switching view2: `view2.updateAllPreviewNull()` THEN assign new view |
| **Template capture pattern** | Capture confirm variant templates from `menuData.getItemMap()` |
| **In-place updates** (no `reopenInventory()`) | Use `inventory.setItem()` directly like BookCraft's `updateMenu()` |
| **Resource tracking** | Track itemData1/itemData2 carefully, return on close |
| **Read implementations, not method names** | Verify view methods do what they claim before delegating |

### Process Improvements Applied

From Phase 2 retrospective (see `.claude/docs/issues/30/`):

- [x] **All modes/states clearly defined** - 15 view types documented above
- [x] **State transitions mapped** - addItem/returnItem/confirm flows documented
- [x] **Cleanup logic identified** - `updateAllPreviewNull()` before view switch
- [x] **5+ concrete examples** - See [Testing Strategy](#9-testing-strategy)
- [x] **Invariants stated** - "Items never duplicate", "Views never persist after close"

---

## 7. State Machine & Transition Diagram

### Menu States

```
EMPTY (no items)
  |
  | [addItem: CEWeapon]
  v
SLOT1_ONLY (weapon in slot1, default view in slot2)
  |                                    ^
  | [addItem: modifier]                | [returnItem("slot2")]
  v                                    |
BOTH_SLOTS (weapon + modifier, specific view active)
  |                                    ^
  | [confirm]                          |
  v                                    |
AFTER_CONFIRM (result applied, item amounts adjusted)
  --> May return to SLOT1_ONLY (if slot2 consumed)
  --> May return to BOTH_SLOTS (if slot2 has amount > 1)
  --> May return to EMPTY (if slot1 consumed/destroyed)
```

### State Transitions

| From | Trigger | To | Cleanup |
|------|---------|-----|---------|
| EMPTY | addItem(weapon) | SLOT1_ONLY | Set view1, set view2=DefaultView, updateMenu() |
| EMPTY | addItem(modifier) | - | Return NOT_SUITABLE (need weapon first? Actually no - modifier can go into slot2 if slot2 is null and view1 exists, but slot1 must have a weapon for view2 to work) |
| SLOT1_ONLY | addItem(modifier) | BOTH_SLOTS | `view2.updateAllPreviewNull()`, set new view2, updateMenu() |
| SLOT1_ONLY | returnItem("slot1") | EMPTY | Clear view2 previews, null view1, view2, itemData1 |
| BOTH_SLOTS | returnItem("slot1") | EMPTY | Clear view2 previews, null all |
| BOTH_SLOTS | returnItem("slot2") | SLOT1_ONLY | Clear view2 previews, view2=DefaultView, null itemData2 |
| BOTH_SLOTS | confirm (SUCCESS) | varies | Apply, update amounts, updateMenu() |
| BOTH_SLOTS | confirm (DESTROY) | varies | Slot1 item may be destroyed |
| BOTH_SLOTS | clickProcess("preview*") | BOTH_SLOTS | View2 handles selection (ListView only) |
| BOTH_SLOTS | clickProcess("next-page") | BOTH_SLOTS | View2 paginates |

### View2 Type Resolution

```
addItem(ceItem):
  1. Check slot1ViewMap: if ceItem matches a Slot1View → goes to slot1
  2. Check slot2ViewMap: if ceItem matches a Slot2View → goes to slot2
  3. Neither → NOT_SUITABLE

View2 determination:
  - No slot2 item + slot1 present → Slot2CEDefaultView (shows removable enchants from weapon)
  - CEBook          → Slot2CEBookView
  - CEEnchantPoint  → Slot2CEEnchantPointView
  - CERemoveEnchant → Slot2CERemoveEnchantView (LIST)
  - CERemoveEnchantPoint → Slot2CERemoveEnchantPointView (LIST)
  - CEGem           → Slot2CEGemView
  - CEGemDrill      → Slot2CEGemDrillView
  - CEProtectDead   → Slot2CEProtectDeadView
  - CERemoveProtectDead → Slot2CERemoveProtectDeadView (LIST)
  - CELoreFormat    → Slot2CELoreFormatView
  - CEEraseEnchant  → Slot2CEEraseEnchantView
  - CEProtectDestroy → Slot2CEProtectDestroyView
  - CERemoveGem     → Slot2CERemoveGemView (LIST)
```

---

## 8. Implementation Plan

### Step 0: File Structure

```
menu/anvil/
  +-- CEAnvilCustomMenu.java          (NEW - extends AbstractMenu)
  +-- CEAnvilExtraData.java           (NEW - extends ExtraData)
  +-- CEAnvilSettings.java            (NEW - @Configuration for slot mappings)
  +-- item/
  |     +-- AnvilSlotItem.java        (NEW - AbstractItem for slot1/slot2)
  |     +-- AnvilConfirmItem.java     (NEW - AbstractItem for confirm)
  |     +-- AnvilPreviewItem.java     (NEW - AbstractItem for preview1-5)
  |     +-- AnvilPageItem.java        (NEW - AbstractItem for prev/next page)
  +-- [KEEP] AnvilSlot1View.java      (ADAPT - change menu reference)
  +-- [KEEP] AnvilSlot2View.java      (ADAPT - change menu reference)
  +-- [KEEP] AnvilSlot2ListView.java  (ADAPT - change menu reference)
  +-- [KEEP] Slot1CEWeaponView.java   (ADAPT - minimal changes)
  +-- [KEEP] Slot2CE*.java (13 files) (ADAPT - minimal changes)
  +-- [KEEP] CEAnvilMenu.java         (KEEP temporarily for legacy compatibility)
  +-- [KEEP] CEAnvilMenuListener.java (KEEP temporarily for legacy compatibility)
```

### Step 1: Create CEAnvilSettings (Config-driven slots)

```java
@Getter
@Configuration
public class CEAnvilSettings {
    // Slot positions read from YAML data section
    // These are NOT hardcoded - they come from the YAML config

    // Read from menuData.getItemMap() at setup time
    private Map<String, List<Integer>> slotMap;       // itemName → slot numbers
    private Map<String, ItemStack> defaultItemStacks;  // itemName → YAML default appearance
    private int[] previewIndexOrder;                   // display order [3,2,4,1,5]

    public void initialize(MenuData menuData) {
        slotMap = new HashMap<>();
        defaultItemStacks = new HashMap<>();

        for (Map.Entry<String, ItemData> entry : menuData.getItemMap().entrySet()) {
            String name = entry.getKey();
            ItemData itemData = entry.getValue();
            slotMap.put(name, itemData.getSlots());
            defaultItemStacks.put(name, itemData.getItemStackBuilder().getItemStack());
        }

        // Read preview index order from data section
        // Default: [3, 2, 4, 1, 5] (center-first spiral)
        AdvancedConfigurationSection dataConfig = menuData.getDataConfig();
        if (dataConfig != null && dataConfig.contains("preview-index-order")) {
            previewIndexOrder = dataConfig.getIntegerList("preview-index-order")
                .stream().mapToInt(Integer::intValue).toArray();
        } else {
            previewIndexOrder = new int[]{3, 2, 4, 1, 5};
        }
    }

    public List<Integer> getSlots(String itemName) {
        return slotMap.getOrDefault(itemName, Collections.emptyList());
    }

    public ItemStack getDefaultItemStack(String itemName) {
        ItemStack cached = defaultItemStacks.get(itemName);
        return cached != null ? cached.clone() : null;
    }
}
```

### Step 2: Create CEAnvilExtraData

```java
@Getter
@Setter
public class CEAnvilExtraData extends ExtraData {
    private AnvilSlot1View view1;
    private AnvilSlot2View view2;
    private CEAnvilMenu.ItemData itemData1;   // Reuse existing ItemData class
    private CEAnvilMenu.ItemData itemData2;
    private CEAnvilSettings settings;
}
```

### Step 3: Adapt View Base Classes

The key change: views need to call `updateSlots()` on the new menu. Two approaches:

**Option A: Interface (Recommended)**

Create an interface that the new menu implements, and views call through:

```java
public interface AnvilMenuAccess {
    CEAnvilMenu.ItemData getItemData1();
    CEAnvilMenu.ItemData getItemData2();
    void updateSlots(String itemName, ItemStack itemStack);
    ItemStack getItemStack(Player player, String itemName);
    CMenuView getMenuView();  // For backward compat if needed
}
```

**Option B: Adapter in CEAnvilCustomMenu**

Keep `CEAnvilMenu` as the view-facing API and delegate to `CEAnvilCustomMenu` internally. This minimizes changes to view classes.

**Recommendation:** Option A is cleaner but requires changing all 15 view constructors. Option B is pragmatic for Phase 3. We can refactor to Option A in Phase 7 (cleanup).

### Step 4: Create AbstractItem Subclasses

#### AnvilSlotItem (for slot1 and slot2)

```java
public class AnvilSlotItem extends AbstractItem<CEAnvilCustomMenu> {
    @Override
    public String getType() { return "anvil-slot"; }

    @Override
    public void handleClick(ClickData data) {
        String slotName = itemData.getDataConfig().getString("slot-name");
        menu.returnItem(slotName);
    }
}
```

#### AnvilConfirmItem

```java
public class AnvilConfirmItem extends AbstractItem<CEAnvilCustomMenu> {
    @Override
    public String getType() { return "anvil-confirm"; }

    @Override
    public void handleClick(ClickData data) {
        menu.confirm();
    }
}
```

#### AnvilPreviewItem

```java
public class AnvilPreviewItem extends AbstractItem<CEAnvilCustomMenu> {
    @Override
    public String getType() { return "anvil-preview"; }

    @Override
    public void handleClick(ClickData data) {
        // Delegate to view2.clickProcess() with the item name
        // Item name like "preview1", "preview2", etc.
        String previewName = getItemName();  // from YAML item key
        CEAnvilExtraData extra = menu.getExtraData();
        if (extra.getView2() != null) {
            extra.getView2().clickProcess(previewName);
        }
    }
}
```

#### AnvilPageItem

```java
public class AnvilPageItem extends AbstractItem<CEAnvilCustomMenu> {
    @Override
    public String getType() { return "anvil-page"; }

    @Override
    public void handleClick(ClickData data) {
        String pageName = itemData.getDataConfig().getString("page-action"); // "next-page" or "previous-page"
        CEAnvilExtraData extra = menu.getExtraData();
        if (extra.getView2() != null) {
            extra.getView2().clickProcess(pageName);
        }
    }
}
```

### Step 5: Create CEAnvilCustomMenu

The main menu class that:
1. Extends `AbstractMenu<MenuData, CEAnvilExtraData>`
2. Implements `AnvilMenuAccess` (or provides equivalent methods)
3. Contains all the core logic from current `CEAnvilMenu` (addItem, confirm, returnItem, updateMenu)
4. Routes AbstractItem clicks to appropriate view methods

### Step 6: Convert YAML

Convert `ce-anvil.yml` from legacy format to BafFramework format:
- Remove `settings.actions`, `layout`
- Add `type: 'ce-anvil'`
- Convert character-grid slots to direct slot numbers
- Keep all confirm variants as template items (no slot assigned)
- Add `data:` section for config values

### Step 7: Register in MenuModule

```java
// In MenuModule.registerMenu()
MenuRegister.instance().registerStrategy(CEAnvilCustomMenu.class);

// View registration stays the same
CEAnvilMenu.registerView1(CEItemType.WEAPON, Slot1CEWeaponView.class);
// ... all 13 Slot2 registrations
```

### Step 8: Update Command

Update the command that opens the anvil to use `MenuOpener` instead of legacy `CustomMenuAPI`.

---

## 9. Testing Strategy

### Unit Tests

| Test Case | What It Verifies |
|-----------|-----------------|
| `addItem_weapon_setsView1` | Adding CEWeapon sets view1 to Slot1CEWeaponView |
| `addItem_weapon_setsDefaultView2` | Adding weapon auto-creates Slot2CEDefaultView |
| `addItem_book_setsBookView` | Adding CEBook sets view2 to Slot2CEBookView |
| `addItem_duplicateSlot1_returnsAlreadyHas` | Adding 2nd weapon returns ALREADY_HAS_SLOT1 |
| `addItem_duplicateSlot2_returnsAlreadyHas` | Adding 2nd modifier returns ALREADY_HAS_SLOT2 |
| `addItem_unsupported_returnsNotSuitable` | Adding non-CE item returns NOT_SUITABLE |
| `returnItem_slot1_clearsAll` | Returning slot1 clears view1, view2 previews |
| `returnItem_slot2_revertsToDefault` | Returning slot2 reverts to DefaultView |
| `confirm_book_appliesEnchant` | Book confirm applies enchant to weapon |
| `confirm_destroy_removesSlot1` | Failed apply with DESTROY removes weapon |
| `viewTransition_clearsPreview` | Switching view2 clears old preview |
| `handleClose_returnsAllItems` | Closing menu returns both items to player |
| `slotsFromConfig_notHardcoded` | All slot numbers come from settings/YAML |

### Edge Cases

1. **Close menu with items** - both items returned
2. **Add modifier before weapon** - NOT_SUITABLE (no slot1 to pair with)
3. **Confirm with stacked items** - amount correctly decremented
4. **ListView pagination** - page navigation works, selection persists
5. **DefaultView enchant list** - filtered by enchant groups from config
6. **GemDrill chance display** - placeholder `{chance}` resolved correctly
7. **Rapid clicks** - no duplication of items

### Manual Testing Checklist

- [ ] Open anvil menu, items display correctly
- [ ] Place weapon in slot1 via player inventory click
- [ ] Default view shows removable enchants from weapon
- [ ] Place book in slot2, preview updates to book view
- [ ] Confirm applies enchant, amounts update
- [ ] Return slot2 item, reverts to default view
- [ ] Return slot1 item, clears everything
- [ ] Close menu, all items returned
- [ ] All 13 slot2 view types work correctly
- [ ] ListView pagination (prev/next) works
- [ ] Confirm button appearance changes per view type
- [ ] No hardcoded slots - slots match YAML config

---

## 10. Risk Assessment

### HIGH Risk

| Risk | Impact | Mitigation |
|------|--------|------------|
| View classes tightly coupled to CMenuView | Views may not work without CMenuView | Create adapter that mimics CMenuView interface |
| 13 confirm variants need template access | Wrong confirm button may show | Store all templates in settings, verify at startup |
| Preview slot mapping hardcoded in views | Views use hardcoded index-to-slot mapping | Create configurable mapping, pass via settings |

### MEDIUM Risk

| Risk | Impact | Mitigation |
|------|--------|------------|
| `removeTemporaryItem` → no equivalent | Slots don't revert to YAML default | Store defaults in settings, restore explicitly |
| Slot2CEDefaultView has duplicated pagination | May diverge from AnvilSlot2ListView | Consider refactoring to extend ListView |
| View registration uses reflection | May break with constructor changes | Keep view constructors compatible |

### LOW Risk

| Risk | Impact | Mitigation |
|------|--------|------------|
| YAML conversion errors | Wrong slot numbers | Verify against layout grid, test each slot |
| Sound not playing | Missing sound on open | Handle in setupMenu() or data config |
| Return menu command | RETURN_MENU execute may not work | Implement manually in handleClick |

---

## Documents Index

| File | Purpose |
|------|---------|
| `issue-34-ceanvil-migration.md` | This master document (READ FIRST) |
| `ARCHITECTURE_COMPARISON.md` | Detailed legacy vs new architecture |
| `VIEW_CATALOG.md` | All 15 view classes with behavior details |
| `YAML_CONVERSION.md` | Complete YAML conversion reference |
| `IMPLEMENTATION_CHECKLIST.md` | Step-by-step implementation checklist |

---

## Summary

The CEAnvilMenu migration is the most complex phase because:
1. **15 view strategy classes** that control multiple slots each
2. **Dynamic confirm button** with 13 variants
3. **No `removeTemporaryItem()` equivalent** in BafFramework
4. **Pagination inside views** (not using framework pagination)

The strategy is:
1. **Keep all view classes** - they contain irreplaceable business logic
2. **Create an adapter layer** so views work with the new menu
3. **Config-drive ALL slot numbers** - nothing hardcoded in Java
4. **Store YAML defaults** for slot restoration
5. **Use AbstractItem subclasses** as thin click routers to views

Expected iterations: **2-3** (applying lessons from Phase 2's 7 iterations)
