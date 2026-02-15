# Tutorial: Creating Custom Menus

This tutorial walks you through creating custom menus using the CustomMenu framework.

## Prerequisites

- BafFramework plugin installed
- CustomMenu module enabled
- Basic understanding of Bukkit plugins

## Part 1: Simple Static Menu

### Step 1: Create the YAML File

Create `plugins/BafFramework/CustomMenu/menu/welcome.yml`:

```yaml
type: 'default'
title: '&6&lWelcome Menu'
row: 3

items:
  # Decorative background
  background:
    item:
      type: STAINED_GLASS_PANE
      damage: 15  # Black glass
      display: ' '
    slot: 0-8,18-26

  # Info item
  info:
    item:
      type: BOOK
      display: '&eServer Information'
      lore:
        - '&7Welcome to our server!'
        - ''
        - '&7Online: &a%server_online%'
        - '&7TPS: &a%server_tps%'
    slot: 13
```

### Step 2: Open the Menu

```java
// In your command or event handler
MenuOpener.builder()
    .player(player)
    .menuData("BafFramework", "welcome")
    .build();
```

### Step 3: Test

Run `/3fcustommenu open <yourname> BafFramework welcome`

## Part 2: Menu with Click Actions

### Step 1: Create Interactive Menu

Create `plugins/BafFramework/CustomMenu/menu/actions.yml`:

```yaml
type: 'default'
title: '&b&lAction Menu'
row: 3

items:
  # Teleport to spawn
  spawn:
    item:
      type: BED
      display: '&aReturn to Spawn'
      lore:
        - '&7Click to teleport'
        - '&7to the spawn point'
    slot: 11
    execute:
      true-execute:
        - 'CLOSE'
        - 'PLAYER_COMMAND spawn'
        - 'PLAYER_MESSAGE &aTeleported to spawn!'
        - 'PLAY_SOUND ENTITY_ENDERMAN_TELEPORT'

  # Give item
  kit:
    item:
      type: CHEST
      display: '&6Daily Kit'
      lore:
        - '&7Claim your daily rewards!'
    slot: 13
    condition:
      - 'PERMISSION kits.daily'
    execute:
      condition:
        - 'COOLDOWN daily-kit 86400'  # 24 hours
      true-execute:
        - 'CONSOLE_COMMAND give %player_name% diamond 3'
        - 'PLAYER_MESSAGE &aYou received 3 diamonds!'
        - 'PLAY_SOUND ENTITY_PLAYER_LEVELUP'
        - 'CLOSE'
      false-execute:
        - 'PLAYER_MESSAGE &cYou already claimed today!'

  # Close button
  close:
    item:
      type: BARRIER
      display: '&cClose Menu'
    slot: 15
    execute:
      true-execute:
        - 'CLOSE'
```

### Step 2: Available Execute Actions

| Execute | Description |
|---------|-------------|
| `CLOSE` | Close the menu |
| `PLAYER_COMMAND <cmd>` | Run command as player |
| `CONSOLE_COMMAND <cmd>` | Run command as console |
| `PLAYER_MESSAGE <msg>` | Send message to player |
| `PLAY_SOUND <sound>` | Play sound to player |
| `OPEN_MENU <plugin> <menu>` | Open another menu |

## Part 3: Custom Menu Type

### Step 1: Create the Menu Class

```java
package com.example.myplugin.menu;

import com.bafmc.bukkit.bafframework.custommenu.menu.AbstractMenu;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ExtraData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.list.DefaultItem;

public class ShopMenu extends AbstractMenu<MenuData, ShopExtraData> {

    @Override
    public String getType() {
        return "shop";  // Used in YAML: type: 'shop'
    }

    @Override
    public void registerItems() {
        registerItem(DefaultItem.class);
        registerItem(BuyItem.class);     // Custom item
        registerItem(SellItem.class);    // Custom item
        registerItem(CategoryItem.class);
    }

    @Override
    public String getTitle() {
        // Dynamic title with category
        ShopExtraData data = getExtraData();
        String category = data != null ? data.getCategory() : "General";
        return super.getTitle().replace("{category}", category);
    }

    @Override
    public void handleClose() {
        // Save cart when closing
        ShopExtraData data = getExtraData();
        if (data != null && !data.getCart().isEmpty()) {
            data.saveCart();
        }
    }
}
```

### Step 2: Create ExtraData

```java
package com.example.myplugin.menu;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ExtraData;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ShopExtraData extends ExtraData {
    private String category;
    private List<ShopCartItem> cart = new ArrayList<>();

    public ShopExtraData(String category) {
        this.category = category;
    }

    public void addToCart(ShopCartItem item) {
        cart.add(item);
    }

    public double getTotal() {
        return cart.stream()
            .mapToDouble(ShopCartItem::getPrice)
            .sum();
    }

    public void saveCart() {
        // Save to database or file
    }
}
```

### Step 3: Create Custom Item

```java
package com.example.myplugin.menu.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.bukkit.api.EconomyAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BuyItem extends AbstractItem<ShopMenu> {

    @Override
    public String getType() {
        return "buy";  // Used in YAML: type: 'buy'
    }

    @Override
    public void handleClick(ClickData data) {
        Player player = data.getPlayer();
        double price = itemData.getDataConfig().getDouble("price");

        if (EconomyAPI.hasMoney(player, price)) {
            EconomyAPI.takeMoney(player, price);
            giveItem(player);
            player.sendMessage("§aPurchased for $" + price);
        } else {
            player.sendMessage("§cNot enough money! Need $" + price);
        }
    }

    @Override
    public boolean canLoadItem() {
        return itemData.getCondition().check(menu.getOwner());
    }

    @Override
    public ItemStack setupItemStack() {
        return itemData.getItemStack(menu.getOwner());
    }

    private void giveItem(Player player) {
        String itemId = itemData.getDataConfig().getString("item-id");
        // Give item logic
    }
}
```

### Step 4: Register Menu Type

```java
// In your plugin's onEnable
@Override
public void onEnable() {
    // Register custom menu type
    MenuRegister.instance().registerStrategy(ShopMenu.class);

    // Register plugin with CustomMenu
    MenuPluginRegister.instance().registerMenuPlugin(this);
}
```

### Step 5: Create YAML Configuration

Create `plugins/MyPlugin/menu/shop.yml`:

```yaml
type: 'shop'
title: '&8&l{category} Shop'
row: 6

items:
  # Background
  background:
    item:
      type: STAINED_GLASS_PANE
      damage: 15
      display: ' '
    slot: 0-8,45-53

  # Category tabs
  weapons-tab:
    type: category
    item:
      type: DIAMOND_SWORD
      display: '&6Weapons'
    slot: 2
    data:
      category: weapons

  armor-tab:
    type: category
    item:
      type: DIAMOND_CHESTPLATE
      display: '&bArmor'
    slot: 4
    data:
      category: armor

  tools-tab:
    type: category
    item:
      type: DIAMOND_PICKAXE
      display: '&aTools'
    slot: 6
    data:
      category: tools

  # Shop items
  diamond-sword:
    type: buy
    item:
      type: DIAMOND_SWORD
      display: '&bDiamond Sword'
      lore:
        - '&7Price: &6$1000'
    slot: 20
    data:
      price: 1000
      item-id: diamond_sword
    condition:
      - 'PERMISSION shop.weapons'

  # More items...

  # Close button
  close:
    item:
      type: BARRIER
      display: '&cClose'
    slot: 49
    execute:
      true-execute:
        - 'CLOSE'
```

### Step 6: Open the Menu

```java
// Open shop with category
MenuOpener.builder()
    .player(player)
    .menuData(myPlugin, "shop")
    .extraData(new ShopExtraData("weapons"))
    .build();
```

## Part 4: Paginated Menu

See [Page Menus](PAGE_MENUS.md) for detailed pagination tutorial.

### Quick Example

```java
public class PlayerListMenu extends PageMenu<MenuData, ExtraData> {
    @Override
    public String getType() {
        return "player-list";
    }

    @Override
    public void registerItems() {
        super.registerItems();  // Includes pagination items
        registerItem(PlayerItem.class);
    }
}

public class PlayerItem extends AbstractPageItem<MenuData, ExtraData, Player> {
    @Override
    public String getType() {
        return "players";
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
            .build();
    }
}
```

```yaml
# player-list.yml
type: 'player-list'
title: '&8Players - Page {page}'
row: 6

items:
  players:
    type: players
    item:
      type: SKULL_ITEM
      data: 3
    slot: 10-16,19-25,28-34,37-43

  previous:
    type: previous-page
    item:
      type: ARROW
      display: '&c<< Previous'
    slot: 48

  next:
    type: next-page
    item:
      type: ARROW
      display: '&aNext >>'
    slot: 50
```

## Best Practices

1. **Start simple** - Create static menus first, then add complexity
2. **Use meaningful IDs** - Item IDs should describe the item
3. **Organize by feature** - Group related menus in subfolders
4. **Test thoroughly** - Check all click actions and conditions
5. **Provide feedback** - Use sounds and messages for actions
6. **Handle errors** - Check for null and invalid states
7. **Use ExtraData** - Pass context data instead of storing in menus

## Common Patterns

### Confirmation Dialog

```yaml
# confirm.yml
type: 'default'
title: '&c&lAre you sure?'
row: 3

items:
  confirm:
    item:
      type: WOOL
      damage: 5  # Lime wool
      display: '&aConfirm'
    slot: 12
    execute:
      true-execute:
        - 'CLOSE'
        - 'PLAYER_COMMAND confirm-action'

  cancel:
    item:
      type: WOOL
      damage: 14  # Red wool
      display: '&cCancel'
    slot: 14
    execute:
      true-execute:
        - 'CLOSE'
```

### Settings Toggle

```java
public class ToggleItem extends AbstractItem<SettingsMenu> {
    @Override
    public void handleClick(ClickData data) {
        String settingKey = itemData.getDataConfig().getString("setting");
        boolean current = playerSettings.get(settingKey);
        playerSettings.set(settingKey, !current);
        menu.reopenInventory();  // Refresh display
    }

    @Override
    public ItemStack setupItemStack() {
        String settingKey = itemData.getDataConfig().getString("setting");
        boolean enabled = playerSettings.get(settingKey);

        ItemStack item = itemData.getItemStack();
        // Modify based on state
        if (enabled) {
            item.setType(Material.LIME_DYE);
        } else {
            item.setType(Material.GRAY_DYE);
        }
        return item;
    }
}
```

## Next Steps

- [Creating Custom Items Tutorial](TUTORIAL_ITEMS.md)
- [Page Menus Documentation](PAGE_MENUS.md)
- [Configuration Reference](CONFIGURATION.md)
