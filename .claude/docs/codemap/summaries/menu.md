# Menu Module Summary

**Package:** `com.bafmc.customenchantment.menu` | **Classes:** 76 | **Init Phase:** 3
**Purpose:** 6 menu strategies, 12 anvil handlers
**Depends On:** ConfigModule

## Execution Flow
- **Item Creation**: ConfigModule (YAML) -> CEEnchantMap/CEItemConfig -> ItemModule (CEItemFactory) -> CEItem/CEWeapon instances -> MenuModule (display/trade) -> Player inventory

## Key Classes

### AbstractListHandler (194 lines)
**Type:** abstract
**Purpose:** Menu — CEAnvil
**Fields:** `int page`, `int maxPage`, `int chooseIndex`, `List<L> list`
**Key Methods:**
- `updateView(CEAnvilCustomMenu menu)`: 
- `updateConfirm(CEAnvilCustomMenu menu)`: 
- `clickProcess(CEAnvilCustomMenu menu, String itemName)`: 
- `apply(CEItem ceItem1, CEItem ceItem2)` -> `ApplyReason`: 
- `clearPreviews(CEAnvilCustomMenu menu)`: 
- `getList(CEItem ceItem1, CEItem ceItem2)` -> `abstract List<L>`: 
- `getDisplayItem(L object)` -> `abstract ItemStack`: 
- `getApplyReason(CEItem ceItem1, CEItem ceItem2, L object)` -> `abstract ApplyReason`: 
**Annotations:** @Getter, @Setter, @Getter, @Setter, @Getter, @Setter, @Getter, @Setter

### TinkererTypeRegister (44 lines)
**Type:** class
**Purpose:** Menu — Tinkerer
**Fields:** `static List<TinkererTypeHook> list`
**Key Methods:**
- `register(TinkererTypeHook hook)` -> `boolean`: 
- `getType(CEItem ceItem)` -> `String`: 
- `getType(CEItem ceItem)` -> `String`: 

### BookCraftCustomMenu (474 lines)
**Type:** class
**Purpose:** Menu — BookCraft
**Fields:** `static String[] BOOK_ITEM_NAMES`
**Key Methods:**
- `registerItems()`: 
- `handlePlayerInventoryClick(ClickData data)`: 
- `handleClose()`: 
- `updateMenu()`: 
- `returnBook(String itemName)`: 
- `returnAllBooks()`: 
- `getType()` -> `String`: 
- `setupItems()`: 

### FastCraftRefactored (327 lines)
**Type:** class
**Purpose:** Menu — BookCraft
**Fields:** `BookCraftCustomMenu menu`, `BookData finalResult`, `List<BookData> leftoverBooks`, `int totalBooksUsed`, `List<Integer> slotsToRemove`
**Key Methods:**
- `calculate(Player player)`: 
- `getSlotsToRemoveCount()` -> `int`: 
**Annotations:** @Getter, @Getter, @Getter

### CEAnvilCustomMenu (342 lines)
**Type:** class
**Purpose:** Menu — CEAnvil
**Key Methods:**
- `registerHandler(String type, Supplier<Slot2Handler> factory)`: 
- `registerItems()`: 
- `handlePlayerInventoryClick(ClickData data)`: 
- `handleClose()`: 
- `confirm()`: 
- `returnItem(String name)`: 
- `getType()` -> `String`: 
- `setupItems()`: 

### BookUpgradeCustomMenu (477 lines)
**Type:** class
**Purpose:** Menu — BookUpgrade
**Fields:** `static BookUpgradeSettings settings`
**Key Methods:**
- `registerItems()`: 
- `handlePlayerInventoryClick(ClickData data)`: 
- `handleClose()`: 
- `addBook(ItemStack clickedItem, CEEnchantSimple ceEnchantSimple)` -> `BookUpgradeAddReason`: 
- `confirmUpgrade()` -> `BookUpgradeConfirmReason`: 
- `returnIngredient(int slotIndex)`: 
- `returnMainBook()`: 
- `updateMenu()`: 

### EquipmentCustomMenu (547 lines)
**Type:** class
**Purpose:** Menu — Equipment
**Fields:** `static String PREFIX`, `static Map<String, EquipmentCustomMenu> menuMap`, `static Map<String, Long> swapSkinCooldowns`, `static long SWAP_SKIN_COOLDOWN_MS`, `BukkitTask updateTask`, `List<EquipmentSlotHandler> handlers` (+1 more)
**Key Methods:**
- `putMenu(Player player, EquipmentCustomMenu menu)` -> `EquipmentCustomMenu`: 
- `removeMenu(Player player)` -> `EquipmentCustomMenu`: 
- `clearAll()`: 
- `registerItems()`: 
- `handlePlayerInventoryClick(ClickData data)`: 
- `handleClose()`: 
- `addItem(InventoryClickEvent e, ItemStack itemStack, CEItem ceItem)` -> `EquipmentAddReason`: 
- `returnItem(String itemName, int slot)`: 

### MenuListenerAbstract (14 lines)
**Type:** abstract
**Purpose:** Menu — Core
**Key Methods:**
- `onMenuOpen(CustomMenuOpenEvent e)` -> `abstract void`: 
- `onMenuClose(CustomMenuCloseEvent e)` -> `abstract void`: 
- `onMenuClick(CustomMenuClickEvent e)` -> `abstract void`: 
- `onInventoryClick(InventoryClickEvent e)` -> `abstract void`: 
- `getMenuName()` -> `abstract String`: 

### TinkererTypeHook (7 lines)
**Type:** abstract
**Purpose:** Menu — Tinkerer
**Key Methods:**
- `getType(CEItem ceItem)` -> `abstract String`: 

### Slot2Handler (20 lines)
**Type:** interface
**Purpose:** Menu — CEAnvil

## Other Classes (66)

- **EquipmentSlotHandler** (17L): Menu — Equipment
- **TinkererCustomMenu** (223L): Menu — Tinkerer
- **EraseEnchantHandler** (193L): Menu — CEAnvil
- **ArtifactUpgradeCustomMenu** (300L): Menu — ArtifactUpgrade
- **ArmorWeaponHandler** (178L): Menu — Equipment
- **MenuModule** (103L): Menu — Core
- **TinkererExtraData** (71L): Menu — Tinkerer
- **TinkererSettings** (81L): Menu — Tinkerer
- **BookCraftExtraData** (66L): Menu — BookCraft
- **BookHandler** (55L): Menu — CEAnvil
- **DefaultHandler** (137L): Menu — CEAnvil
- **EnchantPointHandler** (53L): Menu — CEAnvil
- **GemDrillHandler** (83L): Menu — CEAnvil
- **GemHandler** (53L): Menu — CEAnvil
- **LoreFormatHandler** (53L): Menu — CEAnvil
- **ProtectDeadHandler** (56L): Menu — CEAnvil
- **ProtectDestroyHandler** (53L): Menu — CEAnvil
- **RemoveEnchantPointHandler** (54L): Menu — CEAnvil
- **RemoveGemHandler** (65L): Menu — CEAnvil
- **RemoveProtectDeadHandler** (56L): Menu — CEAnvil
- **BookUpgradeSettings** (96L): Menu — BookUpgrade
- **ArtifactUpgradeExtraData** (54L): Menu — ArtifactUpgrade
- **ArtifactUpgradeSettings** (63L): Menu — ArtifactUpgrade
- **ExtraSlotHandler** (145L): Menu — Equipment
- **ProtectDeadHandler** (56L): Menu — Equipment
- **BookData** (34L): Menu — Core
- **TinkererReward** (31L): Menu — Tinkerer
- **TinkerAcceptItem** (44L): Menu — Tinkerer
- **TinkerSlotItem** (39L): Menu — Tinkerer
- **BookAcceptItem** (31L): Menu — BookCraft
- **BookPreviewItem** (21L): Menu — BookCraft
- **BookReturnItem** (27L): Menu — BookCraft
- **BookSlotItem** (23L): Menu — BookCraft
- **CEAnvilExtraData** (32L): Menu — CEAnvil
- **CEAnvilSettings** (31L): Menu — CEAnvil
- **AnvilItemData** (23L): Menu — CEAnvil
- **AnvilConfirmItem** (18L): Menu — CEAnvil
- **AnvilPageItem** (27L): Menu — CEAnvil
- **AnvilPreviewItem** (27L): Menu — CEAnvil
- **AnvilSlotItem** (21L): Menu — CEAnvil
- **RemoveEnchantHandler** (49L): Menu — CEAnvil
- **BookUpgradeExtraData** (47L): Menu — BookUpgrade
- **BookUpgradeAddReason** (14L): Menu — BookUpgrade
- **BookUpgradeConfirmReason** (14L): Menu — BookUpgrade
- **BookUpgradeData** (22L): Menu — BookUpgrade
- **BookUpgradeLevelData** (12L): Menu — BookUpgrade
- **RequiredXpGroup** (12L): Menu — BookUpgrade
- **XpGroup** (13L): Menu — BookUpgrade
- **BookUpgradeRemindItem** (21L): Menu — BookUpgrade
- **BookUpgradeResultItem** (18L): Menu — BookUpgrade
- **BookUpgradeSlotItem** (19L): Menu — BookUpgrade
- **IngredientPreviewItem** (35L): Menu — BookUpgrade
- **ArtifactUpgradeAddReason** (14L): Menu — ArtifactUpgrade
- **ArtifactUpgradeConfirmReason** (14L): Menu — ArtifactUpgrade
- **ArtifactUpgradeData** (12L): Menu — ArtifactUpgrade
- **ArtifactUpgradeLevelData** (12L): Menu — ArtifactUpgrade
- **ArtifactIngredientPreviewItem** (34L): Menu — ArtifactUpgrade
- **ArtifactRemindItem** (21L): Menu — ArtifactUpgrade
- **PreviewArtifactItem** (18L): Menu — ArtifactUpgrade
- **SelectedArtifactItem** (18L): Menu — ArtifactUpgrade
- **EquipmentExtraData** (30L): Menu — Equipment
- **EquipmentSlotItem** (30L): Menu — Equipment
- **ExtraSlotEquipmentItem** (26L): Menu — Equipment
- **PlayerInfoEquipmentItem** (27L): Menu — Equipment
- **ProtectDeadEquipmentItem** (27L): Menu — Equipment
- **WingsEquipmentItem** (27L): Menu — Equipment
