# Entry Points — CustomEnchantment

## Event Listeners (15 listeners, 50+ events)

### Core Listeners (always registered)

| Listener | Events | Module |
|----------|--------|--------|
| PlayerListener | PlayerJoinEvent, PlayerQuitEvent, PlayerChangedWorldEvent, PlayerDeathEvent, ItemEquipEvent, PlayerMoveEvent, PlayerInteractEvent, PlayerItemConsumeEvent, PlayerToggleSneakEvent, PlayerToggleFlightEvent, CEPlayerStatsModifyEvent | ListenerModule |
| InventoryListener | PrepareItemCraftEvent, PrepareGrindstoneEvent, PrepareSmithingEvent, InventoryOpenEvent, InventoryCloseEvent, InventoryClickEvent, PrepareAnvilEvent | ListenerModule |
| EntityListener | SheepDyeWoolEvent, EntityResurrectEvent, EntityShootBowEvent, ProjectileLaunchEvent, ProjectileHitEvent, EntityDamageEvent(LOWEST), EntityDamageByEntityEvent(HIGHEST) | ListenerModule |
| BlockListener | BlockFromToEvent, BlockBreakEvent, BlockDropItemEvent, BlockExplodeEvent, EntityExplodeEvent | ListenerModule |
| CEProtectDeadListener | PlayerDeathEvent(MONITOR), PlayerRespawnEvent(MONITOR) | ListenerModule |
| GuardListener | CreatureSpawnEvent, EntityTargetLivingEntityEvent, EntityDeathEvent, EntityTameEvent, PlayerTeleportEvent | ListenerModule |
| BannerListener | InventoryClickEvent | ListenerModule |
| OutfitListener | InventoryClickEvent, PlayerDropItemEvent, BlockDropItemEvent | ListenerModule |
| StaffMechanicListener | PlayerInteractEvent, EntityDamageByEntityEvent | ListenerModule |

### Conditional Listeners

| Listener | Condition | Events | Module |
|----------|-----------|--------|--------|
| MobDeathListener | StackMob NOT installed | EntityDeathEvent | ListenerModule |
| MobStackDeathListener | StackMob installed | StackDeathEvent(MONITOR) | ListenerModule |
| CMenuListener | CustomMenu installed | CustomMenuOpenEvent, CustomMenuCloseEvent, CustomMenuClickEvent, InventoryClickEvent | ListenerModule |
| McMMOListener | mcMMO installed | McMMOItemSpawnEvent(MONITOR) | ListenerModule |
| CustomFarmListener | CustomFarm installed | OreBreakEvent | ListenerModule |

## Commands (9 root, 20+ subcommands)

### Root Commands

| Command | Handler | Module |
|---------|---------|--------|
| /customenchantment | CustomEnchantmentCommand | CommandModule |
| /tinkerer | TinkererCommand | CommandModule |
| /bookcraft | BookCraftCommand | CommandModule |
| /ceanvil | CEAnvilCommand | CommandModule |
| /bookupgrade | BookUpgradeCommand | CommandModule |
| /artifactupgrade | ArtifactUpgradeCommand | CommandModule |
| /equipment | EquipmentCommand | CommandModule |
| /nametag | CommandNameTag | CommandModule |
| /cefilter | CommandFilterEnchant | CommandModule |

### Subcommands (/customenchantment ...)

| Subcommand | Class | Purpose |
|-----------|-------|---------|
| addenchant | CommandAddEnchant | Add enchantment to item |
| addgem | CommandAddGem | Add gem to item |
| removegem | CommandRemoveGem | Remove gem from item |
| additem | CommandAddItem | Add CE item to inventory |
| admin | CommandAdmin | Admin operations |
| cleartime | CommandClearTime | Clear cooldowns |
| disablehelmet | CommandDisableHelmet | Disable helmet effects |
| giveitem | CommandGiveItem | Give CE item to player |
| info | CommandInfo | Item/enchant info |
| open | CommandOpen | Open custom menu |
| reload | CommandReload | Reload config |
| removeenchant | CommandRemoveEnchant | Remove enchantment |
| removeitem | CommandRemoveItem | Remove CE item |
| updateitem | CommandUpdateItem | Update item properties |
| useitem | CommandUseItem | Use/activate CE item |
| debug | CommandDebug | Debug single feature |
| fullchance | CommandFullChance | Test chance calculations |
| debugall | CommandDebugAll | Debug all enchantments |
| debugce | CommandDebugCE | Debug CE enchantments |
| test | CommandTest | Test features |

## Scheduled Tasks (16 sync + 6 async)

### Main Thread Tasks

| Task | Period | Purpose |
|------|--------|---------|
| EffectExecuteTask | 1 tick | Execute enchant effects (max 500/tick) |
| CECallerTask | 1 tick | Trigger enchantment callers |
| SpecialMiningTask | 1 tick | Mining speed enchantments |
| BlockTask | 1 tick | Block interaction effects |
| OutfitItemTask | 1 tick | Outfit item processing |
| OutfitTopInventoryTask | 1 tick | Equipment display sync |
| CEExtraSlotTask | 4 ticks | Extra inventory slots |
| RegenerationTask | 4 ticks | Regeneration effects |
| UpdateAttributeTask | 4 ticks | Recalculate attributes |
| SlowResistanceTask | 4 ticks | Slow/resistance effects |
| UpdateJumpTask | 4 ticks | Jump ability effects |
| UnbreakableArmorTask | Config | Armor durability |
| CEPlayerTask | 20 ticks | Update potion effects |
| ArrowTask | 20 ticks | Arrow/projectile effects |
| ExpTask | 200 ticks | Experience management |
| SaveTask | 18000 ticks (15 min) | Persist player data |

### Async Tasks

| Task | Period | Purpose |
|------|--------|---------|
| EffectExecuteTask (async) | 1 tick | Async effect execution |
| RecalculateAttributeTask | 1 tick | Async attribute calc |
| SigilItemTask | 1 tick | Sigil display updates (conditional) |
| OutfitItemAsyncTask | 1 tick | Async outfit data prep |
| OutfitTopInventoryAsyncTask | 1 tick | Async equipment data prep |
| PowerAsyncTask | 20 ticks | Async power/stat processing |

### Task Inheritance

```
PlayerPerTickTask (abstract — processes all online players)
├── RecalculateAttributeTask (async)
├── CEExtraSlotTask
├── RegenerationTask
├── SlowResistanceTask
├── SpecialMiningTask
├── UnbreakableArmorTask
├── OutfitItemTask
├── OutfitTopInventoryTask
└── UpdateJumpTask
```
