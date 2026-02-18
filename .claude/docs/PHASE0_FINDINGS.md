# Phase 0 Findings: CustomMenu Migration

## API Availability ✅ CONFIRMED

### New CustomMenu BafFramework API
**Package:** `com.bafmc.bukkit.bafframework.custommenu.*`
**Source:** `BafFramework:1.0.0` dependency
**Status:** ✅ **AVAILABLE**

**Key Classes Confirmed:**
- `com.bafmc.bukkit.bafframework.custommenu.menu.AbstractMenu`
- `com.bafmc.bukkit.bafframework.custommenu.menu.AbstractItem`
- `com.bafmc.bukkit.bafframework.custommenu.menu.MenuOpener`
- `com.bafmc.bukkit.bafframework.custommenu.register.MenuRegister`
- `com.bafmc.bukkit.bafframework.custommenu.data.MenuData`
- `com.bafmc.bukkit.bafframework.custommenu.data.ExtraData`

### Legacy CustomMenu API
**Package:** `com.bafmc.custommenu.*`
**Source:** `CustomMenu:1.0.0` dependency (compileOnly)
**Status:** ✅ **AVAILABLE** (currently in use)

**Currently Used Classes:**
- `com.bafmc.custommenu.menu.CMenuView`
- `com.bafmc.custommenu.menu.CItem`
- `com.bafmc.custommenu.player.CPlayer`
- `com.bafmc.custommenu.api.CustomMenuAPI`
- `com.bafmc.custommenu.event.*` (CustomMenuClickEvent, etc.)

### Extension APIs Status
**Status:** ⚠️ **NOT FOUND IN TEST CLASSPATH**

These classes are expected in the legacy `com.bafmc.custommenu` package but not available during testing (likely runtime-only):
- `com.bafmc.custommenu.menu.Catalog` (used by `CEBookCatalog`)
- `com.bafmc.custommenu.menu.ItemDisplaySetup` (used by `CustomEnchantmentItemDisplaySetup`)
- `com.bafmc.custommenu.menu.trade.TradeItemCompare` (used by `CustomEnchantmentTradeItemCompare`)

**Implication:** Need to verify if these exist in the new API or require legacy dependency retention.

---

## YAML Format Mapping

### Old Format (Legacy CustomMenu)
```yaml
settings:
  title: '&6Menu Title'
  rows: 6
  sound: 'ENTITY_EXPERIENCE_ORB_PICKUP'
  actions:
    cancel-click-type: 'LEFT,RIGHT'

layout:
  - 'aaaaaaaaa'  # Row 0 (slots 0-8)
  - 'bbbbbbbbb'  # Row 1 (slots 9-17)
  - 'ccccccccc'  # Row 2 (slots 18-26)
  - 'ddddddddd'  # Row 3 (slots 27-35)
  - 'eeeeeeeee'  # Row 4 (slots 36-44)
  - 'fffffffff'  # Row 5 (slots 45-53)

items:
  my-item:
    slots: "a"  # References layout character
    item:
      type: DIAMOND
      display: '&bItem Name'
    command:
      condition:
        CLICK_TYPE: 'LEFT'
      execute:
        - 'RETURN_CLOSE_MENU'
    priority: 100  # Conditional display
```

### New Format (BafFramework CustomMenu)
```yaml
type: 'default'  # Or custom menu type
title: '&6Menu Title'
row: 6

items:
  my-item:
    slot: 0,1,2,3,4,5,6,7,8  # Direct slot numbers (was layout 'a')
    item:
      type: DIAMOND
      display: '&bItem Name'
    condition:
      - "click-type:left"
    execute:
      - "return-close-menu"
```

### Slot Number Reference

| Row | Layout Position | Slot Numbers |
|-----|----------------|--------------|
| 0 | First line | 0-8 |
| 1 | Second line | 9-17 |
| 2 | Third line | 18-26 |
| 3 | Fourth line | 27-35 |
| 4 | Fifth line | 36-44 |
| 5 | Sixth line (last) | 45-53 |

### Conversion Formula
```
slot_number = (row_index * 9) + column_index
```

Where:
- `row_index` = 0-5 (top to bottom)
- `column_index` = 0-8 (left to right)

### Key Format Changes

| Old | New | Notes |
|-----|-----|-------|
| `settings.title` | `title` | Top-level key |
| `settings.rows` | `row` | Singular, top-level |
| `settings.sound` | ? | Check if supported or moved to data |
| `settings.actions` | Handled by framework | Auto-cancel behavior |
| `layout` (grid) | Removed | Use direct slot numbers |
| `items.X.slots: "c"` | `items.X.slot: 18,19,20,...` | Numeric slots |
| `items.X.command` | `items.X.execute` | Renamed |
| `items.X.priority` | `items.X.condition` | Map to conditional display |
| `CLICK_TYPE` condition | `click-type` condition | Lowercase, hyphenated |
| `RETURN_MENU` execute | `return-menu` | Lowercase, hyphenated |
| `RETURN_CLOSE_MENU` execute | `return-close-menu` | Lowercase, hyphenated |
| `HAS_PREVIOUS_MENU` condition | `has-previous-menu` | Lowercase, hyphenated |

---

## Player-Inventory Click Handling

### ✅ SOLUTION FOUND

**Question:** How does the new API handle clicks in the PLAYER's inventory (not the menu GUI)?

**Why This Matters:**
All 6 interactive menus need to intercept `InventoryClickEvent` when the player clicks items in their own inventory to add them to the menu. For example:
- Tinkerer: Click CE items in player inventory → add to tinkerer slots
- Book Craft: Click books in player inventory → add to book slots
- CE Anvil: Click weapons/books in player inventory → add to anvil slots
- Equipment: Click armor in player inventory → equip to armor slots

**Legacy System:**
```java
@EventHandler
public void onInventoryClick(InventoryClickEvent event) {
    // Intercepts clicks in player's inventory
    if (event.getClickedInventory() == player.getInventory()) {
        ItemStack clicked = event.getCurrentItem();
        menu.addItem(clicked);  // Add to menu
    }
}
```

**New System Solution:**

Framework's `InventoryListener` calls `AbstractMenu.handleClick(ClickData)` for **ALL** inventory clicks (both menu GUI and player inventory). The `ClickData` object contains:

```java
public class ClickData {
    private Player player;
    private int clickedSlot;
    private Inventory clickedInventory;       // ✅ Menu GUI or player inventory!
    private InventoryClickEvent event;        // ✅ Full Bukkit event access!
}
```

**Implementation Pattern:**

Override `handleClick()` in custom menu classes to handle player-inventory clicks:

```java
public class TinkererCustomMenu extends AbstractMenu<MenuData, TinkererExtraData> {

    @Override
    public void handleClick(ClickData data) {
        // Check if click was in player's inventory (not menu GUI)
        if (data.getClickedInventory() == data.getPlayer().getInventory()) {
            handlePlayerInventoryClick(data);  // Custom logic
            return;
        }

        // Menu GUI click - delegate to default behavior
        super.handleClick(data);  // Routes to AbstractItem.handleClick()
    }

    private void handlePlayerInventoryClick(ClickData data) {
        Player player = data.getPlayer();
        ItemStack clicked = data.getEvent().getCurrentItem();

        if (clicked != null && isTinkererItem(clicked)) {
            addItemToSlot(clicked);
            data.getEvent().setCancelled(true);  // Prevent normal inventory behavior
        }
    }
}
```

**Why This Works:**

1. Framework calls `handleClick()` for **ALL** clicks while menu is open
2. We can check `data.getClickedInventory()` to distinguish menu vs player inventory
3. We override `handleClick()` to intercept player-inventory clicks **before** they reach the default item-based routing
4. Default behavior (`super.handleClick()`) still handles menu GUI clicks normally

**Status:** ✅ **SOLVED** - No custom listener needed, no framework changes needed!

---

## Decision Point: GO / NO-GO

### ✅ GO Criteria Met

- [x] New CustomMenu BafFramework API available (`com.bafmc.bukkit.bafframework.custommenu.*`)
- [x] All required classes present (`AbstractMenu`, `AbstractItem`, `MenuOpener`, `MenuRegister`, etc`)
- [x] YAML format mapping documented
- [x] **Player-inventory click solution FOUND** (override `handleClick()` to check `clickedInventory`)

### ⚠️ Remaining Questions (Can be resolved during implementation)

- [ ] **Extension APIs** - Do `Catalog`, `ItemDisplaySetup`, `TradeItemCompare` exist in new API? (Phase 6 concern)
- [ ] **Built-in executes/conditions** - Are `RETURN_MENU`, `HAS_PREVIOUS_MENU`, etc. available? (Phase 6 concern)

### Recommendation

**🟢 FULL GO:** All critical Phase 0 requirements met!

**Next Step:** Proceed to **Phase 1 - Tinkerer Menu Migration (Prototype)**

The player-inventory click solution is elegant and requires no framework changes. Remaining questions (extension APIs, built-in features) only affect Phase 6 (declarative YAMLs) and can be investigated later.

---

## Phase 0 Summary

### ✅ Completed Tasks

1. ✅ **API Availability** - CONFIRMED - New API in `BafFramework:1.0.0`
2. ✅ **YAML Mapping** - DOCUMENTED - Complete conversion guide created
3. ✅ **Click Handling** - SOLVED - Override pattern for player-inventory clicks

### ⏸️ Deferred to Later Phases

4. ⏸️ **Extension APIs** - Deferred to Phase 6 (only affects catalog/trade menus)
5. ⏸️ **Built-in Features** - Deferred to Phase 6 (only affects declarative YAMLs)

### Phase 0 Status: ✅ **COMPLETE**

**Time Spent:** ~2 hours
**Outcome:** All critical blockers resolved, ready for Phase 1
**Confidence Level:** HIGH - Clean migration path identified
