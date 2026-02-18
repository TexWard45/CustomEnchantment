# Phase 5 Implementation Plan: Equipment Menu Migration

## Context

Phase 5 of the CustomMenu migration epic (#30). Migrates the **Equipment Menu** — the most complex menu in the system (717 LOC, 7+ subsystems) — from the legacy CustomMenu API to BafFramework CustomMenu.

**Previous phases:** Phase 1 (2 iter), Phase 2 (7 iter), Phase 3 (3 iter), Phase 4 (1 iter)
**Target:** 1-2 iterations by applying ALL documented lessons.

---

## Critical Constraint: DO NOT TOUCH Legacy Code

Legacy files remain **frozen** — `EquipmentMenu.java`, `EquipmentMenuListener.java`, `EquipmentSettings.java`, `menu/equipment.yml`.

**Exception:** `OutfitItemTask.java` needs a minimal 5-line change to support both old and new menu (see Step 5).

---

## Why This Menu Is Fundamentally Different from Phase 1-4

| Aspect | Phase 1-4 Menus | Equipment Menu |
|--------|----------------|----------------|
| Items | Menu holds temp items, returns on close | **Read-only viewer** of player's equipment |
| State | ExtraData holds state | **PlayerEquipment + PlayerStorage** is the state |
| Updates | User-driven only | **External-driven** (OutfitItemTask, auto-update task) |
| Singleton | No | **Yes** — static map tracks open menus |
| Auto-update | No | **Yes** — BukkitRunnable every 5 ticks |
| Click types | LEFT only | **LEFT=unequip, RIGHT=swap skin** |
| Cooldowns | No | **Per-slot 500ms + per-player skin swap 1s** |
| handleClose() | Returns items | **No-op** (nothing to return — items are player equipment) |

---

## Lessons Applied from Previous Phases

| # | Lesson | Source | How Applied |
|---|--------|--------|-------------|
| 1 | `updateSlots(name, null)` resets to YAML default | Phase 4 Bug 2 | Use `updateSlots(name, null)` for empty equipment slots |
| 2 | Sound at top-level `sound: open:` | Phase 4 Bug 4 | Correct format in equipment.yml |
| 3 | Copy Vietnamese text exactly | Phase 3 | Copy all display/lore from legacy YAML |
| 4 | Template items (no-slot YAML) | Phase 3-4 | `-equip`, `-swap`, `-no-skin` as templates |
| 5 | Code review before testing | Phase 4 lesson | Run code-reviewer agent after implementation |
| 6 | `inventory.setItem(slot, null)` → use `getTemplateItemStack()` | Phase 4 Bug 2 | For extra-slot per-index updates |

---

## Subsystem Analysis (7 subsystems)

### 1. Armor/Weapon Slots (helmet, chestplate, leggings, boots, mainhand)
- **Display:** `updateSlotsAdvance()` renders equipped item with skin overlay, or YAML default if empty
- **LEFT click:** Unequip → check `itemInteractList` for full-inventory guard → find first empty storage slot (0-35 only, NOT equipment slots) → move item there
- **RIGHT click:** Swap outfit skin (if outfit with skins exists for that weapon type)
- **Add (from inventory):** Route by material type → swap: `player.getInventory().setItem(equipmentSlot, newItem)` + `e.setCurrentItem(oldItem)` (old armor goes to clicked slot)
- **Cooldown:** 500ms per `EquipSlot`
- **Amount check:** Checks **OLD equipped item** amount > 1 → NOTHING (not the new item)

### 2. Offhand Slot
- **Wings-aware display:** If player has wings → `playerEquipment.getOffhandItemStack()`. Otherwise → `EquipSlot.OFFHAND.getItemStack(player)`
- **LEFT click:** Return offhand. Wings: fire `ItemEquipEvent`, clear `offhandItemStack`. No wings: clear offhand slot directly
- **Add:** If mainhand empty → equip to mainhand. If mainhand full → equip to offhand (wings-aware with `ItemEquipEvent`)
- **Edge case:** Wings player with empty offhand → block return (legacy lines 585-587)

### 3. Extra Slots (artifacts, sigils, outfits)
- **Config-driven:** `ExtraSlotSettingsData` maps types → max count + `EquipSlot` names
- **Per-index rendering:** Each extra slot type has multiple positions, each rendered independently
- **LEFT click:** Check `itemInteractList` for full-inventory guard → `PlayerEquipment.setSlot(slot, null, true)`, give `weaponAbstract.getDefaultItemStack()` back via `InventoryUtils.addItem()`
- **Add validation:** Not amount>1 (**NEW item** checked), has available slot, not duplicate pattern
- **Item consumption:** `e.setCurrentItem(null)` removes clicked item from player inventory (BafFramework: `data.getEvent().setCurrentItem(null)`)
- **Cooldown:** 500ms on `EquipSlot.EXTRA_SLOT`
- **Duplicate check:** Pattern matching via `CEArtifact.getData().getPattern()`, `CESigil.getData().getId()`, `CEOutfit.getData().getId()`
- **`getEmptyExtraSlotIndex()`** internally calls `sortExtraSlot()` before searching (idempotent with render sort)

### 4. Protect Dead Slot (advanced mode)
- **Display:** Shows protect-dead item with amount (via `maxStackSize=99`)
- **LEFT click:** Check `itemInteractList` for full-inventory guard → Withdraw 1 → `StorageUtils.useProtectDead()` + give `protectDead.exportTo()`
- **LEFT click uses `updateMenu()` directly** (NOT `updateMenuWithPreventAction()`) — intentionally different from other return paths
- **Add:** Validate: same type (or first type), not exceeding `maxPoint`. Consume via `e.setCurrentItem(null)` (BafFramework: `data.getEvent().setCurrentItem(null)`)
- **Data source:** `PlayerStorage` (NOT PlayerEquipment)
- **Cleanup:** If stored type no longer exists in config → `StorageUtils.removeProtectDead()`

### 5. Wings Slot
- **3 display states:**
  - No outfit or no wings skins → YAML default (empty slot appearance)
  - Skin index = -1 → `wings-no-skin` template
  - Skin index >= 0 → fetch skin ItemStack from `CEItemType.SKIN` storage
- **RIGHT click only:** Swap skin. Wings index wraps to -1 (off) when past last skin (unlike armor which wraps to 0)
- **No LEFT click** — wings can't be manually unequipped

### 6. Player Info Slot
- **Display:** PLAYER_HEAD with PlaceholderAPI stats — uses `getItemStack(Player)` for PAPI resolution
- **Auto-updated** every 5 ticks via BukkitRunnable
- **No click action**

### 7. Skin Display System (`updateSlotsAdvance()`)
- **3 display modes** per equipped item based on `getSwapSkinIndex()`:
  - `NO_SKIN` → `-equip` template: wraps item's display/lore with "equipped" UI
  - `SKIN_OFF` → `-no-skin` template: shows "click to enable skin"
  - `CAN_SWAP` → `-swap` template: wraps item's display/lore with "click to swap skin" UI
- **Placeholder injection:** `{item_display}` and `{item_lore}` replaced with actual item name/lore
- **`__LORE_REMOVER__` sentinel:** If item has no lore, placeholder resolves to `__LORE_REMOVER__`, then lines containing it are removed

---

## Edge Cases Checklist

### Race Conditions & Timing
1. `inUpdateMenu` flag prevents clicks during scheduled `updateMenu()` — MUST preserve `updateMenuWithPreventAction()` pattern. When `inUpdateMenu` is true in `handlePlayerInventoryClick()`, cancel the event via `data.getEvent().setCancelled(true)` and return
2. Auto-update task self-cancels when `removed=true` — set in `handleClose()` → static `removeMenu()`
3. Both OutfitItemTask and player clicks call `updateMenu()` on main thread — safe but race guard still needed for `runTask()` scheduling

### Item Operations
4. Offhand + Wings: fire `ItemEquipEvent` on offhand changes when wings active
5. Extra slot duplicate: uses pattern matching, handles CEArtifact/CESigil/CEOutfit differently
6. Protect-dead type validation: `isDifferentProtectDead()` rejects mismatched types; `removeProtectDead()` cleans stale config data
7. Amount > 1 check asymmetry: Extra slots check **NEW** item amount > 1 → NOTHING. Armor checks **OLD equipped** item amount > 1 → NOTHING
8. Full inventory guard: `returnItem()` checks `itemInteractList.contains(itemName) || itemName.startsWith(EXTRA_SLOT)` → if `player.getInventory().firstEmpty() == -1` → send `"menu.equipment.return-item.no-empty-slot"` message and abort
9. Vanilla items (null `ceItem`): `CEAPI.getCEItem()` can return null for vanilla items. Falls through extra-slot and protect-dead checks → routes to armor/weapon path by `Material` type

### Display
10. Template null safety: Not all slots have `-swap` templates (e.g., `chestplate-swap` doesn't exist). Must handle null gracefully
11. Player-info needs `getItemStack(Player)` (not bare `getItemStack()`) for PlaceholderAPI
12. Wings skin index -1 → show `wings-no-skin`, not a skin item
13. `playerEquipment.sortExtraSlot()` must be called before extra slot rendering

### Skin Swap
14. Wings wrap: `currentIndex + 1 >= size ? -1 : (currentIndex + 1) % size` — wraps to -1 (off)
15. Non-wings wrap: `(currentIndex + 1) % size` — wraps to 0 (first skin)
16. Per-player 1s cooldown for skin swaps (separate from per-slot 500ms)
17. Empty/null skinList → `NO_SKIN` status → no swap option
18. `swapSkin()` does NOT have the fallback weapon lookup that `getSwapSkinIndex()` has (no `CEAPI.getCEItem(equipSlot.getItemStack())` fallback) — intentional asymmetry from legacy

### BafFramework Migration
19. Item consumption via `data.getEvent().setCurrentItem(null)` — needed for extra slots (line 143), protect-dead (lines 158, 170)
20. Armor swap via `data.getEvent().setCurrentItem(oldItemStack)` — swaps old armor into clicked inventory slot (line 211)
21. Message sending: `CustomEnchantmentMessage.send(player, "menu.equipment.add-equipment." + EnumUtils.toConfigStyle(reason))` must be called in `handlePlayerInventoryClick()` after every `addItem()` call
22. `itemInteractList` must be recreated — contains: protect-dead, helmet, chestplate, leggings, boots, offhand, mainhand (NOT wings, NOT extra-slot prefix — extra-slot checked separately via `startsWith`)

---

## File Structure

### New Files (9 Java + 1 YAML)

```
menu/equipment/                              (SAME package as legacy)
├── EquipmentCustomMenu.java                 (~550 lines — main menu class)
├── EquipmentExtraData.java                  (~40 lines — flags, cooldowns, removed)
├── item/
│   ├── EquipmentSlotItem.java               (~30 lines — armor/weapon/offhand clicks)
│   ├── ExtraSlotEquipmentItem.java          (~25 lines — extra slot clicks)
│   ├── ProtectDeadEquipmentItem.java        (~20 lines — protect dead clicks)
│   ├── WingsEquipmentItem.java              (~20 lines — wings clicks)
│   └── PlayerInfoEquipmentItem.java         (~10 lines — display only)

command/
├── EquipmentNewCommand.java                 (~30 lines — opens menu)

resources/menu-new/
├── equipment.yml                            (~320 lines — full BafFramework format)
```

### Modified Files (4)

| File | Change |
|------|--------|
| `resources/plugin.yml` | Add `equipment-new` command |
| `command/CommandModule.java` | Register `equipment-new` command |
| `menu/MenuModule.java` | Register `EquipmentCustomMenu` strategy |
| `task/OutfitItemTask.java` | Update `updateEquipmentMenu()` to check both old and new menu |

---

## Key Implementation Details

### Static Singleton Registry

```java
// Same pattern as legacy — must coexist with old EquipmentMenu.map
private static final Map<String, EquipmentCustomMenu> menuMap = new HashMap<>();

public static EquipmentCustomMenu putMenu(Player player, EquipmentCustomMenu menu) {
    menuMap.put(player.getName(), menu);
    return menu;
}

public static EquipmentCustomMenu getMenu(Player player) {
    return menuMap.get(player.getName());
}

public static EquipmentCustomMenu removeMenu(Player player) {
    EquipmentCustomMenu menu = menuMap.remove(player.getName());
    if (menu != null) {
        menu.extraData.setRemoved(true);
    }
    return menu;
}
```

### `updateSlotsAdvance()` — Core Rendering

```java
public void updateSlotsAdvance(String itemName, ItemStack itemStack) {
    if (itemStack == null || itemStack.getType() == Material.AIR) {
        updateSlots(itemName, null);  // BafFramework resets to YAML default
        return;
    }

    NextSwapSkinStatus status = getSwapSkinIndex(itemName);
    ItemStack template;

    if (status == CAN_SWAP) {
        template = getTemplateItemStackForEquipment(itemName + "-swap");
        if (template != null) {
            itemStack = applyItemPlaceholders(itemStack, template);
        }
    } else if (status == SKIN_OFF) {
        template = getTemplateItemStackForEquipment(itemName + "-no-skin");
        if (template != null) {
            itemStack = template;  // Replace entirely
        }
    } else { // NO_SKIN
        template = getTemplateItemStackForEquipment(itemName + "-equip");
        if (template != null) {
            itemStack = applyItemPlaceholders(itemStack, template);
        }
    }

    updateSlots(itemName, itemStack);
}
```

**`getTemplateItemStackForEquipment()`** — Wrapper around `getTemplateItemStack()` that handles player PAPI resolution. The legacy `getItemStack(player, name)` resolves PAPI; BafFramework's `getTemplateItemStack(name)` does NOT by default. We need:
```java
private ItemStack getTemplateItemStackForEquipment(String name) {
    ItemData itemData = menuData.getItemMap().get(name);
    return itemData != null ? itemData.getItemStack(owner) : null;
}
```

**`applyItemPlaceholders()`** — Injects `{item_display}` and `{item_lore}`:
```java
private ItemStack applyItemPlaceholders(ItemStack sourceItem, ItemStack template) {
    ItemMeta templateMeta = template.getItemMeta();
    String display = templateMeta.hasDisplayName() ? templateMeta.getDisplayName() : "";
    List<String> lore = templateMeta.hasLore() ? templateMeta.getLore() : new ArrayList<>();

    String itemDisplay = ItemStackUtils.getDisplayName(sourceItem);
    List<String> itemLore = ItemStackUtils.getLore(sourceItem);

    PlaceholderBuilder placeholder = PlaceholderBuilder.builder();
    placeholder.put("{item_display}", itemDisplay != null ? itemDisplay : MaterialUtils.getDisplayName(sourceItem.getType()));
    placeholder.put("{item_lore}", itemLore != null ? itemLore : Arrays.asList("__LORE_REMOVER__"));

    display = placeholder.build().apply(display);
    lore = placeholder.build().apply(lore);
    lore.removeIf(line -> line.contains("__LORE_REMOVER__"));

    ItemStack result = sourceItem.clone();
    ItemMeta resultMeta = result.getItemMeta();
    resultMeta.setDisplayName(display);
    resultMeta.setLore(lore);
    result.setItemMeta(resultMeta);
    return result;
}
```

### Extra Slots — Per-Index Update

BafFramework's `updateSlots(name, itemStack)` updates ALL slots of that name. Extra slots need per-index updates:

```java
public void updateExtraSlots() {
    PlayerEquipment playerEquipment = CEAPI.getCEPlayer(owner).getEquipment();
    playerEquipment.sortExtraSlot();

    Map<String, ExtraSlotSettingsData> map = CustomEnchantment.instance().getMainConfig().getExtraSlotSettingMap();
    for (String key : map.keySet()) {
        ExtraSlotSettingsData data = map.get(key);
        String slotName = EXTRA_SLOT + "-" + key;
        List<Integer> slots = getSlotsByName(slotName);
        int limit = Math.min(data.getMaxCount(), slots.size());

        for (int i = 0; i < limit; i++) {
            CEWeaponAbstract weaponAbstract = playerEquipment.getSlot(data.getSlot(i));
            ItemStack itemStack = (weaponAbstract instanceof CEArtifact || weaponAbstract instanceof CESigil || weaponAbstract instanceof CEOutfit)
                    ? weaponAbstract.getDefaultItemStack() : null;

            if (itemStack != null) {
                // Apply skin display logic for this specific slot
                updateSlotsAdvanceForSlot(slotName, itemStack, slots.get(i));
            } else {
                // Restore YAML default for this specific slot
                inventory.setItem(slots.get(i), getTemplateItemStack(slotName));
            }
        }
    }
}
```

### OutfitItemTask Integration (Minimal Change)

**File:** `src/main/java/com/bafmc/customenchantment/task/OutfitItemTask.java` (lines 100-105)

```java
// CURRENT:
private void updateEquipmentMenu(Player player) {
    EquipmentMenu menu = EquipmentMenu.getMenu(player);
    if (menu != null) {
        menu.updateMenuWithPreventAction();
    }
}

// NEW (add new menu check):
private void updateEquipmentMenu(Player player) {
    EquipmentMenu menu = EquipmentMenu.getMenu(player);
    if (menu != null) {
        menu.updateMenuWithPreventAction();
        return;
    }
    EquipmentCustomMenu newMenu = EquipmentCustomMenu.getMenu(player);
    if (newMenu != null) {
        newMenu.updateMenuWithPreventAction();
    }
}
```

### Auto-Update Task Lifecycle

```java
@Override
public void setupItems() {
    super.setupItems();
    putMenu(owner, this);
    updateMenu();
    autoUpdateMenu();
}

private void autoUpdateMenu() {
    new BukkitRunnable() {
        public void run() {
            if (extraData.isRemoved()) {
                cancel();
                return;
            }
            updatePlayerInfoSlots();
        }
    }.runTaskTimer(CustomEnchantment.instance(), 0, 5);
}

@Override
public void handleClose() {
    removeMenu(owner);
    // No items to return — equipment stays on player
}
```

### Click Routing in Item Classes

Each item class handles both LEFT and RIGHT clicks:

```java
// EquipmentSlotItem — handles helmet/chestplate/leggings/boots/mainhand/offhand
public void handleClick(ClickData data) {
    EquipmentCustomMenu equipMenu = (EquipmentCustomMenu) menu;
    if (equipMenu.isInUpdateMenu()) {
        return;
    }

    String name = itemData.getId();  // "helmet", "chestplate", etc.
    ClickType clickType = data.getEvent().getClick();

    if (clickType == ClickType.LEFT) {
        equipMenu.returnItem(name, data.getClickedSlot());
    } else if (clickType == ClickType.RIGHT) {
        equipMenu.swapSkinWithCooldown(name, data.getClickedSlot(), data.getPlayer());
    }
}
```

### Skin Swap Cooldown (per-player 1s)

Moved from listener to menu or extraData:
```java
// In EquipmentExtraData or EquipmentCustomMenu
private static final Map<String, Long> swapSkinCooldowns = new HashMap<>();
private static final long SWAP_SKIN_COOLDOWN_MS = 1000;

public void swapSkinWithCooldown(String itemName, int slot, Player player) {
    long currentTime = System.currentTimeMillis();
    Long lastTime = swapSkinCooldowns.get(player.getName());
    if (lastTime != null && (currentTime - lastTime) < SWAP_SKIN_COOLDOWN_MS) {
        return;
    }
    swapSkinCooldowns.put(player.getName(), currentTime);
    swapSkin(itemName, slot);
}
```

---

## Implementation Order

### Step 1: YAML + ExtraData
1. `resources/menu-new/equipment.yml` — Full BafFramework format with all items and templates. Copy Vietnamese text exactly from legacy `menu/equipment.yml`.
2. `EquipmentExtraData.java` — Fields: `inUpdateMenu`, `removed`, `lastClickTime` map

### Step 2: Core Menu (~550 lines)
3. `EquipmentCustomMenu.java` — All business logic:
   - Static singleton: `putMenu/getMenu/removeMenu`
   - `getType()` → `"equipment"`
   - `registerItems()` → register all 5 item types + `DefaultItem`
   - `setupItems()` → `super.setupItems()` + `putMenu()` + `updateMenu()` + `autoUpdateMenu()`
   - `handlePlayerInventoryClick()` → guard `inUpdateMenu` (cancel event + return) → `CEAPI.getCEItem()` (can be null for vanilla) → `addItem()` with 3-path routing → `CustomEnchantmentMessage.send()` with reason
   - `handleClose()` → `removeMenu(owner)` (no items to return)
   - `updateMenu()` → calls 7 sub-update methods
   - `updateSlotsAdvance()` — 3 display modes with placeholder injection
   - `getSwapSkinIndex()` — exact same logic as legacy
   - `addItem()` — 3 paths (identical to legacy lines 119-241). Extra-slot/protect-dead: consume via `data.getEvent().setCurrentItem(null)`. Armor: swap via `data.getEvent().setCurrentItem(oldItemStack)`. Offhand: swap old offhand into clicked slot
   - `returnItem()` — 4 paths (identical to legacy lines 534-631). Guard: check `itemInteractList`/extra-slot prefix → reject if inventory full with message. Extra-slot path, offhand path, armor path use `updateMenuWithPreventAction()`. Protect-dead path uses `updateMenu()` directly (intentional)
   - `swapSkin()` — with wings wrap-around (identical to legacy lines 633-665)
   - `updateMenuWithPreventAction()` — schedule `updateMenu()` on main thread with `inUpdateMenu` guard
   - Helpers: `getMaxExtraSlotUseCount`, `getEmptyExtraSlotIndex`, `checkDuplicateExtraSlot`, `getWingsItemStack`, `getSkinItemStack`, `itemInteractList` (full-inventory guard list)
   - `autoUpdateMenu()` — BukkitRunnable every 5 ticks for player-info
   - `updatePlayerInfoSlots()`, `updateOffhandSlots()`, `updateProtectDeadSlots()`, `updateExtraSlots()`, `updateWingsSlots()`

### Step 3: Item Classes (5 classes)
4. `EquipmentSlotItem.java` — Type `equipment-slot`. Handles LEFT=return, RIGHT=swapSkin. Used by `h/c/l/b/m/f` slots. Data config: `slot-name` for identifying which equipment slot.
5. `ExtraSlotEquipmentItem.java` — Type `extra-slot-equipment`. LEFT=return extra slot item. Data config uses item name prefix.
6. `ProtectDeadEquipmentItem.java` — Type `protect-dead-equipment`. LEFT=withdraw 1 protect dead.
7. `WingsEquipmentItem.java` — Type `wings-equipment`. RIGHT=swap wings skin.
8. `PlayerInfoEquipmentItem.java` — Type `player-info-equipment`. Display only, no click action.

### Step 4: Command + Registration
9. `EquipmentNewCommand.java` — Opens menu via `MenuOpener.builder()`. Must close existing inventory first (like legacy).
10. `plugin.yml` — Add `equipment-new` command
11. `CommandModule.java` — Register command
12. `MenuModule.java` — Register `EquipmentCustomMenu.class`

### Step 5: OutfitItemTask Integration
13. `OutfitItemTask.java` — Add 5 lines to `updateEquipmentMenu()` to check `EquipmentCustomMenu.getMenu()` as fallback

### Step 6: Code Review + Build
14. Run code-reviewer agent
15. Run `./gradlew build`

### Step 7: Issue Documentation
16. Create `.claude/docs/issues/36/PHASE5_RETROSPECTIVE.md` — Bugs, lessons, cross-phase metrics
17. Update parent issue (#30) status

---

## Verification Plan

### Build
```bash
./gradlew build    # Must compile cleanly
```

### Manual Testing — Equipment (`/equipment-new`)

**Display (open empty):**
- [ ] Menu opens with correct 6-row layout
- [ ] All empty equipment slots show YAML defaults (WHITE_STAINED_GLASS_PANE with Vietnamese text)
- [ ] Player-info shows PLAYER_HEAD with stats
- [ ] Border items (BLACK_STAINED_GLASS_PANE) display correctly
- [ ] Return/close button works
- [ ] Open sound plays

**Armor equip/unequip:**
- [ ] Click helmet in inventory → equips, menu shows equipped display with `-equip` template
- [ ] LEFT click equipped helmet → moves to first empty storage slot (0-35), slot shows default
- [ ] Same for chestplate, leggings, boots
- [ ] Equip with existing armor → old item swaps into the clicked inventory slot (NOT firstEmpty)
- [ ] OLD equipped item amount > 1 → rejected (NOTHING reason)
- [ ] Full inventory → LEFT click unequip → "no empty slot" message, item stays equipped
- [ ] Vanilla item (non-CE) → routes by Material type correctly

**Mainhand/Offhand:**
- [ ] Click weapon → equips to mainhand (if empty)
- [ ] Click second item with full mainhand → equips to offhand
- [ ] Wings player: offhand uses `PlayerEquipment.offhandItemStack` + fires `ItemEquipEvent`
- [ ] Wings player: unequip offhand fires `ItemEquipEvent`
- [ ] Wings player: empty offhand → return blocked

**Extra slots:**
- [ ] Click artifact → equips to first empty artifact slot, item consumed from inventory
- [ ] Duplicate artifact pattern → rejected (DUPLICATE_EXTRA_SLOT)
- [ ] All artifact slots full → rejected (MAX_EXTRA_SLOT)
- [ ] Item with no extra slot config → falls through to armor logic (NOT NO_EXTRA_SLOT)
- [ ] NEW item amount > 1 → rejected (NOTHING reason)
- [ ] LEFT click occupied extra slot → returns to inventory
- [ ] Full inventory → LEFT click unequip → "no empty slot" message
- [ ] Sigil and outfit extra slots work
- [ ] `sortExtraSlot()` called before render
- [ ] Add reason message sent to player for every outcome

**Protect dead:**
- [ ] Click protect-dead item → adds to storage, item consumed from inventory, shows with amount
- [ ] Click different type → rejected (DIFFERENT_PROTECT_DEAD)
- [ ] Exceed max point → rejected (EXCEED_PROTECT_DEAD)
- [ ] LEFT click → withdraws 1, amount decreases (uses `updateMenu()` directly, NOT `updateMenuWithPreventAction()`)
- [ ] Amount reaches 0 → storage cleared, slot shows default
- [ ] Stale type (config removed) → cleaned up
- [ ] Full inventory → LEFT click withdraw → "no empty slot" message

**Wings:**
- [ ] No outfit → default empty wings display
- [ ] Outfit without wings skins → default display
- [ ] Outfit with wings, index=-1 → `wings-no-skin` template
- [ ] Outfit with wings, index>=0 → skin item displayed
- [ ] RIGHT click → cycles skin, wraps to -1 after last skin

**Skin display system:**
- [ ] Equipped item + no outfit → `-equip` template with `{item_display}`/`{item_lore}`
- [ ] Equipped item + outfit + skin off → `-no-skin` template
- [ ] Equipped item + outfit + skin on → `-swap` template with item info
- [ ] RIGHT click → cycles to next skin
- [ ] Item with no lore → `__LORE_REMOVER__` lines removed cleanly
- [ ] 1s cooldown between skin swaps

**Auto-update:**
- [ ] Player-info updates every 5 ticks
- [ ] Stats change → reflected in player-info
- [ ] Close menu → auto-update task cancels (no lingering tasks)

**Integration with OutfitItemTask:**
- [ ] Equip outfit while equipment menu open → skin changes reflected
- [ ] Wings equip/unequip → menu updates
- [ ] `inUpdateMenu` guard: rapid clicks during update → cancelled

**Cooldowns:**
- [ ] 500ms per-slot cooldown (can't spam unequip)
- [ ] 1s per-player skin swap cooldown
- [ ] Cooldowns don't persist across menu reopens

**Coexistence:**
- [ ] Legacy `/equipment` still works exactly as before
- [ ] Both menus can't be open simultaneously (different commands)
