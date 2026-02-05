# CustomEnchantment Item System Reference

This document provides comprehensive reference for the Item system in CustomEnchantment, including item types, data structures, and item handling.

## Table of Contents

- [Overview](#overview)
- [Item Types](#item-types)
- [Item Architecture](#item-architecture)
- [CEItem Base Class](#ceitem-base-class)
- [CEWeaponAbstract](#ceweaponabstract)
- [Weapon Components](#weapon-components)
- [Item Factory System](#item-factory-system)
- [NBT Data Structure](#nbt-data-structure)
- [Item Storage](#item-storage)
- [Usage Examples](#usage-examples)

---

## Overview

The Item system handles all custom items in the plugin, from weapons with enchantments to consumable items like books and scrolls.

### Key Design Principles

1. **Type-Based Identification** - Items identified by type string in NBT
2. **Factory Pattern** - Each item type has a factory for creation
3. **Component Pattern** - Weapons composed of enchant, gem, display, data components
4. **Import/Export** - Items serialize to/from NBT for persistence

**Source Location:** `src/main/java/com/bafmc/customenchantment/item/`

---

## Item Types

**Source:** `CEItemType.java`

### All Item Types

| Type Constant | Value | Description |
|---------------|-------|-------------|
| `DEFAULT` | `"default"` | Default/unknown type |
| `WEAPON` | `"weapon"` | Custom weapons (swords, bows, armor) |
| `MASK` | `"mask"` | Cosmetic mask items |
| `BOOK` | `"book"` | Enchantment books |
| `RANDOM_BOOK` | `"randombook"` | Random enchantment books |
| `REMOVE_ENCHANT` | `"removeenchant"` | Enchantment removal scrolls |
| `ERASE_ENCHANT` | `"eraseenchant"` | Complete enchant erase |
| `PROTECT_DEAD` | `"protectdead"` | Soul protection items |
| `GEM` | `"gem"` | Socket gems |
| `GEM_DRILL` | `"gemdrill"` | Gem socket drills |
| `REMOVE_PROTECT_DEAD` | `"removeprotectdead"` | Remove soul protection |
| `REMOVE_GEM` | `"removegem"` | Gem removal tools |
| `REMOVE_ENCHANT_POINT` | `"removeenchantpoint"` | Enchant point removal |
| `PROTECT_DESTROY` | `"protectdestroy"` | Destroy protection |
| `NAME_TAG` | `"nametag"` | Custom name tags |
| `ENCHANT_POINT` | `"enchantpoint"` | Enchant point items |
| `INCREASE_RATE_BOOK` | `"increaseratebook"` | Success rate boosters |
| `STORAGE` | `"storage"` | General storage items |
| `BANNER` | `"banner"` | Banner items |
| `LORE_FORMAT` | `"loreformat"` | Lore formatting items |
| `ARTIFACT` | `"artifact"` | Artifact equipment |
| `SIGIL` | `"sigil"` | Sigil items |
| `OUTFIT` | `"outfit"` | Outfit items |
| `SKIN` | `"skin"` | Weapon skin items |

---

## Item Architecture

### Class Hierarchy

```
CEItem<T extends CEItemData>
    │
    ├── CEWeaponAbstract<T>       # Base for equipable items
    │   ├── CEWeapon              # Standard weapons
    │   ├── VanillaItem           # Vanilla items with CE data
    │   ├── CEArtifact            # Artifact items
    │   ├── CESigil               # Sigil items
    │   └── CEOutfit              # Outfit items
    │
    ├── CEBook                    # Enchantment books
    ├── CEGem                     # Socket gems
    ├── CEMask                    # Mask items
    ├── CEProtectDead             # Soul protection
    ├── CERemoveEnchant           # Enchant removal
    └── ... (other consumables)
```

### Component Structure (Weapons)

```
CEWeaponAbstract
    ├── WeaponEnchant      # List of enchantments
    ├── WeaponGem          # List of socketed gems
    ├── WeaponDisplay      # Lore and display settings
    ├── WeaponData         # Item data (enchant points, etc.)
    └── WeaponAttribute    # Vanilla attribute modifiers
```

---

## CEItem Base Class

**Source:** `CEItem.java`

The base class for all custom items.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `type` | `String` | Item type identifier |
| `craftItemStack` | `CECraftItemStackNMS` | NMS item wrapper |
| `data` | `T extends CEItemData` | Item-specific data |

### Key Methods

#### exportTo()

Exports item to Bukkit ItemStack with NBT data.

```java
public ItemStack exportTo() {
    return exportTo(data);
}
```

#### importFrom(ItemStack)

Imports data from an ItemStack's NBT.

```java
// Implemented by subclasses
public void importFrom(ItemStack itemStack);
```

#### applyTo(CEItem)

Applies this item to another item (for consumables).

```java
public ApplyReason applyTo(CEItem<T> ceItem) {
    return ApplyReason.NOTHING;
}
```

### Apply Results

**Source:** `ApplyReason.java`, `ApplyResult.java`

| Result | Description |
|--------|-------------|
| `NOTHING` | No action taken |
| `SUCCESS` | Application successful |
| `FAIL` | Application failed |
| `DESTROY` | Source item destroyed on failure |

---

## CEWeaponAbstract

**Source:** `CEWeaponAbstract.java`

Base class for all equipable items (weapons, armor, artifacts).

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `weaponEnchant` | `WeaponEnchant` | Enchantment list |
| `weaponGem` | `WeaponGem` | Gem socket list |
| `weaponDisplay` | `WeaponDisplay` | Display/lore handler |
| `weaponData` | `WeaponData` | Item data |
| `weaponAttribute` | `WeaponAttribute` | Vanilla attributes |
| `weaponSettingsName` | `String` | Settings profile name |
| `weaponType` | `CEWeaponType` | Custom weapon type |
| `lastTimeModifier` | `long` | Last modification timestamp |

### Import/Export Flow

```
ItemStack
    └── importFrom(ItemStack)
        ├── Read CE compound tag
        ├── Load weaponSettingsName
        ├── weaponEnchant.importFrom(tag.getList("enchant"))
        ├── weaponGem.importFrom(tag.getCompound("gem"))
        ├── weaponDisplay.importFrom(tag.getCompound("lore"))
        ├── weaponData.importFrom(tag.getCompound("data"))
        └── weaponAttribute.importFrom(compound.getList("AttributeModifiers"))

CEWeaponAbstract
    └── exportTo()
        ├── Get CE compound tag
        ├── Set type, settings
        ├── weaponEnchant.exportTo() → tag.set("enchant", ...)
        ├── weaponGem.exportTo() → tag.set("gem", ...)
        ├── weaponDisplay.exportTo() → tag.set("lore", ...)
        ├── weaponData.exportTo() → tag.set("data", ...)
        ├── weaponAttribute.exportTo() → compound.set("AttributeModifiers", ...)
        └── weaponDisplay.apply(itemStack) → Update lore
```

### Static Helper

```java
public static CEWeaponAbstract getCEWeapon(ItemStack itemStack) {
    return (CEWeaponAbstract) CEAPI.getCEItem(itemStack);
}
```

---

## Weapon Components

### WeaponEnchant

**Source:** `WeaponEnchant.java`

Manages the list of enchantments on an item.

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `ceEnchantSimpleList` | `List<CEEnchantSimple>` | List of enchantments |

#### Key Methods

```java
// Add enchantment (if not exists)
void addCESimple(CEEnchantSimple ceEnchantSimple)

// Force add (removes existing, then adds)
void forceAddCESimple(CEEnchantSimple ceEnchantSimple)

// Remove enchantment by name
void removeCESimple(String name)

// Get enchantment by name
CEEnchantSimple getCESimple(String name)

// Check if enchantment exists
boolean containsCESimple(String name)

// Get total enchant points used
int getTotalEnchantPoint()

// Get list sorted by priority
List<CEEnchantSimple> getCESimpleListByPriority()

// Check for blacklist conflicts
boolean hasEnchantBlacklist(CEEnchantSimple ceEnchantSimple)
```

#### NBT Format

```
NBTTagList "enchant":
    - "enchant_name:level:success:destroy"
    - "lifesteal:3:100:0"
    - "critical:2:85:15"
```

---

### WeaponGem

**Source:** `WeaponGem.java`

Manages socket gems and gem drills on an item.

#### Properties

| Property | Type | Description |
|----------|------|-------------|
| `gemList` | `List<CEGemSimple>` | Socketed gems |
| `gemDrillList` | `List<CEGemDrillSimple>` | Gem drill sockets |

#### Key Methods

```java
// Add gem
void addCEGemSimple(CEGemSimple gemSimple)

// Add gem drill socket
void addCEGemDrillSimple(CEGemDrillSimple gemDrillSimple)

// Remove gem by index
void removeCEGemSimple(int index)

// Get gem by name
CEGemSimple getCEGemSimple(String name)

// Count gems of type
int getGemTypeCount(String name)

// Get total gem count
int getTotalGemPoint()

// Check if can add more gems
boolean isEnoughGemPointForNextGem(CEGemSimple gemSimple)

// Find suitable drill for gem
CEGemDrillSimple getSuitableGemDrill(CEGemSimple ceGemSimple)
```

#### NBT Format

```
NBTTagCompound "gem":
    NBTTagList "gem_list":
        - "ruby:3"
        - "diamond:2"
    NBTTagList "gem_drill_list":
        - "drill_red"
        - "drill_blue"
```

---

### WeaponDisplay

**Source:** `WeaponDisplay.java`

Manages item display name and lore.

#### Responsibilities

- Generate enchantment lore lines
- Apply custom display name
- Handle lore formatting
- Placeholder replacement

#### NBT Format

```
NBTTagCompound "lore":
    "name": "Custom Name"
    "lore": ["Line 1", "Line 2"]
```

---

### WeaponData

**Source:** `WeaponData.java`

Stores additional item data.

#### Key Data

| Key | Type | Description |
|-----|------|-------------|
| `totalEnchantPoint` | `int` | Max enchant points allowed |
| `protectDead` | `boolean` | Has soul protection |
| `protectDestroy` | `boolean` | Has destroy protection |

#### NBT Format

```
NBTTagCompound "data":
    "enchant_point": 10
    "protect_dead": true
    "protect_destroy": false
```

---

### WeaponAttribute

**Source:** `WeaponAttribute.java`

Manages vanilla Minecraft attribute modifiers on the item.

#### Key Methods

```java
// Clear all attributes
void clearAttribute()

// Import from NBT
void importFrom(NMSNBTTagList source)

// Export to NBT
NMSNBTTagList exportTo()
```

---

## Item Factory System

**Source:** `CEItemFactory.java`, `CEItemRegister.java`

### CEItemFactory

Abstract factory for creating items.

```java
public abstract class CEItemFactory<T extends CEItem> {
    // Create item from ItemStack
    public abstract T create(ItemStack itemStack);

    // Check if type matches
    public abstract boolean isMatchType(String type);

    // Register factory
    public void register() {
        CEItemRegister.register(this);
    }
}
```

### Factory Registration

**Source:** `ItemModule.java`

```java
public void onEnable() {
    new VanillaItemFactory().register();
    new CEArtifactFactory().register();
    new CEMaskFactory().register();
    new CEWeaponFactory().register();
    new CEBookFactory().register();
    new CEProtectDeadFactory().register();
    new CEGemFactory().register();
    // ... more factories
}
```

### Item Creation Flow

```
ItemStack
    └── CEAPI.getCEItem(itemStack)
        └── CEItemRegister.getCEItem(itemStack)
            └── For each factory:
                ├── factory.isMatchType(itemStack)?
                └── factory.create(itemStack)
```

---

## NBT Data Structure

**Source:** `CENBT.java`

### NBT Keys

| Constant | Value | Description |
|----------|-------|-------------|
| `CE` | `"customenchantment"` | Root CE compound |
| `TYPE` | `"type"` | Item type |
| `SETTINGS` | `"settings"` | Settings profile |
| `ENCHANT` | `"enchant"` | Enchant list |
| `DATA` | `"data"` | Item data |
| `NAME` | `"name"` | Item/enchant name |
| `LEVEL` | `"lvl"` | Level |
| `SUCCESS` | `"success"` | Success rate |
| `DESTROY` | `"destroy"` | Destroy rate |
| `POINT` | `"point"` | Enchant points |
| `TIME` | `"time"` | Modification time |
| `PATTERN` | `"pattern"` | Item pattern |
| `CUSTOM_TYPE` | `"custom-type"` | Weapon subtype |

### Complete NBT Structure

```
ItemStack NBT:
    "customenchantment":
        "type": "weapon"
        "settings": "default"
        "custom-type": "SWORD"
        "time": 1704067200000
        "enchant":
            - "lifesteal:3:100:0"
            - "critical:2:85:15"
        "gem":
            "gem_list":
                - "ruby:3"
            "gem_drill_list":
                - "drill_red"
        "lore":
            "name": "Excalibur"
        "data":
            "enchant_point": 10
            "protect_dead": true
    "AttributeModifiers":
        - {AttributeName: "generic.attack_damage", Amount: 10.0, ...}
```

---

## Item Storage

**Source:** `CEItemStorage.java`, `CEItemStorageMap.java`

### Storage Structure

```
CustomEnchantment
    └── CEItemStorageMap
        ├── "weapon" → CEWeaponStorage
        ├── "book" → CEBookStorage
        ├── "gem" → CEGemStorage
        ├── "artifact" → CEArtifactStorage
        └── ... (other storages)
```

### CEItemStorage

Generic storage for item types.

```java
public class CEItemStorage<T extends CEItem> {
    private Map<String, T> map;

    public T get(String key);
    public void put(String key, T item);
    public List<String> getKeys();
    public int size();
}
```

### Loading Items

Items are loaded from configuration files:

```
plugins/CustomEnchantment/
    ├── weapon/             # Weapon configurations
    │   ├── sword.yml
    │   └── bow.yml
    ├── artifact/           # Artifact configurations
    │   └── artifacts.yml
    └── storage/            # Stored item templates
        └── save-items.yml
```

---

## Usage Examples

### Getting CEItem from ItemStack

```java
ItemStack itemStack = player.getInventory().getItemInHand();
CEItem<?> ceItem = CEAPI.getCEItem(itemStack);

if (ceItem instanceof CEWeaponAbstract weapon) {
    // Access weapon components
    WeaponEnchant enchants = weapon.getWeaponEnchant();
    WeaponGem gems = weapon.getWeaponGem();
}
```

### Adding Enchantment to Weapon

```java
CEWeaponAbstract weapon = CEWeaponAbstract.getCEWeapon(itemStack);
CEEnchantSimple enchant = new CEEnchantSimple("lifesteal", 3);

// Add if not exists
weapon.getWeaponEnchant().addCESimple(enchant);

// Or force add (replace if exists)
weapon.getWeaponEnchant().forceAddCESimple(enchant);

// Export and update
player.setItemInHand(weapon.exportTo());
```

### Adding Gem to Weapon

```java
CEWeaponAbstract weapon = CEWeaponAbstract.getCEWeapon(itemStack);
CEGemSimple gem = new CEGemSimple("ruby", 3);

// Check if can add
if (weapon.getWeaponGem().isEnoughGemPointForNextGem(gem)) {
    weapon.getWeaponGem().addCEGemSimple(gem);
    player.setItemInHand(weapon.exportTo());
}
```

### Checking Enchantments

```java
CEWeaponAbstract weapon = CEWeaponAbstract.getCEWeapon(itemStack);
WeaponEnchant enchants = weapon.getWeaponEnchant();

// Check for specific enchantment
if (enchants.containsCESimple("lifesteal")) {
    CEEnchantSimple lifesteal = enchants.getCESimple("lifesteal");
    int level = lifesteal.getLevel();
}

// Get all enchantments
List<CEEnchantSimple> allEnchants = enchants.getCESimpleList();

// Get enchantments by priority
List<CEEnchantSimple> prioritized = enchants.getCESimpleListByPriority();
```

### Creating Item from Storage

```java
// Get item storage
CEItemStorage<?> weaponStorage = CustomEnchantment.instance()
    .getCeItemStorageMap()
    .get(CEItemType.WEAPON);

// Get item by name
CEItem<?> excalibur = weaponStorage.get("excalibur");

// Export to ItemStack
ItemStack itemStack = excalibur.exportTo();
```

### Applying Consumable to Weapon

```java
CEItem<?> book = CEAPI.getCEItem(bookStack);
CEItem<?> weapon = CEAPI.getCEItem(weaponStack);

ApplyReason result = book.applyTo(weapon);

switch (result) {
    case SUCCESS:
        // Update weapon in inventory
        player.setItemInHand(weapon.exportTo());
        // Remove book
        break;
    case FAIL:
        // Application failed
        break;
    case DESTROY:
        // Weapon destroyed
        break;
}
```

---

## Item Type Specific Documentation

### Enchantment Books (CEBook)

- Contains single enchantment
- Has success/destroy rates
- Can be applied to weapons

### Gems (CEGem)

- Provides attribute bonuses
- Requires drill socket
- Has level system

### Artifacts (CEArtifact)

- Special equipment items
- Has artifact group
- Level-based stats

### Sigils (CESigil)

- Special ability items
- Has sigil group
- Level-based effects

---

## See Also

- [ENCHANTMENT-CONFIG.md](ENCHANTMENT-CONFIG.md) - Enchantment configuration
- [ATTRIBUTE-SYSTEM.md](ATTRIBUTE-SYSTEM.md) - Attribute system
- [COMMANDS.md](COMMANDS.md) - Item-related commands
- [ARCHITECTURE.md](ARCHITECTURE.md) - Overall architecture
