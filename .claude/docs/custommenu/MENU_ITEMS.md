# Menu Items

Menu items are interactive elements within menus. Each item type is a strategy that handles display and click behavior.

## Core Components

### AbstractItem

Base class for all menu items. Extends `AbstractStrategy`.

```java
public abstract class AbstractItem<M extends AbstractMenu> extends AbstractStrategy {
    protected M menu;
    protected ItemData itemData;

    // Abstract methods
    public abstract void handleClick(ClickData data);
    public abstract ItemStack setupItemStack();
    public abstract boolean canLoadItem();

    // Utility methods
    public void displayItemStack();
    public void setItem(int slot, ItemStack itemStack);
    public void setItem(ItemStack itemStack);
}
```

#### Key Methods

| Method | Description |
|--------|-------------|
| `getType()` | Returns the item type identifier |
| `handleClick(ClickData)` | Called when player clicks this item |
| `setupItemStack()` | Creates the ItemStack to display |
| `canLoadItem()` | Returns true if item should be displayed |
| `displayItemStack()` | Calls setupItemStack and sets it in menu |
| `setItem(slot, itemStack)` | Places item at specific slot |
| `setItem(itemStack)` | Places item at configured slots |

### ItemRegister

Per-menu registry for item types.

```java
public class ItemRegister extends StrategyRegister<AbstractItem> {
    // Inherited from StrategyRegister
    public void registerStrategy(Class<? extends AbstractItem> clazz);
    public AbstractItem instanceStrategy(String type);
}
```

#### Usage in Menu

```java
public class MyMenu extends AbstractMenu<MenuData, ExtraData> {
    @Override
    public void registerItems() {
        registerItem(DefaultItem.class);
        registerItem(NextPageItem.class);
        registerItem(MyCustomItem.class);
    }
}
```

## Built-in Item Types

### DefaultItem

Standard item with condition and execute support.

**Type:** `default`

```java
public class DefaultItem<M extends AbstractMenu> extends AbstractItem<M> {
    @Override
    public String getType() {
        return "default";
    }

    @Override
    public void handleClick(ClickData data) {
        Player player = data.getPlayer();
        if (itemData.getExecuteCondition().check(player)) {
            itemData.getTrueExecute().execute(player);
        } else {
            itemData.getFalseExecute().execute(player);
        }
    }

    @Override
    public boolean canLoadItem() {
        return itemData.getCondition().check(menu.getOwner());
    }

    @Override
    public ItemStack setupItemStack() {
        return itemData.getItemStack();
    }
}
```

**YAML Configuration:**
```yaml
items:
  my-item:
    type: default  # Optional, default is "default"
    item:
      type: DIAMOND
      display: '&bClick me!'
      lore:
        - '&7Click to do something'
    slot: 13
    condition:
      - 'PERMISSION my.permission'
    execute:
      condition:
        - 'PERMISSION execute.permission'
      true-execute:
        - 'PLAYER_MESSAGE &aYou have permission!'
      false-execute:
        - 'PLAYER_MESSAGE &cYou need permission!'
```

### NextPageItem

Advances to next page in PageMenu.

**Type:** `next-page`

```java
public class NextPageItem extends DefaultItem {
    @Override
    public String getType() {
        return "next-page";
    }

    @Override
    public void handleClick(ClickData data) {
        super.handleClick(data);
        if (menu instanceof PageMenu) {
            ((PageMenu) menu).nextPage();
        }
    }

    @Override
    public boolean canLoadItem() {
        if (menu instanceof PageMenu) {
            return ((PageMenu) menu).getPage() < ((PageMenu) menu).getMaxPage();
        }
        return super.canLoadItem();
    }
}
```

**YAML Configuration:**
```yaml
items:
  next:
    type: next-page
    item:
      type: ARROW
      display: '&aNext Page >>'
    slot: 53
```

### PreviousPageItem

Goes to previous page in PageMenu.

**Type:** `previous-page`

```java
public class PreviousPageItem extends DefaultItem {
    @Override
    public String getType() {
        return "previous-page";
    }

    @Override
    public void handleClick(ClickData data) {
        super.handleClick(data);
        if (menu instanceof PageMenu) {
            ((PageMenu) menu).previousPage();
        }
    }

    @Override
    public boolean canLoadItem() {
        if (menu instanceof PageMenu) {
            return ((PageMenu) menu).getPage() > 1;
        }
        return super.canLoadItem();
    }
}
```

**YAML Configuration:**
```yaml
items:
  previous:
    type: previous-page
    item:
      type: ARROW
      display: '&c<< Previous Page'
    slot: 45
```

### AbstractPageItem

Base class for paginated list items. See [Page Menus](PAGE_MENUS.md) for details.

```java
public abstract class AbstractPageItem<M extends MenuData, E extends ExtraData, I>
        extends AbstractItem<PageMenu<M, E>> {

    private List<I> items;
    private Map<Integer, I> itemDataBySlot;
    private Map<Integer, I> itemDataByIndex;

    public abstract List<I> setupItems();
    public abstract ItemStack setupItemStack(int index, int slot, I itemData);

    public void displayItemStack(int index, int slot, I itemData);
    public I getItemDataBySlot(int slot);
    public I getItemDataByIndex(int index);
    public int getMaxPage();
}
```

### ExamplePageItem

Example implementation of AbstractPageItem.

**Type:** `example-page`

```java
public class ExamplePageItem extends AbstractPageItem<MenuData, ExtraData, Integer> {
    @Override
    public String getType() {
        return "example-page";
    }

    @Override
    public List<Integer> setupItems() {
        return new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 10, 20, 30, 40, 50, 100));
    }

    @Override
    public ItemStack setupItemStack(int index, int slot, Integer itemData) {
        ItemStack itemStack = setupItemStack();
        return ItemStackUtils.getItemStack(itemStack,
            PlaceholderBuilder.builder()
                .put("{index}", index)
                .put("{item}", itemData)
                .build()
        );
    }
}
```

## Creating Custom Items

### Simple Custom Item

```java
public class CloseMenuItem extends AbstractItem<AbstractMenu> {
    @Override
    public String getType() {
        return "close";
    }

    @Override
    public void handleClick(ClickData data) {
        data.getPlayer().closeInventory();
    }

    @Override
    public boolean canLoadItem() {
        return true;  // Always show
    }

    @Override
    public ItemStack setupItemStack() {
        return itemData.getItemStack();
    }
}
```

### Custom Item with Logic

```java
public class BuyItem extends AbstractItem<ShopMenu> {
    @Override
    public String getType() {
        return "buy";
    }

    @Override
    public void handleClick(ClickData data) {
        Player player = data.getPlayer();
        ShopExtraData shopData = menu.getExtraData();

        double price = itemData.getDataConfig().getDouble("price");
        if (EconomyAPI.hasMoney(player, price)) {
            EconomyAPI.takeMoney(player, price);
            giveItem(player);
            player.sendMessage("Purchase successful!");
        } else {
            player.sendMessage("Not enough money!");
        }
    }

    @Override
    public boolean canLoadItem() {
        return itemData.getCondition().check(menu.getOwner());
    }

    @Override
    public ItemStack setupItemStack() {
        ItemStack item = itemData.getItemStack(menu.getOwner());
        // Add price to lore
        double price = itemData.getDataConfig().getDouble("price");
        return ItemStackUtils.addLore(item, "&7Price: &6$" + price);
    }

    private void giveItem(Player player) {
        // Item giving logic
    }
}
```

**YAML Configuration:**
```yaml
items:
  diamond-sword:
    type: buy
    item:
      type: DIAMOND_SWORD
      display: '&bDiamond Sword'
      lore:
        - '&7A powerful weapon'
    slot: 13
    data:
      price: 1000.0
```

### Custom Paginated Item

```java
public class PlayerListItem extends AbstractPageItem<MenuData, ExtraData, Player> {
    @Override
    public String getType() {
        return "player-list";
    }

    @Override
    public List<Player> setupItems() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    @Override
    public ItemStack setupItemStack(int index, int slot, Player player) {
        return new ItemStackBuilder()
            .type(Material.SKULL_ITEM)
            .data(3)
            .skullOwner(player.getName())
            .display("&e" + player.getName())
            .lore(
                "&7Level: &f" + player.getLevel(),
                "&7Health: &c" + player.getHealth()
            )
            .build();
    }

    @Override
    public void handleClick(ClickData data, Player targetPlayer) {
        if (targetPlayer != null) {
            data.getPlayer().teleport(targetPlayer);
        }
    }
}
```

## Item Configuration Reference

### Basic Item Properties

```yaml
items:
  item-id:
    type: default              # Item type (default: "default")
    item:                      # ItemStackBuilder configuration
      type: DIAMOND            # Material type
      amount: 1                # Stack size
      damage: 0                # Durability/data value
      display: '&bDisplay'     # Display name
      lore:                    # Lore lines
        - '&7Line 1'
        - '&7Line 2'
    slot: 13                   # Single slot
    # OR
    slot: 0,1,2,3              # Multiple slots (comma-separated)
    # OR
    slot: 0-8                  # Slot range
    # OR
    slot: 0-8,45-53            # Combined ranges and individual
    # OR
    slot: 'o'                  # Layout character reference (requires layout: section)
    # OR omit slot entirely for template items (fetched by code)
```

### Template Items (No Slot)

Items defined without a `slot:` property are **template items** — they are not placed in the inventory during `setupItems()`. Instead, they serve as reusable YAML-defined appearances that can be fetched at runtime:

```java
// Fetch template by name
ItemStack item = menu.getTemplateItemStack("confirm-upgrade");

// Fetch with placeholder replacement
ItemStack item = menu.getTemplateItemStack("confirm-cost", placeholder);
```

See [ADVANCED_PATTERNS.md](ADVANCED_PATTERNS.md#template-items-no-slot-items) for detailed usage patterns.

### Conditions and Executes

```yaml
items:
  conditional-item:
    type: default
    item:
      type: EMERALD
      display: '&aConditional Item'
    slot: 22

    # Display condition - item only shows if true
    condition:
      - 'PERMISSION my.view.permission'

    # Click execution
    execute:
      # Condition to check on click
      condition:
        - 'PERMISSION my.click.permission'

      # Executed if condition is true
      true-execute:
        - 'PLAYER_MESSAGE &aSuccess!'

      # Executed if condition is false
      false-execute:
        - 'PLAYER_MESSAGE &cNo permission!'

      # Optional sounds (format: SOUND_NAME or SOUND_NAME volume pitch)
      click-sound: 'UI_BUTTON_CLICK'              # Plays on every click
      true-sound: 'ENTITY_PLAYER_LEVELUP'         # Plays when condition passes
      false-sound: 'ENTITY_VILLAGER_NO 0.7 1.0'   # Plays when condition fails
```

### Custom Data

```yaml
items:
  shop-item:
    type: buy
    item:
      type: DIAMOND_SWORD
      display: '&bDiamond Sword'
    slot: 13
    data:                      # Custom data section
      price: 1000.0
      category: weapons
      max-purchases: 5
```

Access in code:
```java
AdvancedConfigurationSection dataConfig = itemData.getDataConfig();
double price = dataConfig.getDouble("price");
String category = dataConfig.getString("category");
int maxPurchases = dataConfig.getInt("max-purchases");
```

## Best Practices

1. **Extend DefaultItem** for simple items with conditional behavior
2. **Use canLoadItem()** to conditionally show/hide items
3. **Access menu via `menu` field** for menu-specific data
4. **Use ItemData.getDataConfig()** for custom item properties
5. **Register items in menu's registerItems()** method
6. **Keep item logic focused** - one item type per behavior
