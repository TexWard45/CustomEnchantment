# CE Anvil View Catalog

Complete reference for all 15 view strategy classes.

---

## Slot 1 Views (1 implementation)

### Slot1CEWeaponView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot1CEWeaponView.java` |
| **Matches** | `ceItem instanceof CEWeapon` |
| **Complexity** | Trivial |
| **Slots Controlled** | None (just validation) |
| **Has State** | No |
| **Has Pagination** | No |

**Behavior:**
- Only validates that the item is a CEWeapon
- `clickProcess()` is empty (no-op)
- `instance()` creates new Slot1CEWeaponView

**Migration Impact:** Minimal - only constructor parameter type may change.

---

## Slot 2 Views — Simple (8 implementations)

These views show a single preview in `preview3` (center) and a confirm button.

### Slot2CEBookView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot2CEBookView.java` |
| **Matches** | `ceItem instanceof CEBook` |
| **Confirm Name** | `"confirm-book"` |
| **Preview** | `preview3` only (center) |
| **Has State** | No |
| **Has Pagination** | No |

**updateView():**
```java
CEBook book = (CEBook) ceItem2;
ApplyReason reason = book.testApplyTo(ceItem1);
if (reason.getResult() == ApplyResult.SUCCESS) {
    menu.updateSlots("preview3", reason.getSource().exportTo());
} else {
    menu.updateSlots("preview3", null);
}
```

**apply():** `book.applyByMenuTo(ceItem1)`

---

### Slot2CEEnchantPointView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot2CEEnchantPointView.java` |
| **Matches** | `ceItem instanceof CEEnchantPoint` |
| **Confirm Name** | `"confirm-enchant-point"` |
| **Preview** | `preview3` only |
| **Has State** | No |

**Pattern:** Same as BookView - `testApplyTo()` → preview, `applyByMenuTo()` → confirm.

---

### Slot2CEGemView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot2CEGemView.java` |
| **Matches** | `ceItem instanceof CEGem` |
| **Confirm Name** | `"confirm-gem"` |
| **Preview** | `preview3` only |
| **Has State** | No |

---

### Slot2CEGemDrillView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot2CEGemDrillView.java` |
| **Matches** | `ceItem instanceof CEGemDrill` |
| **Confirm Names** | `"confirm-gem-drill"`, `"confirm-gem-drill-max"`, `"confirm-gem-drill-with-chance"` |
| **Preview** | `preview3` only |
| **Has State** | No |
| **Special** | Uses PlaceholderBuilder for `{chance}` |

**updateConfirm() logic:**
```
if drillSize >= maxDrill → "confirm-gem-drill-max"
else if chance < 100% → "confirm-gem-drill-with-chance" (with {chance} placeholder)
else → "confirm-gem-drill"
```

**Migration Note:** `getItemStack(null, name)` must be replaced with template lookup + placeholder support:
```java
ItemStack itemStack = menu.getTemplateItemStack("confirm-gem-drill-with-chance");
itemStack = ItemStackUtils.getItemStack(itemStack, placeholderBuilder.build());
menu.updateSlots("confirm", itemStack);
```

---

### Slot2CEProtectDeadView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot2CEProtectDeadView.java` |
| **Matches** | `ceItem instanceof CEProtectDead` |
| **Confirm Name** | `"confirm-protect-dead"` |
| **Preview** | `preview3` only |
| **Has State** | No |

---

### Slot2CEProtectDestroyView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot2CEProtectDestroyView.java` |
| **Matches** | `ceItem instanceof CEProtectDestroy` |
| **Confirm Name** | `"confirm-protect-destroy"` |
| **Preview** | `preview3` only |
| **Has State** | No |

---

### Slot2CELoreFormatView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot2CELoreFormatView.java` |
| **Matches** | `ceItem instanceof CELoreFormat` |
| **Confirm Name** | `"confirm-lore-format"` |
| **Preview** | `preview3` only |
| **Has State** | No |

---

### Slot2CEEraseEnchantView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot2CEEraseEnchantView.java` |
| **Matches** | `ceItem instanceof CEEraseEnchant` |
| **Confirm Name** | `"confirm-erase-enchant"` (note: CEItemType is `EARSE_ENCHANT`, possible typo) |
| **Preview** | `preview3` only |
| **Has State** | No |

---

## Slot 2 Views — List (4 implementations)

These views extend `AnvilSlot2ListView<T, L>` and show paginated lists in preview1-5.

### Common AnvilSlot2ListView Behavior

All list views share:
- **5 preview slots** displayed in center-first order: [3, 2, 4, 1, 5]
- **Pagination**: `next-page` / `previous-page` controls, page display with ItemStack amount
- **Selection**: Player clicks a preview item to select it, item gets glowing enchant effect
- **Confirm**: Only applies when an item is selected (`chooseIndex != -1`)
- **State**: `Data` inner class with page, maxPage, chooseIndex, list

**clickProcess() routing:**
```java
if (name.equals("next-page")) → nextPage();
if (name.equals("previous-page")) → previousPage();
if (name.startsWith("preview")) → chooseRemove(name);
```

---

### Slot2CERemoveEnchantView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot2CERemoveEnchantView.java` |
| **Matches** | `ceItem instanceof CERemoveEnchant` |
| **Confirm Name** | `"confirm-remove-enchant"` |
| **List Type** | `CEEnchantSimple` |
| **Display** | Enchant book ItemStack per enchant |
| **All 5 Previews** | Yes (paginated list) |

**getList():** `removeEnchant.getRemoveEnchantList(weapon.getWeaponEnchant().getCESimpleList())`

---

### Slot2CERemoveEnchantPointView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot2CERemoveEnchantPointView.java` |
| **Matches** | `ceItem instanceof CERemoveEnchantPoint` |
| **Confirm Name** | `"confirm-remove-enchant-point"` |
| **List Type** | `CEEnchantSimple` |
| **All 5 Previews** | Yes |

---

### Slot2CERemoveGemView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot2CERemoveGemView.java` |
| **Matches** | `ceItem instanceof CERemoveGem` |
| **Confirm Name** | `"confirm-remove-gem"` |
| **List Type** | Gem data |
| **All 5 Previews** | Yes |

---

### Slot2CERemoveProtectDeadView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot2CERemoveProtectDeadView.java` |
| **Matches** | `ceItem instanceof CERemoveProtectDead` |
| **Confirm Name** | `"confirm-remove-protect-dead"` |
| **List Type** | Protection data |
| **All 5 Previews** | Yes |

---

## Special View: Slot2CEDefaultView

| Property | Value |
|----------|-------|
| **File** | `menu/anvil/Slot2CEDefaultView.java` |
| **Matches** | `ceItem == null` (auto-created when only slot1 has item) |
| **Confirm Name** | `"confirm-remove-enchant"` |
| **Has State** | Yes - `EnchantData` (page, maxPage, enchant list) |
| **Has Pagination** | Yes (duplicated from ListView) |
| **All 5 Previews** | Yes |
| **Special** | Reads `default-view.enchant-group` from YAML `data:` section |

**Why it's special:**
1. It's the DEFAULT view when only slot1 is filled (no modifier item)
2. It shows the weapon's current enchants in preview area
3. It has its OWN pagination logic (duplicates AnvilSlot2ListView)
4. It reads `enchant-group` filter from YAML data config
5. Its `apply()` always returns `NOTHING` (display only, no action)
6. Its `isSuitable()` returns `ceItem == null` - it matches "no item"

**Migration consideration:** The duplicated pagination in DefaultView and ListView is a code smell.
During Phase 7 cleanup, consider refactoring DefaultView to extend AnvilSlot2ListView. But for
Phase 3, keep it as-is to minimize changes.

---

## View Method Summary

| Method | Simple Views | List Views | Default View |
|--------|-------------|------------|--------------|
| `isSuitable(CEItem)` | Checks instanceof | Checks instanceof | `ceItem == null` |
| `instance(menu)` | New instance | New instance | New instance |
| `updateView()` | Set preview3 | Populate 5 previews | Populate 5 previews |
| `updateConfirm()` | Set confirm variant | Set confirm variant | Set confirm variant |
| `clickProcess(name)` | No-op | Page/select routing | Page routing |
| `apply(ceItem1, ceItem2)` | Call applyByMenuTo | Select-based apply | Return NOTHING |
| `updateAllPreviewNull()` | No-op (inherited) | Clear 5 previews + pages | Clear 5 previews + pages |

---

## Menu Method Calls from Views

All views call these methods on the menu reference:

| View Method Call | Purpose | New System Equivalent |
|-----------------|---------|----------------------|
| `menu.updateSlots("preview3", item)` | Set preview item | `inventory.setItem(settings.getSlots("preview3").get(0), item)` |
| `menu.updateSlots("confirm", item)` | Set confirm button | `inventory.setItem(settings.getSlots("confirm").get(0), item)` |
| `menu.updateSlots("next-page", item)` | Set page button | `inventory.setItem(settings.getSlots("next-page").get(0), item)` |
| `menu.updateSlots("preview3", null)` | Clear to default | `inventory.setItem(slot, settings.getDefaultItemStack("preview3"))` |
| `menu.getItemStack(null, "confirm-book")` | Get template | `menuData.getItemMap().get("confirm-book").getItemStackBuilder().getItemStack()` |
| `menu.getItemData1().getCEItem()` | Get slot1 CE item | `extraData.getItemData1().getCEItem()` |
| `menu.getMenuView().getCMenu().getDataConfig()` | Get YAML data | `menuData.getDataConfig()` |
