# Item Module Summary

**Package:** `com.bafmc.customenchantment.item` | **Classes:** 129 | **Init Phase:** 3
**Purpose:** 22 item factories for all CE item types

## Execution Flow
- **Item Creation**: ConfigModule (YAML) -> CEEnchantMap/CEItemConfig -> ItemModule (CEItemFactory) -> CEItem/CEWeapon instances -> MenuModule (display/trade) -> Player inventory

## Key Classes

### CEItemRegister (99 lines)
**Type:** class
**Purpose:** Item factory registry
**Key Methods:**
- `register(CEItemFactory clazz)`: 
- `unregister(CEItemFactory clazz)`: 
- `isCEItem(ItemStack itemStack)` -> `boolean`: 
- `isCEItem(ItemStack itemStack, String type)` -> `boolean`: 
- `getCEItem(ItemStack itemStack, String type)` -> `CEItem`: 
- `getCEItemSimple(ItemStack itemStack)` -> `CEItemSimple`: 
- `getCEItem(ItemStack itemStack)` -> `CEItem`: 
- `getCEItemType(ItemStack itemStack)` -> `String`: 

### CEItemFactory (22 lines)
**Type:** abstract
**Purpose:** Base item factory
**Key Methods:**
- `create(ItemStack itemStack)` -> `abstract T`: 
- `register()`: 
- `isMatchType(String type)` -> `abstract boolean`: 
- `isMatchType(ItemStack itemStack)` -> `boolean`: 
- `isAutoGenerateNewItem()` -> `boolean`: 

### CEUnify (167 lines)
**Type:** abstract
**Purpose:** Unified item base
**Fields:** `CEUnifyWeapon unifyWeapon`
**Key Methods:**
- `importFrom(ItemStack itemStack)`: 
- `unifyImportFrom(ItemStack itemStack)`: 
- `exportTo()` -> `ItemStack`: 
- `unifyExportTo()` -> `ItemStack`: 
- `mergeWeapon(CEWeaponAbstract weapon)` -> `CEUnify`: 
- `applyTo(CEItem ceItem)` -> `ApplyReason`: 
- `updateProtectDead(ItemStack itemStack)` -> `ItemStack`: 
- `getDisplay(String display)` -> `String`: 

### CEWeaponAbstract (200 lines)
**Type:** abstract
**Purpose:** Weapon base class
**Fields:** `WeaponEnchant weaponEnchant`, `WeaponDisplay weaponDisplay`, `WeaponGem weaponGem`, `WeaponData weaponData`, `WeaponAttribute weaponAttribute`, `long lastTimeModifier` (+5 more)
**Key Methods:**
- `importFrom(ItemStack itemStack)`: 
- `weaponAbstractImportFrom(ItemStack itemStack)`: 
- `exportTo()` -> `ItemStack`: 
- `weaponAbstractExportTo()` -> `ItemStack`: 
- `updateTimeModifierTag(NMSNBTTagCompound tag)`: 
- `updateTimeModifier()`: 
- `clearTimeModifier()`: 
- `clearRepairCost()`: 
**Annotations:** @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Setter, @Getter, @Getter, @Setter

### CEItem (77 lines)
**Type:** abstract
**Purpose:** Base custom item
**Fields:** `String type`, `CECraftItemStackNMS craftItemStack`, `T data`
**Key Methods:**
- `exportTo()` -> `ItemStack`: 
- `exportTo(T data)` -> `ItemStack`: 
- `updateDefaultItemStack(ItemStack itemStack)`: 
- `applyTo(CEItem<T> ceItem)` -> `ApplyReason`: 
- `getType()` -> `String`: 
- `setCraftItemStack(CECraftItemStackNMS craftItemStack)`: 
- `getCraftItemStack()` -> `CECraftItemStackNMS`: 
- `getCurrentItemStack()` -> `ItemStack`: 

### WeaponDisplay (538 lines)
**Type:** class
**Purpose:** Weapon display expansion
**Fields:** `List<String> beginLore`, `List<String> middleLore`, `List<String> endLore`, `String displayName`, `Map<Enchantment, Integer> enchantMap`, `List<String> beginLore` (+16 more)
**Key Methods:**
- `apply(ItemStack itemStack)`: 
- `applyNewLore(ItemStack itemStack, ItemMeta itemMeta)`: 
- `applyDisplayName(ItemMeta itemMeta)`: 
- `applyItemFlag(ItemMeta itemMeta)`: 
- `applyEnchant(ItemStack itemStack)`: 
- `importFrom(NMSNBTTagCompound source)`: 
- `exportTo()` -> `NMSNBTTagCompound`: 
- `buildLores()` -> `List<String>`: 
**Annotations:** @Getter, @Setter, @AllArgsConstructor, @AllArgsConstructor, @AllArgsConstructor, @AllArgsConstructor, @AllArgsConstructor, @AllArgsConstructor, @AllArgsConstructor, @AllArgsConstructor, @AllArgsConstructor, @AllArgsConstructor

### CEItemData (27 lines)
**Type:** abstract
**Purpose:** Base item data
**Fields:** `String pattern`
**Key Methods:**
- `equals(Object data)` -> `boolean`: 
- `getPattern()` -> `String`: 
- `setPattern(String pattern)`: 

### CEItemStorage (41 lines)
**Type:** abstract
**Purpose:** Base item storage
**Fields:** `static long serialVersionUID`
**Key Methods:**
- `getByParameter(Parameter parameter)` -> `abstract T`: 
- `getItemStacksByParameter(Parameter parameter)` -> `synchronized List<ItemStack>`: 
- `getItemStackByParameter(Parameter parameter)` -> `ItemStack`: 

### CEItemUsable (17 lines)
**Type:** abstract
**Purpose:** Usable item base
**Key Methods:**
- `useBy(Player player)` -> `abstract boolean`: 
- `applyTo(CEItem ceItem)` -> `ApplyReason`: 

### CEWeaponFactory (34 lines)
**Type:** class
**Purpose:** Weapon factory
**Fields:** `static MaterialList materialWhitelist`
**Key Methods:**
- `create(ItemStack itemStack)` -> `CEWeapon`: 
- `setWhitelist(List<MaterialData> list)`: 
- `isMatchType(String type)` -> `boolean`: 
- `isMatchType(ItemStack itemStack)` -> `boolean`: 
- `isAutoGenerateNewItem()` -> `boolean`: 

### VanillaItemFactory (13 lines)
**Type:** class
**Purpose:** Vanilla item factory
**Key Methods:**
- `create(ItemStack itemStack)` -> `VanillaItem`: 
- `isMatchType(String type)` -> `boolean`: 

### WeaponData (170 lines)
**Type:** class
**Purpose:** Weapon data expansion
**Fields:** `int extraEnchantPoint`, `int extraProtectDead`, `int extraProtectDestroy`, `List<String> extraEnchantPointList`, `List<String> extraProtectDeadList`, `List<String> extraProtectDestroyList`
**Key Methods:**
- `removeEnchantPointIndex(int index)`: 
- `removeProtectDeadIndex(int index)`: 
- `removeProtectDestroyIndex(int index)`: 
- `importFrom(NMSNBTTagCompound source)`: 
- `exportTo()` -> `NMSNBTTagCompound`: 
- `setExtraEnchantPoint(int extraEnchantPoint)`: 
- `setExtraProtectDead(int extraProtectDead)`: 
- `setExtraProtectDestroy(int extraProtectDestroy)`: 
**Annotations:** @Getter, @Setter

### Item Factories (21 classes)
**Pattern:** Each extends `CEItemFactory`, implements `getIdentify()` + `setup()` + action method
**Classes:** CEArtifactFactory, CEBannerFactory, CEBookFactory, CEEnchantPointFactory, CEEraseEnchantFactory, CEGemDrillFactory, CEGemFactory, CEIncreaseRateBookFactory
  ... and 13 more

### Item Implementations (21 classes)
**Pattern:** Each item type has Factory + Item + Data + Storage classes

- **Artifact** (1): CEArtifact
- **Book** (3): CEBook, CEIncreaseRateBook, CERandomBook
- **Gem** (2): CEGem, CERemoveGem
- **GemDrill** (1): CEGemDrill
- **Other** (11): CEBanner, CEEnchantPoint, CEEraseEnchant, CELoreFormat, CEMask (+6 more)
- **Outfit** (1): CEOutfit
- **Sigil** (1): CESigil
- **Skin** (1): CESkin

## Other Classes (73)

- **WeaponEnchant** (176L): Weapon enchant expansion
- **WeaponGem** (181L): Weapon gem expansion
- **CERandomBookFilter** (181L): RandomBook item type
- **ItemModule** (57L): PluginModule — item factory registration
- **CEUnifyWeapon** (72L): Unified weapon (ITrade)
- **CEWeapon** (68L): Weapon implementation
- **WeaponAttribute** (96L): Weapon attribute expansion
- **WeaponSettings** (88L): Weapon settings (Builder)
- **CEArtifactData** (51L) extends CEItemData: Artifact item type
- **CEArtifactStorage** (67L) extends CEItemStorage: Artifact item type
- **CEBookStorage** (103L) extends CEItemStorage: Book item type
- **CEGemData** (66L) extends CEItemData: Gem item type
- **CEGemStorage** (61L) extends CEItemStorage: Gem item type
- **CEGemSimple** (58L): Gem item type
- **CEIncreaseRateBookData** (55L) extends CEItemData: IncreaseRateBook item type
- **CEIncreaseRateBookStorage** (59L) extends CEItemStorage: IncreaseRateBook item type
- **CEOutfitData** (104L) extends CEItemData: Outfit item type
- **CEOutfitStorage** (67L) extends CEItemStorage: Outfit item type
- **CERandomBookPlayerFilter** (98L): RandomBook item type
- **CESigilData** (60L) extends CEItemData: Sigil item type
- **CESigilStorage** (67L) extends CEItemStorage: Sigil item type
- **CEItemOptimizeLoader** (49L): Item optimization loader
- **CEItemSimple** (17L): Simple item wrapper
- **CEItemType** (28L): Item type definitions
- **CEItemExpansion** (13L): Item expansion utilities
- **CENBT** (20L): NBT tag handling
- **CEUnifyData** (31L): Unified item data
- **CEWeaponData** (5L): Weapon data
- **CEWeaponStorage** (18L): Weapon storage
- **CEWeaponType** (10L): Weapon types
- **VanillaItem** (48L): Vanilla item wrapper
- **VanillaItemData** (17L): Vanilla item data
- **VanillaItemStorage** (37L): Vanilla item storage
- **CEArtifactGroup** (19L): Artifact item type
- **CEArtifactGroupMap** (6L): Artifact item type
- **CEBannerData** (7L) extends CEItemData: Banner item type
- **CEBannerStorage** (20L) extends CEItemStorage: Banner item type
- **CEBookData** (25L) extends CEItemData: Book item type
- **CEEnchantPointData** (46L) extends CEItemData: EnchantPoint item type
- **CEEnchantPointStorage** (20L) extends CEItemStorage: EnchantPoint item type
- **CEEnchantPointSimple** (11L): EnchantPoint item type
- **CEEraseEnchantData** (27L) extends CEItemData: EraseEnchant item type
- **CEEraseEnchantStorage** (20L) extends CEItemStorage: EraseEnchant item type
- **CEGemSettings** (45L): Gem item type
- **CEGemDrillData** (30L) extends CEItemData: GemDrill item type
- **CEGemDrillStorage** (20L) extends CEItemStorage: GemDrill item type
- **CEGemDrillSimple** (33L): GemDrill item type
- **CELoreFormatData** (24L) extends CEItemData: LoreFormat item type
- **CELoreFormatStorage** (20L) extends CEItemStorage: LoreFormat item type
- **CEMaskData** (7L) extends CEItemData: Mask item type
- **CEMaskStorage** (20L) extends CEItemStorage: Mask item type
- **CENameTagData** (25L) extends CEItemData: NameTag item type
- **CENameTagStorage** (20L) extends CEItemStorage: NameTag item type
- **CEOutfitGroup** (19L): Outfit item type
- **CEOutfitGroupMap** (6L): Outfit item type
- **CEProtectDeadData** (25L) extends CEItemData: ProtectDead item type
- **CEProtectDeadStorage** (20L) extends CEItemStorage: ProtectDead item type
- **CEProtectDestroyData** (35L) extends CEItemData: ProtectDestroy item type
- **CEProtectDestroyStorage** (20L) extends CEItemStorage: ProtectDestroy item type
- **CERandomBookData** (25L) extends CEItemData: RandomBook item type
- **CERandomBookStorage** (20L) extends CEItemStorage: RandomBook item type
- **CERemoveEnchantData** (27L) extends CEItemData: RemoveEnchant item type
- **CERemoveEnchantStorage** (20L) extends CEItemStorage: RemoveEnchant item type
- **CERemoveEnchantPointData** (8L) extends CEItemData: RemoveEnchantPoint item type
- **CERemoveEnchantPointStorage** (20L) extends CEItemStorage: RemoveEnchantPoint item type
- **CERemoveGemData** (8L) extends CEItemData: RemoveGem item type
- **CERemoveGemStorage** (20L) extends CEItemStorage: RemoveGem item type
- **CERemoveProtectDeadData** (25L) extends CEItemData: RemoveProtectDead item type
- **CERemoveProtectDeadStorage** (20L) extends CEItemStorage: RemoveProtectDead item type
- **CESigilGroup** (19L): Sigil item type
- **CESigilGroupMap** (6L): Sigil item type
- **CESkinData** (7L) extends CEItemData: Skin item type
- **CESkinStorage** (20L) extends CEItemStorage: Skin item type
