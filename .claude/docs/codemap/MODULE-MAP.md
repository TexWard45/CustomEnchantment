# Module Map — CustomEnchantment

## Plugin: CustomEnchantment (com.bafmc.customenchantment)
Minecraft: Paper/Leaf 1.21.1 | Framework: BafFramework | Java 21

## Initialization Phases

The plugin uses a **three-phase initialization** strategy:
- **Phase 1 (1-5):** Register hooks and strategies before config loading
- **Phase 2 (6):** Load all configurations (requires hooks from Phase 1)
- **Phase 3 (7-16):** Register feature modules that depend on loaded configs

## Module Load Order

| # | Module | Package | Purpose | Key Classes | Dependencies |
|---|--------|---------|---------|-------------|--------------|
| 1 | FeatureModule | .feature | Item strategy registration | CustomEnchantmentItem | None |
| 2 | EnchantModule | .enchant | 28 conditions + 77 effects | Condition*, Effect* hooks | None |
| 3 | AttributeModule | .attribute | Custom attribute types | CustomAttributeType, AttributeMapRegister | PlayerModule (indirect) |
| 4 | FilterModule | .filter | Item/entity filtering | FilterRegister | None |
| 5 | ExecuteModule | .execute | Give/use item hooks | GiveItemExecute, UseItemExecute | None |
| 6 | ConfigModule | .config | YAML config loading | MainConfig, CEEnchantConfig + 10 config classes | Phase 1 hooks |
| 7 | CustomMenuModule | .custommenu | External menu integration | CEBookCatalog, TradeItemCompare | Optional: CustomMenu |
| 8 | CommandModule | .command | 9 root commands, 20+ subcommands | CustomEnchantmentCommand + 20 subcommand classes | None |
| 9 | ItemModule | .item | 22 item factories | CEWeaponFactory, CEArtifactFactory + 20 factories | None |
| 10 | PlayerModule | .player | 14 player expansions, 6 mining strategies | CEPlayer, PlayerStorage, PlayerEquipment | None |
| 11 | TaskModule | .task | 16 sync + 6 async tasks | EffectExecuteTask, CECallerTask, SaveTask | None |
| 12 | GuardModule | .guard | Guard mob system | GuardManager, GuardTask | None |
| 13 | DatabaseModule | .database | SQLite persistence | Database | None |
| 14 | MenuModule | .menu | 6 menu strategies, 12 anvil handlers | TinkererCustomMenu, CEAnvilCustomMenu | None |
| 15 | PlaceholderModule | .placeholder | PlaceholderAPI integration | CustomEnchantmentPlaceholder | Optional: PlaceholderAPI |
| 16 | ListenerModule | .listener | 9 core + 5 conditional listeners | PlayerListener, EntityListener, BlockListener | Registered last |

## Cross-Module Dependencies

```
ListenerModule → EnchantModule (triggers enchant execution via CECaller)
MenuModule → ConfigModule (reads menu configs)
PlayerModule → EnchantModule (player enchant data)
TaskModule → PlayerModule (tick-based player updates)
ConfigModule → EnchantModule (populates enchant registry maps)
AttributeModule → PlayerModule (uses AttributeMapRegister)
CustomMenuModule → ConfigModule (reads menu config data)
```

## Optional External Dependencies

| Module | External Plugin | Behavior When Missing |
|--------|-----------------|----------------------|
| CustomMenuModule | CustomMenu | Module skips registration |
| PlaceholderModule | PlaceholderAPI | Module skips registration |
| ListenerModule | StackMob | Uses MobDeathListener instead of MobStackDeathListener |
| ListenerModule | mcMMO | McMMOListener not registered |
| ListenerModule | CustomFarm | CustomFarmListener not registered |
| ListenerModule | CustomMenu | CMenuListener not registered |
