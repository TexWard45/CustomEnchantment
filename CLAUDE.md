# CustomEnchantment - Claude Code Instructions

## Project Overview

CustomEnchantment is a Bukkit plugin for Minecraft servers (Paper/Leaf 1.21.1) built on BafFramework.
Multi-module Gradle project written in **Java 21** with **Lombok**.

## Modules

| Module | Purpose | Base Package |
|--------|---------|-------------|
| `AttributeModule` | Attribute system for items and players | `com.bafmc.customenchantment.attribute` |
| `CommandModule` | Command handlers | `com.bafmc.customenchantment.command` |
| `ConfigModule` | Configuration management | `com.bafmc.customenchantment.config` |
| `CustomMenuModule` | Custom menu integration | `com.bafmc.customenchantment.custommenu` |
| `DatabaseModule` | Data persistence | `com.bafmc.customenchantment.database` |
| `EnchantModule` | Custom enchantments | `com.bafmc.customenchantment.enchant` |
| `ExecuteModule` | Execute hooks | `com.bafmc.customenchantment.execute` |
| `FeatureModule` | Game features | `com.bafmc.customenchantment.feature` |
| `FilterModule` | Item filters | `com.bafmc.customenchantment.filter` |
| `GuardModule` | Guard system | `com.bafmc.customenchantment.guard` |
| `ItemModule` | Item management (artifacts, outfits, sigils) | `com.bafmc.customenchantment.item` |
| `ListenerModule` | Event listeners | `com.bafmc.customenchantment.listener` |
| `MenuModule` | Menu system | `com.bafmc.customenchantment.menu` |
| `PlaceholderModule` | Placeholder integration | `com.bafmc.customenchantment.placeholder` |
| `PlayerModule` | Player data management | `com.bafmc.customenchantment.player` |
| `TaskModule` | Scheduled tasks | `com.bafmc.customenchantment.task` |

## Build & Test Commands

```bash
./gradlew build                          # Build the plugin
./gradlew test                           # Run all tests
./gradlew jacocoTestReport               # Generate coverage reports
```

## Tech Stack

- **Java 21** with Lombok annotations (`@Getter`, `@Builder`, `@AllArgsConstructor`)
- **Gradle 8.x** (wrapper included)
- **Minecraft API**: Paper/Leaf 1.21.1
- **Framework**: BafFramework (compileOnly dependency)
- **Testing**: JUnit 5, MockBukkit 4.x, Mockito 5.x, JaCoCo
<!-- Add any additional libraries here -->

## BafFramework API Reference

For framework API details, see `.claude/docs/bafframework/`:
- [QUICK_START.md](.claude/docs/bafframework/QUICK_START.md) — Setup, BafPlugin, first module
- [MODULE_SYSTEM.md](.claude/docs/bafframework/MODULE_SYSTEM.md) — BafPlugin, PluginModule, lifecycle
- [CONFIG_SYSTEM.md](.claude/docs/bafframework/CONFIG_SYSTEM.md) — @Configuration, @Path, AdvancedFileConfiguration
- [COMMAND_SYSTEM.md](.claude/docs/bafframework/COMMAND_SYSTEM.md) — AdvancedCommandBuilder, Argument, tab completion
- [FEATURE_SYSTEM.md](.claude/docs/bafframework/FEATURE_SYSTEM.md) — Conditions, Executes, Requirements
- [PATTERNS.md](.claude/docs/bafframework/PATTERNS.md) — Strategy, Singleton, Builder patterns
- [UTILITIES.md](.claude/docs/bafframework/UTILITIES.md) — ColorUtils, ItemStackBuilder, InventoryUtils, etc.

## Key BafFramework Conventions

- **ArgumentLine** splits on `:` delimiter, NOT spaces: `new ArgumentLine("TYPE:value", 1)`
- **ConditionData** is a HashMap with case-insensitive keys (all lowercased)
- **@Configuration/@Path** annotations for YAML config binding
- **ConditionHook/ExecuteHook**: use `getIdentifier()` (not deprecated `getIdentify()`)
- **CommandRegistrar**: Preferred over deprecated `AbstractCommand` interface
- **AbstractDatabase**: Implements `AutoCloseable`; use `DataSource`/connection pool over deprecated `getConnection()`
- **Singleton cleanup**: All singleton registers should null their `instance` in `onDisable()`

## Key Design Patterns

- **Singleton**: `FilterRegister.instance()` — null in `onDisable()`
- **Strategy**: `StrategyRegister<T>` for extensible config-driven behaviors
- **Builder**: `ItemStackBuilder`, `AdvancedCommandBuilder`, `PlaceholderBuilder`
- **Module**: `PluginModule` lifecycle (onEnable/onReload/onSave/onDisable)

## Testing Rules

- **Always prefer MockBukkit** over Mockito for Bukkit API classes
- Package: `org.mockbukkit.mockbukkit` (NOT the old `be.seeseemelk` package)
- Setup pattern: `@BeforeAll` with `MockBukkit.isMocked()` guard
- Use `server.addPlayer()` instead of `mock(Player.class)`
- Use real `ItemStack` instead of `mock(ItemStack.class)`
- See `.claude/rules/bukkit-testing.md` for full guidelines

## GitHub Issues

- When working with GitHub issues, always verify the issue exists and is accessible before starting implementation.
- When updating GitHub issue checkboxes or task lists, always re-read the issue after updating to verify the changes were actually applied correctly.

## Git Operations

**CRITICAL: Never commit, merge, or push code unless explicitly asked to do so.**

- **Main branch**: `master`
- **Remote**: `https://github.com/TexWard45/CustomEnchantment`
- **Commit format**: `<type>: <description>` (feat, fix, refactor, docs, test, chore)
- **Use worktrees** for issue implementation: `../CustomEnchantment-{issue}-{desc}`
- **No CI**: Build and test locally only

## Debugging & Bug Fixes

When a first fix attempt breaks something or introduces regressions, STOP and analyze the root cause before attempting another fix. Explain what went wrong and propose the corrected approach before implementing.

## User Preferences

- When asked a **question** (what, why, how, explain, show, list, where, describe) → **research only**, do NOT create/modify files
- When asked to **implement** → follow TDD, use MockBukkit, run tests before marking complete
- Keep responses concise
<!-- Add your own preferences below -->

## External Dependencies

**Required:**
- BafFramework (compileOnly — must be installed on server)

**Optional (softdepend):**
- CustomMenu (compileOnly — custom menu integration)
- StackMob (compileOnly — mob stacking compatibility)
- Vouchers (compileOnly — voucher integration)
- ItemDropManager (compileOnly — item drop management)
- mcMMO (compileOnly — mcMMO integration)
- PlaceholderAPI (compileOnly — placeholder support)
- PlaceholderAPIAddon (compileOnly — custom placeholders)
- CustomShop (compileOnly — shop integration)
- DamageIndicator (compileOnly — damage indicator integration)
- CustomFarm (compileOnly — farm integration)
- Citizens (compileOnly — NPC integration)
