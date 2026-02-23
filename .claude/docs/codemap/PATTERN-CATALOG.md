# Pattern Catalog — CustomEnchantment

## Singleton (4 instances)

| Class | Access Method | Cleanup |
|-------|---------------|---------|
| CustomEnchantment | `CustomEnchantment.instance()` | Plugin lifecycle |
| AttributeMapRegister | `AttributeMapRegister.instance()` | Nulled in onDisable() |
| CECallerResult | `CECallerResult.instance()` | Nulled in onDisable() |
| CECaller | `CECaller.instance()` | Nulled in onDisable() |

## Registry/Strategy (6 instances)

| Class | Base Type | Register Method | Items |
|-------|-----------|-----------------|-------|
| AttributeMapRegister | SingletonRegister\<AbstractAttributeMap\> | register() | DefaultAttributeMap, EquipSlotAttributeMap |
| CEItemRegister | List\<CEItemFactory\> | register()/unregister() | 22 item factories |
| FilterRegister | Static registry | register() | Item stack filters |
| TinkererTypeRegister | List\<TinkererTypeHook\> | register() | Tinkerer type hooks |
| PlayerSpecialMiningRegister | Registry | register() | 6 mining strategies |
| CEPlayerExpansionRegister | Registry | register() | 14 player expansions |

## Hook Pattern (110+ implementations)

### ConditionHook (30 classes)

| Class | Identifier | Checks |
|-------|-----------|--------|
| ConditionEquipSlot | EQUIP_SLOT | Equipment slot match |
| ConditionEntityType | ENTITY_TYPE | Entity type match |
| ConditionHealth | HEALTH | Health value |
| ConditionHealthPercent | HEALTH_PERCENT | Health percentage |
| ConditionDamageCause | DAMAGE_CAUSE | Damage cause type |
| ConditionOnGround | ON_GROUND | Player on ground |
| ConditionOnFire | ON_FIRE | Player on fire |
| ConditionInCombat | IN_COMBAT | Combat state |
| ConditionFood | FOOD | Hunger level |
| ConditionLevel | LEVEL | Player level |
| ConditionExp | EXP | Experience points |
| ConditionOxygen | OXYGEN | Oxygen level |
| ConditionHold | HOLD | Holding item |
| ConditionAllowFlight | ALLOW_FLIGHT | Flight permission |
| ConditionCanAttack | CAN_ATTACK | Attack permission |
| ConditionActiveEquipSlot | ACTIVE_EQUIP_SLOT | Active equipment |
| ConditionOnlyActiveEquip | ONLY_ACTIVE_EQUIP | Only active equipment |
| ConditionFakeSource | FAKE_SOURCE | Fake damage source |
| ConditionHasEnemy | HAS_ENEMY | Enemy nearby |
| ConditionHasNearbyEnemy | HAS_NEARBY_ENEMY | Nearby enemies |
| ConditionOutOfSight | OUT_OF_SIGHT | Line of sight |
| ConditionWorldTime | WORLD_TIME | World time |
| ConditionNumberStorage | NUMBER_STORAGE | Number storage value |
| ConditionTextStorage | TEXT_STORAGE | Text storage value |
| ConditionItemConsume | ITEM_CONSUME | Item consumption |
| ConditionHasDamageCause | HAS_DAMAGE_CAUSE | Has damage cause |
| ConditionFoodPercent | FOOD_PERCENT | Food percentage |
| ConditionOxygenPercent | OXYGEN_PERCENT | Oxygen percentage |
| ConditionInFactionTerriority | IN_FACTION_TERRITORY | Faction territory |
| ConditionFactionRelation | FACTION_RELATION | Faction relations |

### EffectHook (77+ classes)

| Category | Examples | Count |
|----------|----------|-------|
| Active abilities | EffectActiveDash, EffectActiveDoubleJump, EffectActiveFlash | 5 |
| Attribute modifiers | EffectAddAttribute, EffectRemoveAttribute | 4 |
| Potion effects | EffectAddPotion, EffectRemovePotion, EffectAddForeverPotion | 6 |
| Damage/Combat | EffectDealDamage, EffectTrueDamage, EffectLifeSteal | 8 |
| Teleportation | EffectTeleport, EffectPull, EffectFixedPull | 3 |
| Particles/Visual | EffectPacketParticle, EffectPacketRedstoneParticle | 3 |
| Sound | EffectPlaySound | 1 |
| Message | EffectMessage, EffectAdvancedMessage | 2 |
| Storage | EffectNumberStorage, EffectTextStorage | 2 |
| Block ops | EffectSetBlock | 1 |
| Misc utility | EffectLightning, EffectExplosion, EffectSummonGuard | 42+ |

### ExecuteHook (2 classes)

| Class | Identifier | Action |
|-------|-----------|--------|
| GiveItemExecute | GIVE_ITEM | Gives items to players |
| UseItemExecute | USE_ITEM | Uses item functionality |

## Module Pattern (16 classes)

All extend `PluginModule<CustomEnchantment>` with lifecycle: onEnable → onReload → onSave → onDisable.
See MODULE-MAP.md for full list.

## Listener Pattern (15 classes)

| Listener | Event Count | Priority Notes |
|----------|-------------|----------------|
| PlayerListener | 11 events | Mostly MONITOR |
| InventoryListener | 7 events | Mostly MONITOR |
| EntityListener | 7 events | LOWEST for EntityDamage, HIGHEST for EntityDamageByEntity |
| BlockListener | 5+ events | ignoreCancelled |
| GuardListener | 5 events | MONITOR |
| CEProtectDeadListener | 2 events | MONITOR |
| OutfitListener | 3 events | Default |
| BannerListener | 1 event | Default |
| StaffMechanicListener | 2 events | Default |
| MobDeathListener | 1 event | Conditional: no StackMob |
| MobStackDeathListener | 1 event | Conditional: StackMob present |
| CMenuListener | 4 events | Conditional: CustomMenu present |
| McMMOListener | 1 event | Conditional: mcMMO present |
| CustomFarmListener | 1 event | Conditional: CustomFarm present |
| MobDamageTrackerListener | - | Currently disabled |

## Factory Pattern (22 factories)

All extend `CEItemFactory<T extends CEItem>` and register in CEItemRegister:

VanillaItemFactory, CEWeaponFactory, CEArtifactFactory, CEMaskFactory, CEBannerFactory, CEBookFactory, CEProtectDeadFactory, CEGemFactory, CERemoveProtectDeadFactory, CEProtectDestroyFactory, CENameTagFactory, CEEnchantPointFactory, CEIncreaseRateBookFactory, CERandomBookFactory, CERemoveEnchantFactory, CERemoveGemFactory, CERemoveEnchantPointFactory, CEEraseEnchantFactory, CELoreFormatFactory, CEGemDrillFactory, CESigilFactory, CEOutfitFactory, CESkinFactory

## Builder Pattern (7+ classes)

| Class | Fields | Annotation |
|-------|--------|------------|
| CEArtifactGroup | name, display, itemDisplay, itemLore, levelColors | @Builder |
| CEOutfitGroup | outfit group config fields | @Builder |
| CESigilGroup | sigil group config fields | @Builder |
| CEGemData | gem data fields | @Builder |
| CEOutfitData | outfit data fields | @Builder |
| AttributeMapData | cePlayer, slotMap | @Builder @AllArgsConstructor |
| WeaponSettings | weapon config fields | @Builder |

## Abstract Template (13+ classes)

| Abstract Class | Subclass Count | Purpose |
|----------------|---------------|---------|
| ConditionHook | 30 | Condition checking framework |
| EffectHook | 77+ | Effect execution framework |
| AbstractAttributeMap | 2 | Attribute map implementations |
| AbstractSpecialMine | 6 | Mining mechanic variants |
| CEWeaponAbstract\<T\> | Multiple | Weapon item base |
| CEItem\<T\> | Multiple | Custom item base |
| CEItemData | Multiple | Item data base |
| CEItemStorage | Multiple | Storage base |
| AbstractListHandler | 12 | Menu handler base |
| MenuListenerAbstract | Multiple | Menu event base |
| TinkererTypeHook | Multiple | Tinkerer type detection |
| AbstractConfig | Multiple | Config base |
| AbstractTarget | Multiple | Combat log target base |
| PlayerPerTickTask | 10 | Per-tick player processing base |

## Configuration Pattern (15+ classes)

All use `@Configuration` + `@Path` annotations for YAML binding:
MainConfig, BookCraftConfig, CEEnchantConfig, CEEnchantGroupConfig, CEArtifactGroupConfig, CESigilGroupConfig, CEOutfitGroupConfig, CEItemConfig, VanillaItemConfig, BookUpgradeConfig, TinkererConfig, ArtifactUpgradeConfig, ExtraSlotSettingsData, and nested config classes.
