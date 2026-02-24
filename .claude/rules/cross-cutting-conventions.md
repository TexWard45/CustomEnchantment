# Cross-Cutting Conventions

Rules that span multiple modules. Violating these causes bugs that summaries and codemaps alone cannot prevent.

## 1. Player Messages — CEMessageKey + messages.yml

All player-facing messages go through the centralized message system.

### Decision Table

| Scenario | API | Example |
|----------|-----|---------|
| Player-facing feedback | `CustomEnchantmentMessage.send(player, CEMessageKey.X)` | Combat warnings, menu errors |
| Player-facing with placeholders | `CustomEnchantmentMessage.send(player, CEMessageKey.X, placeholderMap)` | Messages needing player name, amounts |
| Dynamic CE-item messages | `CustomEnchantmentMessage.send(player, CEMessageKey.ceItem(type, suffix))` | Item-specific messages varying by type |
| Server log / debug | `plugin.getLogger().info(...)` / `.warning(...)` / `.severe(...)` | Startup, config errors, internal state |

### Adding a New Message

1. Add enum constant to `CEMessageKey` (`src/main/java/.../constant/CEMessageKey.java`)
2. Add YAML entry to `messages.yml` (path matches the key, e.g. `combat.require-weapon`)
3. Call `CustomEnchantmentMessage.send(player, CEMessageKey.YOUR_KEY)`

### Built-in Prefixes (in messages.yml values)

| Prefix | Color | Use For |
|--------|-------|---------|
| `SUCCESS ` | Green | Successful actions |
| `INFO ` | Cyan | Informational |
| `DANGER ` | Red | Errors, failures |
| `WARN ` | Yellow | Warnings |
| `EVENT ` | Purple | Event notifications |
| `DEPRECATED ` | Gray | Deprecated feature notices |
| `MESSAGE ` | None | Raw message (no prefix) |

### NEVER

- `player.sendMessage("inline string")` — all active listener/command code uses `CustomEnchantmentMessage.send()`
- Hardcode color codes in messages — use `messages.yml` with prefixes instead
- Put player-facing text in `config.yml` — it belongs in `messages.yml`

## 2. Color Translation

```java
// CORRECT — the only method
ColorUtils.t(text)
```

### NEVER

- `ColorUtils.color()` — **does not exist**, will cause compilation error
- `ChatColor.translateAlternateColorCodes()` — use `ColorUtils.t()` instead
- Direct `ChatColor` constants for dynamic text — use `ColorUtils.t()` with `&` codes

## 3. CE Item Detection

Use `CEAPI` (public API) for all item detection in feature code.

### Decision Table

| Need | Method | Returns |
|------|--------|---------|
| Get the CE item object | `CEAPI.getCEItem(itemStack)` | `CEItem` or `null` |
| Get CE item of specific type | `CEAPI.getCEItem(itemStack, "TYPE")` | `CEItem` or `null` |
| Boolean check only | `CEAPI.isCEItem(itemStack)` | `boolean` |
| Boolean check with type | `CEAPI.isCEItem(itemStack, "TYPE")` | `boolean` |
| Get item type string | `CEAPI.getCEItemType(itemStack)` | `String` or `null` |
| Get simplified item data | `CEAPI.getCEItemSimple(itemStack)` | `CEItemSimple` or `null` |

### Common Pattern

```java
CEItem ceItem = CEAPI.getCEItem(itemStack);
if (ceItem == null) return; // not a CE item
// ... use ceItem
```

### NEVER

- `CECraftItemStackNMS.isSetCECompound()` — unused legacy NMS method, zero usages in codebase
- `CEItemRegister.getCEItem()` directly — use `CEAPI` wrapper (it delegates to `CEItemRegister` internally)
- NMS-level checks in feature code — `CEAPI` abstracts this away

## 4. Event Handler Annotations

### Decision Table

| Handler Type | Annotation | Why |
|--------------|-----------|-----|
| Most handlers (default) | `@EventHandler(ignoreCancelled = true)` | Skip cancelled events; 59% of handlers |
| UI/menu click handlers | `@EventHandler` (bare) | Must process clicks even if "cancelled" by Bukkit |
| Post-event observation | `@EventHandler(priority = EventPriority.MONITOR)` | Read-only observation; most common priority (50%) |
| Damage processing (attack) | `@EventHandler(priority = EventPriority.HIGHEST)` | EntityListener attack flow needs late execution |
| Guard/protection checks | `@EventHandler(priority = EventPriority.LOWEST)` | Cancel early before other plugins process |

### Rules

- **Default to `ignoreCancelled = true`** unless you have a specific reason not to
- **MONITOR priority** is read-only — never modify the event in a MONITOR handler
- **HIGHEST/LOWEST** are reserved — only use for damage processing or early cancellation
- Bare `@EventHandler` is for UI handlers (BannerListener, OutfitListener, CMenuListener patterns)

## 5. Config vs Messages File Split

| File | Contains | Access Pattern |
|------|----------|---------------|
| `config.yml` | Behavior toggles, numeric values, item lists, feature settings | `plugin.getMainConfig().getXxx()` |
| `messages.yml` | All player-facing text with color codes and prefixes | `CustomEnchantmentMessage.send(player, key)` |

### Rules

- **config.yml** = "how the plugin behaves" (cooldowns, toggle flags, item materials, numeric thresholds)
- **messages.yml** = "what the player sees" (feedback, warnings, errors, notifications)
- Config classes use `@Getter @Configuration` with `@Path` annotations
- Listeners access config via stored `plugin` field: `plugin.getMainConfig()`
- Never put translatable/player-facing strings in `config.yml`
- Never put behavior-controlling values in `messages.yml`
