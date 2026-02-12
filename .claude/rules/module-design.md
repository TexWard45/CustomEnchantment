# Module Design

## When to Use What

| Type | When | Example |
|------|------|---------|
| **PluginModule** | Self-contained feature within a plugin | `CoreModule`, `StorageModule`, `LangModule` |
| **Utility class** | Stateless helpers, no lifecycle needed | `ColorUtils`, `ExpUtils`, `MaterialUtils` |
| **Manager** | Stateful logic that isn't a full module | `PlayerManager`, `EconomyManager` |

## Module Lifecycle

Modules implement `ModuleLifecycle` with 4 methods called in this order:

```
Server Start:   onEnable() → onReload()
Config Reload:  onReload()
Server Save:    onSave()
Server Stop:    onSave() → onDisable()
```

### onEnable()
- Register listeners
- Register commands
- Start tasks
- Call onReload() for initial config load

### onReload()
- Load/reload configuration files
- Re-initialize data from config
- Do NOT re-register listeners or commands

### onSave()
- Persist data to files/database
- Flush caches

### onDisable()
- Cancel tasks
- Close connections
- Clean up resources

## Module Registration

Register modules in the plugin's `registerModules()`:

```java
public class YourPlugin extends BafPlugin {
    @Override
    public void registerModules() {
        registerModule(new CoreModule(this));
        registerModule(new StorageModule(this));
    }
}
```

Order matters — modules are enabled in registration order.

## One Module = One Responsibility

Each module owns:
- Its own config section
- Its own listeners
- Its own commands
- Its own data/state

Modules communicate through the plugin's module registry:
```java
// Module A accessing Module B
CoreModule core = getPlugin().getModule(CoreModule.class);
```

## Feature Registration Pattern

When a module registers conditions/executes/requirements, do it in `onEnable()`:

```java
@Override
public void onEnable() {
    onReload();
    registerConditions();
    registerExecutes();
}

private void registerConditions() {
    new PermissionCondition().register();
    new MoneyCondition().register();
}
```
