# Utilities Reference

BafFramework provides utility classes in package `com.bafmc.bukkit.utils`.

---

## ColorUtils

Package: `com.bafmc.bukkit.utils.ColorUtils`

Translates color codes (`&a`, `&#FF5500`, hex, gradients) in strings.

```java
import com.bafmc.bukkit.utils.ColorUtils;

String colored = ColorUtils.t("&aGreen &6Gold &#FF5500Hex");
List<String> coloredList = ColorUtils.t(List.of("&aLine 1", "&bLine 2"));
```

Supports:
- Legacy codes: `&a`, `&l`, `&n`
- Hex codes: `&#FF5500`
- Gradient syntax: `<GRADIENTS:FF0000,00FF00>text</GRADIENTS>`
- Built-in prefixes: `SUCCESS `, `INFO `, `DANGER `, `WARN `, `EVENT `, `DEPRECATED `

---

## ConfigUtils

Package: `com.bafmc.bukkit.utils.ConfigUtils`

Load config files with default resources from JAR.

```java
import com.bafmc.bukkit.utils.ConfigUtils;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;

// Load config.yml with defaults from JAR resource
AdvancedFileConfiguration config = ConfigUtils.setupResource(
    plugin, "/config.yml", new File(plugin.getDataFolder(), "config.yml")
);

// Load default config.yml from plugin data folder
AdvancedFileConfiguration config = ConfigUtils.setupConfig(plugin);

// Copy non-YAML resource
ConfigUtils.setupOtherResource(plugin, "/data.json", new File(dataFolder, "data.json"));
```

---

## ItemStackBuilder

Package: `com.bafmc.bukkit.utils.ItemStackBuilder`

Fluent item creation with PlaceholderAPI support. See [PATTERNS.md](PATTERNS.md) for full usage.

```java
import com.bafmc.bukkit.utils.ItemStackBuilder;

ItemStack item = ItemStackBuilder.create()
    .setMaterial(Material.DIAMOND)
    .setDisplay("&bShiny Diamond")
    .setLore(List.of("&7A rare gem"))
    .setAmount(1)
    .setGlowing(true)
    .getItemStack();
```

Key setters: `setMaterial()`, `setAmount()`, `setDisplay()`, `setLore()`, `setEnchantments()`, `setFlags()`, `setUnbreakable()`, `setGlowing()`, `setSkullOwner()`, `setCustomModelData()`, `setColor()`, `setDamage()`

---

## ItemStackUtils

Package: `com.bafmc.bukkit.utils.ItemStackUtils`

Static utility methods for ItemStack operations.

| Method | Description |
|--------|-------------|
| `isEmpty(ItemStack)` | Check if null or AIR |
| `getItemStack(ItemStack, int)` | Clone with new amount |
| `getItemStacks(ItemStack, int)` | Split into max-stack-size chunks |
| `getGlowingItemStack(ItemStack)` | Clone with enchant glow |
| `toString(ItemStack)` | Serialize to YAML string |
| `fromString(String)` | Deserialize from YAML string |
| `getItemStackWithPlaceholder(ItemStack, Player)` | Apply PlaceholderAPI to display/lore |
| `getDisplayName(ItemStack)` | Get display name (or null) |
| `getLore(ItemStack)` | Get lore list (or null) |
| `isSimilarItem(ItemStack, ItemStack, boolean)` | Compare items (optional damage ignore) |
| `updateColorToItemStack(ItemStack)` | Translate color codes in display/lore |

```java
import com.bafmc.bukkit.utils.ItemStackUtils;

if (!ItemStackUtils.isEmpty(item)) {
    String yaml = ItemStackUtils.toString(item);
    ItemStack restored = ItemStackUtils.fromString(yaml);
    ItemStack glowing = ItemStackUtils.getGlowingItemStack(item);
}
```

---

## InventoryUtils

Package: `com.bafmc.bukkit.utils.InventoryUtils`

| Method | Description |
|--------|-------------|
| `addItem(Player, List<ItemStack>)` | Add items, drop overflow |
| `addItem(Player, ItemStack)` | Add single item |
| `isFullSlot(Player, int)` | Check if player has enough empty slots |
| `getEmptySlots(Player)` | Count empty inventory slots |
| `setItem(Inventory, List<Integer>, ItemStack)` | Set item in multiple slots |

```java
import com.bafmc.bukkit.utils.InventoryUtils;

if (!InventoryUtils.isFullSlot(player, 1)) {
    InventoryUtils.addItem(player, new ItemStack(Material.DIAMOND, 64));
}
int empty = InventoryUtils.getEmptySlots(player);
```

---

## MessageUtils

Package: `com.bafmc.bukkit.utils.MessageUtils`

Send color-translated messages.

```java
import com.bafmc.bukkit.utils.MessageUtils;
import com.bafmc.bukkit.feature.placeholder.Placeholder;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;

// Simple send (auto color translation)
MessageUtils.send(player, "&aWelcome to the server!");
MessageUtils.send(player, List.of("&aLine 1", "&bLine 2"));

// With placeholders
Placeholder ph = PlaceholderBuilder.builder().put("{coins}", "1000").build();
MessageUtils.send(player, List.of("&aYou have {coins} coins"), ph);

// With PlaceholderAPI support
MessageUtils.sendWithPlaceholder(player, List.of("&aHealth: %player_health%"), ph);

// Broadcast to all players + console
MessageUtils.broadcast(List.of("&6Server announcement!"));

// Console only
MessageUtils.send("&cServer starting...");
```

---

## StringUtils

Package: `com.bafmc.bukkit.utils.StringUtils`

| Method | Description |
|--------|-------------|
| `split(String, String, int)` | Split with regex, skip first N elements |
| `getIntList(String, String, String)` | Parse "1,3,5-10" into int list |

---

## FormatUtils

Package: `com.bafmc.bukkit.utils.FormatUtils`

| Method | Description | Example |
|--------|-------------|---------|
| `format(double)` | Format with `#.#` pattern | `1234.5` |
| `format1(double)` | Locale-formatted, max 2 decimals | `1,234.56` |
| `format2(double)` | Compact notation | `1.23M`, `5.5k`, `100` |

```java
import com.bafmc.bukkit.utils.FormatUtils;

String price = FormatUtils.format2(1500000);  // "1.5M"
String exact = FormatUtils.format1(1234.5);   // "1,234.5"
```

---

## DateUtils

Package: `com.bafmc.bukkit.utils.DateUtils`

| Method | Returns |
|--------|---------|
| `getCurrentTimeDateFormat()` | `"2024-01-15 14:30:00"` |
| `getCurrentDateFormat()` | `"2024-01-15"` |
| `getCurrentTimeFormat()` | `"14:30:00"` |
| `formatTimeDate(LocalDateTime)` | Formatted datetime string |
| `parseTimeDate(String)` | Epoch millis from datetime string |

Formatters: `TIME_DATE_FORMAT`, `DATE_FORMAT`, `TIME_FORMAT`, `TIME_MILLIS_FORMAT`

---

## ExpUtils

Package: `com.bafmc.bukkit.utils.ExpUtils`

Accurate total experience calculation for Minecraft's XP curve.

| Method | Description |
|--------|-------------|
| `getTotalExperience(Player)` | Get player's total XP across all levels |
| `setTotalExperience(Player, int)` | Set player's total XP (resets level/progress) |
| `getExpAtLevel(int)` | XP needed to go from level N to N+1 |
| `getExpToLevel(int)` | Total XP needed to reach level N from 0 |
| `getExpUntilNextLevel(Player)` | Remaining XP to next level |

---

## LocationUtils

Package: `com.bafmc.bukkit.utils.LocationUtils`

| Method | Description |
|--------|-------------|
| `toString(Location)` | Serialize to `"world,x,y,z"` |
| `fromString(String)` | Deserialize from `"world,x,y,z"` |
| `distance(Location, Location)` | Safe distance (MAX_VALUE if different worlds) |
| `isDifferentBlock(Location, Location)` | Check if locations are in different blocks |
| `isDifferentChunk(Location, Location)` | Check if locations are in different chunks |
| `getDirection(Location)` | Get cardinal direction (NORTH/EAST/SOUTH/WEST) |

---

## MaterialUtils

Package: `com.bafmc.bukkit.utils.MaterialUtils`

| Method | Description |
|--------|-------------|
| `isSimilar(Material, String)` | Check if material name contains string |
| `hasSimilarInList(Material, List<String>)` | Check against list of name patterns |

Uses XSeries (`com.cryptomorin.xseries.XMaterial`) for cross-version material resolution.

---

## FileUtils

Package: `com.bafmc.bukkit.utils.FileUtils`

| Method | Description |
|--------|-------------|
| `createFolder(File)` | Create directory if not exists |
| `createFile(File, boolean)` | Create file (optionally create parent dirs) |
| `copyFile(File, File)` | Copy file contents |
| `removeExtension(String)` | Strip file extension |
| `getExtension(String)` | Get file extension |
