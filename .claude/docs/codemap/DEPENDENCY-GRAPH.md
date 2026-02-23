# Dependency Graph — CustomEnchantment

## Module-Level Dependencies

### Initialization Order Constraints

```
Phase 1 (Hooks):     FeatureModule → EnchantModule → AttributeModule → FilterModule → ExecuteModule
                                                          ↓
Phase 2 (Config):    ConfigModule (requires all Phase 1 hooks registered)
                         ↓
Phase 3 (Features):  CustomMenuModule → CommandModule → ItemModule → PlayerModule
                         ↓                                               ↓
                     TaskModule → GuardModule → DatabaseModule → MenuModule
                         ↓
                     PlaceholderModule → ListenerModule (LAST — all systems ready)
```

### Direct Module Dependencies (getPlugin().getModule() calls)

| Source Module | Target Module | Reason |
|--------------|---------------|--------|
| ListenerModule | EnchantModule | CECaller triggers enchant execution |
| MenuModule | ConfigModule | Reads menu configuration data |
| PlayerModule | EnchantModule | Manages player enchant data |
| TaskModule | PlayerModule | Tick-based player stat updates |
| ConfigModule | EnchantModule | Populates enchant registry maps |
| AttributeModule | PlayerModule | Uses AttributeMapRegister for player attributes |
| CustomMenuModule | ConfigModule | Reads menu config for CustomMenu integration |

### Implicit Dependencies (Shared Data)

| Producer | Consumer | Shared Data |
|----------|----------|-------------|
| ConfigModule | ItemModule | CEEnchantMap, CEGroupMap, CEItemConfig |
| ConfigModule | MenuModule | BookCraftConfig, TinkererConfig, ArtifactUpgradeConfig |
| PlayerModule | TaskModule | CEPlayer instances, PlayerStorage |
| EnchantModule | ListenerModule | ConditionHook/EffectHook registrations |
| ItemModule | MenuModule | CEItemFactory registrations, CEItemRegister |
| DatabaseModule | PlayerModule | Database connection for player data persistence |

## Key Class Dependencies

### Core Data Flow

```
Event (Bukkit) → ListenerModule → CECaller → EnchantModule (conditions + effects)
                                      ↓
                              PlayerModule (CEPlayer)
                                      ↓
                              TaskModule (scheduled updates)
```

### Item Creation Flow

```
ConfigModule (loads YAML) → CEEnchantMap/CEItemConfig
                                  ↓
ItemModule (CEItemFactory) → CEItem/CEWeapon instances
                                  ↓
MenuModule (display/trade) → Player inventory
```

### Player Data Flow

```
PlayerListener (join) → PlayerModule (create CEPlayer)
                              ↓
                    PlayerExpansions (14 systems: Equipment, Attributes, Potions, etc.)
                              ↓
                    TaskModule (periodic updates via PlayerPerTickTask)
                              ↓
                    DatabaseModule (persist via SaveTask every 15 min)
```

## External Plugin Dependencies

```
CustomEnchantment
├── BafFramework (REQUIRED — compile dependency)
├── CustomMenu (OPTIONAL — menu integration)
├── StackMob (OPTIONAL — mob stacking compat)
├── Vouchers (OPTIONAL — voucher integration)
├── ItemDropManager (OPTIONAL — item drop mgmt)
├── mcMMO (OPTIONAL — mcMMO integration)
├── PlaceholderAPI (OPTIONAL — placeholder support)
├── CustomShop (OPTIONAL — shop integration)
├── DamageIndicator (OPTIONAL — damage display)
├── CustomFarm (OPTIONAL — farm integration)
└── Citizens (OPTIONAL — NPC integration)
```
