# Migration Strategy: No Legacy Code Modifications

## Rule: DO NOT TOUCH Legacy Code

All existing files remain **frozen**. The new menu runs in parallel via `/ceanvil-new`.

### Frozen Files (DO NOT MODIFY)

```
menu/anvil/
  CEAnvilMenu.java              FROZEN
  CEAnvilMenuListener.java      FROZEN
  MenuAbstract.java             FROZEN
  MenuListenerAbstract.java     FROZEN
  AnvilSlot1View.java           FROZEN
  AnvilSlot2View.java           FROZEN
  AnvilSlot2ListView.java       FROZEN
  Slot1CEWeaponView.java        FROZEN
  Slot2CEBookView.java          FROZEN
  Slot2CEDefaultView.java       FROZEN
  Slot2CEEnchantPointView.java  FROZEN
  Slot2CEEraseEnchantView.java  FROZEN
  Slot2CEGemDrillView.java      FROZEN
  Slot2CEGemView.java           FROZEN
  Slot2CELoreFormatView.java    FROZEN
  Slot2CEProtectDeadView.java   FROZEN
  Slot2CEProtectDestroyView.java FROZEN
  Slot2CERemoveEnchantPointView.java FROZEN
  Slot2CERemoveEnchantView.java FROZEN
  Slot2CERemoveGemView.java     FROZEN
  Slot2CERemoveProtectDeadView.java FROZEN

menu/ce-anvil.yml               FROZEN (legacy YAML)
```

### Decisions (Confirmed with User)

1. **YAML type:** `'ce-anvil'` (same as legacy - no conflict because different folders)
2. **DefaultHandler:** Include full implementation (enchant list with pagination)
3. **Package:** `menu.anvil` (same package as legacy - new files coexist alongside frozen files)
4. **Confirm logic:** Exact replication of all 6 ApplyResult cases + logging + rewards + messages

### New Files to Create

```
menu/anvil/
  CEAnvilCustomMenu.java        NEW - extends AbstractMenu (same package as legacy)
  CEAnvilExtraData.java         NEW - extends ExtraData
  CEAnvilSettings.java          NEW - config-driven slot mappings
  AnvilItemData.java            NEW - replaces CEAnvilMenu.ItemData
  Slot2Handler.java             NEW - interface replacing AnvilSlot2View
  AbstractListHandler.java      NEW - replaces AnvilSlot2ListView
  handler/
    BookHandler.java            NEW - replaces Slot2CEBookView
    EnchantPointHandler.java    NEW - replaces Slot2CEEnchantPointView
    GemHandler.java             NEW - replaces Slot2CEGemView
    GemDrillHandler.java        NEW - replaces Slot2CEGemDrillView
    ProtectDeadHandler.java     NEW - replaces Slot2CEProtectDeadView
    ProtectDestroyHandler.java  NEW - replaces Slot2CEProtectDestroyView
    LoreFormatHandler.java      NEW - replaces Slot2CELoreFormatView
    EraseEnchantHandler.java    NEW - replaces Slot2CEEraseEnchantView
    RemoveEnchantHandler.java   NEW - replaces Slot2CERemoveEnchantView
    RemoveEnchantPointHandler.java NEW
    RemoveGemHandler.java       NEW
    RemoveProtectDeadHandler.java NEW
    DefaultHandler.java         NEW - replaces Slot2CEDefaultView (full with pagination)
  item/
    AnvilSlotItem.java          NEW - AbstractItem for slot1/slot2
    AnvilConfirmItem.java       NEW - AbstractItem for confirm
    AnvilPreviewItem.java       NEW - AbstractItem for preview1-5
    AnvilPageItem.java          NEW - AbstractItem for prev/next page

command/
  CEAnvilCommand.java           NEW - opens new menu

menu-new/
  ce-anvil.yml                  NEW - BafFramework format YAML
```

### Modified Files (APPEND ONLY)

```
plugin.yml                      ADD ceanvil-new command entry
CommandModule.java              ADD CEAnvilCommand registration
MenuModule.java                 ADD CEAnvilCustomMenu registration + handler registration
```

---

## Why New Handlers Instead of Reusing Views

The legacy view classes (`AnvilSlot2View`, etc.) are **tightly coupled** to `CEAnvilMenu`:

```java
// Legacy view - hardwired to CEAnvilMenu
public abstract class AnvilSlot2View<T extends AnvilSlot2View> {
    private CEAnvilMenu anvilMenu;  // <-- Direct reference to legacy class

    public AnvilSlot2View(CEAnvilMenu anvilMenu) {  // <-- Takes legacy class
        this.anvilMenu = anvilMenu;
    }

    // All methods call this.anvilMenu.updateSlots(), this.anvilMenu.getItemStack()
    // which rely on CMenuView (legacy CustomMenu API)
}
```

We **cannot modify** these classes, and we **cannot pass** `CEAnvilCustomMenu` where
`CEAnvilMenu` is expected (different class hierarchy). Therefore: **new handler classes**.

### The Good News: Business Logic Is Separate

The actual enchantment/gem/book logic lives in `CEItem` subclasses, NOT in the views:

```java
// CEBook.java (domain model - NOT part of menu system)
public ApplyReason applyByMenuTo(CEItem target) { ... }
public ApplyReason testApplyTo(CEItem target) { ... }

// CEGemDrill.java
public ApplyReason applyByMenuTo(CEItem target) { ... }
public ApplyReason testApplyByMenuTo(CEItem target) { ... }

// CERemoveEnchant.java
public ApplyReason applyByMenuTo(CEItem target, CEEnchantSimple enchant) { ... }
public List<CEEnchantSimple> getRemoveEnchantList(List<CEEnchantSimple> enchants) { ... }
```

The views are **thin display adapters** that:
1. Call CEItem methods to get preview/apply results
2. Update slots with the results
3. Handle pagination for list views

This logic is straightforward to replicate in new handler classes.

---

## Command Registration

### plugin.yml (Add entry)

```yaml
  ceanvil-new:
    description: Anvil CustomEnchantment item (new API)
    usage: /<command>
```

### CommandModule.java (Append registration)

Following the exact pattern of `tinkerer-new` and `bookcraft-new`:

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

### CEAnvilCommand.java (New file)

```java
package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandExecutor;
import com.bafmc.bukkit.command.Argument;
import com.bafmc.bukkit.bafframework.custommenu.menu.builder.MenuOpener;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.menu.anvil.CEAnvilCustomMenu;
import com.bafmc.customenchantment.menu.anvil.CEAnvilExtraData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

---

## Handler Registration Pattern

### MenuModule.java (Append to existing registrations)

```java
// In registerMenu():
MenuRegister.instance().registerStrategy(CEAnvilCustomMenu.class);

// Handler registration (replaces legacy view registration)
CEAnvilCustomMenu.registerHandler(CEItemType.BOOK, new BookHandler());
CEAnvilCustomMenu.registerHandler(CEItemType.ENCHANT_POINT, new EnchantPointHandler());
CEAnvilCustomMenu.registerHandler(CEItemType.GEM, new GemHandler());
CEAnvilCustomMenu.registerHandler(CEItemType.GEM_DRILL, new GemDrillHandler());
CEAnvilCustomMenu.registerHandler(CEItemType.PROTECT_DEAD, new ProtectDeadHandler());
CEAnvilCustomMenu.registerHandler(CEItemType.PROTECT_DESTROY, new ProtectDestroyHandler());
CEAnvilCustomMenu.registerHandler(CEItemType.LORE_FORMAT, new LoreFormatHandler());
CEAnvilCustomMenu.registerHandler(CEItemType.EARSE_ENCHANT, new EraseEnchantHandler());
CEAnvilCustomMenu.registerHandler(CEItemType.REMOVE_ENCHANT, new RemoveEnchantHandler());
CEAnvilCustomMenu.registerHandler(CEItemType.REMOVE_ENCHANT_POINT, new RemoveEnchantPointHandler());
CEAnvilCustomMenu.registerHandler(CEItemType.REMOVE_GEM, new RemoveGemHandler());
CEAnvilCustomMenu.registerHandler(CEItemType.REMOVE_PROTECT_DEAD, new RemoveProtectDeadHandler());

// NOTE: Legacy view registrations (CEAnvilMenu.registerView1/2) remain untouched
// They serve the legacy /ceanvil command
```

---

## Slot2Handler vs Legacy AnvilSlot2View

| Feature | Legacy AnvilSlot2View | New Slot2Handler |
|---------|----------------------|------------------|
| Menu reference | `CEAnvilMenu` (CMenuView-based) | `CEAnvilCustomMenu` (AbstractMenu-based) |
| Slot updates | `menu.updateSlots("name", item)` via CMenuView | `menu.updateSlots("name", item)` via Inventory + config |
| Template fetch | `menu.getItemStack(null, "confirm-book")` via CItem | `menu.getTemplateItemStack("confirm-book")` via MenuData |
| State | Inner classes (EnchantData, Data) | Fields on handler instance |
| Null handling | `removeTemporaryItem()` → YAML default | Must manually restore from `settings.getDefaultItemStack()` |
| Registration | `CEAnvilMenu.registerView2(type, Class)` (reflection) | `CEAnvilCustomMenu.registerHandler(type, instance)` (direct) |
| Preview mapping | Hardcoded `indexToSlot()` / `slotToIndex()` | Config-driven via settings |

---

## Comparison: What Each Handler Needs to Implement

### Simple Handler (e.g., BookHandler)

Replaces `Slot2CEBookView` (53 lines). New handler will be ~40 lines:

```java
public class BookHandler implements Slot2Handler {
    public boolean isSuitable(CEItem ceItem) {
        return ceItem instanceof CEBook;
    }

    public void updateView(CEAnvilCustomMenu menu) {
        CEBook book = (CEBook) menu.getExtraData().getItemData2().getCEItem();
        CEItem weapon = menu.getExtraData().getItemData1().getCEItem();
        ApplyReason reason = book.testApplyTo(weapon);
        if (reason.getResult() == ApplyResult.SUCCESS) {
            menu.updateSlots("preview3", reason.getSource().exportTo());
        } else {
            menu.updateSlots("preview3", null);
        }
    }

    public void updateConfirm(CEAnvilCustomMenu menu) {
        menu.updateSlots("confirm", menu.getTemplateItemStack("confirm-book"));
    }

    public void clickProcess(CEAnvilCustomMenu menu, String name) { /* no-op */ }

    public ApplyReason apply(CEItem ceItem1, CEItem ceItem2) {
        return (ceItem2 instanceof CEBook book) ? book.applyByMenuTo(ceItem1) : ApplyReason.NOTHING;
    }

    public void clearPreviews(CEAnvilCustomMenu menu) {
        menu.updateSlots("preview3", null);
    }
}
```

### List Handler (e.g., RemoveEnchantHandler)

Replaces `Slot2CERemoveEnchantView` (59 lines) + inherits from `AnvilSlot2ListView` (234 lines).
New handler extends `AbstractListHandler` (~60 lines implementation + ~150 lines base):

```java
public class RemoveEnchantHandler extends AbstractListHandler<CEEnchantSimple> {
    public boolean isSuitable(CEItem ceItem) {
        return ceItem instanceof CERemoveEnchant;
    }

    public String getConfirmTemplateName() {
        return "confirm-remove-enchant";
    }

    public List<CEEnchantSimple> getList(CEItem ceItem1, CEItem ceItem2) {
        if (ceItem1 instanceof CEWeapon weapon && ceItem2 instanceof CERemoveEnchant re) {
            return re.getRemoveEnchantList(weapon.getWeaponEnchant().getCESimpleList());
        }
        return new ArrayList<>();
    }

    public ItemStack getDisplayItem(CEEnchantSimple enchant) {
        return CEAPI.getCEBookItemStack(enchant);
    }

    public ApplyReason getApplyReason(CEItem ceItem1, CEItem ceItem2, CEEnchantSimple selected) {
        if (ceItem2 instanceof CERemoveEnchant re) {
            return re.applyByMenuTo(ceItem1, selected);
        }
        return ApplyReason.NOTHING;
    }
}
```

---

## Implementation Order

1. **CEAnvilExtraData + AnvilItemData** - Data classes
2. **CEAnvilSettings** - Config-driven slot mappings
3. **Slot2Handler interface + AbstractListHandler** - Handler abstractions
4. **Simple handlers** (8 files) - Stateless, easy to test
5. **List handlers** (4 files) - Stateful with pagination
6. **DefaultHandler** - Special case with enchant display
7. **AbstractItem subclasses** (4 files) - Click routing
8. **CEAnvilCustomMenu** - Main menu class
9. **YAML file** - `menu-new/ce-anvil.yml`
10. **CEAnvilCommand** - Command class
11. **plugin.yml + CommandModule + MenuModule** - Registration (append only)
12. **Tests** - Unit + manual testing
