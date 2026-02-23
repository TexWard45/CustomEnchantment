# Attribute Module Summary

**Package:** `com.bafmc.customenchantment.attribute` | **Classes:** 4 | **Init Phase:** 1
**Purpose:** Custom attribute types and attribute map registration
**Depends On:** PlayerModule

## Key Classes

### AttributeCalculate (132 lines)
**Type:** class
**Purpose:** Attribute math utilities
**Key Methods:**
- `calculate(CEPlayer cePlayer, CustomAttributeType type, double amount, List<NMSAttribute> additionalList)` -> `double`: 
- `calculate(CustomAttributeType type, double amount, List<NMSAttribute> list)` -> `double`: 
- `calculate(double amount, List<NMSAttribute> list)` -> `double`: 
- `calculateAttributeModifier(double amount, Collection<AttributeModifier> list)` -> `double`: 
- `calculateAttributeModifier(double amount, Collection<AttributeModifier> list, boolean noNegative)` -> `double`: 

### CustomAttributeType (86 lines)
**Type:** class
**Purpose:** NMSAttributeType extension
**Fields:** `double baseValue`, `boolean percent`, `static CustomAttributeType[] values`
**Key Methods:**
- `init()`: 
- `register()` -> `NMSAttributeType`: 
**Annotations:** @Getter, @Getter

### RangeAttribute (55 lines)
**Type:** class
**Purpose:** NMSAttribute with range + Cloneable
**Fields:** `RandomRange amount`, `Chance chance`
**Key Methods:**
- `hasChance()` -> `boolean`: 
- `clone()` -> `RangeAttribute`: 
- `getAmount()` -> `double`: 
**Annotations:** @Getter

### AttributeModule (16 lines)
**Type:** class
**Purpose:** PluginModule — attribute system init
**Key Methods:**
- `onEnable()`: 
