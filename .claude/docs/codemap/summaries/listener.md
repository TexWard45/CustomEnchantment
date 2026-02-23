# Listener Module Summary

**Package:** `com.bafmc.customenchantment.listener` | **Classes:** 15 | **Init Phase:** 3
**Purpose:** 9 core + 5 conditional event listeners (50+ events)
**Depends On:** EnchantModule

## Execution Flow
- **Event Processing**: Bukkit Event -> ListenerModule -> CECaller -> EnchantModule (conditions + effects) -> PlayerModule (CEPlayer) -> TaskModule (scheduled updates)
- **Player Data**: PlayerListener (join) -> PlayerModule (create CEPlayer) -> PlayerExpansions (14 systems) -> TaskModule (periodic updates) -> DatabaseModule (persist via SaveTask)

## Event Listeners
- **PlayerListener**: PlayerJoinEvent, PlayerQuitEvent, PlayerChangedWorldEvent, PlayerDeathEvent (+7 more)
- **InventoryListener**: PrepareItemCraftEvent, PrepareGrindstoneEvent, PrepareSmithingEvent, InventoryOpenEvent (+3 more)
- **EntityListener**: SheepDyeWoolEvent, EntityResurrectEvent, EntityShootBowEvent, ProjectileLaunchEvent (+3 more)
  Priority: LOWEST for EntityDamage, HIGHEST for EntityDamageByEntity
- **BlockListener**: BlockFromToEvent, BlockBreakEvent, BlockDropItemEvent, BlockExplodeEvent (+1 more)
- **CEProtectDeadListener**: PlayerDeathEvent, PlayerRespawnEvent
  Priority: MONITOR priority
- **GuardListener**: CreatureSpawnEvent, EntityTargetLivingEntityEvent, EntityDeathEvent, EntityTameEvent (+1 more)
- **BannerListener**: InventoryClickEvent
- **OutfitListener**: InventoryClickEvent, PlayerDropItemEvent, BlockDropItemEvent
- **StaffMechanicListener**: PlayerInteractEvent, EntityDamageByEntityEvent
- **MobDeathListener**: EntityDeathEvent
- **MobStackDeathListener**: StackDeathEvent
- **CMenuListener**: CustomMenuOpenEvent, CustomMenuCloseEvent, CustomMenuClickEvent, InventoryClickEvent
- **McMMOListener**: McMMOItemSpawnEvent
- **CustomFarmListener**: OreBreakEvent

## Key Classes

### PlayerListener (602 lines)
**Type:** class
**Purpose:** Player events (11 handlers)
**Fields:** `CustomEnchantment plugin`
**Key Methods:**
- `onStatsChange(CEPlayerStatsModifyEvent e)`: 
- `onJoin(PlayerJoinEvent e)`: 
- `onQuit(PlayerQuitEvent e)`: 
- `onChancedWorld(PlayerChangedWorldEvent e)`: 
- `onDeath(PlayerDeathEvent e)`: 
- `onItemEquip(ItemEquipEvent e)`: 
- `onMove(PlayerMoveEvent e)`: 
- `updateMoveDirection(CEPlayer cePlayer, Location from, Location to)`: 

### InventoryListener (454 lines)
**Type:** class
**Purpose:** Inventory events (7 handlers)
**Fields:** `CustomEnchantment plugin`
**Key Methods:**
- `onPrepareCrafting(PrepareItemCraftEvent e)`: 
- `onPrepareGrindstone(PrepareGrindstoneEvent e)`: 
- `onPrepareSmithing(PrepareSmithingEvent e)`: 
- `onInventoryOpen(InventoryOpenEvent e)`: 
- `onInventoryClose(InventoryCloseEvent e)`: 
- `onAnvilClick(InventoryClickEvent e)`: 
- `onAnvilPrepare(PrepareAnvilEvent e)`: 
- `run()`: 

### EntityListener (916 lines)
**Type:** class
**Purpose:** Entity events (7 handlers)
**Fields:** `static ConcurrentHashMap<Entity, ArrowData> arrowMap`, `CustomEnchantment plugin`, `GuardManager guardManager`, `boolean damageIndicatorPluginEnabled`, `static List<String> mainArrowShootList`, `static Map<String, Integer> combatWarnTime`
**Key Methods:**
- `removeMainArrowShootList(String uuid)`: 
- `onSheepDyeWool(SheepDyeWoolEvent e)`: 
- `onEntityResurrect(EntityResurrectEvent e)`: 
- `onProjectile(EntityShootBowEvent e)`: 
- `onProjectile(ProjectileLaunchEvent e)`: 
- `onProjectile(ProjectileHitEvent e)`: 
- `putArrow(Entity entity, CEWeaponAbstract weapon, float force)`: 
- `onAttack(EntityDamageEvent e)`: 
**Annotations:** @AllArgsConstructor, @Getter
**Notes:** Thread-safe collections

### StaffMechanicListener (306 lines)
**Type:** class
**Purpose:** Staff weapon events
**Fields:** `static double HAND_OFFSET`, `static double HITBOX_EXPAND`, `static double PARTICLE_VIEW_DISTANCE_SQ`, `static boolean magicShot`, `CustomEnchantment plugin`, `ParticleSupport particleSupport`
**Key Methods:**
- `onLeftClick(PlayerInteractEvent event)`: 
- `onMelee(EntityDamageByEntityEvent event)`: 
- `run()`: 

### BlockListener (173 lines)
**Type:** class
**Purpose:** Block events (5+ handlers)
**Fields:** `CustomEnchantment plugin`, `BlockTask blockTask`
**Key Methods:**
- `onBlockFromTo(BlockFromToEvent e)`: 
- `onBlockBreak(BlockBreakEvent e)`: 
- `onBlockBreak(BlockDropItemEvent e)`: 
- `onSugarCaneBreak(BlockBreakEvent e)`: 
- `onBlockBreakDropItemMornitor(BlockDropItemEvent e)`: 
- `onBlockExplode(BlockExplodeEvent e)`: 
- `onBlockExplode(EntityExplodeEvent e)`: 

### CEProtectDeadListener (112 lines)
**Type:** class
**Purpose:** Death protection events
**Fields:** `CustomEnchantment plugin`
**Key Methods:**
- `onDeath(PlayerDeathEvent e)`: 
- `onRespawn(PlayerRespawnEvent e)`: 

### GuardListener (84 lines)
**Type:** class
**Purpose:** Guard system events
**Fields:** `CustomEnchantment plugin`, `GuardManager guardManager`
**Key Methods:**
- `onEntityTarget(CreatureSpawnEvent e)`: 
- `onEntityTarget(EntityTargetLivingEntityEvent e)`: 
- `onEntityTarget(EntityDeathEvent e)`: 
- `onTame(EntityTameEvent e)`: 
- `onTeleport(PlayerTeleportEvent e)`: 

### BannerListener (102 lines)
**Type:** class
**Purpose:** Banner inventory events
**Fields:** `CustomEnchantment main`, `List<ClickType> clickTypes`
**Key Methods:**
- `onInventoryClick(InventoryClickEvent e)`: 

### OutfitListener (69 lines)
**Type:** class
**Purpose:** Outfit system events
**Fields:** `static int OFFHAND_SLOT`, `CustomEnchantment plugin`
**Key Methods:**
- `onInventoryClick(InventoryClickEvent e)`: 
- `onPlayerDropItemEvent(PlayerDropItemEvent e)`: 
- `onPlayerDropItemEvent(BlockDropItemEvent e)`: 

### MobStackDeathListener (67 lines)
**Type:** class
**Purpose:** Mob death (StackMob)
**Key Methods:**
- `onMobDeath(StackDeathEvent e)`: 

## Other Classes (5)

- **CMenuListener** (86L): CustomMenu events
- **ListenerModule** (41L): PluginModule — listener registration
- **MobDeathListener** (41L): Mob death (no StackMob)
- **McMMOListener** (37L): mcMMO integration
- **CustomFarmListener** (39L): CustomFarm integration
