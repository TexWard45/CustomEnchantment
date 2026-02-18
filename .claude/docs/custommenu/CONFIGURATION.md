# Configuration

CustomMenu uses YAML configuration files for menus, messages, and plugin settings.

## File Structure

```
plugins/BafFramework/CustomMenu/
├── config.yml       # Main configuration
├── message.yml      # Localized messages
└── menu/            # Menu definitions
    ├── example.yml
    ├── shop.yml
    └── subfolder/
        └── nested-menu.yml
```

## FileStorage

Manages file paths for CustomMenu:

```java
public class FileStorage extends ChildModule<CustomMenu> {
    public File getConfigFile();   // config.yml
    public File getMessageFile();  // message.yml
    public File getMenuFolder();   // menu/ folder
}

// Usage
FileStorage storage = FileStorage.instance();
File configFile = storage.getConfigFile();
```

## MainConfig

Main plugin configuration (currently empty, extensible):

```java
@Configuration
@Getter
public class MainConfig {
    // Add configuration fields as needed
}
```

```yaml
# config.yml
# Future configuration options
```

## Message Configuration

Messages are loaded into a `MessageConfig` for localized, formatted output.

### message.yml

```yaml
command:
  player-not-found: '&c[!] Player {player} not found!'
  plugin-not-found: '&c[!] Plugin {plugin} not found!'
  menu-not-found: '&c[!] Menu {menu} not found!'
  help:
    - "&6======== &eCustomMenu Commands &6========"
    - "&f/custommenu reload &e- Reload configuration"
  reload: '&a[!] Configuration reloaded!'
```

### Sending Messages

```java
ConfigModule.instance().getMessageConfig()
    .sendMessage(sender, "command.player-not-found",
        PlaceholderBuilder.builder()
            .put("{player}", playerName)
            .build()
    );
```

## Menu Configuration

### Basic Menu Structure

```yaml
# menu/example.yml
type: 'default'              # Menu type (default, example-page, custom)
title: '&8&lMenu Title'      # Title with color codes
row: 3                       # Rows (1-6, determines size)
data:                        # Custom data section (optional)
  custom-key: value
items:                       # Item definitions
  item-id:
    # Item configuration
```

### Menu Types

| Type | Description |
|------|-------------|
| `default` | Basic menu with DefaultItem support |
| `example-page` | Paginated menu with page navigation |
| Custom | Your registered menu types |

### Title

Supports:
- Color codes: `&a`, `&b`, `&c`, etc.
- Hex colors: `&#FF5555`
- Placeholders: `{page}` (for page menus), PlaceholderAPI

```yaml
title: '&8&l{category} Shop - Page {page}'
```

### Rows

```yaml
row: 1    # 9 slots
row: 2    # 18 slots
row: 3    # 27 slots
row: 4    # 36 slots
row: 5    # 45 slots
row: 6    # 54 slots (max)
```

When using `layout:`, `row` is auto-derived from the number of layout lines if omitted or smaller.

### Layout (Character Grid)

Optional visual layout that maps characters to slot numbers. Parsed once during YAML loading — zero runtime overhead after loading.

```yaml
layout:
  - 'ooooooooo'
  - 'o.......o'
  - 'o.......o'
  - 'o.......o'
  - 'o.......o'
  - 'boo.c.oon'

items:
  border:
    slot: 'o'        # resolves to [0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,46,47,48,49,50,51,52,53]
  content:
    slot: '.'        # resolves to [10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34]
  back:
    slot: 'b'        # resolves to [45]
  next:
    slot: 'n'        # resolves to [53]
  confirm:
    slot: 'c'        # resolves to [49]
```

**Rules:**
- Each layout line represents one row (max 9 characters per line, max 6 lines)
- Slot calculation: `row_index * 9 + column_index` (0-indexed)
- Items reference layout characters with a **single character** in `slot:`
- Numeric `slot:` values still work alongside layout (e.g., `slot: 10,11`)
- Items without `slot:` are template items (not placed in inventory)
- `row:` can be omitted when using layout — auto-derived from grid size

**Mixed mode:** You can use layout references for some items and numeric slots for others in the same menu. Only single-character `slot:` values that match a layout character are resolved; numeric or multi-character values are parsed normally.

### Sound

Optional menu-level sounds for open and close events. Format: `SOUND_NAME` (default vol=1, pitch=1) or `SOUND_NAME volume pitch`.

```yaml
sound:
  open: 'BLOCK_CHEST_OPEN'
  close: 'BLOCK_CHEST_CLOSE 0.8 1.2'
```

Omit the `sound:` section entirely for no menu-level sounds (fully backward compatible).

### Custom Data

Menu-wide data accessible in code:

```yaml
data:
  shop-category: weapons
  discount: 0.15
  max-items: 100
```

Access:
```java
// Via MenuData directly
AdvancedConfigurationSection data = menuData.getDataConfig();
String category = data.getString("shop-category");
double discount = data.getDouble("discount");

// Via AbstractMenu convenience method (null-safe)
String category = menu.getDataConfig().getString("shop-category");
```

## Item Configuration

### Basic Item

```yaml
items:
  my-item:
    type: default              # Item type
    item:                      # ItemStack configuration
      type: DIAMOND
      amount: 1
      display: '&bDiamond'
      lore:
        - '&7Line 1'
        - '&7Line 2'
    slot: 13                   # Slot position
```

### Item Properties

#### type

Item handler type:
- `default` - Standard click handling
- `next-page` - Go to next page (PageMenu)
- `previous-page` - Go to previous page (PageMenu)
- Custom types you register

#### item

ItemStackBuilder configuration:

```yaml
item:
  type: DIAMOND_SWORD          # Material type
  amount: 1                    # Stack size (default: 1)
  damage: 0                    # Durability/data value
  data: 3                      # Legacy data value (e.g., SKULL_ITEM:3)
  display: '&bDisplay Name'    # Display name
  lore:                        # Lore lines
    - '&7First line'
    - '&7Second line'
    - ''                       # Empty line
    - '&eClick to buy!'
  enchants:                    # Enchantments
    - 'SHARPNESS:5'
    - 'UNBREAKING:3'
  flags:                       # Item flags
    - HIDE_ENCHANTS
    - HIDE_ATTRIBUTES
  glow: true                   # Enchant glow without visible enchants
  skull-owner: Notch           # For SKULL_ITEM
  custom-model-data: 1001      # Custom model data (1.14+)
```

#### slot

Slot specification (numeric or layout character):

```yaml
# Single slot
slot: 13

# Multiple slots (comma-separated)
slot: 0,1,2,3,4

# Range (inclusive)
slot: 0-8

# Combined
slot: 0-8,45-53

# Complex
slot: 0,2,4,6,8,10-16,27-35

# Layout character reference (requires layout: section)
slot: 'o'

# No slot — template item (not placed in inventory, fetched by code)
# (simply omit the slot field)
```

Slot layout (6-row inventory):
```
 0  1  2  3  4  5  6  7  8
 9 10 11 12 13 14 15 16 17
18 19 20 21 22 23 24 25 26
27 28 29 30 31 32 33 34 35
36 37 38 39 40 41 42 43 44
45 46 47 48 49 50 51 52 53
```

#### condition

Display condition - item only shows if conditions pass:

```yaml
condition:
  - 'PERMISSION my.permission'
  - 'WORLD world_name'
```

See [Conditions](../features/CONDITIONS.md) for available conditions.

#### execute

Click execution configuration:

```yaml
execute:
  condition:                   # Condition checked on click
    - 'PERMISSION my.click.permission'
  true-execute:                # Executed if condition passes
    - 'PLAYER_MESSAGE &aSuccess!'
    - 'CLOSE'
  false-execute:               # Executed if condition fails
    - 'PLAYER_MESSAGE &cNo permission!'
  click-sound: 'UI_BUTTON_CLICK'              # Plays on every click (optional)
  true-sound: 'ENTITY_PLAYER_LEVELUP'         # Plays when condition passes (optional)
  false-sound: 'ENTITY_VILLAGER_NO 0.7 1.0'   # Plays when condition fails (optional)
```

Sound format: `SOUND_NAME` (vol=1, pitch=1) or `SOUND_NAME volume pitch`. All sound fields are optional — omit for no sound.

See [Executes](../features/EXECUTES.md) for available executes.

#### data

Item-specific custom data:

```yaml
data:
  price: 1000
  category: weapons
  stock: 50
```

Access:
```java
AdvancedConfigurationSection data = itemData.getDataConfig();
double price = data.getDouble("price");
```

#### rootConfig

Access any config property:

```yaml
items:
  paginated-item:
    type: custom-page
    item:
      type: DIAMOND
    slot: 10-16
    empty-item:                # Custom property
      type: BARRIER
      display: '&cEmpty'
```

Access:
```java
ItemStack emptyItem = itemData.getRootConfig().getItemStack("empty-item");
```

## Complete Example

```yaml
# shop.yml
type: 'default'
title: '&8&lWeapon Shop'
row: 6
sound:
  open: 'BLOCK_CHEST_OPEN'
  close: 'BLOCK_CHEST_CLOSE 0.8 1.2'
data:
  category: weapons
  tax-rate: 0.05

items:
  # Background
  background:
    item:
      type: STAINED_GLASS_PANE
      damage: 15
      display: ' '
    slot: 0-8,45-53

  # Shop items
  diamond-sword:
    type: buy-item
    item:
      type: DIAMOND_SWORD
      display: '&bDiamond Sword'
      lore:
        - '&7A powerful weapon'
        - ''
        - '&7Price: &6$1000'
        - ''
        - '&eClick to purchase!'
      enchants:
        - 'SHARPNESS:3'
    slot: 20
    data:
      price: 1000
      item-id: diamond_sword
    condition:
      - 'PERMISSION shop.buy'
    execute:
      click-sound: 'UI_BUTTON_CLICK'
      condition:
        - 'HAS_MONEY 1000'
      true-execute:
        - 'TAKE_MONEY 1000'
        - 'GIVE_ITEM diamond_sword'
        - 'PLAYER_MESSAGE &aPurchased Diamond Sword!'
      true-sound: 'ENTITY_PLAYER_LEVELUP'
      false-execute:
        - 'PLAYER_MESSAGE &cNot enough money!'
      false-sound: 'ENTITY_VILLAGER_NO'

  # Close button
  close:
    type: default
    item:
      type: BARRIER
      display: '&cClose'
    slot: 49
    execute:
      true-execute:
        - 'CLOSE'
```

## Configuration Reloading

Menus are reloaded when:
- `/3fcustommenu reload` is executed
- `plugin.onReload()` is called
- `MenuManager.instance().reloadMenu(plugin)` is called

```java
// Programmatic reload
CustomMenu.instance().onReload();

// Reload specific plugin's menus
MenuManager.instance().reloadMenu(myPlugin);
```

## Best Practices

1. **Organize menus** in subfolders for large plugins
2. **Use descriptive item IDs** (e.g., `diamond-sword` not `item1`)
3. **Comment your configs** for maintainability
4. **Use placeholders** for dynamic content
5. **Test conditions** before deploying
6. **Provide defaults** with ConfigUtils.setupResource()
