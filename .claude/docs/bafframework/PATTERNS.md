# Design Patterns

BafFramework provides reusable pattern implementations: Strategy, Singleton, and Builder.

---

## Strategy Pattern

Parse config strings like `"TYPE:arg1:arg2"` into typed behavior objects.

### AbstractStrategy / AbstractLineStrategy

Package: `com.bafmc.bukkit.template.strategy.AbstractStrategy`, `com.bafmc.bukkit.template.strategy.AbstractLineStrategy`

```java
import com.bafmc.bukkit.template.strategy.AbstractLineStrategy;
import com.bafmc.bukkit.feature.argument.ArgumentLine;

public class FireReward extends AbstractLineStrategy {
    private int duration;

    @Override
    public String getType() {
        return "FIRE";
    }

    @Override
    public void setup(ArgumentLine args) {
        // Input "FIRE:100" -> args starts at index 1, so args[0] = "100"
        this.duration = args.getInt(0);
    }
}
```

- `AbstractStrategy` -- base class with `getType()`
- `AbstractLineStrategy` -- extends `AbstractStrategy`, adds `setup(String)` and `setup(ArgumentLine)` for colon-delimited parsing

When `setup(String line)` is called, the line is split on `:` and the type prefix is skipped (starts at index 1).

### StrategyRegister / StrategyLineRegister

Package: `com.bafmc.bukkit.template.strategy.StrategyRegister`, `com.bafmc.bukkit.template.strategy.StrategyLineRegister`

```java
import com.bafmc.bukkit.template.strategy.StrategyLineRegister;

public class RewardRegister extends StrategyLineRegister<AbstractReward> {
    private static RewardRegister instance;

    public static RewardRegister instance() {
        if (instance == null) instance = new RewardRegister();
        return instance;
    }

    public static void cleanup() {
        instance = null;  // Prevent memory leaks on disable
    }
}
```

### Registering and Instantiating

```java
// Register strategy classes
RewardRegister.instance().registerStrategy(FireReward.class);
RewardRegister.instance().registerStrategy(MoneyReward.class);

// Create instance from config string
AbstractReward reward = RewardRegister.instance().instanceStrategy("FIRE:100");
// Returns a new FireReward with duration=100
```

### Key Methods on StrategyRegister

| Method | Description |
|--------|-------------|
| `registerStrategy(Class)` | Register a strategy class by its type |
| `instanceStrategy(String)` | Create a new instance (StrategyLineRegister parses the line) |
| `unregisterStrategy(String)` | Remove a strategy type |
| `getStrategy(String)` | Get the registered class for a type |
| `getInstance(String)` | Get the cached prototype instance |
| `getMap()` | Get a copy of all registered types |

### ArgumentLine Reminder

`ArgumentLine` splits on `:`, NOT spaces. See [FEATURE_SYSTEM.md](FEATURE_SYSTEM.md) for the full API.

```
"MONEY:1000:true" -> ["MONEY", "1000", "true"]
ArgumentLine(line, 1) skips index 0 -> args[0]="1000", args[1]="true"
```

---

## Singleton Pattern

Named instance registries for objects identified by a string key.

### AbstractSingleton / SingletonRegister

Package: `com.bafmc.bukkit.template.singleton.AbstractSingleton`, `com.bafmc.bukkit.template.singleton.SingletonRegister`

```java
import com.bafmc.bukkit.template.singleton.AbstractSingleton;

public class MyMenu extends AbstractSingleton {
    @Override
    public String getType() {
        return "shop-menu";
    }
}
```

```java
import com.bafmc.bukkit.template.singleton.SingletonRegister;

public class MenuRegister extends SingletonRegister<MyMenu> {
    private static MenuRegister instance;

    public static MenuRegister instance() {
        if (instance == null) instance = new MenuRegister();
        return instance;
    }

    public static void cleanup() {
        instance = null;  // IMPORTANT: null in onDisable()
    }
}
```

### Key Methods on SingletonRegister

| Method | Description |
|--------|-------------|
| `registerSingleton(T)` | Register an instance by its type name |
| `unregisterSingleton(String)` | Remove by type name |
| `getSingleton(String)` | Get instance by type name (or null) |
| `getSingletonList()` | Get all registered instances |

### Usage

```java
// Register
MenuRegister.instance().registerSingleton(new ShopMenu());

// Retrieve
MyMenu menu = MenuRegister.instance().getSingleton("shop-menu");
```

### Singleton Cleanup

All singleton registers must null their static instance in `onDisable()` to prevent memory leaks on plugin reload:

```java
@Override
public void onDisable() {
    MenuRegister.cleanup();
}
```

---

## Builder Pattern

Fluent object construction used throughout the framework.

### ItemStackBuilder

Package: `com.bafmc.bukkit.utils.ItemStackBuilder`

```java
import com.bafmc.bukkit.utils.ItemStackBuilder;
import org.bukkit.Material;

ItemStack item = ItemStackBuilder.create()
    .setMaterial(Material.DIAMOND_SWORD)
    .setDisplay("&6Legendary Sword")
    .setLore(List.of("&7A powerful weapon", "&7Damage: &c50"))
    .setAmount(1)
    .setGlowing(true)
    .setUnbreakable(true)
    .setCustomModelData("1001")
    .getItemStack();

// With player placeholders
ItemStack playerItem = ItemStackBuilder.create()
    .setMaterial(Material.PLAYER_HEAD)
    .setDisplay("&b%player_name%'s Head")
    .setSkullOwner("%player_name%")
    .getItemStack(player);

// With custom placeholders
Placeholder placeholder = PlaceholderBuilder.builder()
    .put("{price}", "1000")
    .build();
ItemStack priced = ItemStackBuilder.create()
    .setMaterial(Material.EMERALD)
    .setDisplay("&aShop Item")
    .setLore(List.of("&7Price: &6{price}"))
    .getItemStack(player, placeholder);
```

### PlaceholderBuilder

Package: `com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder`

```java
import com.bafmc.bukkit.feature.placeholder.Placeholder;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;

Placeholder placeholder = PlaceholderBuilder.builder()
    .put("{player}", player.getName())
    .put("{amount}", 100)
    .put("{items}", List.of("Sword", "Shield"))  // Joins list
    .build();

String result = placeholder.apply("Hello {player}, you have {amount} coins");
List<String> lore = placeholder.apply(loreLines);
```

### AdvancedCommandBuilder

See [COMMAND_SYSTEM.md](COMMAND_SYSTEM.md) for the full builder API.

---

## Pattern Selection Guide

| Scenario | Pattern | Example |
|----------|---------|---------|
| Config string `"TYPE:args"` mapped to behavior | Strategy | Requirements, rewards |
| Named instances looked up by string key | Singleton | Menus, message channels |
| Fluent multi-step object construction | Builder | Items, commands, placeholders |
| Check-then-act on player state | Condition/Execute hooks | Permissions, money checks |
