# BafFramework Quick Start

Get a Bukkit plugin running on BafFramework in under 10 minutes.

## Gradle Setup

### build.gradle

```gradle
plugins {
    id 'java'
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = 'https://repo.papermc.io/repository/maven-public/' }
}

dependencies {
    // BafFramework API (not bundled in your JAR)
    compileOnly 'com.bafmc:BafFramework:1.0.0'

    // Alternative: local file dependency
    // compileOnly files('../libs/BafFramework.jar')

    // Paper API (provided at runtime by server)
    compileOnly 'io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT'

    // Lombok (optional but recommended)
    compileOnly 'org.projectlombok:lombok:1.18.34'
    annotationProcessor 'org.projectlombok:lombok:1.18.34'
}
```

## Main Plugin Class

Create your plugin class extending `BafPlugin`.

Package: `com.bafmc.bukkit.BafPlugin`

```java
import com.bafmc.bukkit.BafPlugin;
import lombok.Getter;

public class MyPlugin extends BafPlugin {
    @Getter
    private static MyPlugin instance;

    @Override
    public void registerModules() {
        instance = this;
        registerModule(new CoreModule(this));
    }

    // onEnable(), onReload(), onSave(), onDisable() are handled
    // by BafPlugin — modules receive lifecycle callbacks automatically.
}
```

`BafPlugin` extends `JavaPlugin` and manages the module lifecycle. You do not need to override `onEnable()`/`onDisable()` unless you have plugin-level logic outside of modules.

## First Module

Package: `com.bafmc.bukkit.module.PluginModule`

```java
import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.bukkit.utils.ConfigUtils;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import lombok.Getter;

@Getter
public class CoreModule extends PluginModule<MyPlugin> {
    private MainConfig mainConfig;

    public CoreModule(MyPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        onReload();
        // Register listeners, commands, conditions, executes here
    }

    @Override
    public void onReload() {
        AdvancedFileConfiguration config = ConfigUtils.setupResource(
            getPlugin(), "/config.yml",
            new java.io.File(getPlugin().getDataFolder(), "config.yml")
        );
        this.mainConfig = config.get(MainConfig.class);
    }

    @Override
    public void onSave() {
        // Persist data to files/database
    }

    @Override
    public void onDisable() {
        // Cancel tasks, close connections, clean up
    }
}
```

## Configuration Class

Package: `com.bafmc.bukkit.config.annotation.Configuration`, `com.bafmc.bukkit.config.annotation.Path`

```java
import com.bafmc.bukkit.config.annotation.Configuration;
import com.bafmc.bukkit.config.annotation.Path;
import lombok.Getter;

@Getter
@Configuration
public class MainConfig {
    @Path("plugin.prefix")
    private String prefix = "&7[&bMyPlugin&7] ";

    @Path("plugin.debug")
    private boolean debug = false;
}
```

See [CONFIG_SYSTEM.md](CONFIG_SYSTEM.md) for the full annotation reference.

## plugin.yml

```yaml
name: MyPlugin
version: '1.0.0'
main: com.example.myplugin.MyPlugin
api-version: '1.21'
depend: [BafFramework]
commands:
  myplugin:
    description: Main plugin command
```

The `depend: [BafFramework]` entry ensures BafFramework loads first and its API is available.

## Build and Deploy

```bash
./gradlew build
```

Copy the output JAR from `build/libs/` into your server's `plugins/` directory alongside the BafFramework JAR. Start the server.

## Module Lifecycle Order

```
Server Start:   registerModules() -> onEnable() -> onReload()
Config Reload:  onReload()
Server Save:    onSave()
Server Stop:    onSave() -> onDisable()
```

Modules are called in registration order. Register dependencies first.

## Accessing Modules

From anywhere with a plugin reference:

```java
CoreModule core = MyPlugin.getInstance().getModule(CoreModule.class);
String prefix = core.getMainConfig().getPrefix();
```

## Next Steps

- [MODULE_SYSTEM.md](MODULE_SYSTEM.md) -- module and child plugin lifecycle
- [CONFIG_SYSTEM.md](CONFIG_SYSTEM.md) -- annotation-based configuration
- [COMMAND_SYSTEM.md](COMMAND_SYSTEM.md) -- building command trees
- [FEATURE_SYSTEM.md](FEATURE_SYSTEM.md) -- conditions, executes, requirements
- [PATTERNS.md](PATTERNS.md) -- strategy, singleton, builder patterns
- [UTILITIES.md](UTILITIES.md) -- utility class reference
