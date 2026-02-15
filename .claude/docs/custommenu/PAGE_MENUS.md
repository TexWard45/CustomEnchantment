# Page Menus

PageMenu provides built-in pagination support for menus that display lists of items across multiple pages.

## PageMenu Class

Base class for paginated menus.

```java
@Getter
@Setter
public abstract class PageMenu<M extends MenuData, E extends ExtraData> extends AbstractMenu<M, E> {
    protected int page = 1;
    protected int maxPage = 1;

    public void setMaxPage(int maxPage);
    public void setExpandMaxPage(int maxPage);
    public void nextPage();
    public void previousPage();

    @Override
    public void registerItems() {
        registerItem(DefaultItem.class);
        registerItem(NextPageItem.class);
        registerItem(PreviousPageItem.class);
    }

    @Override
    public String getTitle() {
        return PlaceholderBuilder.builder()
            .put("{page}", this.page)
            .build()
            .apply(super.getTitle());
    }
}
```

### Key Features

- **Auto-registered items**: DefaultItem, NextPageItem, PreviousPageItem
- **Page placeholder**: `{page}` in title is replaced with current page
- **Safe navigation**: nextPage/previousPage clamp to valid range
- **Auto-refresh**: Page changes trigger menu reopen

## AbstractPageItem

Base class for items that display paginated content.

```java
public abstract class AbstractPageItem<M extends MenuData, E extends ExtraData, I>
        extends AbstractItem<PageMenu<M, E>> {

    private List<I> items;
    private Map<Integer, I> itemDataBySlot = new HashMap<>();
    private Map<Integer, I> itemDataByIndex = new HashMap<>();

    // Abstract methods
    public abstract List<I> setupItems();
    public abstract ItemStack setupItemStack(int index, int slot, I itemData);

    // Utility methods
    public void displayItemStack(int index, int slot, I itemData);
    public I getItemDataBySlot(int slot);
    public I getItemDataByIndex(int index);
    public int getMaxPage();
    public void resetItems();
}
```

### How Pagination Works

```
Items: [A, B, C, D, E, F, G, H, I, J]
Slots per page: 4 (e.g., slots 10, 11, 12, 13)

Page 1: A, B, C, D
Page 2: E, F, G, H
Page 3: I, J, (empty), (empty)
```

Calculation:
```java
int itemsPerPage = slots.size();
int maxPage = (int) Math.ceil((double) items.size() / itemsPerPage);

// For page N, display indices:
int startIndex = (page - 1) * itemsPerPage;
int endIndex = Math.min(startIndex + itemsPerPage, items.size());
```

## Creating a Page Menu

### Step 1: Create the Menu Class

```java
public class PlayerListMenu extends PageMenu<MenuData, ExtraData> {
    @Override
    public String getType() {
        return "player-list";
    }

    @Override
    public void registerItems() {
        super.registerItems();  // Register default pagination items
        registerItem(PlayerListItem.class);  // Your paginated item
    }
}
```

### Step 2: Create the Paginated Item

```java
public class PlayerListItem extends AbstractPageItem<MenuData, ExtraData, Player> {

    @Override
    public String getType() {
        return "players";
    }

    @Override
    public List<Player> setupItems() {
        // Return the list of items to paginate
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
                "&7#" + (index + 1),
                "&7Level: &f" + player.getLevel(),
                "",
                "&aClick to teleport"
            )
            .build();
    }

    @Override
    public void handleClick(ClickData data, Player targetPlayer) {
        if (targetPlayer != null) {
            data.getPlayer().teleport(targetPlayer);
            data.getPlayer().closeInventory();
        }
    }
}
```

### Step 3: Register and Configure

```java
// Register the menu type
MenuRegister.instance().registerStrategy(PlayerListMenu.class);
```

```yaml
# player-list.yml
type: 'player-list'
title: '&8Online Players - Page {page}'
row: 6

items:
  # Background
  background:
    type: default
    item:
      type: STAINED_GLASS_PANE
      damage: 15
      display: ' '
    slot: 0-8,45-53

  # Paginated player heads
  players:
    type: players
    item:
      type: SKULL_ITEM
      data: 3
      display: '&ePlaceholder'  # Overridden in setupItemStack
    slot: 10-16,19-25,28-34,37-43  # 28 slots = 28 players per page
    empty-item:
      type: STAINED_GLASS_PANE
      damage: 7
      display: ' '

  # Navigation
  previous:
    type: previous-page
    item:
      type: ARROW
      display: '&c<< Previous'
      lore:
        - '&7Go to previous page'
    slot: 48

  next:
    type: next-page
    item:
      type: ARROW
      display: '&aNext >>'
      lore:
        - '&7Go to next page'
    slot: 50
```

## Advanced Pagination

### Filtering Items

```java
@Getter
@Setter
public class FilteredListExtraData extends ExtraData {
    private String searchText = "";
}

public class FilteredListItem extends AbstractPageItem<MenuData, FilteredListExtraData, String> {
    @Override
    public List<String> setupItems() {
        String filter = menu.getExtraData().getSearchText().toLowerCase();
        return getAllItems().stream()
            .filter(item -> item.toLowerCase().contains(filter))
            .collect(Collectors.toList());
    }

    // When filter changes, reset items
    public void updateFilter(String newFilter) {
        menu.getExtraData().setSearchText(newFilter);
        resetItems();  // Clear cached items
        menu.reopenInventory();  // Refresh display
    }
}
```

### Sorted Pagination

```java
public class SortedPlayerItem extends AbstractPageItem<MenuData, ExtraData, Player> {
    @Override
    public List<Player> setupItems() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        // Sort by configured method
        String sortBy = itemData.getDataConfig().getString("sort-by", "name");
        switch (sortBy) {
            case "level":
                players.sort(Comparator.comparingInt(Player::getLevel).reversed());
                break;
            case "health":
                players.sort(Comparator.comparingDouble(Player::getHealth).reversed());
                break;
            default:
                players.sort(Comparator.comparing(Player::getName));
        }

        return players;
    }
}
```

### Dynamic Data Loading

```java
public class ShopItemListItem extends AbstractPageItem<MenuData, ShopExtraData, ShopProduct> {

    @Override
    public List<ShopProduct> setupItems() {
        String category = menu.getExtraData().getCategory();
        // Load from database or external source
        return ShopDatabase.getProducts(category);
    }

    @Override
    public ItemStack setupItemStack(int index, int slot, ShopProduct product) {
        ItemStack base = itemData.getItemStack();
        return ItemStackUtils.getItemStack(base,
            PlaceholderBuilder.builder()
                .put("{name}", product.getName())
                .put("{price}", product.getPrice())
                .put("{stock}", product.getStock())
                .build()
        );
    }
}
```

### Empty Slot Customization

```java
@Override
public void displayEmptyItemStack(int index, int slot) {
    // Custom empty slot item
    ItemStack emptyItem = itemData.getRootConfig().getItemStack("empty-item");

    if (emptyItem == null) {
        // Default: air
        setItem(slot, new ItemStack(Material.AIR));
    } else {
        setItem(slot, emptyItem);
    }
}
```

YAML:
```yaml
items:
  product-list:
    type: products
    item:
      type: DIAMOND
      display: '{name}'
    slot: 10-16,19-25
    empty-item:
      type: BARRIER
      display: '&cNo more items'
```

## Built-in Page Menu: ExamplePageMenu

A simple example showing how page menus work:

```java
public class ExamplePageMenu extends PageMenu {
    @Override
    public String getType() {
        return "example-page";
    }

    @Override
    public void registerItems() {
        super.registerItems();
        registerItem(ExamplePageItem.class);
    }
}

public class ExamplePageItem extends AbstractPageItem<MenuData, ExtraData, Integer> {
    @Override
    public String getType() {
        return "example-page";
    }

    @Override
    public List<Integer> setupItems() {
        return Arrays.asList(1, 2, 3, 4, 5, 10, 20, 30, 40, 50, 100);
    }

    @Override
    public ItemStack setupItemStack(int index, int slot, Integer number) {
        return ItemStackUtils.getItemStack(
            itemData.getItemStack(),
            PlaceholderBuilder.builder()
                .put("{index}", index)
                .put("{item}", number)
                .build()
        );
    }
}
```

## Page Navigation Items

### NextPageItem

Only shows when not on last page:

```java
@Override
public boolean canLoadItem() {
    if (menu instanceof PageMenu) {
        PageMenu pageMenu = (PageMenu) menu;
        return pageMenu.getPage() < pageMenu.getMaxPage();
    }
    return super.canLoadItem();
}
```

### PreviousPageItem

Only shows when not on first page:

```java
@Override
public boolean canLoadItem() {
    if (menu instanceof PageMenu) {
        PageMenu pageMenu = (PageMenu) menu;
        return pageMenu.getPage() > 1;
    }
    return super.canLoadItem();
}
```

## Best Practices

1. **Override registerItems()** and call `super.registerItems()` to include pagination items
2. **Use meaningful slot ranges** for paginated items (e.g., center of inventory)
3. **Provide empty-item** configuration for empty slots
4. **Use {page} placeholder** in title for current page display
5. **Cache items** with `getItems()` - only calls `setupItems()` once
6. **Call resetItems()** when underlying data changes
7. **Use handleClick(data, itemData)** for item-specific click handling
