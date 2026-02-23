# Class Registry — CustomEnchantment

**Total: 545 classes across 23 packages**

## Root (9 classes)

| Class | Type | Extends/Implements | Summary |
|-------|------|-------------------|---------|
| CustomEnchantment | class | BafPlugin, Listener | Main plugin entry point (singleton) |
| CustomEnchantmentDebug | class | — | Debug utilities |
| CustomEnchantmentLog | class | — | Logging utilities |
| CustomEnchantmentMessage | class | — | Message config holder |
| CEEnchantMap | class | StorageMap\<CEEnchant\> | Enchantment storage map |
| CEGroupMap | class | StorageMap\<CEGroup\> | Group storage map |
| CEItemStorageMap | class | StorageMap\<CEItemStorage\> | Item storage map |
| CEPlayerMap | class | — | Player map storage |
| MobDamageTrackerListener | class | Listener | Mob damage tracking |

## API (16 classes)

| Class | Type | Summary |
|-------|------|---------|
| CEAPI | class | Custom enchantment API utilities |
| CombatLogXAPI | class | CombatLogX integration API |
| CompareOperation | enum | Comparison operators |
| EntityTypeList | class | ArrayList\<EntityType\> wrapper |
| EquipSlotAPI | class | Equipment slot utilities |
| ILine | interface | Display line interface |
| ItemPacketAPI | class | Item packet utilities |
| ITrade\<T\> | interface | Trade interface |
| LocationFormat | class | Location formatting |
| MaterialData | class | Material data holder |
| MaterialList | class | ArrayList\<MaterialData\> wrapper |
| Pair\<K,V\> | class | Generic pair |
| Parameter | class | Parameter holder |
| ParticleAPI | class | Particle effect API |
| ParticleSupport | class | Particle support utilities |
| VectorFormat | class | Vector formatting |

## Attribute (4 classes)

| Class | Type | Summary |
|-------|------|---------|
| AttributeCalculate | class | Attribute math utilities |
| AttributeModule | class | PluginModule — attribute system init |
| CustomAttributeType | class | NMSAttributeType extension |
| RangeAttribute | class | NMSAttribute with range + Cloneable |

## CombatLog (5 classes)

| Class | Type | Summary |
|-------|------|---------|
| CombatLogModule | class | PluginModule — combat logging |
| CombatLogSet | class | Combat log data set |
| AbstractTarget\<T\> | abstract | Base combat target |
| PlayerTarget | class | AbstractTarget\<Player\> |
| TargetFactory | class | Creates target instances |

## Command (31 classes)

| Class | Type | Summary |
|-------|------|---------|
| CommandModule | class | PluginModule — command registration |
| ArtifactUpgradeCommand | class | /artifactupgrade handler |
| BookCraftCommand | class | /bookcraft handler |
| BookUpgradeCommand | class | /bookupgrade handler |
| CEAnvilCommand | class | /ceanvil handler |
| EquipmentCommand | class | /equipment handler |
| TinkererCommand | class | /tinkerer handler |
| CustomEnchantmentCommand | class | /customenchantment main handler |
| CommandAddEnchant | class | Subcommand: add enchant |
| CommandAddGem | class | Subcommand: add gem |
| CommandAddItem | class | Subcommand: add CE item |
| CommandAdmin | class | Subcommand: admin ops |
| CommandClearTime | class | Subcommand: clear cooldowns |
| CommandDebug | class | Subcommand: debug feature |
| CommandDebugAll | class | Subcommand: debug all |
| CommandDebugCE | class | Subcommand: debug CE enchants |
| CommandDisableHelmet | class | Subcommand: disable helmet |
| CommandEnableHelmet | class | Subcommand: enable helmet |
| CommandFilterEnchant | class | /cefilter handler |
| CommandFullChance | class | Subcommand: test chances |
| CommandGiveItem | class | Subcommand: give item |
| CommandInfo | class | Subcommand: show info |
| CommandNameTag | class | /nametag handler |
| CommandOpen | class | Subcommand: open menu |
| CommandReload | class | Subcommand: reload config |
| CommandRemoveEnchant | class | Subcommand: remove enchant |
| CommandRemoveGem | class | Subcommand: remove gem |
| CommandRemoveItem | class | Subcommand: remove item |
| CommandTest | class | Subcommand: test features |
| CommandUpdateItem | class | Subcommand: update item |
| CommandUseItem | class | Subcommand: use item |

## Config (18 classes)

| Class | Type | Summary |
|-------|------|---------|
| ConfigModule | class | PluginModule — config loading |
| AbstractConfig | abstract | Base config class |
| MainConfig | class | Main plugin config (config.yml) |
| BookCraftConfig | class | Book crafting config |
| BookUpgradeConfig | class | Book upgrade config |
| ArtifactUpgradeConfig | class | Artifact upgrade config |
| TinkererConfig | class | Tinkerer config |
| CEEnchantConfig | class | Enchantment definitions |
| CEEnchantGroupConfig | class | Enchant group config |
| CEArtifactGroupConfig | class | Artifact group config |
| CESigilGroupConfig | class | Sigil group config |
| CEOutfitGroupConfig | class | Outfit group config |
| CEItemConfig | class | Item definitions |
| VanillaItemConfig | class | Vanilla item config |
| CEOutfitConfig | class | Outfit config |
| CESkinConfig | class | Skin config |
| CEWeaponConfig | class | Weapon config |
| ExtraSlotSettingsData | class | Extra slot settings (@Configuration) |

## Constant (3 classes)

| Class | Type | Summary |
|-------|------|---------|
| CEConstants | class | Plugin constants |
| CEMessageKey | enum | Message key enum (implements MessageKey) |
| MessageKey | interface | Message key interface |

## CustomMenu (4 classes)

| Class | Type | Summary |
|-------|------|---------|
| CustomMenuModule | class | PluginModule — CustomMenu integration |
| CEBookCatalog | class | Catalog extension for books |
| CustomEnchantmentItemDisplaySetup | class | Item display setup |
| CustomEnchantmentTradeItemCompare | class | Trade item comparison |

## Database (2 classes)

| Class | Type | Summary |
|-------|------|---------|
| DatabaseModule | class | PluginModule — SQLite persistence |
| Database | class | Database operations |

## Enchant (141 classes)

### Core (21 classes)

| Class | Type | Summary |
|-------|------|---------|
| EnchantModule | class | PluginModule — enchant registration |
| CECaller | class | Enchantment trigger executor (singleton) |
| CECallerBuilder | class | Builder for CECaller |
| CECallerList | class | ArrayList\<CECaller\> |
| CECallerResult | class | Caller result holder (singleton) |
| CEDisplay | class | Enchantment display info |
| CEEnchant | class | Main enchantment definition |
| CEEnchantSimple | class | Simple enchant wrapper (ILine) |
| CEFunction | class | Enchantment function |
| CEFunctionData | class | Function data (Cloneable) |
| CEGroup | class | Enchantment group |
| CELevel | class | Enchantment level |
| CELevelMap | class | LinkedHashMap\<Integer, CELevel\> |
| CEPlaceholder | class | Enchantment placeholder |
| CEType | class | Enchantment type |
| Condition | class | Condition definition |
| ConditionOR | class | OR-combined conditions |
| ConditionSettings | class | Condition settings |
| Cooldown | class | Cooldown tracker (Cloneable) |
| Effect | class | Effect definition |
| EffectData | class | Effect data holder |
| EffectSettings | class | Effect settings (Cloneable) |
| EffectTaskSeparate | class | Separate task effect execution |
| EffectUtil | class | Effect utilities |
| Function | class | Function definition |
| IEffectAction | interface | Effect action interface |
| ModifyType | enum | Modification type |
| Option | class | Option config |
| Priority | enum | Priority levels |
| StepAction | enum | Step action types |
| Target | enum | Target types |
| TargetFilter | class | Target filtering |

### ConditionHook — abstract base class (30 implementations)

ConditionActiveEquipSlot, ConditionAllowFlight, ConditionCanAttack, ConditionDamageCause, ConditionEntityType, ConditionEquipSlot, ConditionExp, ConditionFactionRelation, ConditionFakeSource, ConditionFood, ConditionFoodPercent, ConditionHasDamageCause, ConditionHasEnemy, ConditionHasNearbyEnemy, ConditionHealth, ConditionHealthPercent, ConditionHold, ConditionInCombat, ConditionInFactionTerriority, ConditionItemConsume, ConditionLevel, ConditionNumberStorage, ConditionOnFire, ConditionOnGround, ConditionOnlyActiveEquip, ConditionOutOfSight, ConditionOxygen, ConditionOxygenPercent, ConditionTextStorage, ConditionWorldTime

### EffectHook — abstract base class (77+ implementations)

**Active abilities:** EffectActiveAbility, EffectActiveDash, EffectActiveDoubleJump, EffectActiveEquipSlot, EffectActiveFlash
**Deactivate:** EffectDeactiveAbility, EffectDeactiveDash, EffectDeactiveDoubleJump, EffectDeactiveEquipSlot, EffectDeactiveFlash
**Add attribute/potion:** EffectAddAttribute, EffectAddAutoAttribute, EffectAddCustomAttribute, EffectAddPotion, EffectAddRandomPotion, EffectAddForeverPotion
**Remove attribute/potion:** EffectRemoveAttribute, EffectRemoveAutoAttribute, EffectRemoveCustomAttribute, EffectRemovePotion, EffectRemoveRandomPotion, EffectRemoveForeverPotion, EffectBlockForeverPotion, EffectUnblockForeverPotion
**Mining:** EffectAddBlockBonus, EffectRemoveBlockBonus, EffectAddBlockDropBonusMining, EffectRemoveBlockDropBonusMining, EffectAddExplosionMining, EffectRemoveExplosionMining, EffectAddFurnaceMining, EffectRemoveFurnaceMining, EffectAddVeinMining, EffectRemoveVeinMining, EffectEnableTelepathy, EffectDisableTelepathy, EffectEnableAutoSell, EffectDisableAutoSell
**Combat:** EffectDealDamage, EffectTrueDamage, EffectAbsorptionHeart, EffectAddMobBonus, EffectRemoveMobBonus
**Movement:** EffectTeleport, EffectPull, EffectFixedPull, EffectSetFlight, EffectWindCharge
**Visual/Audio:** EffectPacketParticle, EffectPacketRedstoneParticle, EffectPacketCircleRedstoneParticle, EffectPacketSpiralRedstoneParticle, EffectPlaySound, EffectLightning, EffectExplosion, EffectSetStaffParticle, EffectRemoveStaffParticle
**Player stats:** EffectHealth, EffectFood, EffectOxygen, EffectExp, EffectDurability, EffectOnFire
**Messages:** EffectMessage, EffectAdvancedMessage
**Storage:** EffectNumberStorage, EffectTextStorage
**Misc:** EffectSetBlock, EffectShootArrow, EffectSummonGuard, EffectSummonBabyZombieGuard, EffectSummonCustomGuard, EffectRemoveGuard, EffectRemoveTask, EffectRemoveTaskAsync, EffectEnableMultipleArrow, EffectDisableMultipleArrow

## Event (1 class)

| Class | Type | Summary |
|-------|------|---------|
| CEPlayerStatsModifyEvent | class | Custom event — player stats changed (Cancellable) |

## Exception (1 class)

| Class | Type | Summary |
|-------|------|---------|
| CENotSuitableTypeException | class | Exception for wrong CE item type |

## Execute (3 classes)

| Class | Type | Summary |
|-------|------|---------|
| ExecuteModule | class | PluginModule — execute hook registration |
| GiveItemExecute | class | ExecuteHook — gives items |
| UseItemExecute | class | ExecuteHook — uses items |

## Feature (5 classes)

| Class | Type | Summary |
|-------|------|---------|
| FeatureModule | class | PluginModule — feature registration |
| CustomEnchantmentItem | class | AbstractItem for CE menus |
| DashFeature | class | Dash ability logic |
| DoubleJumpFeature | class | Double jump ability logic |
| FlashFeature | class | Flash ability logic |

## Filter (3 classes)

| Class | Type | Summary |
|-------|------|---------|
| FilterModule | class | PluginModule — filter registration |
| FilterRegister | class | Static filter registry |
| CEWeaponFilter | class | ItemStackFilter for CE weapons |

## Guard (5 classes)

| Class | Type | Summary |
|-------|------|---------|
| GuardModule | class | PluginModule — guard system |
| Guard | class | Guard entity representation |
| GuardManager | class | Guard lifecycle management |
| GuardSetting | class | Guard configuration |
| PlayerGuard | class | Per-player guard holder |

## Item (129 classes)

### Core (24 classes)

| Class | Type | Summary |
|-------|------|---------|
| ItemModule | class | PluginModule — item factory registration |
| CEItem\<T\> | abstract | Base custom item |
| CEItemData | abstract | Base item data |
| CEItemFactory\<T\> | abstract | Base item factory |
| CEItemOptimizeLoader | class | Item optimization loader |
| CEItemRegister | class | Item factory registry |
| CEItemSimple | class | Simple item wrapper |
| CEItemStorage\<T\> | abstract | Base item storage |
| CEItemType | class | Item type definitions |
| CEItemUsable\<T\> | abstract | Usable item base |
| CEItemExpansion | class | Item expansion utilities |
| CENBT | class | NBT tag handling |
| CEUnify\<T\> | abstract | Unified item base |
| CEUnifyData | class | Unified item data |
| CEUnifyWeapon | class | Unified weapon (ITrade) |
| CEWeapon | class | Weapon implementation |
| CEWeaponAbstract\<T\> | abstract | Weapon base class |
| CEWeaponData | class | Weapon data |
| CEWeaponFactory | class | Weapon factory |
| CEWeaponStorage | class | Weapon storage |
| CEWeaponType | enum | Weapon types |
| VanillaItem | class | Vanilla item wrapper |
| VanillaItemData | class | Vanilla item data |
| VanillaItemFactory | class | Vanilla item factory |
| VanillaItemStorage | class | Vanilla item storage |
| WeaponAttribute | class | Weapon attribute expansion |
| WeaponData | class | Weapon data expansion |
| WeaponDisplay | class | Weapon display expansion |
| WeaponEnchant | class | Weapon enchant expansion |
| WeaponGem | class | Weapon gem expansion |
| WeaponSettings | class | Weapon settings (Builder) |

### Item Types (each follows pattern: CE*Item + CE*Data + CE*Factory + CE*Storage)

| Type | Item Class | Additional Classes |
|------|-----------|-------------------|
| Artifact | CEArtifact, CEArtifactData, CEArtifactFactory, CEArtifactStorage | CEArtifactGroup, CEArtifactGroupMap |
| Banner | CEBanner, CEBannerData, CEBannerFactory, CEBannerStorage | — |
| Book | CEBook, CEBookData, CEBookFactory, CEBookStorage | — |
| EnchantPoint | CEEnchantPoint, CEEnchantPointData, CEEnchantPointFactory, CEEnchantPointStorage | CEEnchantPointSimple |
| EraseEnchant | CEEraseEnchant, CEEraseEnchantData, CEEraseEnchantFactory, CEEraseEnchantStorage | — |
| Gem | CEGem, CEGemData, CEGemFactory, CEGemStorage | CEGemSettings, CEGemSimple |
| GemDrill | CEGemDrill, CEGemDrillData, CEGemDrillFactory, CEGemDrillStorage | CEGemDrillSimple |
| IncreaseRateBook | CEIncreaseRateBook, CEIncreaseRateBookData, CEIncreaseRateBookFactory, CEIncreaseRateBookStorage | — |
| LoreFormat | CELoreFormat, CELoreFormatData, CELoreFormatFactory, CELoreFormatStorage | — |
| Mask | CEMask, CEMaskData, CEMaskFactory, CEMaskStorage | — |
| NameTag | CENameTag, CENameTagData, CENameTagFactory, CENameTagStorage | — |
| Outfit | CEOutfit, CEOutfitData, CEOutfitFactory, CEOutfitStorage | CEOutfitGroup, CEOutfitGroupMap |
| ProtectDead | CEProtectDead, CEProtectDeadData, CEProtectDeadFactory, CEProtectDeadStorage | — |
| ProtectDestroy | CEProtectDestroy, CEProtectDestroyData, CEProtectDestroyFactory, CEProtectDestroyStorage | — |
| RandomBook | CERandomBook, CERandomBookData, CERandomBookFactory, CERandomBookStorage | CERandomBookFilter, CERandomBookPlayerFilter |
| RemoveEnchant | CERemoveEnchant, CERemoveEnchantData, CERemoveEnchantFactory, CERemoveEnchantStorage | — |
| RemoveEnchantPoint | CERemoveEnchantPoint, CERemoveEnchantPointData, CERemoveEnchantPointFactory, CERemoveEnchantPointStorage | — |
| RemoveGem | CERemoveGem, CERemoveGemData, CERemoveGemFactory, CERemoveGemStorage | — |
| RemoveProtectDead | CERemoveProtectDead, CERemoveProtectDeadData, CERemoveProtectDeadFactory, CERemoveProtectDeadStorage | — |
| Sigil | CESigil, CESigilData, CESigilFactory, CESigilStorage | CESigilGroup, CESigilGroupMap |
| Skin | CESkin, CESkinData, CESkinFactory, CESkinStorage | — |

## Listener (15 classes)

| Class | Type | Summary |
|-------|------|---------|
| ListenerModule | class | PluginModule — listener registration |
| PlayerListener | class | Player events (11 handlers) |
| InventoryListener | class | Inventory events (7 handlers) |
| EntityListener | class | Entity events (7 handlers) |
| BlockListener | class | Block events (5+ handlers) |
| CEProtectDeadListener | class | Death protection events |
| GuardListener | class | Guard system events |
| BannerListener | class | Banner inventory events |
| OutfitListener | class | Outfit system events |
| StaffMechanicListener | class | Staff weapon events |
| MobDeathListener | class | Mob death (no StackMob) |
| MobStackDeathListener | class | Mob death (StackMob) |
| CMenuListener | class | CustomMenu events |
| McMMOListener | class | mcMMO integration |
| CustomFarmListener | class | CustomFarm integration |

## Menu (76 classes)

### Core (3 classes)
MenuModule, MenuListenerAbstract (abstract), BookData

### Tinkerer (8 classes)
TinkererCustomMenu, TinkererExtraData, TinkererReward, TinkererSettings, TinkererTypeHook (abstract), TinkererTypeRegister, TinkerAcceptItem, TinkerSlotItem

### BookCraft (7 classes)
BookCraftCustomMenu, BookCraftExtraData, FastCraftRefactored, BookAcceptItem, BookPreviewItem, BookReturnItem, BookSlotItem

### CEAnvil (16 classes)
CEAnvilCustomMenu, CEAnvilExtraData, CEAnvilSettings, AnvilItemData, AnvilConfirmItem, AnvilPageItem, AnvilPreviewItem, AnvilSlotItem, Slot2Handler (interface), AbstractListHandler (abstract), BookHandler, DefaultHandler, EnchantPointHandler, EraseEnchantHandler, GemDrillHandler, GemHandler, LoreFormatHandler, ProtectDeadHandler, ProtectDestroyHandler, RemoveEnchantHandler, RemoveEnchantPointHandler, RemoveGemHandler, RemoveProtectDeadHandler

### BookUpgrade (13 classes)
BookUpgradeCustomMenu, BookUpgradeExtraData, BookUpgradeSettings, BookUpgradeAddReason, BookUpgradeConfirmReason, BookUpgradeData, BookUpgradeLevelData, RequiredXpGroup, XpGroup, BookUpgradeRemindItem, BookUpgradeResultItem, BookUpgradeSlotItem, IngredientPreviewItem

### ArtifactUpgrade (11 classes)
ArtifactUpgradeCustomMenu, ArtifactUpgradeExtraData, ArtifactUpgradeSettings, ArtifactUpgradeAddReason, ArtifactUpgradeConfirmReason, ArtifactUpgradeData, ArtifactUpgradeLevelData, ArtifactIngredientPreviewItem, ArtifactRemindItem, PreviewArtifactItem, SelectedArtifactItem

### Equipment (11 classes)
EquipmentCustomMenu, EquipmentExtraData, EquipmentSlotItem, ExtraSlotEquipmentItem, PlayerInfoEquipmentItem, ProtectDeadEquipmentItem, WingsEquipmentItem, EquipmentSlotHandler (interface), ArmorWeaponHandler, ExtraSlotHandler, ProtectDeadHandler

## NMS (3 classes)

| Class | Type | Summary |
|-------|------|---------|
| CECraftItemStackNMS | class | NMS ItemStack wrapper |
| EntityInsentientNMS | class | NMS insentient entity |
| EntityLivingNMS | class | NMS living entity |

## Placeholder (2 classes)

| Class | Type | Summary |
|-------|------|---------|
| PlaceholderModule | class | PluginModule — PAPI integration |
| CustomEnchantmentPlaceholder | class | PlaceholderExpansion impl |

## Player (41 classes)

### Core (8 classes)
CEPlayer (ICEPlayerEvent), CEPlayerExpansion (abstract), CEPlayerExpansionRegister, ICEPlayerEvent (interface), CancelManager, PlayerSet, PlayerStorage, PlayerTemporaryStorage

### Attributes (5 classes)
AbstractAttributeMap (abstract), AttributeMapData (@Builder), AttributeMapRegister (SingletonRegister), DefaultAttributeMap, EquipSlotAttributeMap

### Bonuses (3 classes)
Bonus\<K\>, BlockBonus, EntityTypeBonus

### Mining (9 classes)
AbstractSpecialMine (abstract), AutoSellSpecialMine, BlockDropBonusSpecialMine, ExplosionSpecialMine, FurnaceSpecialMine, TelepathySpecialMine, VeinSpecialMine, SpecialMiningData, PlayerSpecialMiningRegister

### Player Expansions (14 classes)
PlayerAbility, PlayerBlockBonus, PlayerCECooldown, PlayerCEManager, PlayerCustomAttribute, PlayerEquipment, PlayerExtraSlot, PlayerGem, PlayerMobBonus, PlayerNameTag, PlayerPotion, PlayerSpecialMining, PlayerVanillaAttribute, PlayerPotionData

### Other (2 classes)
TemporaryKey, PlayerGuard (in guard package)

## Task (24 classes)

| Class | Type | Summary |
|-------|------|---------|
| TaskModule | class | PluginModule — task scheduling |
| EffectExecuteTask | class | Effect execution (sync+async, 1 tick) |
| CECallerTask | class | Enchant caller trigger (1 tick) |
| CEPlayerTask | class | Player potion updates (20 ticks) |
| CEExtraSlotTask | class | Extra slot management (4 ticks) |
| RegenerationTask | class | Regen effects (4 ticks) |
| UpdateAttributeTask | class | Attribute updates (4 ticks) |
| SlowResistanceTask | class | Slow/resist effects (4 ticks) |
| SpecialMiningTask | class | Mining enchants (1 tick) |
| BlockTask | class | Block interaction effects (1 tick) |
| ArrowTask | class | Arrow/projectile effects (20 ticks) |
| SaveTask | class | Data persistence (15 min) |
| UnbreakableArmorTask | class | Armor durability (config) |
| ExpTask | class | Experience management (200 ticks) |
| UpdateJumpTask | class | Jump ability (4 ticks) |
| OutfitItemTask | class | Outfit processing (1 tick) |
| OutfitTopInventoryTask | class | Equipment display (1 tick) |
| RecalculateAttributeTask | class | Async attribute calc (1 tick) |
| SigilItemTask | class | Sigil display async (1 tick) |
| OutfitItemAsyncTask | class | Async outfit prep (1 tick) |
| OutfitTopInventoryAsyncTask | class | Async equipment prep (1 tick) |
| PowerAsyncTask | class | Async power calc (20 ticks) |
| GuardTask | class | Guard management (20 ticks) |
| AutoUpdateItemTask | class | Auto-update items (disabled) |

## Utils (4 classes)

| Class | Type | Summary |
|-------|------|---------|
| AttributeUtils | class | Attribute math utilities |
| DamageUtils | class | Damage calculation utilities |
| McMMOUtils | class | mcMMO integration utilities |
| StorageUtils | class | Storage/data utilities |
