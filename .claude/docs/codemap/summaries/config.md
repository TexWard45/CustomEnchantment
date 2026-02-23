# Config Module Summary

**Package:** `com.bafmc.customenchantment.config` | **Classes:** 18 | **Init Phase:** 2
**Purpose:** YAML config loading for all 15+ config classes
**Depends On:** EnchantModule | **Depended By:** MenuModule, CustomMenuModule

## Execution Flow
- **Item Creation**: ConfigModule (YAML) -> CEEnchantMap/CEItemConfig -> ItemModule (CEItemFactory) -> CEItem/CEWeapon instances -> MenuModule (display/trade) -> Player inventory

## Configuration
- `config.yml` -> `MainConfig`
  Keys: event.move-period, event.sneak-period, combat.time, combat.staff-min-required-attack-strength-scale, combat.require-weapon (+4 more)
- `messages.yml` -> `CustomEnchantmentMessage`
- `book-craft.yml` -> `BookCraftConfig`
  Keys: money-require.*
- `book-upgrade.yml` -> `BookUpgradeConfig`
- `tinkerer.yml` -> `TinkererConfig`
- `artifact-upgrade.yml` -> `ArtifactUpgradeConfig`
- `groups.yml` -> `CEEnchantGroupConfig`
- `artifact-groups.yml` -> `CEArtifactGroupConfig`
- `sigil-groups.yml` -> `CESigilGroupConfig`
- `outfit-groups.yml` -> `CEOutfitGroupConfig`
- `items.yml` -> `CEItemConfig`
- `enchantment/*.yml` -> `CEEnchantConfig`
- `vanilla-item.yml` -> `VanillaItemConfig`
- `weapon/*.yml` -> `CEWeaponConfig`
- `outfit/*.yml` -> `CEOutfitConfig`
- `skin/*.yml` -> `CESkinConfig`
- `extra-slot-settings (nested in config.yml)` -> `ExtraSlotSettingsData`
  Keys: maxCount, list, slots

## Key Classes

### CEEnchantConfig (489 lines)
**Type:** class
**Purpose:** Enchantment definitions
**Key Methods:**
- `loadConfig()`: 
- `loadCEEnchant(String key, AdvancedConfigurationSection config)` -> `CEEnchant`: 
- `loadDescriptionMap(AdvancedConfigurationSection config)` -> `HashMap<Integer, List<String>>`: 
- `loadCELevels(AdvancedConfigurationSection config)` -> `CELevelMap`: 
- `loadCELevel(AdvancedConfigurationSection defaultConfig, AdvancedConfigurationSection levelConfig)` -> `CELevel`: 
- `loadFunctionMap(AdvancedConfigurationSection defaultConfig, AdvancedConfigurationSection levelConfig)` -> `LinkedHashMap<String, CEFunction>`: 
- `loadCEFunction(String key, AdvancedConfigurationSection config)` -> `CEFunction`: 
- `loadTargetFilter(AdvancedConfigurationSection config)` -> `TargetFilter`: 

### CEItemConfig (909 lines)
**Type:** class
**Purpose:** Item definitions
**Key Methods:**
- `loadConfig()`: 
- `loadWeaponSettingsMap()`: 
- `loadWeaponSettingsSection(AdvancedConfigurationSection config)` -> `Map<String, WeaponSettings>`: 
- `loadWeaponSettings(AdvancedConfigurationSection config, AdvancedConfigurationSection alternativeConfig)` -> `WeaponSettings`: 
- `loadGemPointMap(AdvancedConfigurationSection config, AdvancedConfigurationSection alternativeConfig)` -> `Map<MaterialList, Integer>`: 
- `loadCEBookStorage()`: 
- `loadCEProtectDeadStorage()`: 
- `loadCERemoveProtectDeadStorage()`: 

### AbstractConfig (35 lines)
**Type:** abstract
**Purpose:** Base config class
**Fields:** `AdvancedFileConfiguration config`
**Key Methods:**
- `loadConfig(File file)`: 
- `loadConfig(AdvancedFileConfiguration config)`: 
- `loadConfig()` -> `abstract void`: 

### MainConfig (180 lines)
**Type:** class
**Purpose:** Main plugin config (config.yml)
**Fields:** `List<String> ceItemMaterialWhitelist`, `long moveEventPeriod`, `long sneakEventPeriod`, `int maxExtraSlotUseCount`, `int combatTime`, `double combatStaffMinRequiredAttackStrengthScale` (+19 more)
**Key Methods:**
- `loadConfig(String s, ConfigurationSection config)`: 
- `isEnchantDisableLocation(Location location)` -> `boolean`: 
- `isEnchantDisableLocation(World world)` -> `boolean`: 
- `isEnchantDisableLocation(String world)` -> `boolean`: 
- `getExtraSlotSettings(CEItem ceItem)` -> `ExtraSlotSettingsData`: 
- `getExtraSlotSettingMap()` -> `Map<String, ExtraSlotSettingsData>`: 
**Annotations:** @Configuration, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter, @Getter
**Notes:** YAML-bound config

### ConfigModule (98 lines)
**Type:** class
**Purpose:** PluginModule — config loading
**Key Methods:**
- `onEnable()`: 
- `onReload()`: 

### BookUpgradeConfig (144 lines)
**Type:** class
**Purpose:** Book upgrade config
**Key Methods:**
- `loadConfig()`: 
- `loadXpGroup(AdvancedConfigurationSection config)` -> `Map<String, XpGroup>`: 
- `loadRequiredXpGroup(AdvancedConfigurationSection config)` -> `Map<String, RequiredXpGroup>`: 
- `loadBookUpgradeLevelData(AdvancedConfigurationSection config)` -> `Map<String, BookUpgradeLevelData>`: 

### ArtifactUpgradeConfig (62 lines)
**Type:** class
**Purpose:** Artifact upgrade config
**Key Methods:**
- `loadConfig()`: 
- `loadArtifactUpgradeLevelData(AdvancedConfigurationSection config)` -> `Map<String, ArtifactUpgradeLevelData>`: 

### TinkererConfig (65 lines)
**Type:** class
**Purpose:** Tinkerer config
**Key Methods:**
- `loadConfig()`: 
- `getTinkererRewardMap(AdvancedConfigurationSection config)` -> `ConcurrentHashMap<String, TinkererReward>`: 
- `getTinkererBookGroupMap(AdvancedConfigurationSection config)` -> `ConcurrentHashMap<String, TinkererReward>`: 

## Other Classes (10)

- **CEArtifactGroupConfig** (60L): Artifact group config
- **CESigilGroupConfig** (60L): Sigil group config
- **CEOutfitGroupConfig** (60L): Outfit group config
- **CEOutfitConfig** (129L): Outfit config
- **CEWeaponConfig** (54L): Weapon config
- **BookCraftConfig** (48L): Book crafting config
- **CEEnchantGroupConfig** (39L): Enchant group config
- **VanillaItemConfig** (45L): Vanilla item config
- **CESkinConfig** (36L): Skin config
- **ExtraSlotSettingsData** (27L): Extra slot settings (@Configuration)
