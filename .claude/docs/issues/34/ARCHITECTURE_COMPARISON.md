# Architecture Comparison: Legacy vs BafFramework

## Event Flow Comparison

### Legacy: CEAnvilMenuListener

```
Player clicks player inventory:
  InventoryClickEvent
    → CEAnvilMenuListener.onInventoryClick(e)
      → Check: clickedInventory.getType() == PLAYER
      → CEAPI.getCEItem(clickedItem)
      → menu.addItem(clickedItem, ceItem)
      → If SUCCESS: e.setCurrentItem(null)
      → Send message based on reason

Player clicks menu GUI:
  CustomMenuClickEvent
    → CEAnvilMenuListener.onMenuClick(e)
      → e.getClickedCItem().getName() → "slot1", "confirm", "preview3", etc.
      → menu.clickProcess(name)
        → if "slot1"/"slot2": returnItem(name)
        → if "confirm": confirm()
        → view1.clickProcess(name)
        → view2.clickProcess(name)

Player opens menu:
  CustomMenuOpenEvent
    → CEAnvilMenuListener.onMenuOpen(e)
      → CEAnvilMenu.putCEAnvilMenu(player, cMenuView)

Player closes menu:
  CustomMenuCloseEvent
    → CEAnvilMenuListener.onMenuClose(e)
      → CEAnvilMenu.removeCEAnvilMenu(player).returnItems()
```

### New: CEAnvilCustomMenu (BafFramework)

```
Player clicks player inventory:
  Framework separates events automatically
    → CEAnvilCustomMenu.handlePlayerInventoryClick(ClickData)
      → CEAPI.getCEItem(clickedItem)
      → addItem(clickedItem, ceItem)
      → If SUCCESS: data.getEvent().setCurrentItem(null)
      → Send message based on reason

Player clicks menu GUI:
  Framework routes click to AbstractItem at that slot
    → AbstractItem.handleClick(ClickData)
      → AnvilSlotItem: menu.returnItem("slot1")
      → AnvilConfirmItem: menu.confirm()
      → AnvilPreviewItem: extraData.getView2().clickProcess("preview3")
      → AnvilPageItem: extraData.getView2().clickProcess("next-page")
      → DefaultItem: execute condition/true-execute (for return button)

Player opens menu:
  MenuOpener.builder()
    → Framework creates CEAnvilCustomMenu instance
    → setExtraData(extraData) - pre-populated state
    → setupMenu(player, menuData)
    → openInventory()

Player closes menu:
  Framework calls handleClose()
    → CEAnvilCustomMenu.handleClose()
      → returnItems() to player
```

## Slot Update Comparison

### Legacy: Name-Based via CMenuView

```java
// CEAnvilMenu extends MenuAbstract
public class MenuAbstract {
    protected CMenuView menuView;  // Legacy CustomMenu API

    public void updateSlots(String itemName, ItemStack itemStack) {
        for (Integer slot : getSlots(itemName)) {
            if (itemStack == null) {
                menuView.removeTemporaryItem(slot);  // Reverts to YAML default
            } else {
                menuView.setTemporaryItem(slot, itemStack);  // Overlays temp item
            }
        }
    }

    public List<Integer> getSlots(String itemName) {
        CItem cItem = menuView.getCMenu().getMenuItem().getCItemByName(itemName);
        return cItem.getSlots();  // Slot numbers from CItem
    }

    public ItemStack getItemStack(Player player, String itemName) {
        CItem cItem = menuView.getCMenu().getMenuItem().getCItemByName(itemName);
        return cItem.getItemStack(player);  // Configured appearance
    }
}
```

**Key:** `removeTemporaryItem(slot)` automatically reverts to the YAML-configured item.

### New: Config-Driven via Inventory + Settings

```java
// CEAnvilCustomMenu extends AbstractMenu
public class CEAnvilCustomMenu extends AbstractMenu<MenuData, CEAnvilExtraData> {
    // inventory is the Bukkit Inventory (from AbstractMenu)

    public void updateSlots(String itemName, ItemStack itemStack) {
        CEAnvilSettings settings = extraData.getSettings();
        for (int slot : settings.getSlots(itemName)) {
            if (itemStack == null) {
                // Must EXPLICITLY restore default (no auto-revert!)
                inventory.setItem(slot, settings.getDefaultItemStack(itemName));
            } else {
                inventory.setItem(slot, itemStack);
            }
        }
    }

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
}
```

**Key Difference:** No `removeTemporaryItem()`. Must store defaults and restore manually.

## State Management Comparison

### Legacy: Static HashMap

```java
public class CEAnvilMenu extends MenuAbstract {
    // Static map: player name → menu instance
    private static HashMap<String, CEAnvilMenu> map = new HashMap<>();

    // Static view registries (shared across all menus)
    private static HashMap<String, AnvilSlot1View> slot1ViewMap = new HashMap<>();
    private static HashMap<String, AnvilSlot2View> slot2ViewMap = new HashMap<>();

    // Instance state
    private AnvilSlot1View view1;
    private AnvilSlot2View view2;
    private ItemData itemData1;
    private ItemData itemData2;

    public static CEAnvilMenu putCEAnvilMenu(Player p, CMenuView v) { ... }
    public static CEAnvilMenu getCEAnvilMenu(Player p) { ... }
    public static CEAnvilMenu removeCEAnvilMenu(Player p) { ... }
}
```

**Problems:**
- Static map requires manual cleanup on close
- If close event is missed → memory leak
- No framework lifecycle management

### New: ExtraData (Framework-Managed)

```java
public class CEAnvilExtraData extends ExtraData {
    // Instance state (framework creates per menu instance)
    private AnvilSlot1View view1;
    private AnvilSlot2View view2;
    private CEAnvilMenu.ItemData itemData1;
    private CEAnvilMenu.ItemData itemData2;
    private CEAnvilSettings settings;
}

// In CEAnvilCustomMenu
public class CEAnvilCustomMenu extends AbstractMenu<MenuData, CEAnvilExtraData> {
    // View registries stay static (they're shared type lookups)
    private static Map<String, AnvilSlot1View> slot1ViewMap = new HashMap<>();
    private static Map<String, AnvilSlot2View> slot2ViewMap = new HashMap<>();

    // Per-instance state is in extraData (framework-managed)
    // No static player map needed!
}
```

**Benefits:**
- Framework creates/destroys menu instances automatically
- No static player map → no memory leak risk
- ExtraData is tied to menu lifecycle

## Click Routing Comparison

### Legacy: String-Based Name Dispatch

```java
// In CEAnvilMenu
public void clickProcess(String name) {
    if (name.equals("slot1") || name.equals("slot2")) {
        returnItem(name);
    }
    if (name.equals("confirm")) {
        confirm();
    }
    if (view1 != null) {
        view1.clickProcess(name);  // Forward ALL names to views
    }
    if (view2 != null) {
        view2.clickProcess(name);  // Forward ALL names to views
    }
}
```

**Problem:** Every click forwards to ALL views, even if irrelevant.

### New: Slot-Based AbstractItem Dispatch

```java
// Framework routes click to the AbstractItem at the clicked slot
// Each AbstractItem only handles its own slot

public class AnvilSlotItem extends AbstractItem<CEAnvilCustomMenu> {
    @Override
    public void handleClick(ClickData data) {
        String slotName = itemData.getDataConfig().getString("slot-name");
        menu.returnItem(slotName);
    }
}

public class AnvilConfirmItem extends AbstractItem<CEAnvilCustomMenu> {
    @Override
    public void handleClick(ClickData data) {
        menu.confirm();
    }
}

public class AnvilPreviewItem extends AbstractItem<CEAnvilCustomMenu> {
    @Override
    public void handleClick(ClickData data) {
        String previewName = itemData.getDataConfig().getString("preview-name");
        if (menu.getExtraData().getView2() != null) {
            menu.getExtraData().getView2().clickProcess(previewName);
        }
    }
}
```

**Benefit:** Clean separation - each item handles only its concern.

## Summary: What Changes, What Stays

| Component | Change Type | Reason |
|-----------|-------------|--------|
| `CEAnvilMenu.addItem()` | **Move** to CEAnvilCustomMenu | Core logic preserved |
| `CEAnvilMenu.confirm()` | **Move** to CEAnvilCustomMenu | Core logic preserved |
| `CEAnvilMenu.returnItem()` | **Move** to CEAnvilCustomMenu | Core logic preserved |
| `CEAnvilMenu.updateMenu()` | **Move** to CEAnvilCustomMenu | Core logic preserved |
| `CEAnvilMenu.updateSlots()` | **Rewrite** | CMenuView → Inventory + config |
| `CEAnvilMenu.getItemStack()` | **Rewrite** | CItem → MenuData.getItemMap() |
| `CEAnvilMenu.static map` | **Remove** | ExtraData replaces static state |
| `CEAnvilMenuListener` | **Remove** | handleClick/handlePlayerInventoryClick replaces |
| `MenuAbstract` | **Not used** | AbstractMenu replaces |
| `AnvilSlot1View` | **Adapt** | Menu reference type changes |
| `AnvilSlot2View` | **Adapt** | Menu reference type changes |
| `AnvilSlot2ListView` | **Adapt** | Menu reference type changes |
| `Slot1CEWeaponView` | **Minimal** | Just constructor |
| 13x `Slot2CE*View` | **Minimal** | Just constructor + menu.updateSlots() |
| `ce-anvil.yml` | **Rewrite** | Legacy format → BafFramework format |
