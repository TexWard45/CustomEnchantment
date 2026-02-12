---
description: Scaffold a new BafFramework module with all boilerplate. Creates Data, Config, Manager, Listener, and Module classes following project conventions.
---

# Module Create

Scaffold a new BafFramework feature module following the established project patterns.

## What This Command Does

1. **Decide location** - Module vs ChildPlugin based on scope
2. **Generate classes** - Create all boilerplate files in correct order
3. **Follow conventions** - Use project naming patterns and Lombok annotations
4. **Register module** - Add registration to the parent plugin
5. **Create test stubs** - Generate test files for each class

## Usage

```
/module-create MyFeature                    # Create in Plugin-BafFramework
/module-create MyFeature --child-plugin     # Create as new child plugin
```

## Generated Structure (PluginModule)

```
Plugin-BafFramework/src/main/java/com/bafmc/bafframework/myfeature/
├── data/
│   └── MyFeatureData.java          # Data class with @Getter @Setter
├── config/
│   └── MyFeatureConfig.java        # Config with @Configuration @Path
├── manager/
│   └── MyFeatureManager.java       # Business logic
├── listener/
│   └── MyFeatureListener.java      # Event handlers
├── condition/
│   └── MyFeatureCondition.java     # ConditionHook (if needed)
├── execute/
│   └── MyFeatureExecute.java       # ExecuteHook (if needed)
└── MyFeatureModule.java            # Module with lifecycle
```

## Generated Structure (ChildPlugin)

```
Plugin-MyFeature/
├── build.gradle
└── src/main/java/com/bafmc/myfeature/
    ├── core/
    │   ├── config/
    │   │   └── MainConfig.java
    │   └── CoreModule.java
    ├── MyFeaturePlugin.java        # extends BafChildPlugin
    └── ...
```

## Class Templates

### Data Class
```java
@Getter
@Setter
public class MyFeatureData {
    private String name;
    private int value;
}
```

### Config Class
```java
@Getter
@Configuration
public class MyFeatureConfig {
    @Path("my-feature.enabled")
    private boolean enabled = true;
}
```

### Module Class
```java
public class MyFeatureModule extends PluginModule<BafFramework> {
    @Override
    public void onEnable() {
        onReload();
        new MyFeatureListener(getPlugin());
    }

    @Override
    public void onReload() {
        // Load config
    }
}
```

### Listener Class
```java
public class MyFeatureListener implements Listener {
    public MyFeatureListener(BafFramework plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
```

## Implementation Order

1. Data classes (plain objects)
2. Config class (@Configuration + @Path)
3. Manager (business logic)
4. Conditions/Executes (if config-driven)
5. Listener (if event-driven)
6. Module (ties everything together)
7. Register in parent plugin

## Test Generation

For each class created, generate a corresponding test:

```
Plugin-BafFramework/src/test/java/com/bafmc/bafframework/myfeature/
├── data/
│   └── MyFeatureDataTest.java
├── config/
│   └── MyFeatureConfigTest.java
└── MyFeatureModuleTest.java
```

## Workflow

1. Ask user for feature name and scope
2. Determine PluginModule vs ChildPlugin
3. Create package structure
4. Generate classes following templates
5. Register module in parent plugin
6. Generate test stubs
7. Run build to verify compilation

## Related

- See `new-feature-checklist.md` for full implementation guide
- See `module-design.md` for Module vs ChildPlugin decision
- See `java-conventions.md` for naming rules
- See `configuration-rules.md` for config patterns
