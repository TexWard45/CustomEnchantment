# Root (Core) Module Summary

**Package:** `com.bafmc.customenchantment` | **Classes:** 37
**Purpose:** Main plugin class, storage maps, logging, messages, utilities
**Depended By:** all modules

## Event Listeners
- **MobDamageTrackerListener**: 

## Key Classes

### CustomEnchantment (205 lines)
**Type:** class extends BafPlugin, Listener
**Purpose:** Main plugin entry point (singleton)
**Fields:** `static CustomEnchantment instance`, `CEEnchantMap ceEnchantMap`, `CEGroupMap ceGroupMap`, `CEItemStorageMap ceItemStorageMap`, `CEArtifactGroupMap ceArtifactGroupMap`, `CESigilGroupMap ceSigilGroupMap` (+20 more)
**Key Methods:**
- `onEnable()`: 
- `registerModules()`: 
- `addEffectTask(EffectTaskSeparate effectTask)`: 
- `removeEffectTask(String playerName, String name)`: 
- `instance()` -> `CustomEnchantment`: 
- `getGeneralDataFolder()` -> `File`: 
- `getPlayerDataFolder()` -> `File`: 
- `getWeaponFolder()` -> `File`: 
**Annotations:** @Getter, @Setter

### CEEnchantMap (13 lines)
**Type:** class extends StorageMap
**Purpose:** Enchantment storage map
**Key Methods:**
- `getKeys()` -> `List<String>`: 

### CEGroupMap (7 lines)
**Type:** class extends StorageMap
**Purpose:** Group storage map

### CEItemStorageMap (8 lines)
**Type:** class extends StorageMap
**Purpose:** Item storage map

### MessageKey (9 lines)
**Type:** interface
**Purpose:** Message key interface

### CEConstants (180 lines)
**Type:** class
**Purpose:** Plugin constants

### ILine (7 lines)
**Type:** interface
**Purpose:** Display line interface
**Key Methods:**
- `toLine()` -> `String`: 
- `fromLine(String line)`: 

### ITrade (7 lines)
**Type:** interface
**Purpose:** Trade interface
**Key Methods:**
- `importFrom(T source)`: 
- `exportTo()` -> `T`: 

### CEMessageKey (81 lines)
**Type:** enum
**Purpose:** Message key enum (implements MessageKey)
**Fields:** `String key`
**Key Methods:**
- `ceItem(String type, String suffix)` -> `MessageKey`: 
- `getKey()` -> `String`: 

### DamageUtils (78 lines)
**Type:** class
**Purpose:** Damage calculation utilities
**Key Methods:**
- `getDamageAfterAbsorb(LivingEntity livingEntityBukkit, float damageAmount, DamageSource damageSourceBukkit, int armorPenetration)` -> `float`: 
- `getArmorDamageModifier(LivingEntity livingEntityBukkit, float damageAmount, DamageSource damageSourceBukkit, int armorPenetration)` -> `Function<Double, Double>`: 
- `getResistanceDamageModifier(LivingEntity livingEntityBukkit, DamageSource damageSourceBukkit, float resistanceBonus)` -> `Function<Double, Double>`: 

## Other Classes (27)

- **CustomEnchantmentMessage** (69L): Message config holder
- **CEPlayerMap** (68L): Player map storage
- **MobDamageTrackerListener** (137L) extends Listener: Mob damage tracking
- **CEAPI** (138L): Custom enchantment API utilities
- **CompareOperation** (63L): Comparison operators
- **EntityTypeList** (77L): ArrayList<EntityType> wrapper
- **LocationFormat** (127L): Location formatting
- **MaterialData** (88L): Material data holder
- **MaterialList** (70L): ArrayList<MaterialData> wrapper
- **Parameter** (97L): Parameter holder
- **ParticleAPI** (87L): Particle effect API
- **ParticleSupport** (54L): Particle support utilities
- **VectorFormat** (103L): Vector formatting
- **CEPlayerStatsModifyEvent** (85L): Custom event — player stats changed (Cancellable)
- **EntityInsentientNMS** (51L): NMS insentient entity
- **AttributeUtils** (50L): Attribute math utilities
- **McMMOUtils** (41L): mcMMO integration utilities
- **StorageUtils** (41L): Storage/data utilities
- **CustomEnchantmentDebug** (27L): Debug utilities
- **CustomEnchantmentLog** (17L): Logging utilities
- **CombatLogXAPI** (16L): CombatLogX integration API
- **EquipSlotAPI** (19L): Equipment slot utilities
- **ItemPacketAPI** (36L): Item packet utilities
- **Pair** (19L): Generic pair
- **CENotSuitableTypeException** (12L): Exception for wrong CE item type
- **CECraftItemStackNMS** (38L): NMS ItemStack wrapper
- **EntityLivingNMS** (23L): NMS living entity
