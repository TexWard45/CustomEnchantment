# CE Anvil Migration - Implementation Checklist

## CRITICAL: Do NOT Touch Legacy Code

All legacy files are FROZEN. See `NO_LEGACY_TOUCH_STRATEGY.md` for details.

## Pre-Implementation

- [ ] Read master document: `issue-34-ceanvil-migration.md`
- [ ] Read no-legacy strategy: `NO_LEGACY_TOUCH_STRATEGY.md`
- [ ] Read view catalog: `VIEW_CATALOG.md`
- [ ] Read YAML conversion: `YAML_CONVERSION.md`
- [ ] Read Phase 2 lessons: `.claude/docs/issues/30/LESSONS_FOR_NEXT_PHASE.md`
- [ ] Verify understanding of all 15 view types and what CEItem methods they call

## Phase 3A: Data Classes & Config

### CEAnvilExtraData (NEW)
- [ ] `package com.bafmc.customenchantment.menu.anvil`
- [ ] Extends `ExtraData`
- [ ] Fields: `Slot2Handler activeHandler`, `AnvilItemData itemData1`, `AnvilItemData itemData2`, `CEAnvilSettings settings`
- [ ] Use Lombok `@Getter @Setter`

### AnvilItemData (NEW)
- [ ] Replaces `CEAnvilMenu.ItemData` (cannot reuse - inner class of frozen code)
- [ ] Fields: `ItemStack itemStack`, `CEItem ceItem`
- [ ] Method: `updateItemStack(ItemStack)` → also refreshes ceItem via `CEAPI.getCEItem()`

### CEAnvilSettings (NEW)
- [ ] `initialize(MenuData)` reads all slot positions from `ItemData.getSlots()`
- [ ] Stores default ItemStacks for slot restoration (clone from `ItemStackBuilder`)
- [ ] `getSlots(String itemName)` returns slot list from YAML
- [ ] `getDefaultItemStack(String itemName)` returns cloned YAML default

### YAML File (NEW)
- [ ] Create `src/main/resources/menu-new/ce-anvil.yml`
- [ ] `type: 'ce-anvil'` matches `CEAnvilCustomMenu.getType()`
- [ ] All slot numbers from legacy layout grid (verified in YAML_CONVERSION.md)
- [ ] Confirm variants as template items (NO slot field)
- [ ] Page indicator templates (`has-next-page`, `has-previous-page`) as templates (NO slot)
- [ ] `data:` section with `default-view.enchant-group` and `preview-index-order`
- [ ] UTF-8 encoding for Vietnamese text

## Phase 3B: Handler Interface & Abstractions

### Slot2Handler Interface (NEW)
- [ ] `boolean isSuitable(CEItem ceItem)`
- [ ] `void updateView(CEAnvilCustomMenu menu)`
- [ ] `void updateConfirm(CEAnvilCustomMenu menu)`
- [ ] `void clickProcess(CEAnvilCustomMenu menu, String itemName)`
- [ ] `ApplyReason apply(CEItem ceItem1, CEItem ceItem2)`
- [ ] `void clearPreviews(CEAnvilCustomMenu menu)`

### AbstractListHandler<L> (NEW)
- [ ] Implements `Slot2Handler`
- [ ] State: page, maxPage, chooseIndex, list
- [ ] Abstract: `getList()`, `getDisplayItem()`, `getApplyReason()`, `getConfirmTemplateName()`
- [ ] Pagination: `nextPage()`, `previousPage()`, `updatePageDisplay()`
- [ ] Selection: `chooseItem(String previewName)` with glow effect
- [ ] Preview index mapping from config (`preview-index-order`)
- [ ] `clearPreviews()` clears all 5 previews + page indicators

## Phase 3C: Simple Handlers (8 files)

Each handler is stateless, shows 1 preview (`preview3`), calls CEItem methods directly:

### BookHandler
- [ ] `isSuitable()`: `ceItem instanceof CEBook`
- [ ] `updateView()`: `book.testApplyTo(weapon)` → preview3
- [ ] `updateConfirm()`: template `"confirm-book"`
- [ ] `apply()`: `book.applyByMenuTo(ceItem1)`

### EnchantPointHandler
- [ ] `isSuitable()`: `ceItem instanceof CEEnchantPoint`
- [ ] `updateView()`: `enchantPoint.testApplyTo(weapon)` → preview3
- [ ] `updateConfirm()`: template `"confirm-enchant-point"`
- [ ] `apply()`: `enchantPoint.applyByMenuTo(ceItem1)`

### GemHandler
- [ ] `isSuitable()`: `ceItem instanceof CEGem`
- [ ] `updateConfirm()`: template `"confirm-gem"`

### GemDrillHandler
- [ ] `isSuitable()`: `ceItem instanceof CEGemDrill`
- [ ] `updateView()`: `gemDrill.testApplyByMenuTo(weapon)` → preview3
- [ ] `updateConfirm()`: THREE variants based on drill state:
  - `drillSize >= maxDrill` → `"confirm-gem-drill-max"`
  - `chance < 100` → `"confirm-gem-drill-with-chance"` with `{chance}` placeholder
  - `else` → `"confirm-gem-drill"`
- [ ] `apply()`: `gemDrill.applyByMenuTo(ceItem1)`

### ProtectDeadHandler
- [ ] `isSuitable()`: `ceItem instanceof CEProtectDead`
- [ ] `updateConfirm()`: template `"confirm-protect-dead"`

### ProtectDestroyHandler
- [ ] `isSuitable()`: `ceItem instanceof CEProtectDestroy`
- [ ] `updateConfirm()`: template `"confirm-protect-destroy"`

### LoreFormatHandler
- [ ] `isSuitable()`: `ceItem instanceof CELoreFormat`
- [ ] `updateConfirm()`: template `"confirm-lore-format"`

### EraseEnchantHandler
- [ ] `isSuitable()`: `ceItem instanceof CEEraseEnchant`
- [ ] `updateConfirm()`: template name TBD (check legacy YAML)

## Phase 3D: List Handlers (4 files)

Each handler extends `AbstractListHandler<L>`, shows paginated list in preview1-5:

### RemoveEnchantHandler
- [ ] Extends `AbstractListHandler<CEEnchantSimple>`
- [ ] `getList()`: `removeEnchant.getRemoveEnchantList(weapon.getCESimpleList())`
- [ ] `getDisplayItem()`: `CEAPI.getCEBookItemStack(enchant)`
- [ ] `getApplyReason()`: `removeEnchant.applyByMenuTo(ceItem1, selected)`
- [ ] `getConfirmTemplateName()`: `"confirm-remove-enchant"`

### RemoveEnchantPointHandler
- [ ] Extends `AbstractListHandler<CEEnchantSimple>`
- [ ] `getConfirmTemplateName()`: `"confirm-remove-enchant-point"`

### RemoveGemHandler
- [ ] `getConfirmTemplateName()`: `"confirm-remove-gem"`

### RemoveProtectDeadHandler
- [ ] `getConfirmTemplateName()`: `"confirm-remove-protect-dead"`

## Phase 3E: DefaultHandler (Special)

- [ ] Implements `Slot2Handler`
- [ ] `isSuitable()`: always used when slot2 is empty but slot1 has weapon
- [ ] Shows weapon's current enchants in preview1-5 (filtered by enchant-group from YAML data)
- [ ] Has own pagination state (page, maxPage, enchant list)
- [ ] `apply()` returns `NOTHING` (display only)
- [ ] Reads `default-view.enchant-group` from `menuData.getDataConfig()`

## Phase 3F: AbstractItem Subclasses (4 files)

### AnvilSlotItem
- [ ] `getType()` returns `"anvil-slot"`
- [ ] `handleClick()`: reads `slot-name` from `itemData.getDataConfig()`, calls `menu.returnItem(slotName)`

### AnvilConfirmItem
- [ ] `getType()` returns `"anvil-confirm"`
- [ ] `handleClick()`: calls `menu.confirm()`

### AnvilPreviewItem
- [ ] `getType()` returns `"anvil-preview"`
- [ ] `handleClick()`: reads `preview-name` from data, calls `handler.clickProcess(menu, previewName)`

### AnvilPageItem
- [ ] `getType()` returns `"anvil-page"`
- [ ] `handleClick()`: reads `page-action` from data, calls `handler.clickProcess(menu, pageAction)`

## Phase 3G: CEAnvilCustomMenu (Main Menu)

### Core Structure
- [ ] `package com.bafmc.customenchantment.menu.anvil`
- [ ] Extends `AbstractMenu<MenuData, CEAnvilExtraData>`
- [ ] `MENU_NAME = "ce-anvil"` (same as legacy - YAML type must match)
- [ ] `getType()` returns `MENU_NAME`
- [ ] `registerItems()`: DefaultItem, AnvilSlotItem, AnvilConfirmItem, AnvilPreviewItem, AnvilPageItem

### Static Handler Registry
- [ ] `static Map<String, Slot2Handler> handlerMap`
- [ ] `static registerHandler(String type, Slot2Handler handler)`
- [ ] Handler lookup by CEItemType

### Slot1 Validation
- [ ] `isSlot1Suitable(CEItem)`: checks if item is CEWeapon (or use handler pattern)
- [ ] Only CEWeapon goes into slot1

### Lifecycle Methods
- [ ] `setupItems()`: `super.setupItems()` → `settings.initialize(menuData)` → `updateMenu()` if state exists
- [ ] `handleClick(ClickData)`: `super.handleClick(data)` (routes to AbstractItem)
- [ ] `handlePlayerInventoryClick(ClickData)`: CEAPI.getCEItem → addItem
- [ ] `handleClose()`: returnItems to player

### Core Logic (Re-implemented from CEAnvilMenu)
- [ ] `addItem(ItemStack, CEItem)` → returns AddReason enum
  - Check if slot1 suitable → set itemData1
  - Auto-create DefaultHandler when slot1 filled
  - Check handler map for slot2 → set activeHandler + itemData2
  - Return ALREADY_HAS / NOT_SUITABLE as appropriate
- [ ] `confirm()` → delegates to `activeHandler.apply()`
  - Handles ApplyResult: SUCCESS, FAIL, FAIL_AND_UPDATE, DESTROY, NOTHING, CANCEL
  - Updates item amounts
  - Writes logs
  - Gives rewards
  - Sends messages (from message keys, NOT hardcoded)
- [ ] `returnItem(String name)` → returns item to player
  - Clears handler previews before transition
  - Slot2 return: reverts to DefaultHandler
  - Slot1 return: clears everything
- [ ] `returnItems()` → returns both items on close
- [ ] `updateMenu()` → updateSlot1 + updateSlot2 + updatePreview + updateConfirm

### Config-Driven Utilities
- [ ] `updateSlots(String name, ItemStack)`: slot lookup from settings, null→restore default
- [ ] `getTemplateItemStack(String name)`: fetch from menuData.getItemMap()
- [ ] `getTemplateItemStack(String name, Placeholder)`: fetch with placeholder support
- [ ] Zero hardcoded slot numbers in Java
- [ ] Zero hardcoded messages in Java

## Phase 3H: Command & Registration

### CEAnvilCommand.java (NEW)
- [ ] `package com.bafmc.customenchantment.command`
- [ ] Implements `AdvancedCommandExecutor`
- [ ] Opens menu via `MenuOpener.builder()` (follows BookCraftCommand pattern)

### plugin.yml (APPEND)
- [ ] Add `ceanvil-new` entry with description

### CommandModule.java (APPEND)
- [ ] Register `/ceanvil-new` command using `CEAnvilCommand`
- [ ] Follow exact pattern of `tinkerer-new` / `bookcraft-new` registration

### MenuModule.java (APPEND)
- [ ] Register `CEAnvilCustomMenu` with `MenuRegister.instance().registerStrategy()`
- [ ] Register all 13 handlers with `CEAnvilCustomMenu.registerHandler()`
- [ ] Do NOT modify legacy view registrations

## Phase 3I: Testing

### Unit Tests
- [ ] addItem weapon → sets itemData1
- [ ] addItem weapon → auto-creates DefaultHandler
- [ ] addItem book → sets BookHandler as activeHandler
- [ ] addItem duplicate slot1 → returns ALREADY_HAS_SLOT1
- [ ] addItem duplicate slot2 → returns ALREADY_HAS_SLOT2
- [ ] addItem unsupported → returns NOT_SUITABLE
- [ ] returnItem slot1 → clears all state
- [ ] returnItem slot2 → reverts to DefaultHandler
- [ ] handleClose → returns all items to player
- [ ] Slot numbers from config, not hardcoded
- [ ] Handler resolution for all 13 CEItem types

### Manual Testing
- [ ] `/ceanvil-new` opens menu with correct layout
- [ ] Place weapon via player inventory click
- [ ] DefaultHandler shows enchant list from weapon
- [ ] Place book → BookHandler shows preview
- [ ] Confirm → applies enchant, amounts update
- [ ] Return slot2 → reverts to default
- [ ] Return slot1 → clears all
- [ ] Close → items returned to player
- [ ] All 13 handler types functional
- [ ] List handler pagination (prev/next) works
- [ ] List handler selection (glow effect) works
- [ ] Confirm button appearance changes per handler type
- [ ] GemDrill chance placeholder `{chance}` displays correctly
- [ ] Legacy `/ceanvil` still works unchanged

## Invariants (Verify After Each Change)

1. **Legacy untouched** - `git diff` shows zero changes to frozen files
2. **Items never duplicate** - returned count == placed count
3. **Handler cleanup on transition** - old previews cleared before new handler
4. **Slots from config** - zero hardcoded slot numbers in Java
5. **Messages from config** - zero hardcoded messages in Java
6. **Templates accessible** - all confirm variants fetchable from menuData
7. **Both menus work** - `/ceanvil` (legacy) and `/ceanvil-new` (new) both functional
