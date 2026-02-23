# Player Module Summary

**Package:** `com.bafmc.customenchantment.player` | **Classes:** 41 | **Init Phase:** 3
**Purpose:** 14 player expansions, 6 mining strategies, PlayerStorage
**Depends On:** EnchantModule | **Depended By:** TaskModule, AttributeModule

## Execution Flow
- **Event Processing**: Bukkit Event -> ListenerModule -> CECaller -> EnchantModule (conditions + effects) -> PlayerModule (CEPlayer) -> TaskModule (scheduled updates)
- **Item Creation**: ConfigModule (YAML) -> CEEnchantMap/CEItemConfig -> ItemModule (CEItemFactory) -> CEItem/CEWeapon instances -> MenuModule (display/trade) -> Player inventory
- **Player Data**: PlayerListener (join) -> PlayerModule (create CEPlayer) -> PlayerExpansions (14 systems) -> TaskModule (periodic updates) -> DatabaseModule (persist via SaveTask)

## Key Classes

### CEPlayer (178 lines)
**Type:** class
**Purpose:** Player — Core
**Fields:** `static CEPlayerMap cePlayerMap`, `Player player`, `boolean register`, `boolean adminMode`, `boolean debugMode`, `int deathTime` (+4 more)
**Key Methods:**
- `onJoin()`: 
- `onQuit()`: 
- `register()`: 
- `unregister()`: 
- `addExpansion(CEPlayerExpansion expansion)`: 
- `removeExpansion(CEPlayerExpansion> clazz)`: 
- `getCePlayerMap()` -> `CEPlayerMap`: 
- `isOnline()` -> `boolean`: 
**Annotations:** @Getter, @Getter, @Setter, @Getter, @Getter, @Setter, @Setter, @Getter, @Setter, @Getter, @Setter, @Getter, @Setter, @Getter, @Setter, @Getter

### PlayerStorage (37 lines)
**Type:** class
**Purpose:** Player — Core
**Fields:** `AdvancedFileConfiguration config`
**Key Methods:**
- `onJoin()`: 
- `onQuit()`: 
- `setup()`: 
- `getConfig()` -> `AdvancedFileConfiguration`: 
- `getPlayerDataFile()` -> `File`: 

### CEPlayerExpansionRegister (31 lines)
**Type:** class
**Purpose:** Player — Core
**Key Methods:**
- `register(CEPlayerExpansion> clazz)`: 
- `unregister(CEPlayerExpansion> clazz)`: 
- `setup(CEPlayer cePlayer)`: 

### CancelManager (64 lines)
**Type:** class
**Purpose:** Player — Core
**Fields:** `CEPlayerExpansion cePlayerExpansion`, `ConcurrentHashMap<String, CancelData> list`, `boolean cancel`, `long endTime`
**Key Methods:**
- `setCancel(String unique, boolean cancel, long duration)`: 
- `isCancel()` -> `boolean`: 
**Annotations:** @Getter, @AllArgsConstructor
**Notes:** Thread-safe collections

### AttributeMapRegister (18 lines)
**Type:** class
**Purpose:** Player — Attributes
**Fields:** `static AttributeMapRegister instance`
**Key Methods:**
- `instance()` -> `AttributeMapRegister`: 
- `init()`: 

### PlayerSpecialMiningRegister (50 lines)
**Type:** class
**Purpose:** Player — Mining
**Key Methods:**
- `compare(AbstractSpecialMine o1, AbstractSpecialMine o2)` -> `int`: 
- `register(AbstractSpecialMine> clazz)`: 
- `unregister(AbstractSpecialMine> clazz)`: 
- `setup(PlayerSpecialMining playerSpecialMine)`: 

### ICEPlayerEvent (7 lines)
**Type:** interface
**Purpose:** Player — Core
**Key Methods:**
- `onJoin()`: 
- `onQuit()`: 

### AbstractAttributeMap (10 lines)
**Type:** abstract
**Purpose:** Player — Attributes
**Key Methods:**
- `loadAttributeMap(AttributeMapData data)` -> `abstract Multimap<CustomAttributeType, NMSAttribute>`: 

### AbstractSpecialMine (28 lines)
**Type:** abstract
**Purpose:** Player — Mining
**Fields:** `PlayerSpecialMining playerSpecialMining`
**Key Methods:**
- `doSpecialMine(SpecialMiningData data, boolean fake)` -> `abstract void`: 
- `getPriority()` -> `int`: 
- `isWork(boolean fake)` -> `abstract Boolean`: 
- `getDrops(SpecialMiningData specialMiningData, List<ItemStack> drops, boolean fake)` -> `abstract List<ItemStack>`: 
- `getPlayerSpecialMining()` -> `PlayerSpecialMining`: 

### PlayerCEManager (41 lines)
**Type:** class
**Purpose:** Player — Player Expansions
**Fields:** `ConcurrentHashMap<EquipSlot, CancelManager> map`
**Key Methods:**
- `onJoin()`: 
- `onQuit()`: 
- `setCancelSlot(EquipSlot slot, String unique, boolean cancel, long duration)`: 
- `isCancelSlot(EquipSlot slot)` -> `boolean`: 
**Notes:** Thread-safe collections

### PlayerTemporaryStorage (154 lines)
**Type:** class
**Purpose:** Player — Core
**Fields:** `ConcurrentHashMap<String, Object> map`
**Key Methods:**
- `onJoin()`: 
- `onQuit()`: 
- `unset(String key)`: 
- `removeStartsWith(String key)`: 
- `set(String key, Object value)`: 
- `getKeys()` -> `List<String>`: 
- `get(String key)` -> `Object`: 
- `get(String key, Object value)` -> `Object`: 
**Notes:** Thread-safe collections

### PlayerEquipment (256 lines)
**Type:** class
**Purpose:** Player — Player Expansions
**Fields:** `ConcurrentHashMap<EquipSlot, CEWeaponAbstract> slotMap`, `ConcurrentHashMap<EquipSlot, Boolean> disableSlotMap`, `Map<Integer, CEItem> ceItemCacheMap`, `CEWeaponAbstract wings`, `ItemStack offhandItemStack`
**Key Methods:**
- `onJoin()`: 
- `onQuit()`: 
- `saveSlot()`: 
- `updateSlot()`: 
- `sortExtraSlot()`: 
- `removeCEItemCache(int slotIndex)`: 
- `hasWings()` -> `boolean`: 
- `hasOffhandItemStack()` -> `boolean`: 
**Annotations:** @Getter, @Setter, @Getter, @Setter
**Notes:** Thread-safe collections

### Player Expansions (1 classes)
**Pattern:** Each extends `CEPlayerExpansion`, implements `getIdentify()` + `setup()` + action method
**Classes:** CEPlayerExpansion

### Special Mining (6 classes)
**Pattern:** Each extends `AbstractSpecialMine`, implements `getIdentify()` + `setup()` + action method
**Classes:** AutoSellSpecialMine, BlockDropBonusSpecialMine, ExplosionSpecialMine, FurnaceSpecialMine, TelepathySpecialMine, VeinSpecialMine

## Other Classes (22)

- **PlayerGem** (242L): Player — Player Expansions
- **PlayerPotion** (194L): Player — Player Expansions
- **PlayerSpecialMining** (210L): Player — Player Expansions
- **PlayerVanillaAttribute** (220L): Player — Player Expansions
- **PlayerSet** (126L): Player — Core
- **EquipSlotAttributeMap** (53L) extends AbstractAttributeMap: Player — Attributes
- **PlayerCECooldown** (126L): Player — Player Expansions
- **PlayerCustomAttribute** (104L): Player — Player Expansions
- **PlayerExtraSlot** (113L): Player — Player Expansions
- **PlayerGuard** (123L): Player — Other
- **AttributeMapData** (18L): Player — Attributes
- **DefaultAttributeMap** (31L) extends AbstractAttributeMap: Player — Attributes
- **Bonus** (28L): Player — Bonuses
- **BlockBonus** (25L): Player — Bonuses
- **EntityTypeBonus** (26L): Player — Bonuses
- **SpecialMiningData** (38L): Player — Mining
- **PlayerAbility** (43L): Player — Player Expansions
- **PlayerBlockBonus** (31L): Player — Player Expansions
- **PlayerMobBonus** (37L): Player — Player Expansions
- **PlayerNameTag** (25L): Player — Player Expansions
- **PlayerPotionData** (36L): Player — Player Expansions
- **TemporaryKey** (47L): Player — Other
