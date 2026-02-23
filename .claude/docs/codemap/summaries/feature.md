# Feature Module Summary

**Package:** `com.bafmc.customenchantment.feature` | **Classes:** 5 | **Init Phase:** 1
**Purpose:** Item strategy registration (CustomEnchantmentItem)

## Key Classes

### DashFeature (62 lines)
**Type:** class
**Purpose:** Dash ability logic
**Fields:** `Player player`, `String particle`, `int count`
**Key Methods:**
- `dash(Player player, double power, String particleForward, String particleBackward)`: 
- `run()`: 

### FlashFeature (112 lines)
**Type:** class
**Purpose:** Flash ability logic
**Fields:** `static List<Material> TRANSPARENT`
**Key Methods:**
- `flash(Player player, double power, String particle, boolean smart)` -> `boolean`: 
- `getLegitLocation(Location from, Location to)` -> `Location`: 

### FeatureModule (17 lines)
**Type:** class
**Purpose:** PluginModule — feature registration
**Key Methods:**
- `onEnable()`: 

### CustomEnchantmentItem (37 lines)
**Type:** class
**Purpose:** AbstractItem for CE menus
**Key Methods:**
- `isMatch(ItemStack itemStack, ArgumentLine argumentLine)` -> `boolean`: 
- `getItemStack(ArgumentLine argumentLine)` -> `ItemStack`: 
- `getType()` -> `String`: 

### DoubleJumpFeature (27 lines)
**Type:** class
**Purpose:** Double jump ability logic
**Key Methods:**
- `jump(Player player, double power, String particle)`: 
