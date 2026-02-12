# CustomEnchantment Filtering System Reference

This document provides comprehensive reference for the filtering systems in the CustomEnchantment plugin, including material filters, entity filters, target filters, and item type filters.

## Table of Contents

- [Overview](#overview)
- [Material Filtering](#material-filtering)
- [Entity Type Filtering](#entity-type-filtering)
- [Target Filtering](#target-filtering)
- [CE Item Filtering](#ce-item-filtering)
- [Random Book Filtering](#random-book-filtering)
- [Configuration Examples](#configuration-examples)

---

## Overview

The filtering system provides several ways to restrict operations:

| Filter Type | Purpose |
|-------------|---------|
| MaterialList | Filter by Minecraft materials |
| EntityTypeList | Filter by entity types |
| TargetFilter | Filter by player targets (area effects) |
| CEWeaponFilter | Filter by CE item types |
| CERandomBookFilter | Filter enchantment book generation |

---

## Material Filtering

### MaterialData Class

Represents a material with optional block data:

```java
import com.bafmc.customenchantment.api.MaterialData;

// From ItemStack
MaterialData data = new MaterialData(itemStack);

// From Block (includes block data like crop age)
MaterialData blockData = new MaterialData(block);

// Direct construction
MaterialData diamond = new MaterialData(Material.DIAMOND_SWORD);
MaterialData wheat = new MaterialData(Material.WHEAT, 7); // Fully grown

// Parse from string
MaterialData parsed = MaterialData.getMaterialNMSByString("DIAMOND_SWORD");
MaterialData withData = MaterialData.getMaterialNMSByString("WHEAT 7");
```

### MaterialList Class

Collection of MaterialData for group filtering:

```java
import com.bafmc.customenchantment.api.MaterialList;

// Create material list
MaterialList swords = new MaterialList();
swords.add(MaterialData.getMaterialNMSByString("DIAMOND_SWORD"));
swords.add(MaterialData.getMaterialNMSByString("NETHERITE_SWORD"));
swords.add(MaterialData.getMaterialNMSByString("IRON_SWORD"));

// Define globally (prevents Material enum conflicts)
MaterialList.defineMaterialList("SWORDS", swords);

// Retrieve by name
MaterialList swordList = MaterialList.getMaterialList("SWORDS");

// Parse from config list
List<String> configList = Arrays.asList("DIAMOND_SWORD", "IRON_SWORD", "SWORDS");
MaterialList parsed = MaterialList.getMaterialList(configList);
// "SWORDS" will be expanded to the defined list

// Check if defined
ConcurrentHashMap<String, MaterialList> allLists = MaterialList.getMap();
```

### Material List Configuration

```yaml
# In enchantment config
conditions:
  - type: HOLD
    args: "MATERIAL_SWORDS"  # References defined material list

# Pre-defined material groups are loaded at startup
material-lists:
  SWORDS:
    - DIAMOND_SWORD
    - NETHERITE_SWORD
    - IRON_SWORD
    - GOLDEN_SWORD
    - STONE_SWORD
    - WOODEN_SWORD
  PICKAXES:
    - DIAMOND_PICKAXE
    - NETHERITE_PICKAXE
    - IRON_PICKAXE
    - GOLDEN_PICKAXE
    - STONE_PICKAXE
    - WOODEN_PICKAXE
```

---

## Entity Type Filtering

### EntityTypeList Class

Collection of EntityType for filtering targets:

```java
import com.bafmc.customenchantment.api.EntityTypeList;
import org.bukkit.entity.EntityType;

// Create entity type list
EntityTypeList hostiles = new EntityTypeList();
hostiles.add(EntityType.ZOMBIE);
hostiles.add(EntityType.SKELETON);
hostiles.add(EntityType.CREEPER);
hostiles.add(EntityType.SPIDER);

// Define globally
EntityTypeList.defineEntityTypeList("HOSTILE_MOBS", hostiles);

// Retrieve by name
EntityTypeList mobs = EntityTypeList.getEntityTypeList("HOSTILE_MOBS");

// Parse from config list
List<String> configList = Arrays.asList("ZOMBIE", "SKELETON", "HOSTILE_MOBS");
EntityTypeList parsed = EntityTypeList.getEntityTypeList(configList);
// "HOSTILE_MOBS" will be expanded

// Access all defined lists
ConcurrentHashMap<String, EntityTypeList> allLists = EntityTypeList.getMap();
```

### Entity Type Configuration

```yaml
# In enchantment config
conditions:
  - type: ENTITY_TYPE
    args: "UNDEAD"  # References defined entity list

# Pre-defined entity groups
entity-lists:
  UNDEAD:
    - ZOMBIE
    - SKELETON
    - WITHER_SKELETON
    - ZOMBIE_VILLAGER
    - DROWNED
    - HUSK
    - STRAY
    - PHANTOM
  ANIMALS:
    - COW
    - PIG
    - SHEEP
    - CHICKEN
    - HORSE
```

---

## Target Filtering

### TargetFilter Class

Filters players by distance and count for area effects:

```java
import com.bafmc.customenchantment.enchant.TargetFilter;
import com.bafmc.customenchantment.enchant.Target;

// Create target filter
TargetFilter filter = new TargetFilter(
    true,           // enable
    Target.PLAYER,  // center target
    true,           // except player
    true,           // except enemy
    5.0,            // min distance
    20.0,           // max distance
    1,              // min targets required
    10              // max targets
);

// Get filtered targets
List<Player> targets = filter.getTargetsByPlayer(player, enemy);
```

### TargetFilter Configuration

```yaml
# In enchantment effect config
effects:
  - type: ADD_POTION
    args: "SPEED:1:200"
    target: PLAYER
    target-filter:
      enable: true
      target: PLAYER        # Center point for distance
      except-player: true   # Exclude self
      except-enemy: true    # Exclude current enemy
      min-distance: 0
      max-distance: 15      # 15 block radius
      min-target: 1         # Need at least 1 target
      max-target: 5         # Affect up to 5 players
```

### Filter Parameters

| Parameter | Description |
|-----------|-------------|
| `enable` | Enable/disable the filter |
| `target` | Center point (PLAYER or ENEMY) |
| `except-player` | Exclude the item owner |
| `except-enemy` | Exclude the current enemy |
| `min-distance` | Minimum distance from center |
| `max-distance` | Maximum distance from center |
| `min-target` | Minimum targets required (0 = effect skipped) |
| `max-target` | Maximum targets affected |

---

## CE Item Filtering

### CEWeaponFilter

Filters ItemStacks by CE item type:

```java
import com.bafmc.customenchantment.filter.CEWeaponFilter;

// Filter checks if item matches CE type whitelist
public class CEWeaponFilter extends ItemStackFilter.ItemFilter {
    public boolean isMatch(int amount, ItemStack itemStack, ItemMeta itemMeta,
                          AdvancedConfigurationSection config) {
        List<String> ceTypeWhitelist = config.getStringList("ce-type-whitelist");
        if (ceTypeWhitelist.isEmpty()) {
            return true; // No filter
        }

        String type = CEAPI.getCEItemType(itemStack);
        if (type == null) {
            return false; // Not a CE item
        }

        return ceTypeWhitelist.contains(type);
    }
}
```

### Filter Registration

```java
import com.bafmc.customenchantment.filter.FilterRegister;
import com.bafmc.bukkit.feature.filter.ItemStackFilter;

// Register custom filter
ItemStackFilter.addFilter(new CEWeaponFilter());

// Filter registration happens in FilterModule
public class FilterModule extends PluginModule<CustomEnchantment> {
    public void onEnable() {
        FilterRegister.register();
    }
}
```

### CE Type Whitelist Configuration

```yaml
# In menu/item configuration
item-filter:
  ce-type-whitelist:
    - WEAPON
    - ARTIFACT
    - SIGIL
```

---

## Random Book Filtering

### CERandomBookFilter

Controls which enchantments can appear in random books:

```java
import com.bafmc.customenchantment.item.randombook.CERandomBookFilter;

CERandomBookFilter filter = new CERandomBookFilter();

// Parse filter configuration
List<String> filterConfig = Arrays.asList(
    "PUT_GROUP=legendary",      // Add all legendary enchants
    "PUT_GROUP=epic",           // Add all epic enchants
    "REMOVE_ENCHANT=forbidden", // Remove specific enchant
    "MIN_LEVEL=2",              // Minimum level 2
    "MAX_LEVEL=5",              // Maximum level 5
    "SUCCESS=75",               // 75% success rate
    "DESTROY=10"                // 10% destroy rate
);
filter.parse(filterConfig);

// Get random enchantment
CEEnchantSimple enchant = filter.getRandomEnchant();
```

### Filter Commands

| Command | Description |
|---------|-------------|
| `PUT_GROUP=<name>` | Add all enchantments from group |
| `REMOVE_GROUP=<name>` | Remove all enchantments from group |
| `ADD_ENCHANT=<name>` | Add specific enchantment |
| `REMOVE_ENCHANT=<name>` | Remove specific enchantment |
| `MIN_LEVEL=<n>` | Set minimum enchant level |
| `MAX_LEVEL=<n>` | Set maximum enchant level |
| `SUCCESS=<n>` | Override success rate |
| `DESTROY=<n>` | Override destroy rate |
| `LEVEL_SIGMA=<n>` | Gaussian distribution for levels |

### Random Book Configuration

```yaml
# In random book item config
random-book:
  filter:
    - "PUT_GROUP=legendary"
    - "PUT_GROUP=epic"
    - "PUT_GROUP=rare"
    - "REMOVE_ENCHANT=banned_enchant"
    - "MIN_LEVEL=1"
    - "MAX_LEVEL=3"
    - "SUCCESS=50"
    - "DESTROY=25"
    - "LEVEL_SIGMA=0.5"  # Higher levels less common
```

### Level Distribution

With `LEVEL_SIGMA`:
- `0` = Uniform distribution (equal chance)
- `0.5` = Lower levels more common
- `1.0` = Strong bias toward level 1

---

## Configuration Examples

### Mining Filter Configuration

```yaml
# Vein mining block filter
vein-mining:
  blocks:
    - DIAMOND_ORE
    - DEEPSLATE_DIAMOND_ORE
    - IRON_ORE
    - DEEPSLATE_IRON_ORE
    - COAL_ORE
    - DEEPSLATE_COAL_ORE
  max-blocks: 64
```

### Mob Bonus Filter Configuration

```yaml
# Mob drop bonus filter
mob-bonus:
  entities:
    - ZOMBIE
    - SKELETON
    - CREEPER
  drop-multiplier: 1.5
```

### Area Effect Filter Configuration

```yaml
# Heal nearby allies
effects:
  - type: HEALTH
    args: "+5"
    target-filter:
      enable: true
      target: PLAYER
      except-player: false  # Include self
      except-enemy: true    # Exclude enemies
      min-distance: 0
      max-distance: 10
      min-target: 1
      max-target: 10
```

### Enchantment Condition Filter

```yaml
# Enchantment with material condition
functions:
  damage:
    conditions:
      - type: HOLD
        args: "SWORDS"  # Only triggers with swords
      - type: ENTITY_TYPE
        args: "UNDEAD"  # Only against undead
    effects:
      - type: DEAL_DAMAGE
        args: "10"
```

---

## Creating Custom Filters

### Custom ItemStack Filter

```java
import com.bafmc.bukkit.feature.filter.ItemStackFilter;
import com.bafmc.bukkit.config.AdvancedConfigurationSection;

public class MyCustomFilter extends ItemStackFilter.ItemFilter {
    @Override
    public boolean isMatch(int amount, ItemStack itemStack, ItemMeta itemMeta,
                          AdvancedConfigurationSection config) {
        // Custom filter logic
        String required = config.getString("my-filter-key");
        if (required == null) {
            return true; // No filter configured
        }

        // Check item against filter
        return checkItem(itemStack, required);
    }

    private boolean checkItem(ItemStack item, String required) {
        // Implementation
        return true;
    }
}

// Register
ItemStackFilter.addFilter(new MyCustomFilter());
```

### Custom Player Filter

```java
public class MyPlayerFilter {
    private List<String> allowedWorlds;
    private double minHealth;
    private double maxHealth;

    public boolean matches(Player player) {
        // World check
        if (!allowedWorlds.isEmpty() &&
            !allowedWorlds.contains(player.getWorld().getName())) {
            return false;
        }

        // Health check
        double healthPercent = player.getHealth() / player.getMaxHealth() * 100;
        if (healthPercent < minHealth || healthPercent > maxHealth) {
            return false;
        }

        return true;
    }
}
```

---

## Best Practices

### Performance

1. **Cache filter results**: Avoid recalculating for same inputs
2. **Pre-compute lists**: Build MaterialList/EntityTypeList at startup
3. **Limit iterations**: Use max-target for area effects

### Configuration

1. **Use groups**: Define reusable material/entity groups
2. **Whitelist preferred**: Whitelist is often safer than blacklist
3. **Sensible defaults**: Empty filter = no restriction

### Error Handling

```java
// Always handle null/missing materials
Material material = EnumUtils.valueOf(Material.class, materialName);
if (material == null) {
    System.out.println("Cannot find Material." + materialName);
    return null;
}
```

---

## See Also

- [ARCHITECTURE.md](ARCHITECTURE.md) - System architecture
- [ENCHANTMENT-CONFIG.md](ENCHANTMENT-CONFIG.md) - Enchantment configuration
- [API.md](API.md) - Developer API reference
