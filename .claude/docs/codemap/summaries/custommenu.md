# CustomMenu Module Summary

**Package:** `com.bafmc.customenchantment.custommenu` | **Classes:** 4 | **Init Phase:** 3
**Purpose:** External CustomMenu plugin integration
**Depends On:** ConfigModule

## Key Classes

### CustomEnchantmentTradeItemCompare (223 lines)
**Type:** class
**Purpose:** Trade item comparison
**Key Methods:**
- `getIdentify()` -> `String`: 
- `setupItemStack(Player player, ItemStack itemStack, AdvancedConfigurationSection dataConfig, TradeItemRequiredHistory history)` -> `ItemStack`: 
- `getWeaponMigration(String type, String name, TradeItemRequiredHistory history)` -> `ItemStack`: 
- `getCurrentWeapon(TradeItemRequiredHistory history)` -> `CEWeaponAbstract`: 
- `isSimilarItem(Player player, ItemStack itemStack1, ItemStack itemStack2)` -> `boolean`: 

### CEBookCatalog (123 lines)
**Type:** class
**Purpose:** Catalog extension for books
**Fields:** `List<CEBookData> list`
**Key Methods:**
- `setup(AdvancedConfigurationSection config)`: 
- `getPlaceholder(Player player, int index)` -> `HashMap<String, String>`: 
- `getSize()` -> `int`: 

### CustomEnchantmentItemDisplaySetup (52 lines)
**Type:** class
**Purpose:** Item display setup
**Key Methods:**
- `getKey()` -> `String`: 
- `getItemStack(Player player, ItemStack itemStack, AdvancedConfigurationSection dataConfig)` -> `ItemStack`: 

### CustomMenuModule (36 lines)
**Type:** class
**Purpose:** PluginModule — CustomMenu integration
**Key Methods:**
- `onEnable()`: 
- `registerCMenu()`: 
- `onDisable()`: 
