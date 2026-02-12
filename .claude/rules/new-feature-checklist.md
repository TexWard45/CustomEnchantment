# New Feature Checklist

## Before Writing Code

1. **Decide where it belongs**
   - Core plugin feature? → Create a new module
   - Utility function? → Add to existing utils package
   - See `module-design.md` for Module vs Utility decision

2. **Check if similar code exists**
   - Search for related classes in existing packages
   - Check if BafFramework provides the functionality
   - Reuse `ConditionHook`, `ExecuteHook` patterns where appropriate

3. **Plan the classes** using naming conventions from `java-conventions.md`

## Implementation Order

### Step 1: Data classes first
```java
@Getter @Setter
public class MyFeatureData { ... }
```

### Step 2: Config class
```java
@Getter @Configuration
public class MyFeatureConfig {
    @Path("my-feature.enabled")
    private boolean enabled = true;
}
```

### Step 3: Core logic (Manager or utility)
```java
public class MyFeatureManager {
    // Business logic here
}
```

### Step 4: Conditions / Executes / Requirements (if config-driven)
```java
public class MyFeatureCondition extends ConditionHook {
    @Override
    public String getIdentifier() { return "MY_FEATURE"; }

    @Override
    public boolean check(ConditionData data, String value) { ... }
}
```

### Step 5: Listener (if event-driven)
```java
public class MyFeatureListener implements Listener {
    public MyFeatureListener(YourPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEvent(SomeEvent e) { ... }
}
```

### Step 6: Module (ties everything together)
```java
public class MyFeatureModule extends PluginModule<YourPlugin> {
    @Override
    public void onEnable() {
        onReload();
        new MyFeatureListener(getPlugin());
        new MyFeatureCondition().register();
    }

    @Override
    public void onReload() {
        // Load config
    }
}
```

### Step 7: Register in plugin
```java
// In YourPlugin.registerModules()
registerModule(new MyFeatureModule(this));
```

## Testing

Write tests alongside implementation (TDD preferred):

1. **Data classes** → Test builders, getters, edge cases
2. **Config** → Test defaults, field values
3. **Conditions** → Test check() with matching/non-matching values
4. **Executes** → Test getIdentifier(), execute() where possible
5. **Manager logic** → Test business rules
6. **Listener** → Test with MockBukkit events (if feasible)

Use MockBukkit for anything touching Bukkit API. See `bukkit-testing.md`.

```bash
./gradlew test  # Run all tests
./gradlew :YourModule:test  # Run single module tests
```

## Before Submitting

- [ ] All new classes follow naming conventions
- [ ] Config uses `@Configuration` + `@Path` annotations
- [ ] Conditions/Executes registered in module's `onEnable()`
- [ ] Listeners self-register in constructor
- [ ] Tests written and passing
- [ ] No `printStackTrace()` — use plugin logger
- [ ] No hardcoded sensitive values in config defaults (see `security.md`)
