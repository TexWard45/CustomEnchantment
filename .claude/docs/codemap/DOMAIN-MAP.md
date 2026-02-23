# Domain Map — CustomEnchantment

## enchantment
Core enchantment system: conditions, effects, callers, enchant data.
- **Packages:** .enchant, .enchant.condition, .enchant.effect
- **Key classes:** CECaller, CECallerResult, 30 ConditionHook impls, 77+ EffectHook impls
- **Config:** enchantment/*.yml → CEEnchantConfig

## items
Custom item types: weapons, artifacts, books, gems, sigils, outfits, skins.
- **Packages:** .item, .item.artifact, .item.book, .item.gem, .item.outfit, .item.sigil, .item.skin
- **Key classes:** CEItem, CEWeaponAbstract, CEItemRegister, 22 CEItemFactory impls
- **Config:** items.yml, artifact/*.yml, weapon/*.yml, outfit/*.yml, skin/*.yml

## player
Player data management: expansions, attributes, equipment, abilities.
- **Packages:** .player, .player.attribute, .player.mining
- **Key classes:** CEPlayer, PlayerStorage, 14 expansion classes, 6 SpecialMine classes
- **Config:** data/player/<name>.yml

## gui
Menu system: tinkerer, book craft, CE anvil, book upgrade, artifact upgrade, equipment.
- **Packages:** .menu, .menu.tinkerer, .menu.bookcraft, .menu.ceanvil, .menu.bookupgrade, .menu.artifactupgrade, .menu.equipment
- **Key classes:** 6 CustomMenu impls, 12 CEAnvil handlers, MenuListenerAbstract
- **Config:** menu/*.yml

## combat
Damage processing, combat logging, staff mechanics.
- **Packages:** .combatlog, .combatlog.target
- **Key classes:** CombatLogModule, AbstractTarget, StaffMechanicListener
- **Related conditions:** ConditionDamageCause, ConditionInCombat, ConditionCanAttack
- **Related effects:** EffectDealDamage, EffectTrueDamage, EffectLifeSteal

## configuration
YAML config loading, file management, data classes.
- **Packages:** .config, .config.data
- **Key classes:** MainConfig, ConfigModule, 15+ @Configuration classes
- **Config:** config.yml, messages.yml, groups.yml, book-craft.yml, etc.

## events
Event listeners for Bukkit events, conditional plugin integration.
- **Packages:** .listener
- **Key classes:** PlayerListener, EntityListener, BlockListener, InventoryListener + 11 more
- **Events handled:** 50+ distinct event types

## scheduling
Background tasks: effect execution, player updates, data saving.
- **Packages:** .task
- **Key classes:** PlayerPerTickTask (abstract), 16 sync tasks, 6 async tasks
- **Critical paths:** EffectExecuteTask (1 tick), CECallerTask (1 tick), SaveTask (15 min)

## attributes
Custom attribute system for items and players.
- **Packages:** .attribute, .player.attribute
- **Key classes:** CustomAttributeType, AttributeMapRegister, AbstractAttributeMap
- **Related effects:** EffectAddAttribute, EffectRemoveAttribute

## persistence
Database operations and data storage.
- **Packages:** .database
- **Key classes:** Database, DatabaseModule
- **Storage:** SQLite, storage/save-items.yml, data/player/*.yml

## mobs
Guard mob system: spawning, targeting, management.
- **Packages:** .guard
- **Key classes:** GuardManager, GuardTask, GuardListener
- **Related effects:** EffectSummonGuard

## filtering
Item and entity filtering system.
- **Packages:** .filter
- **Key classes:** FilterRegister, FilterModule
- **Command:** /cefilter

## commands
Command handlers for all plugin commands.
- **Packages:** .command
- **Key classes:** CustomEnchantmentCommand + 20 subcommand classes
- **Commands:** 9 root commands

## integration
External plugin integrations.
- **Packages:** .custommenu, .placeholder
- **Key classes:** CustomMenuModule, PlaceholderModule, CEBookCatalog
- **External:** CustomMenu, PlaceholderAPI, StackMob, mcMMO, CustomFarm
