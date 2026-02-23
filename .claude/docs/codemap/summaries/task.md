# Task Module Summary

**Package:** `com.bafmc.customenchantment.task` | **Classes:** 24 | **Init Phase:** 3
**Purpose:** 16 sync + 6 async scheduled tasks
**Depends On:** PlayerModule

## Execution Flow
- **Event Processing**: Bukkit Event -> ListenerModule -> CECaller -> EnchantModule (conditions + effects) -> PlayerModule (CEPlayer) -> TaskModule (scheduled updates)
- **Player Data**: PlayerListener (join) -> PlayerModule (create CEPlayer) -> PlayerExpansions (14 systems) -> TaskModule (periodic updates) -> DatabaseModule (persist via SaveTask)

## Key Classes

### EffectExecuteTask (151 lines)
**Type:** class
**Purpose:** Effect execution (sync+async, 1 tick)
**Fields:** `int maxProcessPerTick`, `ConcurrentHashMap<String, EffectData> effectSchedulerMap`, `List<EffectData> list`, `boolean async`
**Key Methods:**
- `run()`: 
- `addEffectDataList(List<EffectData> list)`: 
- `removeEffectData(CEPlayer caller, String name)`: 
- `removeEffectData(String prefix, String name)`: 
- `removeEffectData(String name)`: 
- `isLastEffectData(EffectData effectData)` -> `boolean`: 
- `getEffectData(String name)` -> `EffectData`: 
- `getEffectSchedulerName(EffectData effectData)` -> `String`: 
**Annotations:** @Getter
**Notes:** Thread-safe collections

### CEExtraSlotTask (176 lines)
**Type:** class
**Purpose:** Extra slot management (4 ticks)
**Fields:** `CustomEnchantment plugin`, `Set<String> inDisableExtraSlotSet`
**Key Methods:**
- `run(Player player)`: 
- `updateExtraSlot(Player player)` -> `boolean`: 
- `handleExtraSlotActivation(CEPlayer cePlayer, Map<EquipSlot, CEWeaponAbstract> map)`: 
- `handleExtraSlotDeactivation(CEPlayer cePlayer)`: 
- `getPlayerPerTick()` -> `int`: 

### OutfitItemAsyncTask (205 lines)
**Type:** class
**Purpose:** Async outfit prep (1 tick)
**Fields:** `Map<String, PlayerItemTracker> playerItemTrackers`, `Map<Integer, PlayerItemHistory> itemHistories`, `CEWeaponAbstract wingsWeapon`, `boolean forceUpdate`, `boolean noNeedUpdateWings`, `ItemStack oldItemStack` (+2 more)
**Key Methods:**
- `run()`: 
- `run(Player player)`: 
- `getPlayerItemTracker(String playerName)` -> `PlayerItemTracker`: 
- `getItemHistory(int slot)` -> `PlayerItemHistory`: 
- `setItemHistory(int slot, ItemStack oldItemStack, CEItem newCEItem)`: 
- `isUpdated()` -> `boolean`: 
**Annotations:** @Getter, @Getter, @Setter, @Getter

### PowerAsyncTask (185 lines)
**Type:** class
**Purpose:** Async power calc (20 ticks)
**Fields:** `CustomEnchantment plugin`, `Workbook workbook`, `FormulaEvaluator evaluator`, `Sheet sheet`, `boolean reloading`, `static LinkedHashMap<Integer, AttributeSetter> ATTRIBUTE_SETTERS` (+1 more)
**Key Methods:**
- `load()`: 
- `run()`: 
- `run(Player player)`: 
- `close()`: 
- `cancel()`: 
- `getPowerCalculatorFile()` -> `File`: 
**Annotations:** @Setter

### TaskModule (126 lines)
**Type:** class
**Purpose:** PluginModule — task scheduling
**Fields:** `EffectExecuteTask asyncEffectExecuteTask`, `EffectExecuteTask effectExecuteTask`, `CECallerTask ceCallerTask`, `CEExtraSlotTask extraSlotTask`, `RecalculateAttributeTask attributeTask`, `RegenerationTask regenerationTask` (+14 more)
**Key Methods:**
- `onEnable()`: 
- `onDisable()`: 
**Annotations:** @Getter

### RegenerationTask (86 lines)
**Type:** class
**Purpose:** Regen effects (4 ticks)
**Fields:** `CustomEnchantment plugin`, `Map<String, Long> lastRegeneration`
**Key Methods:**
- `run()`: 
- `run(Player player)`: 

### UpdateAttributeTask (52 lines)
**Type:** class
**Purpose:** Attribute updates (4 ticks)
**Fields:** `CustomEnchantment plugin`
**Key Methods:**
- `run()`: 
- `run(Player player)`: 

### SlowResistanceTask (112 lines)
**Type:** class
**Purpose:** Slow/resist effects (4 ticks)
**Fields:** `CustomEnchantment plugin`, `static Map<String, Double> previousSpeedRatio`
**Key Methods:**
- `run()`: 
- `update(Player player)`: 

## Other Classes (16)

- **SpecialMiningTask** (86L): Mining enchants (1 tick)
- **BlockTask** (78L): Block interaction effects (1 tick)
- **OutfitItemTask** (141L): Outfit processing (1 tick)
- **OutfitTopInventoryTask** (86L): Equipment display (1 tick)
- **SigilItemTask** (80L): Sigil display async (1 tick)
- **OutfitTopInventoryAsyncTask** (119L): Async equipment prep (1 tick)
- **AutoUpdateItemTask** (65L): Auto-update items (disabled)
- **CECallerTask** (26L): Enchant caller trigger (1 tick)
- **CEPlayerTask** (29L): Player potion updates (20 ticks)
- **ArrowTask** (26L): Arrow/projectile effects (20 ticks)
- **SaveTask** (19L): Data persistence (15 min)
- **UnbreakableArmorTask** (48L): Armor durability (config)
- **ExpTask** (43L): Experience management (200 ticks)
- **UpdateJumpTask** (48L): Jump ability (4 ticks)
- **RecalculateAttributeTask** (26L): Async attribute calc (1 tick)
- **GuardTask** (22L): Guard management (20 ticks)
