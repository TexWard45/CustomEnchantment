# Java Conventions

## Class Naming

Follow these naming conventions for consistency with BafFramework patterns:

| Suffix | Purpose | Example |
|--------|---------|---------|
| `Abstract*` | Base class to extend | `AbstractMenu`, `AbstractCommand`, `AbstractDatabase` |
| `*Module` | Plugin module with lifecycle | `CoreModule`, `MenuModule`, `StorageModule` |
| `*Manager` | Manages state/logic for a feature | `PlayerManager`, `EconomyManager` |
| `*Listener` | Bukkit event handler | `PlayerListener`, `EntityListener` |
| `*Execute` | Action triggered by config | `BroadcastExecute`, `GiveMoneyExecute` |
| `*Condition` | Config-driven condition check | `PermissionCondition`, `MoneyCondition` |
| `*Requirement` | Player requirement with pay/check | `ExpRequirement`, `PermissionRequirement` |
| `*Data` | Data holder / POJO | `PlayerData`, `MenuData`, `ClickData` |
| `*Config` | Configuration class with @Path | `MainConfig`, `DatabaseConfig` |
| `*Register` | Registry (singleton or strategy) | `MenuRegister`, `ItemRegister`, `MessageRegister` |
| `*Utils` | Static utility methods | `ColorUtils`, `ItemStackUtils`, `ExpUtils` |
| `*Builder` | Fluent builder pattern | `ItemStackBuilder`, `AdvancedCommandBuilder` |
| `*Task` | Scheduled/repeating task | `TpsTask`, `EconomyTask` |
| `*Hook` | Extension point for features | `ConditionHook`, `ExecuteHook` |

## Lombok Usage

```java
// Data classes: @Getter + @Setter or @Builder
@Getter
@Setter
public class PlayerData { ... }

// Immutable data: @Getter + @Builder + @AllArgsConstructor
@Getter
@Builder
@AllArgsConstructor
public class ConsoleMessageData { ... }

// Config classes: @Getter only (set via reflection)
@Getter
@Configuration
public class MainConfig { ... }
```

Do NOT use `@Data` (generates equals/hashCode which can cause issues with Bukkit objects).

## Package Structure

Organize your plugin packages by feature:

```
com.yourcompany.yourplugin/
├── core/               # Module, config, main logic
│   ├── YourPluginModule.java
│   └── config/
│       └── MainConfig.java
├── command/            # Commands
├── listener/           # Event listeners
├── feature/            # Conditions, executes, requirements
│   ├── condition/
│   ├── execute/
│   └── requirement/
├── manager/            # Business logic managers
├── data/               # Data classes
├── storage/            # Caching/persistence
└── utils/              # Feature-specific utilities
```

## Access Patterns

Prefer module registry over static singletons:

```java
// PREFERRED: Module registry
getPlugin().getModule(CoreModule.class).getMainConfig();

// ACCEPTABLE: Singleton for cross-module access
YourPlugin.instance().getModule(CoreModule.class);

// AVOID: Direct static field access patterns in new code
```

## Generics

Use bounded type parameters for reusable components:

```java
// Module with plugin type
public class MyModule extends PluginModule<YourPlugin> { ... }

// Listener with generic plugin
public class MyListener<T extends Plugin> implements Listener { ... }

// Menu with data types (if using CustomMenu)
public class ShopMenu extends AbstractMenu<ShopMenuData, ShopExtraData> { ... }
```
