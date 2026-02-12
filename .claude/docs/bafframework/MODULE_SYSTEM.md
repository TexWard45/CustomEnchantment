# Module System

BafFramework organizes plugin features into modules with managed lifecycles.

## BafPlugin

Package: `com.bafmc.bukkit.BafPlugin`

`BafPlugin` extends `JavaPlugin` and provides the module registry.

```java
import com.bafmc.bukkit.BafPlugin;

public class MyPlugin extends BafPlugin {
    @Override
    public void registerModules() {
        registerModule(new DatabaseModule(this));  // Register first (dependency)
        registerModule(new CoreModule(this));       // Depends on DatabaseModule
    }
}
```

### Key Methods

| Method | Description |
|--------|-------------|
| `registerModule(PluginModule)` | Register a module (called in `registerModules()`) |
| `getModule(Class<T>)` | Retrieve a module by class |
| `registerChildPlugin(Class<?>)` | Register a child plugin (called in `registerChildPlugins()`) |
| `getChildPlugin(Class<T>)` | Retrieve a child plugin by class |

`registerModules()` is called automatically at the start of `onEnable()`. Registration order determines lifecycle call order.

## PluginModule

Package: `com.bafmc.bukkit.module.PluginModule`

Base class for all modules. The generic type `T` gives type-safe access to your plugin via `getPlugin()`.

```java
import com.bafmc.bukkit.module.PluginModule;
import lombok.Getter;

@Getter
public class CoreModule extends PluginModule<MyPlugin> {
    private MainConfig mainConfig;

    public CoreModule(MyPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        onReload();  // Load config on first enable
        new MyListener(getPlugin());  // Register listeners
        new MyCondition().register();  // Register conditions
    }

    @Override
    public void onReload() {
        // Reload config -- do NOT re-register listeners or commands
    }

    @Override
    public void onSave() {
        if (mainConfig == null) return;  // Guard against early calls
        // Persist data
    }

    @Override
    public void onDisable() {
        // Cancel tasks, close connections
    }
}
```

### Lifecycle Order

```
Server Start:   onEnable() -> onReload() for each module (in registration order)
Config Reload:  onReload() for each module
Server Save:    onSave() for each module
Server Stop:    onSave() -> onDisable() for each module
```

### Lifecycle Interface

Package: `com.bafmc.bukkit.module.ModuleLifecycle`

All four methods have default empty implementations, so you only override what you need:

```java
public interface ModuleLifecycle {
    default void onEnable() {}
    default void onDisable() {}
    default void onReload() {}
    default void onSave() {}
}
```

## BafChildPlugin

Package: `com.bafmc.bukkit.plugin.BafChildPlugin`

For bundling separate sub-plugins into a single JAR. Each child plugin has its own module registry, logger, and data folder.

```java
import com.bafmc.bukkit.plugin.BafChildPlugin;
import com.bafmc.bukkit.BafPlugin;

public class MyChildPlugin extends BafChildPlugin {
    public MyChildPlugin(BafPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "MyChildPlugin";
    }

    @Override
    public void registerModules() {
        registerModule(new ChildCoreModule(this));
    }
}
```

Child plugin modules extend `ChildModule<T>` instead of `PluginModule<T>`:

```java
import com.bafmc.bukkit.module.ChildModule;

public class ChildCoreModule extends ChildModule<MyChildPlugin> {
    public ChildCoreModule(MyChildPlugin plugin) {
        super(plugin);
    }
}
```

Register child plugins in your main plugin:

```java
@Override
public void registerChildPlugins() {
    registerChildPlugin(MyChildPlugin.class);
}
```

### When to Use ChildPlugin vs Module

| Use Case | Choice |
|----------|--------|
| Feature is part of the core plugin | `PluginModule` |
| Feature has its own config files and commands | `BafChildPlugin` |
| Feature could be disabled independently | `BafChildPlugin` |
| Feature is tightly coupled with other modules | `PluginModule` |

### Child Plugin Lifecycle

Child plugins receive lifecycle calls after all modules:

```
onEnable():  modules first, then registerChildPlugins() + child onEnable()
onReload():  modules first, then child onReload()
onSave():    modules first, then child onSave()
onDisable(): onSave() -> modules onDisable(), then child onSave() -> child onDisable()
```

## Best Practices

- Always call `onReload()` from `onEnable()` to load initial config
- Add null guards in `onSave()` and `onDisable()` for partially initialized modules
- Use `@Getter` on the module class to expose config/managers to other modules
- Access other modules via `getPlugin().getModule(OtherModule.class)`
- Register dependencies before dependent modules
