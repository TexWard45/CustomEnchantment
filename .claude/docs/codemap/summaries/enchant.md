# Enchant Module Summary

**Package:** `com.bafmc.customenchantment.enchant` | **Classes:** 141 | **Init Phase:** 1
**Purpose:** 28 conditions + 77 effects hook registration
**Depended By:** ListenerModule, ConfigModule, PlayerModule

## Execution Flow
- **Event Processing**: Bukkit Event -> ListenerModule -> CECaller -> EnchantModule (conditions + effects) -> PlayerModule (CEPlayer) -> TaskModule (scheduled updates)
- **Item Creation**: ConfigModule (YAML) -> CEEnchantMap/CEItemConfig -> ItemModule (CEItemFactory) -> CEItem/CEWeapon instances -> MenuModule (display/trade) -> Player inventory

## Key Classes

### CECaller (458 lines)
**Type:** class
**Purpose:** Enchantment trigger executor (singleton)
**Fields:** `CEType ceType`, `CEPlayer caller`, `CEFunctionData data`, `List<CEEnchantSimple> ceEnchantSimpleList`, `List<EquipSlot> equipSlotList`, `boolean byPassCooldown` (+13 more)
**Key Methods:**
- `instance()` -> `CECaller`: 
- `resetResult()` -> `CECaller`: 
- `call()` -> `CECaller`: 
- `callEquipSlot()` -> `CECaller`: 
- `callCE()` -> `CECaller`: 
- `callCEFunction()` -> `StepAction`: 
- `callGem()`: 
- `executeEffect(Effect effect, CEFunctionData data)`: 

### CEFunctionData (257 lines)
**Type:** class
**Purpose:** Function data (Cloneable)
**Fields:** `ConcurrentHashMap<String, Object> map`, `List<String> generatedPrefixList`, `Target target`
**Key Methods:**
- `remove(String key)` -> `CEFunctionData`: 
- `checkValue(String key, Class<?> clazz)` -> `boolean`: 
- `clone()` -> `CEFunctionData`: 
- `getOwner()` -> `Player`: 
- `getPlayer()` -> `Player`: 
- `getDeathTime()` -> `int`: 
- `getNextPrefix(String prefix, int index)` -> `String`: 
- `setPlayer(Player player)` -> `CEFunctionData`: 
**Notes:** Thread-safe collections

### CEDisplay (66 lines)
**Type:** class
**Purpose:** Enchantment display info
**Fields:** `String display`, `String bookDisplay`, `Map<String, String> customDisplayLore`, `boolean disableEnchantLore`, `List<String> description`, `List<String> detailDescription` (+1 more)
**Key Methods:**
- `getDefaultDisplay()` -> `String`: 
- `getCustomDisplayFormat()` -> `Map<String, String>`: 
- `getDisplay()` -> `String`: 
- `getBookDisplay()` -> `String`: 
- `isDisableEnchantLore()` -> `boolean`: 
- `getDescription()` -> `List<String>`: 
- `getDetailDescription()` -> `List<String>`: 
- `getAppliesDescription()` -> `List<String>`: 

### CEEnchant (87 lines)
**Type:** class
**Purpose:** Main enchantment definition
**Fields:** `String name`, `String groupName`, `int maxLevel`, `int valuable`, `int enchantPoint`, `CEDisplay ceDisplay` (+5 more)
**Key Methods:**
- `getName()` -> `String`: 
- `getGroupName()` -> `String`: 
- `getCEGroup()` -> `CEGroup`: 
- `getMaxLevel()` -> `int`: 
- `getValuable()` -> `int`: 
- `getEnchantPoint()` -> `int`: 
- `getCEDisplay()` -> `CEDisplay`: 
- `getCELevel(int level)` -> `CELevel`: 

### CEFunction (144 lines)
**Type:** class
**Purpose:** Enchantment function
**Fields:** `String name`, `CEType ceType`, `Chance chance`, `Cooldown cooldown`, `List<EquipSlot> chanceSlot`, `List<EquipSlot> cooldownSlot` (+15 more)
**Key Methods:**
- `getName()` -> `String`: 
- `getCEType()` -> `CEType`: 
- `getChance()` -> `Chance`: 
- `getCooldown()` -> `Cooldown`: 
- `getChanceSlot()` -> `List<EquipSlot>`: 
- `getCooldownSlot()` -> `List<EquipSlot>`: 
- `getActiveSlot()` -> `List<EquipSlot>`: 
- `getTargetFilter()` -> `TargetFilter`: 

### CELevel (21 lines)
**Type:** class
**Purpose:** Enchantment level
**Fields:** `LinkedHashMap<String, CEFunction> functionMap`
**Key Methods:**
- `getFunctionList()` -> `List<CEFunction>`: 
- `getFunctionMap()` -> `LinkedHashMap<String, CEFunction>`: 

### IEffectAction (17 lines)
**Type:** interface
**Purpose:** Effect action interface
**Key Methods:**
- `updateAndExecute(CEFunctionData data)`: 
- `execute(CEFunctionData data)`: 

### CECallerBuilder (140 lines)
**Type:** class
**Purpose:** Builder for CECaller
**Fields:** `Player player`, `CEType ceType`, `EquipSlot activeEquipSlot`, `CEFunctionData ceFunctionData`, `Map<EquipSlot, CEWeaponAbstract> weaponMap`, `boolean executeLater` (+2 more)
**Key Methods:**
- `build()` -> `CECallerBuilder`: 
- `build(Player player)` -> `CECallerBuilder`: 
- `call()` -> `CECallerList`: 
- `setPlayer(Player player)` -> `CECallerBuilder`: 
- `setCEType(CEType ceType)` -> `CECallerBuilder`: 
- `setActiveEquipSlot(EquipSlot activeEquipSlot)` -> `CECallerBuilder`: 
- `setCEFunctionData(CEFunctionData ceFunctionData)` -> `CECallerBuilder`: 
- `setWeaponMap(Map<EquipSlot, CEWeaponAbstract> weaponMap)` -> `CECallerBuilder`: 

### CEPlaceholder (209 lines)
**Type:** class
**Purpose:** Enchantment placeholder
**Key Methods:**
- `setPlaceholder(String string, Map<String, String> map)` -> `String`: 
- `getTemporaryStoragePlaceholder(PlayerTemporaryStorage storage)` -> `Map<String, String>`: 
- `getCEFunctionDataPlaceholder(String text, CEFunctionData data)` -> `Map<String, String>`: 
- `getCESimplePlaceholder(CEEnchantSimple ceEnchantSimple)` -> `Map<String, String>`: 
- `getEnchantRequiredXp(CEEnchantSimple ceEnchantSimple)` -> `int`: 
- `getEnchantProgress(CEEnchantSimple ceEnchantSimple)` -> `String`: 

### EnchantModule (130 lines)
**Type:** class
**Purpose:** PluginModule — enchant registration
**Key Methods:**
- `onEnable()`: 
- `setupCondition()`: 
- `setupEffect()`: 

### CEEnchantSimple (97 lines)
**Type:** class
**Purpose:** Simple enchant wrapper (ILine)
**Fields:** `String name`, `int level`, `RandomRangeInt success`, `RandomRangeInt destroy`, `int xp`
**Key Methods:**
- `toLine()` -> `String`: 
- `fromLine(String line)`: 
- `toString()` -> `String`: 
- `getName()` -> `String`: 
- `getLevel()` -> `int`: 
- `getCEEnchant()` -> `CEEnchant`: 
- `getSuccess()` -> `RandomRangeInt`: 
- `getDestroy()` -> `RandomRangeInt`: 
**Annotations:** @Setter

### CEGroup (60 lines)
**Type:** class
**Purpose:** Enchantment group
**Fields:** `String name`, `String display`, `String enchantDisplay`, `String bookDisplay`, `String prefix`, `boolean disableEnchantLore` (+6 more)
**Key Methods:**
- `getEnchantNameList()` -> `List<String>`: 
- `getEnchantList()` -> `List<CEEnchant>`: 
**Annotations:** @Getter

### CEType (69 lines)
**Type:** class
**Purpose:** Enchantment type
**Fields:** `static ConcurrentHashMap<String, CEType> map`, `String type`
**Key Methods:**
- `register()` -> `CEType`: 
- `equals(Object obj)` -> `boolean`: 
- `valueOf(String name)` -> `CEType`: 
- `values()` -> `CEType[]`: 
- `toString()` -> `String`: 
- `getType()` -> `String`: 
**Notes:** Thread-safe collections

### Condition (111 lines)
**Type:** class
**Purpose:** Condition definition
**Fields:** `static HashMap<String, ConditionHook> hookMap`, `List<ConditionOR> conditions`
**Key Methods:**
- `check(Player player)` -> `boolean`: 
- `check(CEFunctionData data)` -> `boolean`: 
- `register(ConditionHook conditionHook)` -> `boolean`: 
- `unregister(ConditionHook conditionHook)`: 
- `isRegister(String identify)` -> `boolean`: 
- `isRegister(ConditionHook conditionHook)` -> `boolean`: 
- `get(String identify, String[] args)` -> `ConditionHook`: 

### Cooldown (63 lines)
**Type:** class
**Purpose:** Cooldown tracker (Cloneable)
**Fields:** `long start`, `long end`, `long countdown`
**Key Methods:**
- `start()`: 
- `stop()`: 
- `clone()` -> `Cooldown`: 
- `toString()` -> `String`: 
- `isInCooldown()` -> `boolean`: 
- `setStart(Long start, Long end)`: 
- `getStart()` -> `Long`: 
- `getEnd()` -> `Long`: 

### Condition Hooks (30 classes)
**Pattern:** Each extends `ConditionHook`, implements `getIdentify()` + `setup()` + action method
**Identifiers:** `ACTIVE_EQUIP_SLOT`, `ALLOW_FLIGHT`, `CAN_ATTACK`, `DAMAGE_CAUSE`, `ENTITY_TYPE`, `EQUIP_SLOT`, `EXP`, `FACTION_RELATION`, `FAKE_SOURCE`, `FOOD` ... (+20 more)
**Classes:** ConditionActiveEquipSlot, ConditionAllowFlight, ConditionCanAttack, ConditionDamageCause, ConditionEntityType, ConditionEquipSlot, ConditionExp, ConditionFactionRelation
  ... and 22 more

### Effect Hooks (77 classes)
**Pattern:** Each extends `EffectHook`, implements `getIdentify()` + `setup()` + action method
**Identifiers:** `ACTIVE_ABILITY`, `ACTIVE_DASH`, `ACTIVE_DOUBLE_JUMP`, `ACTIVE_EQUIP_SLOT`, `ACTIVE_FLASH`, `DEACTIVE_ABILITY`, `DEACTIVE_DASH`, `DEACTIVE_DOUBLE_JUMP`, `DEACTIVE_EQUIP_SLOT`, `DEACTIVE_FLASH` ... (+67 more)
**Classes:** EffectAbsorptionHeart, EffectActiveAbility, EffectActiveDash, EffectActiveDoubleJump, EffectActiveEquipSlot, EffectActiveFlash, EffectAddAttribute, EffectAddAutoAttribute
  ... and 69 more

## Other Classes (17)

- **Effect** (67L): Effect definition
- **EffectData** (76L): Effect data holder
- **EffectSettings** (99L): Effect settings (Cloneable)
- **TargetFilter** (101L): Target filtering
- **CECallerList** (33L): ArrayList<CECaller>
- **CECallerResult** (41L): Caller result holder (singleton)
- **CELevelMap** (18L): LinkedHashMap<Integer, CELevel>
- **ConditionOR** (19L): OR-combined conditions
- **ConditionSettings** (31L): Condition settings
- **EffectTaskSeparate** (36L): Separate task effect execution
- **EffectUtil** (45L): Effect utilities
- **Function** (31L): Function definition
- **ModifyType** (5L): Modification type
- **Option** (22L): Option config
- **Priority** (8L): Priority levels
- **StepAction** (32L): Step action types
- **Target** (5L): Target types
