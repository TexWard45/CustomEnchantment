# Menu Data Structures

CustomMenu uses several data classes to represent menu configuration and runtime state.

## MenuData

Represents the configuration of a menu loaded from YAML.

```java
@Configuration
@Getter
@Setter
public class MenuData {
    private String id;                                    // Menu identifier (filename)
    @Path
    private String type;                                  // Menu type (e.g., "default", "page")
    @Path
    private String title;                                 // Menu title (supports color codes)
    @Path
    private int row = 1;                                  // Number of rows (1-6)
    @Path("data")
    private AdvancedConfigurationSection dataConfig;      // Custom data section
    @Path("items")
    @ValueType(ItemData.class)
    private Map<String, ItemData> itemMap;                // Item configurations
}
```

### YAML Structure

```yaml
type: 'default'                # Menu type
title: '&8&lMy Menu'           # Title with color codes
row: 3                         # 3 rows = 27 slots
data:                          # Custom data (optional)
  custom-key: value
items:                         # Item definitions
  item-id:
    # ... item configuration
```

### Accessing Menu Data

```java
// In AbstractMenu
MenuData menuData = getMenuData();
String title = menuData.getTitle();
int slots = menuData.getRow() * 9;
Map<String, ItemData> items = menuData.getItemMap();

// Custom data
AdvancedConfigurationSection data = menuData.getDataConfig();
String customValue = data.getString("custom-key");
```

## ItemData

Represents the configuration of a menu item.

```java
@Configuration
@Getter
public class ItemData implements IConfigurationLoader {
    @Key
    private String id;                                        // Item identifier
    @Path("type")
    private String type = "default";                          // Item type
    @Path(isRoot = true)
    private AdvancedConfigurationSection rootConfig;          // Full config section
    @Path("data")
    private AdvancedConfigurationSection dataConfig;          // Custom data
    @Path("item")
    private ItemStackBuilder itemStackBuilder;                // Item appearance
    @Path("slot")
    private String slotFormat;                                // Slot specification
    private List<Integer> slots;                              // Parsed slots
    @Path("condition")
    private List<String> conditionFormat;                     // Display conditions
    private Condition condition;                              // Parsed condition
    @Path("execute.condition")
    private List<String> executeConditionFormat;              // Click conditions
    private Condition executeCondition;                       // Parsed execute condition
    @Path("execute.true-execute")
    private List<String> trueExecuteFormat;                   // Actions if condition true
    private Execute trueExecute;                              // Parsed true execute
    @Path("execute.false-execute")
    private List<String> falseExecuteFormat;                  // Actions if condition false
    private Execute falseExecute;                             // Parsed false execute

    // Methods
    public ItemStack getItemStack();                          // Get item without placeholders
    public ItemStack getItemStack(Player player);             // Get item with placeholders
}
```

### YAML Structure

```yaml
items:
  my-item:
    type: default                    # Item handler type
    item:                            # Item appearance
      type: DIAMOND
      amount: 1
      display: '&bDiamond'
      lore:
        - '&7A shiny diamond'
    slot: 13                         # Single slot
    # OR
    slot: 0,1,2,3                    # Multiple slots
    # OR
    slot: 0-8                        # Range
    # OR
    slot: 0-8,45-53                  # Combined
    data:                            # Custom data
      price: 100
    condition:                       # Display condition
      - 'PERMISSION view.item'
    execute:
      condition:                     # Click condition
        - 'PERMISSION use.item'
      true-execute:                  # If click condition true
        - 'PLAYER_MESSAGE &aClicked!'
      false-execute:                 # If click condition false
        - 'PLAYER_MESSAGE &cNo permission!'
```

### Slot Format

```yaml
# Single slot
slot: 13

# Multiple slots (comma-separated)
slot: 0,1,2,3,4

# Range (inclusive)
slot: 0-8          # Slots 0,1,2,3,4,5,6,7,8

# Combined
slot: 0-8,45-53    # Top row and bottom row

# Complex
slot: 0,2,4,6,8,10-16,27-35
```

### Accessing Item Data

```java
// In AbstractItem
ItemData itemData = getItemData();

// Basic properties
String type = itemData.getType();
List<Integer> slots = itemData.getSlots();
ItemStack item = itemData.getItemStack();
ItemStack playerItem = itemData.getItemStack(player);

// Conditions and executes
Condition displayCondition = itemData.getCondition();
Condition clickCondition = itemData.getExecuteCondition();
Execute trueExecute = itemData.getTrueExecute();
Execute falseExecute = itemData.getFalseExecute();

// Custom data
AdvancedConfigurationSection data = itemData.getDataConfig();
double price = data.getDouble("price");

// Root config (for non-standard properties)
AdvancedConfigurationSection root = itemData.getRootConfig();
ItemStack emptyItem = root.getItemStack("empty-item");
```

## ClickData

Contains information about a click event.

```java
@Getter
@Setter
@Builder
public class ClickData {
    private Player player;                    // Player who clicked
    private int clickedSlot;                  // Slot that was clicked
    private Inventory clickedInventory;       // The inventory
    private InventoryClickEvent event;        // Original Bukkit event
}
```

### Usage in Items

```java
@Override
public void handleClick(ClickData data) {
    Player player = data.getPlayer();
    int slot = data.getClickedSlot();

    // Check click type
    InventoryClickEvent event = data.getEvent();
    if (event.isLeftClick()) {
        // Left click action
    } else if (event.isRightClick()) {
        // Right click action
    } else if (event.isShiftClick()) {
        // Shift click action
    }
}
```

## ExtraData

Base class for menu-specific runtime data.

```java
public class ExtraData {
    // Empty base class - extend for custom data
}
```

### Creating Custom ExtraData

```java
@Getter
@Setter
public class ShopExtraData extends ExtraData {
    private String category;
    private List<ShopItem> cart = new ArrayList<>();
    private double discount = 0.0;

    public ShopExtraData(String category) {
        this.category = category;
    }

    public void addToCart(ShopItem item) {
        cart.add(item);
    }

    public double getTotal() {
        return cart.stream()
            .mapToDouble(ShopItem::getPrice)
            .sum() * (1.0 - discount);
    }
}
```

### Using ExtraData

```java
// When opening menu
MenuOpener.builder()
    .player(player)
    .menuData(plugin, "shop")
    .extraData(new ShopExtraData("weapons"))
    .build();

// In menu
@Override
public void setupMenu() {
    ShopExtraData shopData = getExtraData();
    String category = shopData.getCategory();
    // Use category to filter items
    super.setupMenu();
}

// In item
@Override
public void handleClick(ClickData data) {
    ShopExtraData shopData = menu.getExtraData();
    shopData.addToCart(shopItem);
}
```

## PageMenuData

Base class for paginated menu data.

```java
public abstract class PageMenuData<T> {
    protected int page;
    protected int maxPage;

    public abstract int getMaxPage();

    public void nextPage() {
        this.page++;
        if (this.page > this.maxPage)
            this.page = this.maxPage;
    }

    public void previousPage() {
        this.page--;
        if (this.page < 1)
            this.page = 1;
    }
}
```

### Usage

Note: PageMenuData is typically not used directly. Instead, PageMenu handles pagination internally. Use ExtraData for additional page menu state:

```java
@Getter
@Setter
public class PlayerListExtraData extends ExtraData {
    private String filterText = "";
    private SortOrder sortOrder = SortOrder.NAME;

    public enum SortOrder {
        NAME, LEVEL, JOIN_TIME
    }
}
```

## PlayerData

Stores player-specific menu state.

```java
// Managed by PlayerManager
PlayerData playerData = PlayerManager.instance().getPlayerData(player);

// Current open menu
AbstractMenu currentMenu = playerData.getMenu();

// Set when menu opens/closes
playerData.setMenu(menu);
playerData.setMenu(null);
```

## Data Flow

```
1. YAML file loaded
   ↓
2. MenuData populated (via @Configuration annotations)
   ↓
3. ItemData parsed for each item in items map
   ↓
4. Conditions and Executes parsed from string lists
   ↓
5. Menu opened with optional ExtraData
   ↓
6. AbstractMenu.setupMenu() creates inventory
   ↓
7. AbstractItem.setupItemStack() creates display items
   ↓
8. ClickData passed on player interaction
   ↓
9. AbstractItem.handleClick() processes action
```

## Best Practices

1. **Use ExtraData** for menu-specific runtime state
2. **Use ItemData.getDataConfig()** for item-specific configuration
3. **Use MenuData.getDataConfig()** for menu-wide configuration
4. **Parse ClickData.getEvent()** for detailed click information
5. **Keep data classes immutable** where possible
6. **Validate data** in loadConfig() implementations
