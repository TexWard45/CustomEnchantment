# Tutorial: Creating Custom Menu Items

This tutorial covers creating custom menu items for the CustomMenu framework.

## Prerequisites

- Completed [Creating Custom Menus Tutorial](TUTORIAL_MENUS.md)
- Understanding of AbstractItem and ItemData

## Part 1: Basic Custom Item

### Step 1: Extend AbstractItem

```java
package com.example.myplugin.menu.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.AbstractMenu;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HealItem extends AbstractItem<AbstractMenu> {

    @Override
    public String getType() {
        return "heal";  // Type identifier for YAML
    }

    @Override
    public void handleClick(ClickData data) {
        Player player = data.getPlayer();

        // Get heal amount from config
        double healAmount = itemData.getDataConfig().getDouble("amount", 10.0);

        // Heal the player
        double newHealth = Math.min(player.getHealth() + healAmount,
                                    player.getMaxHealth());
        player.setHealth(newHealth);

        // Feedback
        player.sendMessage("§aHealed for " + healAmount + " hearts!");

        // Execute configured actions
        if (itemData.getExecuteCondition().check(player)) {
            itemData.getTrueExecute().execute(player);
        }
    }

    @Override
    public boolean canLoadItem() {
        // Check display condition
        return itemData.getCondition().check(menu.getOwner());
    }

    @Override
    public ItemStack setupItemStack() {
        // Get item with placeholder support
        return itemData.getItemStack(menu.getOwner());
    }
}
```

### Step 2: Register the Item

```java
// In your menu class
public class MyMenu extends AbstractMenu<MenuData, ExtraData> {
    @Override
    public void registerItems() {
        registerItem(DefaultItem.class);  // Always include default
        registerItem(HealItem.class);     // Your custom item
    }
}
```

### Step 3: Configure in YAML

```yaml
items:
  heal-button:
    type: heal
    item:
      type: GOLDEN_APPLE
      display: '&aHeal'
      lore:
        - '&7Click to heal!'
        - '&7Heals: &c+10 hearts'
    slot: 13
    data:
      amount: 10.0
    condition:
      - 'PERMISSION heal.use'
    execute:
      true-execute:
        - 'PLAY_SOUND ENTITY_PLAYER_LEVELUP'
```

## Part 2: Item with Economy

### Step 1: Create Purchase Item

```java
package com.example.myplugin.menu.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.bukkit.api.EconomyAPI;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.utils.ItemStackUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class PurchaseItem extends AbstractItem<ShopMenu> {

    @Override
    public String getType() {
        return "purchase";
    }

    @Override
    public void handleClick(ClickData data) {
        Player player = data.getPlayer();
        double price = getPrice();
        int amount = getAmount();

        // Check balance
        if (!EconomyAPI.hasMoney(player, price)) {
            player.sendMessage("§cNot enough money! Need $" + price);
            return;
        }

        // Check inventory space
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§cInventory full!");
            return;
        }

        // Process purchase
        EconomyAPI.takeMoney(player, price);
        giveItems(player, amount);

        player.sendMessage("§aPurchased " + amount + "x for $" + price);

        // Refresh menu to update stock/display
        menu.reopenInventory();
    }

    @Override
    public boolean canLoadItem() {
        // Only show if in stock
        int stock = getStock();
        if (stock != -1 && stock <= 0) {
            return false;
        }
        return itemData.getCondition().check(menu.getOwner());
    }

    @Override
    public ItemStack setupItemStack() {
        ItemStack item = itemData.getItemStack(menu.getOwner());

        // Add dynamic lore
        double price = getPrice();
        int stock = getStock();

        PlaceholderBuilder placeholders = PlaceholderBuilder.builder()
            .put("{price}", String.format("%.2f", price))
            .put("{stock}", stock == -1 ? "Unlimited" : String.valueOf(stock));

        return ItemStackUtils.getItemStack(item, placeholders.build());
    }

    private double getPrice() {
        return itemData.getDataConfig().getDouble("price", 100.0);
    }

    private int getAmount() {
        return itemData.getDataConfig().getInt("amount", 1);
    }

    private int getStock() {
        return itemData.getDataConfig().getInt("stock", -1);  // -1 = unlimited
    }

    private void giveItems(Player player, int amount) {
        String materialName = itemData.getDataConfig().getString("material", "DIAMOND");
        Material material = Material.valueOf(materialName);
        player.getInventory().addItem(new ItemStack(material, amount));
    }
}
```

### Step 2: YAML Configuration

```yaml
items:
  diamond-purchase:
    type: purchase
    item:
      type: DIAMOND
      display: '&bDiamond'
      lore:
        - '&7Price: &6${price}'
        - '&7Stock: &e{stock}'
        - ''
        - '&aClick to buy!'
    slot: 13
    data:
      price: 1000.0
      amount: 1
      stock: -1
      material: DIAMOND
```

## Part 3: Toggle Item

### Step 1: Create Toggle Item

```java
package com.example.myplugin.menu.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import com.bafmc.bukkit.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ToggleItem extends AbstractItem<SettingsMenu> {

    @Override
    public String getType() {
        return "toggle";
    }

    @Override
    public void handleClick(ClickData data) {
        Player player = data.getPlayer();
        String settingKey = getSettingKey();

        // Toggle the setting
        boolean current = getSettingValue(player);
        setSettingValue(player, !current);

        // Play feedback sound
        if (!current) {
            player.playSound(player.getLocation(),
                org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
        } else {
            player.playSound(player.getLocation(),
                org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
        }

        // Refresh to show updated state
        menu.reopenInventory();
    }

    @Override
    public boolean canLoadItem() {
        return itemData.getCondition().check(menu.getOwner());
    }

    @Override
    public ItemStack setupItemStack() {
        Player player = menu.getOwner();
        boolean enabled = getSettingValue(player);

        // Get base item from config
        ItemStack baseItem = itemData.getItemStack(player);

        // Modify based on state
        return new ItemStackBuilder(baseItem)
            .type(enabled ? Material.LIME_DYE : Material.GRAY_DYE)
            .lore(enabled ? "§aEnabled" : "§cDisabled", "§7Click to toggle")
            .build();
    }

    private String getSettingKey() {
        return itemData.getDataConfig().getString("setting", "unknown");
    }

    private boolean getSettingValue(Player player) {
        String key = getSettingKey();
        return SettingsManager.get(player, key);
    }

    private void setSettingValue(Player player, boolean value) {
        String key = getSettingKey();
        SettingsManager.set(player, key, value);
    }
}
```

### Step 2: YAML Configuration

```yaml
items:
  sound-toggle:
    type: toggle
    item:
      type: NOTE_BLOCK
      display: '&eSounds'
    slot: 11
    data:
      setting: sounds_enabled

  notification-toggle:
    type: toggle
    item:
      type: PAPER
      display: '&eNotifications'
    slot: 13
    data:
      setting: notifications_enabled
```

## Part 4: Menu Navigation Item

### Step 1: Create Navigation Item

```java
package com.example.myplugin.menu.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.builder.MenuOpener;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OpenMenuItem extends AbstractItem<AbstractMenu> {

    @Override
    public String getType() {
        return "open-menu";
    }

    @Override
    public void handleClick(ClickData data) {
        Player player = data.getPlayer();
        String pluginName = getPluginName();
        String menuName = getMenuName();

        // Open target menu
        MenuOpener.builder()
            .player(player)
            .menuData(pluginName, menuName)
            .async(true)
            .build();
    }

    @Override
    public boolean canLoadItem() {
        return itemData.getCondition().check(menu.getOwner());
    }

    @Override
    public ItemStack setupItemStack() {
        return itemData.getItemStack(menu.getOwner());
    }

    private String getPluginName() {
        return itemData.getDataConfig().getString("plugin", "BafFramework");
    }

    private String getMenuName() {
        return itemData.getDataConfig().getString("menu");
    }
}
```

### Step 2: YAML Configuration

```yaml
items:
  go-to-shop:
    type: open-menu
    item:
      type: EMERALD
      display: '&aOpen Shop'
      lore:
        - '&7Click to browse items'
    slot: 13
    data:
      plugin: MyPlugin
      menu: shop
```

## Part 5: Paginated Item

For items that display lists, extend `AbstractPageItem`:

```java
package com.example.myplugin.menu.item;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ExtraData;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.AbstractPageItem;
import com.bafmc.bukkit.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class WarpListItem extends AbstractPageItem<MenuData, ExtraData, WarpPoint> {

    @Override
    public String getType() {
        return "warp-list";
    }

    @Override
    public List<WarpPoint> setupItems() {
        // Load warps from database/config
        return WarpManager.getWarps().stream()
            .filter(warp -> warp.isPublic() ||
                           warp.getOwner().equals(menu.getOwner().getUniqueId()))
            .collect(Collectors.toList());
    }

    @Override
    public ItemStack setupItemStack(int index, int slot, WarpPoint warp) {
        return new ItemStackBuilder()
            .type(Material.ENDER_PEARL)
            .display("§b" + warp.getName())
            .lore(
                "§7World: §f" + warp.getWorld(),
                "§7Location: §f" + formatLocation(warp),
                "",
                "§aClick to teleport"
            )
            .build();
    }

    @Override
    public void handleClick(ClickData data, WarpPoint warp) {
        if (warp != null) {
            Player player = data.getPlayer();
            player.teleport(warp.getLocation());
            player.closeInventory();
            player.sendMessage("§aTeleported to " + warp.getName());
        }
    }

    private String formatLocation(WarpPoint warp) {
        return String.format("%.0f, %.0f, %.0f",
            warp.getX(), warp.getY(), warp.getZ());
    }
}
```

### YAML Configuration

```yaml
# In a PageMenu
items:
  warp-list:
    type: warp-list
    item:
      type: ENDER_PEARL
      display: '§bWarp'
    slot: 10-16,19-25,28-34
    empty-item:
      type: STAINED_GLASS_PANE
      damage: 7
      display: ' '
```

## Part 6: Conditional Display Item

### Different Items Based on State

```java
public class StatusItem extends AbstractItem<AbstractMenu> {

    @Override
    public String getType() {
        return "status";
    }

    @Override
    public void handleClick(ClickData data) {
        // No click action - display only
    }

    @Override
    public boolean canLoadItem() {
        return true;  // Always show
    }

    @Override
    public ItemStack setupItemStack() {
        Player player = menu.getOwner();

        // Different display based on player state
        if (player.getHealth() < 5) {
            return new ItemStackBuilder()
                .type(Material.REDSTONE)
                .display("§c§lLow Health!")
                .lore("§7Health: §c" + player.getHealth())
                .build();
        } else if (player.getHealth() < 15) {
            return new ItemStackBuilder()
                .type(Material.GOLD_INGOT)
                .display("§e§lModerate Health")
                .lore("§7Health: §e" + player.getHealth())
                .build();
        } else {
            return new ItemStackBuilder()
                .type(Material.EMERALD)
                .display("§a§lGood Health")
                .lore("§7Health: §a" + player.getHealth())
                .build();
        }
    }
}
```

## Item Implementation Checklist

When creating custom items:

- [ ] **Extend correct base class** - `AbstractItem` or `AbstractPageItem`
- [ ] **Implement getType()** - Unique identifier used in YAML
- [ ] **Implement handleClick()** - Click behavior
- [ ] **Implement canLoadItem()** - Display condition (usually delegate to itemData.getCondition())
- [ ] **Implement setupItemStack()** - Create display ItemStack
- [ ] **Register in menu** - Call registerItem() in menu's registerItems()
- [ ] **Handle null cases** - Check for null data/config values
- [ ] **Provide feedback** - Sounds, messages for actions
- [ ] **Refresh when needed** - Call menu.reopenInventory() after state changes

## Best Practices

1. **Reuse DefaultItem** - Extend it for items that just need custom click handling
2. **Use getDataConfig()** - Store item-specific data in the `data` section
3. **Support placeholders** - Use `itemData.getItemStack(player)` for placeholder support
4. **Validate input** - Check permissions, balance, inventory space before actions
5. **Provide clear feedback** - Players should know what happened
6. **Keep items focused** - One item type per behavior
7. **Document type names** - Comment what type string to use in YAML

## Next Steps

- [Menu System Documentation](MENU_SYSTEM.md)
- [Page Menus Documentation](PAGE_MENUS.md)
- [Configuration Reference](CONFIGURATION.md)
