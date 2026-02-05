# CustomEnchantment Events Reference

This document provides comprehensive reference for the event system in the CustomEnchantment plugin, including custom events, Bukkit event handling, and enchantment trigger types.

## Table of Contents

- [Overview](#overview)
- [Custom Events](#custom-events)
- [Enchantment Trigger Types](#enchantment-trigger-types)
- [Bukkit Event Handling](#bukkit-event-handling)
- [Player Lifecycle Events](#player-lifecycle-events)
- [Creating Custom Events](#creating-custom-events)

---

## Overview

The event system integrates with Bukkit's event system and provides:

| Component | Purpose |
|-----------|---------|
| Custom Events | CE-specific events (stats modification) |
| CEType | Enchantment trigger types |
| Listeners | Handle Bukkit and custom events |
| ICEPlayerEvent | Player lifecycle callbacks |

---

## Custom Events

### CEPlayerStatsModifyEvent

Called when a player's stats are about to be modified.

```java
import com.bafmc.customenchantment.event.CEPlayerStatsModifyEvent;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.enchant.ModifyType;

public class CEPlayerStatsModifyEvent extends Event implements Cancellable {
    private CEPlayer cePlayer;
    private CustomAttributeType statsType;
    private ModifyType modifyType;
    private double currentValue;
    private double changeValue;
}
```

#### Event Properties

| Property | Description |
|----------|-------------|
| `cePlayer` | The affected player |
| `statsType` | Type of stat being modified |
| `modifyType` | How the stat changes (ADD, REMOVE, SET) |
| `currentValue` | Current stat value |
| `changeValue` | Amount to change |

#### Listening to Stats Events

```java
@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
public void onStatsChange(CEPlayerStatsModifyEvent e) {
    CEPlayer cePlayer = e.getCEPlayer();
    CustomAttributeType type = e.getStatsType();

    // Modify the change amount
    double newValue = calculateModifiedValue(e.getChangeValue());
    e.setValue(newValue);

    // Or cancel the event
    if (shouldPrevent(type)) {
        e.setCancelled(true);
    }
}
```

#### ModifyType Enum

| Type | Description | Final Value Calculation |
|------|-------------|------------------------|
| `ADD` | Add to current | `current + change` |
| `REMOVE` | Subtract from current | `current - change` |
| `SET` | Set absolute value | `change` |

#### Firing Stats Events

```java
import com.bafmc.customenchantment.event.CEPlayerStatsModifyEvent;
import com.bafmc.customenchantment.enchant.ModifyType;

// Fire event before modifying stats
CEPlayerStatsModifyEvent event = new CEPlayerStatsModifyEvent(
    cePlayer,
    CustomAttributeType.STAT_HEALTH,
    ModifyType.ADD,
    currentHealth,
    healAmount,
    false  // async = false for main thread
);

Bukkit.getPluginManager().callEvent(event);

if (!event.isCancelled()) {
    player.setHealth(Math.min(maxHealth, event.getFinalValue()));
}
```

---

## Enchantment Trigger Types

### CEType Enum

Defines when enchantments can trigger:

```java
import com.bafmc.customenchantment.enchant.CEType;

// Pre-defined trigger types
CEType.AUTO           // Every tick (automatic)
CEType.ATTACK         // When attacking
CEType.FINAL_ATTACK   // After attack damage calculated
CEType.DEFENSE        // When taking damage
CEType.UNKNOWN_DEFENSE // Taking damage from unknown source
CEType.HURT           // Any hurt event
CEType.ARROW_HIT      // Arrow hits target
CEType.ARROW_DEFENSE  // Defending against arrows
CEType.BOW_SHOOT      // Shooting a bow
CEType.SNEAK          // Toggle sneak
CEType.MINING         // Breaking blocks
CEType.HOLD           // Holding item
CEType.CHANGE_HAND    // Switching held item
CEType.HOTBAR_HOLD    // Item in hotbar
CEType.HOTBAR_CHANGE  // Changing hotbar selection
CEType.EXTRA_SLOT_EQUIP   // Equipping extra slot item
CEType.EXTRA_SLOT_UNEQUIP // Unequipping extra slot item
CEType.KILL_PLAYER    // Killing a player
CEType.DEATH          // On death
CEType.ARMOR_EQUIP    // Putting on armor
CEType.ARMOR_UNDRESS  // Taking off armor
CEType.QUIT           // Player disconnecting
CEType.JOIN           // Player joining
CEType.MOVE           // Player movement
CEType.STATS_CHANGE   // Stats modification
CEType.ITEM_CONSUME   // Consuming items (food, potions)
```

### Registering Custom Trigger Types

```java
// Create and register new trigger type
CEType MY_CUSTOM_TRIGGER = new CEType("MY_CUSTOM_TRIGGER").register();

// Use in enchantment caller
CECallerBuilder
    .build(player)
    .setCEType(MY_CUSTOM_TRIGGER)
    .call();
```

### Calling Enchantments

```java
import com.bafmc.customenchantment.enchant.CECallerBuilder;

// Basic call
CECallerBuilder
    .build(player)
    .setCEType(CEType.ATTACK)
    .call();

// With weapon map
CECallerBuilder
    .build(player)
    .setCEType(CEType.ARMOR_EQUIP)
    .setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.ARMOR_ARRAY))
    .call();

// With enemy
CECallerBuilder
    .build(player)
    .setEnemy(enemy)
    .setCEType(CEType.ATTACK)
    .call();
```

---

## Bukkit Event Handling

### ListenerModule

Registers all event listeners:

```java
public class ListenerModule extends PluginModule<CustomEnchantment> {
    public void onEnable() {
        new PlayerListener(getPlugin());
        new InventoryListener(getPlugin());
        new EntityListener(getPlugin());
        new BlockListener(getPlugin());
        new CEProtectDeadListener(getPlugin());
        new GuardListener(getPlugin());
        new BannerListener(getPlugin());
        new OutfitListener(getPlugin());
        new StaffMechanicListener(getPlugin());

        // Conditional listeners
        if (Bukkit.getPluginManager().isPluginEnabled("StackMob")) {
            new MobStackDeathListener(getPlugin());
        } else {
            new MobDeathListener(getPlugin());
        }
    }
}
```

### Available Listeners

| Listener | Purpose |
|----------|---------|
| `PlayerListener` | Player join/quit, movement, actions |
| `InventoryListener` | Inventory interactions |
| `EntityListener` | Combat, damage, entity events |
| `BlockListener` | Block breaking, mining |
| `CEProtectDeadListener` | Death protection items |
| `GuardListener` | Summoned guard entities |
| `BannerListener` | Banner display items |
| `OutfitListener` | Outfit item events |
| `StaffMechanicListener` | Staff weapon mechanics |
| `MobDeathListener` | Mob death and drops |
| `CMenuListener` | Custom menu events |
| `McMMOListener` | McMMO integration |
| `CustomFarmListener` | Custom farming integration |

### Example Listener Implementation

```java
public class PlayerListener implements Listener {
    private CustomEnchantment plugin;

    public PlayerListener(CustomEnchantment plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        cePlayer.onJoin();

        // Trigger armor equip enchantments
        CECallerBuilder
            .build(player)
            .setCEType(CEType.ARMOR_EQUIP)
            .setWeaponMap(CEAPI.getCEWeaponMap(player, EquipSlot.ARMOR_ARRAY))
            .call();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStatsChange(CEPlayerStatsModifyEvent e) {
        // Modify stats based on player attributes
        CEPlayer cePlayer = e.getCEPlayer();
        PlayerCustomAttribute attribute = cePlayer.getCustomAttribute();
        CustomAttributeType type = e.getStatsType();

        double changeValue = e.getChangeValue();
        double newChangeValue = attribute.getValue(type, changeValue);
        e.setValue(newChangeValue);
    }
}
```

---

## Player Lifecycle Events

### ICEPlayerEvent Interface

Callback interface for player lifecycle:

```java
public interface ICEPlayerEvent {
    void onJoin();
    void onQuit();
}
```

### CEPlayerExpansion Implementation

All player expansions implement lifecycle callbacks:

```java
public abstract class CEPlayerExpansion implements ICEPlayerEvent {
    protected CEPlayer cePlayer;
    protected Player player;

    public CEPlayerExpansion(CEPlayer cePlayer) {
        this.cePlayer = cePlayer;
        this.player = cePlayer.getPlayer();
    }

    @Override
    public abstract void onJoin();

    @Override
    public abstract void onQuit();
}
```

### Example Player Expansion

```java
public class PlayerStorage extends CEPlayerExpansion {
    private AdvancedFileConfiguration config;

    public PlayerStorage(CEPlayer cePlayer) {
        super(cePlayer);
    }

    @Override
    public void onJoin() {
        // Load player data
        File file = getPlayerDataFile();
        FileUtils.createFile(file);
        this.config = new AdvancedFileConfiguration(file);
    }

    @Override
    public void onQuit() {
        // Save player data
        this.config.save();
    }
}
```

---

## Creating Custom Events

### Event Class Structure

```java
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;

public class MyCustomEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player player;
    private double value;

    public MyCustomEvent(Player player, double value) {
        super(false); // false = sync, true = async
        this.player = player;
        this.value = value;
    }

    // Getters and setters
    public Player getPlayer() { return player; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    // Required for Bukkit event system
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    // Cancellable implementation
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
```

### Firing Custom Events

```java
// Create and fire event
MyCustomEvent event = new MyCustomEvent(player, 100.0);
Bukkit.getPluginManager().callEvent(event);

// Check result
if (!event.isCancelled()) {
    // Apply the potentially modified value
    applyValue(event.getValue());
}
```

### Listening to Custom Events

```java
@EventHandler(priority = EventPriority.NORMAL)
public void onMyCustomEvent(MyCustomEvent event) {
    Player player = event.getPlayer();

    // Modify value
    event.setValue(event.getValue() * 1.5);

    // Or cancel
    if (shouldCancel(player)) {
        event.setCancelled(true);
    }
}
```

---

## Event Priority

### Bukkit Event Priorities

| Priority | Use Case |
|----------|----------|
| `LOWEST` | First to run, for setup |
| `LOW` | Early processing |
| `NORMAL` | Default, most handlers |
| `HIGH` | After normal processing |
| `HIGHEST` | Near-final processing |
| `MONITOR` | Read-only, final state observation |

### Priority Guidelines

```java
// Use MONITOR for logging/observation (don't modify)
@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
public void onEvent(Event e) {
    // Only observe, don't modify
    logEvent(e);
}

// Use NORMAL for standard processing
@EventHandler(priority = EventPriority.NORMAL)
public void onEvent(Event e) {
    // Normal modifications
    processEvent(e);
}

// Use HIGHEST for final overrides
@EventHandler(priority = EventPriority.HIGHEST)
public void onEvent(Event e) {
    // Final modifications before MONITOR
    finalizeEvent(e);
}
```

---

## Best Practices

### Event Handling

1. **Use `ignoreCancelled = true`** for MONITOR handlers
2. **Check event state** before processing
3. **Don't modify in MONITOR** priority
4. **Handle exceptions** gracefully

```java
@EventHandler(priority = EventPriority.NORMAL)
public void onPlayerAction(PlayerInteractEvent e) {
    try {
        if (e.isCancelled()) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR) return;

        // Process event
        handleAction(e.getPlayer());
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}
```

### Performance

1. **Early return** if conditions aren't met
2. **Cache frequently accessed data**
3. **Use async events** for non-Bukkit operations
4. **Minimize event handler work**

### Thread Safety

```java
// Async event - be careful with Bukkit API
public class AsyncEvent extends Event {
    public AsyncEvent() {
        super(true); // async = true
    }
}

// When handling async events that need Bukkit API:
@EventHandler
public void onAsyncEvent(AsyncEvent e) {
    // Schedule Bukkit API calls on main thread
    Bukkit.getScheduler().runTask(plugin, () -> {
        player.sendMessage("Hello!");
    });
}
```

---

## See Also

- [ARCHITECTURE.md](ARCHITECTURE.md) - System architecture
- [API.md](API.md) - Developer API reference
- [TASKS.md](TASKS.md) - Background task system
- [ENCHANTMENT-CONFIG.md](ENCHANTMENT-CONFIG.md) - Enchantment configuration
