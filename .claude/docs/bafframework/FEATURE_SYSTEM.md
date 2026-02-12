# Feature System

BafFramework provides three config-driven extension points: **Conditions**, **Executes**, and **Requirements**.

---

## Conditions

Conditions check whether a player meets criteria defined in YAML config.

### ConditionHook

Package: `com.bafmc.bukkit.feature.condition.ConditionHook`

Extend this class to create a custom condition.

```java
import com.bafmc.bukkit.feature.condition.ConditionHook;
import com.bafmc.bukkit.feature.condition.ConditionData;

public class VipCondition extends ConditionHook {
    @Override
    public String getIdentifier() {
        return "VIP";
    }

    // getIdentifier() is deprecated -- override getIdentifier() instead
    @Override
    public String getIdentifier() {
        return getIdentifier();
    }

    @Override
    public boolean check(ConditionData data, String value) {
        Player player = data.getPlayer();
        if (player == null) return false;
        return player.hasPermission("vip." + value);
    }
}
```

### Register / Unregister

```java
import com.bafmc.bukkit.feature.condition.Condition;

// In module onEnable()
VipCondition vipCondition = new VipCondition();
vipCondition.register();

// In module onDisable()
Condition.unregister(vipCondition);
```

### ConditionData

Package: `com.bafmc.bukkit.feature.condition.ConditionData`

A key-value context map passed to conditions. Keys are case-insensitive (lowercased).

```java
import com.bafmc.bukkit.feature.condition.ConditionData;

ConditionData data = new ConditionData(player);
data.put("menu", "shop");
data.put("slot", 5);

Player p = data.getPlayer();
Object val = data.getValue("menu");
boolean has = data.isSet("slot");
```

### Config Syntax

In YAML, conditions use `"TYPE value"` format. Multiple conditions in a list use AND logic (all must pass). Within a single line, `" | "` separates OR alternatives.

```yaml
conditions:
  - "PERMISSION vip.basic"              # Must have permission
  - "VIP gold | VIP diamond"            # Must be gold OR diamond VIP
  - "MONEY 1000"                        # Must have 1000+ money
```

Lines are AND (all must pass). Entries within a line separated by ` | ` are OR (any must pass).

### Parsing and Checking Conditions

```java
import com.bafmc.bukkit.feature.condition.Condition;

List<String> conditionLines = config.getStringList("conditions");
Condition condition = Condition.parse(conditionLines);

boolean passes = condition.check(player);
// Or with custom data:
boolean passes2 = condition.check(new ConditionData(player).put("menu", "shop"));
```

### Built-in Conditions

BafFramework registers these conditions by default:

| Identifier | Description | Example |
|-----------|-------------|---------|
| `PERMISSION` | Player has permission | `PERMISSION vip.basic` |
| `MONEY` | Player has enough money (Vault) | `MONEY 1000` |
| `LEVEL` | Player level check | `LEVEL 30` |
| `XP` | Player experience check | `XP 500` |
| `PLAYER_POINT` | PlayerPoints balance | `PLAYER_POINT 100` |
| `NUMBER_COMPARE` | Numeric comparison | `NUMBER_COMPARE value>=10` |
| `CLICK_TYPE` | Inventory click type | `CLICK_TYPE LEFT` |
| `PLACEHOLDERAPI` | PlaceholderAPI expression | `PLACEHOLDERAPI %player_health%>=10` |

---

## Executes

Executes perform actions triggered by YAML config.

### ExecuteHook

Package: `com.bafmc.bukkit.feature.execute.ExecuteHook`

```java
import com.bafmc.bukkit.feature.execute.ExecuteHook;
import org.bukkit.entity.Player;

public class AnnounceExecute extends ExecuteHook {
    @Override
    public String getIdentifier() {
        return "ANNOUNCE";
    }

    @Override
    public String getIdentifier() {
        return getIdentifier();
    }

    @Override
    public void execute(Player player, String value) {
        Bukkit.broadcastMessage(ColorUtils.t(value));
    }

    // Optional: register aliases
    @Override
    public String[] getIds() {
        return new String[] { "ANNOUNCE", "BROADCAST_ALL" };
    }
}
```

### Register / Unregister

```java
import com.bafmc.bukkit.feature.execute.Execute;

// In module onEnable()
AnnounceExecute hook = new AnnounceExecute();
hook.register();

// In module onDisable()
Execute.unregister(hook);
```

### Config Syntax

```yaml
executes:
  - "CONSOLE_COMMAND give %player_name% diamond 1"
  - "PLAYER_MESSAGE &aYou received a diamond!"
  - "PLAY_SOUND ENTITY_PLAYER_LEVELUP"
  - "ANNOUNCE &6Server event starting!"
```

Each line: `"IDENTIFIER value"`. The value supports PlaceholderAPI placeholders.

### Parsing and Running Executes

```java
import com.bafmc.bukkit.feature.execute.Execute;
import com.bafmc.bukkit.feature.placeholder.Placeholder;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;

List<String> executeLines = config.getStringList("executes");
Execute execute = Execute.parse(executeLines);

// Run for a player
execute.execute(player);

// Run with custom placeholders
Placeholder placeholder = PlaceholderBuilder.builder()
    .put("{item}", "Diamond Sword")
    .put("{amount}", "1")
    .build();
execute.execute(player, placeholder);

// Run without a player (console-only actions)
execute.execute();
```

### Built-in Executes

| Identifier | Description | Example |
|-----------|-------------|---------|
| `CONSOLE_COMMAND` | Run as console | `CONSOLE_COMMAND give %player_name% diamond 1` |
| `PLAYER_COMMAND` | Run as player | `PLAYER_COMMAND spawn` |
| `PLAYER_MESSAGE` | Send message to player | `PLAYER_MESSAGE &aWelcome!` |
| `BROADCAST` | Broadcast to all | `BROADCAST &6Server message` |
| `GIVE_MONEY` | Give money (Vault) | `GIVE_MONEY 1000` |
| `TAKE_MONEY` | Take money (Vault) | `TAKE_MONEY 500` |
| `SET_MONEY` | Set money (Vault) | `SET_MONEY 0` |
| `PLAY_SOUND` | Play sound | `PLAY_SOUND ENTITY_PLAYER_LEVELUP` |
| `CLOSE` | Close inventory | `CLOSE` |
| `GIVE_EXP` | Give experience | `GIVE_EXP 100` |
| `TAKE_EXP` | Take experience | `TAKE_EXP 50` |

---

## Placeholders

Package: `com.bafmc.bukkit.feature.placeholder.Placeholder`, `com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder`

Custom placeholders for executes and other text processing.

```java
import com.bafmc.bukkit.feature.placeholder.Placeholder;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;

Placeholder placeholder = PlaceholderBuilder.builder()
    .put("{player}", player.getName())
    .put("{amount}", 100)
    .put("{item}", "Diamond")
    .build();

String result = placeholder.apply("&a{player} received {amount}x {item}");
List<String> results = placeholder.apply(loreList);
```

PlaceholderAPI (`%placeholder%`) is also supported automatically in conditions and executes.

---

## Requirements

Requirements combine a check-and-pay pattern: first verify the player meets all requirements, then deduct the cost.

### AbstractRequirement

Package: `com.bafmc.bukkit.feature.requirement.AbstractRequirement`

```java
import com.bafmc.bukkit.feature.requirement.AbstractRequirement;
import com.bafmc.bukkit.feature.requirement.IRequirementPayment;
import com.bafmc.bukkit.feature.requirement.RequirementSession;
import com.bafmc.bukkit.feature.argument.ArgumentLine;
import org.bukkit.entity.Player;

public class TokenRequirement extends AbstractRequirement implements IRequirementPayment {
    private int amount;

    @Override
    public String getType() {
        return "TOKEN";
    }

    @Override
    public void setup(ArgumentLine args) {
        // Input: "TOKEN:100" -> args splits on ':', index 0 = amount
        this.amount = args.getInt(0);
    }

    @Override
    public boolean check(Player player, RequirementSession session) {
        return getTokens(player) >= amount;
    }

    @Override
    public void pay(Player player, RequirementSession session) {
        removeTokens(player, amount);
    }
}
```

### ArgumentLine

Package: `com.bafmc.bukkit.feature.argument.ArgumentLine`

Splits strings on the `:` delimiter (NOT spaces).

```java
import com.bafmc.bukkit.feature.argument.ArgumentLine;

// "TOKEN:100:true" splits to ["TOKEN", "100", "true"]
ArgumentLine args = new ArgumentLine("TOKEN:100:true", 1); // skip index 0 (type)
int amount = args.getInt(0);     // 100
boolean flag = args.getBoolean(1); // true
String raw = args.getString(0);   // "100"
```

| Method | Returns |
|--------|---------|
| `getInt(index)` | `int` (0 if out of bounds) |
| `getDouble(index)` | `double` (0 if out of bounds) |
| `getBoolean(index)` | `boolean` (false if out of bounds) |
| `getString(index)` | `String` (null if out of bounds) |
| `getStringToEnd(index)` | Remaining args joined with `:` |
| `hasArg(index)` | `boolean` |
| `getLength()` | Total arg count |

### Registering Requirements

```java
import com.bafmc.bukkit.feature.requirement.RequirementRegister;

// In module onEnable()
RequirementRegister.instance().registerRequirement(TokenRequirement.class);
```

### RequirementManager

Package: `com.bafmc.bukkit.feature.requirement.RequirementManager`

Two-phase flow: check all requirements, then pay all.

```java
import com.bafmc.bukkit.feature.requirement.RequirementManager;
import com.bafmc.bukkit.feature.requirement.RequirementList;

// Parse from config lines
List<String> lines = List.of("MONEY:1000", "TOKEN:50", "PERMISSION:shop.buy");
RequirementList requirements = RequirementManager.instance()
    .createRequirementList(plugin, lines);

// Phase 1: Check all
boolean canAfford = RequirementManager.instance().checkRequirements(player, requirements);

// Phase 2: Pay all (only if check passed)
if (canAfford) {
    RequirementManager.instance().payRequirements(player, requirements);
}

// Or get the first failed requirement for error messages
AbstractRequirement failed = RequirementManager.instance()
    .getFailedRequirement(player, requirements);
if (failed != null) {
    player.sendMessage("Missing requirement: " + failed.getType());
}
```

### Config Syntax

Requirements use `TYPE:value` format (colon-delimited, not spaces):

```yaml
requirements:
  - "MONEY:1000"
  - "EXP:500"
  - "PERMISSION:shop.vip"
  - "!PERMISSION:shop.banned"   # Negated with ! prefix
```

### Negation

Prefix with `!` to negate a requirement:

```yaml
- "!PERMISSION:shop.banned"   # Must NOT have this permission
```

### Built-in Requirements

| Type | Description | Example |
|------|-------------|---------|
| `MONEY` | Vault money | `MONEY:1000` |
| `POINT` | PlayerPoints | `POINT:50` |
| `EXP` | Experience points | `EXP:500` |
| `ITEM` | Item in inventory | `ITEM:DIAMOND:64` |
| `PERMISSION` | Permission check (no cost) | `PERMISSION:shop.buy` |

### IRequirementPayment

Package: `com.bafmc.bukkit.feature.requirement.IRequirementPayment`

Implement this interface on requirements that have a cost to deduct:

```java
public interface IRequirementPayment {
    void pay(Player player, RequirementSession session);
}
```

Requirements without `IRequirementPayment` (like `PERMISSION`) are check-only -- they are verified but nothing is deducted.

### RequirementSession

Package: `com.bafmc.bukkit.feature.requirement.RequirementSession`

A `LinkedHashMap<String, Object>` shared across all requirements in a single check/pay cycle. Use it to pass data between requirements (e.g., cache computed values).
