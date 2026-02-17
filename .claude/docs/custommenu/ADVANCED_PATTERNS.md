# Advanced CustomMenu Patterns

This guide covers advanced patterns for building complex menus with the BafFramework CustomMenu system. All examples are generic and applicable to any plugin.

## Table of Contents

- [Handler Strategy Pattern](#handler-strategy-pattern)
- [Template Items (No-Slot Items)](#template-items-no-slot-items)
- [State Machine Menu](#state-machine-menu)
- [Paginated List Handler](#paginated-list-handler)
- [Settings Cache Pattern](#settings-cache-pattern)
- [Dynamic Slot Updates](#dynamic-slot-updates)
- [Item Delegation Pattern](#item-delegation-pattern)
- [Common Pitfalls](#common-pitfalls)

---

## Handler Strategy Pattern

**When to use:** A menu slot changes its behavior depending on what type of item the player places (e.g., a crafting menu that handles swords differently from potions).

**Problem:** A single menu needs 10+ different behaviors based on item type, each with its own display, confirmation, and action logic.

**Solution:** Define a handler interface and register implementations per type using `Supplier` factories.

### Step 1: Define the Handler Interface

```java
public interface SlotHandler {
    /** Check if this handler can handle the given item */
    boolean isSuitable(ItemStack item);

    /** Update preview/display slots based on current state */
    void updateView(MyMenu menu);

    /** Update the confirm button appearance */
    void updateConfirm(MyMenu menu);

    /** Handle clicks on preview/navigation items */
    void clickProcess(MyMenu menu, String itemName);

    /** Execute the action when confirm is clicked */
    boolean apply(MyMenu menu);

    /** Clear all preview slots (reset to YAML default) */
    void clearPreviews(MyMenu menu);
}
```

### Step 2: Implement Handlers

```java
// Simple handler — shows preview and applies on confirm
public class SwordUpgradeHandler implements SlotHandler {

    @Override
    public boolean isSuitable(ItemStack item) {
        return item.getType() == Material.DIAMOND_SWORD;
    }

    @Override
    public void updateView(MyMenu menu) {
        // Show preview of the upgraded result
        ItemStack preview = createUpgradedPreview(menu.getSlot1Item());
        menu.updateSlots("preview-center", preview);
    }

    @Override
    public void updateConfirm(MyMenu menu) {
        // Fetch the "confirm-upgrade" template from YAML (defined without slot)
        ItemStack confirmItem = menu.getTemplateItemStack("confirm-upgrade");
        menu.updateSlots("confirm", confirmItem);
    }

    @Override
    public void clickProcess(MyMenu menu, String itemName) {
        // No extra click handling for simple handlers
    }

    @Override
    public boolean apply(MyMenu menu) {
        // Perform the upgrade
        return upgradeItem(menu.getSlot1Item(), menu.getSlot2Item());
    }

    @Override
    public void clearPreviews(MyMenu menu) {
        menu.updateSlots("preview-center", null);  // Reset to YAML default
    }
}
```

### Step 3: Register Handlers with Suppliers

```java
public class MyMenu extends AbstractMenu<MenuData, MyExtraData> {

    private static final Map<String, Supplier<SlotHandler>> handlerFactoryMap = new HashMap<>();

    /** Register a handler factory (called once during plugin startup) */
    public static void registerHandler(String type, Supplier<SlotHandler> factory) {
        handlerFactoryMap.put(type, factory);
    }

    /** Find and create a NEW handler instance for the given item */
    public SlotHandler findHandler(ItemStack item) {
        for (Map.Entry<String, Supplier<SlotHandler>> entry : handlerFactoryMap.entrySet()) {
            SlotHandler prototype = entry.getValue().get();
            if (prototype.isSuitable(item)) {
                return entry.getValue().get();  // Create NEW instance (not reuse prototype)
            }
        }
        return null;
    }
}
```

### Step 4: Register During Plugin Startup

```java
// In MenuModule.onEnable()
MyMenu.registerHandler("sword", SwordUpgradeHandler::new);
MyMenu.registerHandler("potion", PotionBrewHandler::new);
MyMenu.registerHandler("armor", ArmorEnhanceHandler::new);
```

**Why `Supplier` (not singleton):** Each player's menu needs its own handler instance. Handlers may hold per-player state (pagination page, selected index, etc.). `Supplier::get` creates a fresh instance each time, preventing state leakage between players.

```java
// BAD: Singleton handler — state shared between ALL players
handlerMap.put("sword", new SwordUpgradeHandler());  // One instance for everyone!

// GOOD: Supplier creates NEW instance per player menu
handlerFactoryMap.put("sword", SwordUpgradeHandler::new);  // Fresh instance each time
```

---

## Template Items (No-Slot Items)

**When to use:** You need YAML-defined ItemStack appearances that are used dynamically by code, but don't occupy a fixed slot in the menu.

**Problem:** A confirm button has multiple different appearances depending on context (e.g., "Confirm upgrade", "Confirm brew", "Confirm repair", "Not enough materials"). You want these configurable in YAML but they shouldn't display by default.

**Solution:** Define items in YAML without a `slot:` property. Override `setupItems()` to skip them. Fetch them as templates at runtime.

### YAML Configuration

```yaml
items:
  # Normal item WITH slot — displayed on menu open
  confirm:
    type: my-confirm
    item:
      type: ANVIL
      amount: 1
      display: '&ePlease select items first'
    slot: 31

  # Template item WITHOUT slot — fetched dynamically by code
  confirm-upgrade:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eConfirm upgrade'
      lore:
        - ''
        - '&fClick to upgrade'

  confirm-brew:
    type: default
    item:
      type: BREWING_STAND
      amount: 1
      display: '&eConfirm brew'
      lore:
        - ''
        - '&fClick to brew'

  # Template with placeholder support
  confirm-with-cost:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eConfirm action'
      lore:
        - ''
        - '&aCost: &f{cost} coins'
        - ''
        - '&fClick to confirm'
```

### Override setupItems()

The framework's default `super.setupItems()` calls `setItem(getSlots())` on every item — this **crashes with NPE** on items without a `slot:` property.

```java
@Override
public void setupItems() {
    // Cannot call super.setupItems() because template items have no slot.
    // The framework's displayItemStack() calls setItem(getSlots()) which
    // NPEs on null slots, crashing the entire forEach loop.
    for (ItemData itemData : getMenuData().getItemMap().values()) {
        List<Integer> slots = itemData.getSlots();
        if (slots != null && !slots.isEmpty()) {
            setupItem(itemData);  // Only setup items that have slots
        }
    }

    // Initialize any custom settings after items are set up
    extraData.getSettings().initialize(getMenuData());
}
```

### Fetch Templates at Runtime

Add helper methods to your menu class:

```java
/** Fetch a template item's ItemStack by name from YAML */
public ItemStack getTemplateItemStack(String templateName) {
    MenuData menuData = getMenuData();
    if (menuData == null || menuData.getItemMap() == null) return null;

    ItemData data = menuData.getItemMap().get(templateName);
    if (data == null) return null;

    return data.getItemStackBuilder().getItemStack();
}

/** Fetch a template item's ItemStack with placeholder replacement */
public ItemStack getTemplateItemStack(String templateName, Placeholder placeholder) {
    MenuData menuData = getMenuData();
    if (menuData == null || menuData.getItemMap() == null) return null;

    ItemData data = menuData.getItemMap().get(templateName);
    if (data == null) return null;

    return data.getItemStackBuilder().getItemStack(placeholder);
}
```

Usage in handler:

```java
// Simple template
ItemStack confirmItem = menu.getTemplateItemStack("confirm-upgrade");
menu.updateSlots("confirm", confirmItem);

// Template with placeholders
Placeholder ph = PlaceholderBuilder.builder().set("cost", "500").build();
ItemStack confirmItem = menu.getTemplateItemStack("confirm-with-cost", ph);
menu.updateSlots("confirm", confirmItem);
```

**Benefits:**
- Server admins can customize all messages/appearances in YAML without recompiling
- No hardcoded ItemStack creation in Java code
- Placeholder support for dynamic values like cost, chance, etc.

---

## State Machine Menu

**When to use:** Your menu has distinct states with different displays and behaviors depending on which items the player has placed.

**Example states:**
```
EMPTY → SLOT1_ONLY → BOTH_SLOTS
                   ← (return slot2)
        (return slot1) ←
```

### ExtraData as State Container

```java
@Getter @Setter
public class MyExtraData extends ExtraData {
    private SlotHandler activeHandler;  // Current behavior strategy
    private SlotItemData itemData1;     // Slot1 item (e.g., equipment)
    private SlotItemData itemData2;     // Slot2 item (e.g., material)
    private MyMenuSettings settings;    // Cached YAML settings

    public enum AddReason {
        SUCCESS, ALREADY_HAS_SLOT1, ALREADY_HAS_SLOT2, NOT_SUITABLE
    }
}
```

### State Transition Logic

```java
public AddReason addItem(ItemStack itemStack) {
    // SLOT1: Main item
    if (isMainItem(itemStack)) {
        if (extraData.getItemData1() != null) {
            return AddReason.ALREADY_HAS_SLOT1;
        }
        extraData.setItemData1(new SlotItemData(itemStack.clone()));

        // Set default handler when only slot1 is filled
        if (extraData.getActiveHandler() == null) {
            extraData.setActiveHandler(new DefaultInfoHandler());
        }

        updateMenu();
        return AddReason.SUCCESS;
    }

    // SLOT2: Find matching handler
    SlotHandler handler = findHandler(itemStack);
    if (handler != null) {
        if (extraData.getItemData2() != null) {
            return AddReason.ALREADY_HAS_SLOT2;
        }

        // CRITICAL: Clear old handler's previews before switching
        if (extraData.getActiveHandler() != null) {
            extraData.getActiveHandler().clearPreviews(this);
        }

        extraData.setActiveHandler(handler);
        extraData.setItemData2(new SlotItemData(itemStack.clone()));
        updateMenu();
        return AddReason.SUCCESS;
    }

    return AddReason.NOT_SUITABLE;
}
```

### Critical: Return Item with Handler Preservation

When returning slot1 while slot2 still has an item, you must **preserve the handler**:

```java
public void returnItem(String name) {
    if ("slot1".equals(name) && extraData.getItemData1() != null) {
        ItemStack itemStack = extraData.getItemData1().getItemStack();

        if (extraData.getActiveHandler() != null) {
            extraData.getActiveHandler().clearPreviews(this);
        }

        extraData.setItemData1(null);

        // IMPORTANT: Only null handler if slot2 is also empty.
        // If slot2 has an item, preserve the handler so it restores
        // when a new item is added to slot1.
        if (extraData.getItemData2() == null) {
            extraData.setActiveHandler(null);
        }

        InventoryUtils.addItem(owner, itemStack);
        updateMenu();

    } else if ("slot2".equals(name) && extraData.getItemData2() != null) {
        ItemStack itemStack = extraData.getItemData2().getItemStack();

        if (extraData.getActiveHandler() != null) {
            extraData.getActiveHandler().clearPreviews(this);
        }

        // Revert to default info handler if slot1 still has an item
        if (extraData.getItemData1() != null) {
            extraData.setActiveHandler(new DefaultInfoHandler());
        } else {
            extraData.setActiveHandler(null);
        }

        extraData.setItemData2(null);
        InventoryUtils.addItem(owner, itemStack);
        updateMenu();
    }
}
```

### State Transition Table

Always map out all transitions before coding:

| Current State | Action | Handler After | slot1 | slot2 |
|---|---|---|---|---|
| EMPTY | add main item | DefaultInfoHandler | set | null |
| EMPTY | add material | MaterialHandler | null | set |
| SLOT1_ONLY | add material | MaterialHandler | set | set |
| SLOT2_ONLY | add main item | MaterialHandler (preserved) | set | set |
| BOTH_SLOTS | return slot1 | MaterialHandler (preserved) | null | set |
| BOTH_SLOTS | return slot2 | DefaultInfoHandler (reverted) | set | null |
| SLOT1_ONLY | return slot1 | null | null | null |
| BOTH_SLOTS | confirm | depends on result | may change | may change |

---

## Paginated List Handler

**When to use:** Your handler needs to display a scrollable list of items in preview slots (e.g., a list of enchantments, gems, or recipes to choose from).

### Base Class (Template Method Pattern)

```java
public abstract class AbstractListHandler<L> implements SlotHandler {

    @Getter @Setter private int page = 1;
    @Getter @Setter private int maxPage = 1;
    @Getter @Setter private int chooseIndex = -1;
    @Getter @Setter private List<L> list = new ArrayList<>();

    // Subclasses implement these 4 methods:
    /** Get the list of objects to display */
    public abstract List<L> getList(MyMenu menu);
    /** Convert an object to its display ItemStack */
    public abstract ItemStack getDisplayItem(L object);
    /** Execute when confirm is clicked with a selected item */
    public abstract boolean applySelected(MyMenu menu, L selectedObject);
    /** Which YAML template to use for the confirm button */
    public abstract String getConfirmTemplateName();

    @Override
    public void updateView(MyMenu menu) {
        List<L> newList = getList(menu);
        this.list = newList;
        this.maxPage = Math.max(1, (int) Math.ceil(newList.size() / 5.0));
        updateListDisplay(menu);
    }

    @Override
    public void updateConfirm(MyMenu menu) {
        ItemStack confirmItem = menu.getTemplateItemStack(getConfirmTemplateName());
        menu.updateSlots("confirm", confirmItem);
    }

    @Override
    public void clickProcess(MyMenu menu, String itemName) {
        if ("next-page".equals(itemName)) {
            if (page < maxPage) { page++; updateListDisplay(menu); }
        } else if ("previous-page".equals(itemName)) {
            if (page > 1) { page--; updateListDisplay(menu); }
        } else if (itemName.startsWith("preview")) {
            chooseItem(menu, itemName);
        }
    }

    @Override
    public boolean apply(MyMenu menu) {
        if (list.isEmpty() || chooseIndex == -1 || chooseIndex >= list.size()) {
            return false;
        }
        L selected = list.get(chooseIndex);
        chooseIndex = -1;
        return applySelected(menu, selected);
    }

    @Override
    public void clearPreviews(MyMenu menu) {
        int[] previewOrder = menu.getExtraData().getSettings().getPreviewIndexOrder();
        for (int previewNum : previewOrder) {
            menu.updateSlots("preview" + previewNum, null);
        }
        menu.updateSlots("next-page", null);
        menu.updateSlots("previous-page", null);
    }

    private void updateListDisplay(MyMenu menu) {
        int[] previewOrder = menu.getExtraData().getSettings().getPreviewIndexOrder();
        int itemsPerPage = previewOrder.length;

        for (int i = 0; i < itemsPerPage; i++) {
            int listIndex = i + (page - 1) * itemsPerPage;
            int previewNum = previewOrder[i];

            if (listIndex >= list.size()) {
                menu.updateSlots("preview" + previewNum, null);
            } else {
                ItemStack displayItem = getDisplayItem(list.get(listIndex));
                if (listIndex == chooseIndex) {
                    // Add glow effect to highlight selected item
                    addGlowEffect(displayItem);
                }
                menu.updateSlots("preview" + previewNum, displayItem);
            }
        }
        updatePageIndicators(menu);
    }

    private void chooseItem(MyMenu menu, String previewName) {
        int previewNum = Integer.parseInt(previewName.replace("preview", ""));
        int[] previewOrder = menu.getExtraData().getSettings().getPreviewIndexOrder();

        int localIndex = -1;
        for (int i = 0; i < previewOrder.length; i++) {
            if (previewOrder[i] == previewNum) {
                localIndex = i;
                break;
            }
        }
        if (localIndex == -1) return;

        chooseIndex = localIndex + (page - 1) * previewOrder.length;
        updateListDisplay(menu);
    }

    private void updatePageIndicators(MyMenu menu) {
        if (page < maxPage) {
            ItemStack nextItem = menu.getTemplateItemStack("has-next-page");
            if (nextItem != null) nextItem.setAmount(page + 1);
            menu.updateSlots("next-page", nextItem);
        } else {
            menu.updateSlots("next-page", null);
        }

        if (page > 1) {
            ItemStack prevItem = menu.getTemplateItemStack("has-previous-page");
            if (prevItem != null) prevItem.setAmount(page - 1);
            menu.updateSlots("previous-page", prevItem);
        } else {
            menu.updateSlots("previous-page", null);
        }
    }
}
```

### Implementing a List Handler

```java
public class RemoveEnchantmentHandler extends AbstractListHandler<Enchantment> {

    @Override
    public boolean isSuitable(ItemStack item) {
        return item.getType() == Material.BOOK;  // "Remove enchant" book
    }

    @Override
    public List<Enchantment> getList(MyMenu menu) {
        // Get all enchantments on the main item
        ItemStack mainItem = menu.getExtraData().getItemData1().getItemStack();
        return new ArrayList<>(mainItem.getEnchantments().keySet());
    }

    @Override
    public ItemStack getDisplayItem(Enchantment enchantment) {
        ItemStack display = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = display.getItemMeta();
        meta.setDisplayName("§e" + enchantment.getKey().getKey());
        display.setItemMeta(meta);
        return display;
    }

    @Override
    public boolean applySelected(MyMenu menu, Enchantment selected) {
        ItemStack mainItem = menu.getExtraData().getItemData1().getItemStack();
        mainItem.removeEnchantment(selected);
        return true;
    }

    @Override
    public String getConfirmTemplateName() {
        return "confirm-remove-enchant";
    }
}
```

### Preview Index Order (Spiral Pattern)

You can configure the display order of preview slots in YAML so center slots display first:

```yaml
data:
  # Indices map: list index 0 → preview3, 1 → preview2, 2 → preview4, etc.
  # This creates a center-first spiral pattern
  preview-index-order: [3, 2, 4, 1, 5]
```

Layout result:
```
preview1(far left) ← preview2 ← preview3(CENTER) → preview4 → preview5(far right)
  index 3             index 1     index 0            index 2      index 4
```

---

## Settings Cache Pattern

**When to use:** You need to look up slot numbers and default ItemStacks by item name throughout the menu's lifecycle.

**Problem:** Repeatedly calling `getMenuData().getItemMap().get("confirm").getSlots()` is verbose and error-prone.

**Solution:** Parse and cache all slot mappings once at menu open, then lookup by name.

```java
@Getter
public class MenuSettings {

    private final Map<String, List<Integer>> slotMap = new HashMap<>();
    private final Map<String, ItemStack> defaultItemStackMap = new HashMap<>();
    private int[] previewIndexOrder;

    /** Called once after setupItems() during menu open */
    public void initialize(MenuData menuData) {
        if (menuData == null || menuData.getItemMap() == null) return;

        for (Map.Entry<String, ItemData> entry : menuData.getItemMap().entrySet()) {
            String name = entry.getKey();
            ItemData itemData = entry.getValue();

            List<Integer> slots = itemData.getSlots();
            if (slots != null && !slots.isEmpty()) {
                slotMap.put(name, new ArrayList<>(slots));
                // Clone default ItemStack for a pristine copy
                defaultItemStackMap.put(name, itemData.getItemStackBuilder().getItemStack().clone());
            }
        }

        // Load custom data from YAML data: section
        if (menuData.getDataConfig() != null) {
            List<Integer> orderList = menuData.getDataConfig().getIntegerList("preview-index-order");
            if (orderList != null && !orderList.isEmpty()) {
                previewIndexOrder = orderList.stream().mapToInt(Integer::intValue).toArray();
            } else {
                previewIndexOrder = new int[]{3, 2, 4, 1, 5};  // Default spiral
            }
        }
    }

    public List<Integer> getSlots(String itemName) {
        return slotMap.getOrDefault(itemName, new ArrayList<>());
    }

    /** Returns a CLONE of the default ItemStack (safe to modify) */
    public ItemStack getDefaultItemStack(String itemName) {
        ItemStack defaultItem = defaultItemStackMap.get(itemName);
        return defaultItem != null ? defaultItem.clone() : null;
    }
}
```

### Usage in Menu: updateSlots()

```java
public void updateSlots(String itemName, ItemStack itemStack) {
    MenuSettings settings = extraData.getSettings();
    List<Integer> slots = settings.getSlots(itemName);

    for (int slot : slots) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            // Reset to YAML default appearance
            inventory.setItem(slot, settings.getDefaultItemStack(itemName));
        } else {
            inventory.setItem(slot, itemStack);
        }
    }
}
```

**Benefits:**
- Handlers use `menu.updateSlots("confirm", item)` — no slot numbers in code
- Menu layout can be changed entirely in YAML without touching Java
- Default ItemStacks always available for reset

---

## Dynamic Slot Updates

**When to use:** You need to change the appearance of a slot at runtime (e.g., show an item in a slot, change a button's appearance, reset to default).

### Pattern: updateSlots(name, itemStack)

```java
// Set a custom item in the named slot
menu.updateSlots("slot1", playerItem);

// Reset to YAML default (pass null)
menu.updateSlots("slot1", null);

// Update confirm button with a template
ItemStack confirmItem = menu.getTemplateItemStack("confirm-upgrade");
menu.updateSlots("confirm", confirmItem);
```

### Pattern: updateMenu() Orchestration

```java
private void updateMenu() {
    updateSlot1Display();
    updateSlot2Display();

    if (extraData.getItemData1() != null && extraData.getActiveHandler() != null) {
        extraData.getActiveHandler().updateView(this);
        extraData.getActiveHandler().updateConfirm(this);
    } else {
        clearAllDynamicSlots();
    }
}

private void clearAllDynamicSlots() {
    int[] previewOrder = extraData.getSettings().getPreviewIndexOrder();
    for (int previewNum : previewOrder) {
        updateSlots("preview" + previewNum, null);
    }
    updateSlots("confirm", null);
    updateSlots("next-page", null);
    updateSlots("previous-page", null);
}
```

---

## Item Delegation Pattern

**When to use:** YAML item types should delegate clicks to the menu or handler rather than handling them directly. Keeps AbstractItem subclasses tiny.

### Thin Delegate Items

```java
// Slot item: delegate to menu.returnItem()
public class SlotItem extends AbstractItem<MyMenu> {
    @Override
    public String getType() { return "my-slot"; }

    @Override
    public void handleClick(ClickData data) {
        String slotName = itemData.getDataConfig().getString("slot-name");
        if (slotName != null) {
            menu.returnItem(slotName);
        }
    }
}

// Confirm item: delegate to menu.confirm()
public class ConfirmItem extends AbstractItem<MyMenu> {
    @Override
    public String getType() { return "my-confirm"; }

    @Override
    public void handleClick(ClickData data) {
        menu.confirm();
    }
}

// Preview item: delegate to handler.clickProcess()
public class PreviewItem extends AbstractItem<MyMenu> {
    @Override
    public String getType() { return "my-preview"; }

    @Override
    public void handleClick(ClickData data) {
        String previewName = itemData.getDataConfig().getString("preview-name");
        SlotHandler handler = menu.getExtraData().getActiveHandler();
        if (handler != null && previewName != null) {
            handler.clickProcess(menu, previewName);
        }
    }
}

// Page navigation item: delegate to handler.clickProcess()
public class PageItem extends AbstractItem<MyMenu> {
    @Override
    public String getType() { return "my-page"; }

    @Override
    public void handleClick(ClickData data) {
        String pageAction = itemData.getDataConfig().getString("page-action");
        SlotHandler handler = menu.getExtraData().getActiveHandler();
        if (handler != null && pageAction != null) {
            handler.clickProcess(menu, pageAction);
        }
    }
}
```

### YAML Data Config for Item Identity

```yaml
items:
  slot1:
    type: my-slot
    item:
      type: LIGHT_GRAY_STAINED_GLASS_PANE
      display: '&fSlot 1'
    slot: 12
    data:
      slot-name: slot1    # Identifies which slot this is

  preview3:
    type: my-preview
    item:
      type: BLACK_STAINED_GLASS_PANE
      display: '&fPreview'
    slot: 22
    data:
      preview-name: preview3  # Identifies which preview this is

  next-page:
    type: my-page
    item:
      type: RED_STAINED_GLASS_PANE
      display: '&7'
    slot: 25
    data:
      page-action: next-page  # Identifies navigation direction
```

**Benefits:**
- Item classes are 5-10 lines each
- All routing information comes from YAML `data:` config
- Adding new slots requires only YAML changes, not new Java classes

---

## Common Pitfalls

### 1. Always Clone ItemStacks When Storing

```java
// BAD: Stores reference — can become stale when player inventory changes
public SlotItemData(ItemStack itemStack) {
    this.itemStack = itemStack;
}

// GOOD: Clone to prevent reference issues
public SlotItemData(ItemStack itemStack) {
    this.itemStack = itemStack.clone();
}
```

### 2. Always Return Items on Menu Close

```java
@Override
public void handleClose() {
    List<ItemStack> items = new ArrayList<>();
    if (extraData.getItemData1() != null) {
        items.add(extraData.getItemData1().getItemStack());
    }
    if (extraData.getItemData2() != null) {
        items.add(extraData.getItemData2().getItemStack());
    }
    InventoryUtils.addItem(owner, items);
}
```

### 3. Clear Previews Before Switching Handlers

```java
// BAD: Old handler's display items remain visible
extraData.setActiveHandler(newHandler);

// GOOD: Clear old display, then set new handler
if (extraData.getActiveHandler() != null) {
    extraData.getActiveHandler().clearPreviews(this);
}
extraData.setActiveHandler(newHandler);
```

### 4. State Transitions Must Consider ALL Related State

```java
// BAD: Clears handler even when slot2 still has item
extraData.setItemData1(null);
extraData.setActiveHandler(null);  // Breaks when slot2 still present!

// GOOD: Preserve handler when slot2 still has item
extraData.setItemData1(null);
if (extraData.getItemData2() == null) {
    extraData.setActiveHandler(null);
}
```

### 5. Don't Use reopenInventory() for Simple Updates

```java
// BAD: Causes visible flicker
menu.reopenInventory();

// GOOD: Update in-place (instant, no flicker)
menu.updateSlots("slot1", newItem);
```

---

## Complete Example: Opening the Menu

```java
public class MyCommand implements AdvancedCommandExecutor {
    private final BafPlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Argument arg) {
        if (!(sender instanceof Player player)) return true;

        // Create settings and extra data
        MenuSettings settings = new MenuSettings();
        MyExtraData extraData = new MyExtraData(settings);

        // Open menu using MenuOpener
        MenuOpener.builder()
                .player(player)
                .menuData(plugin, MyMenu.MENU_NAME)
                .extraData(extraData)
                .async(false)
                .build();

        return true;
    }
}
```

---

## Related Documentation

- [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md) — Basic migration from legacy to BafFramework
- [TUTORIAL_MENUS.md](TUTORIAL_MENUS.md) — Creating simple menus
- [TUTORIAL_ITEMS.md](TUTORIAL_ITEMS.md) — Creating custom item types
- [MENU_SYSTEM.md](MENU_SYSTEM.md) — Core AbstractMenu API
- [MENU_ITEMS.md](MENU_ITEMS.md) — AbstractItem API
