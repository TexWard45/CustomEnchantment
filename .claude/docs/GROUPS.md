# CustomEnchantment Groups Reference

Complete reference for the group/rarity system in the CustomEnchantment plugin. Groups define rarity tiers, display formats, success/destroy rates, and value settings.

## Table of Contents

1. [Overview](#overview)
2. [Group Properties](#group-properties)
3. [Standard Groups](#standard-groups)
4. [Prime Groups](#prime-groups)
5. [Special Groups](#special-groups)
6. [Display Placeholders](#display-placeholders)
7. [Success/Destroy Rates](#successdestroy-rates)
8. [Priority System](#priority-system)
9. [Custom Group Creation](#custom-group-creation)
10. [Examples](#examples)

---

## Overview

Groups are defined in `src/main/resources/groups.yml` and serve as rarity tiers for enchantments. Each enchantment references a group to inherit its display formatting, success/destroy rates, and value settings.

---

## Group Properties

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `display` | String | Yes | Human-readable group name |
| `enchant-display` | String | Yes | Format for enchantment on item lore |
| `book-display` | String | Yes | Format for enchantment book name |
| `prefix` | String | Yes | Color prefix for enchantment name |
| `success` | String/Number | Yes | Success rate range (e.g., `0->50` or `100`) |
| `success-sigma` | Number | No | Gaussian sigma for success distribution |
| `destroy` | String/Number | Yes | Destroy rate range |
| `destroy-sigma` | Number | No | Gaussian sigma for destroy distribution |
| `valuable` | Number | Yes | Base value for tinkerer/trading |
| `craft` | Boolean | No | Whether enchantment books can be crafted |
| `filter` | Boolean | No | Whether to show in filter/search menus |
| `priority` | String | No | Processing priority level |

---

## Standard Groups

These are the main rarity tiers used for combat and equipment enchantments.

### Common (Thường)
```yaml
common:
  display: Thường
  enchant-display: "&#94a3b8%enchant_display% %enchant_level%"
  book-display: "&#94a3b8&l&n%enchant_display% %enchant_level%"
  prefix: "&#94a3b8"
  success: 0->50
  success-sigma: 0.5
  destroy: 0->100
  destroy-sigma: 0.5
  valuable: 0
  craft: true
  filter: true
```

| Property | Value |
|----------|-------|
| Color | Gray (#94a3b8) |
| Success Rate | 0-50% (avg ~25%) |
| Destroy Rate | 0-100% |
| Value | 0 |

---

### Rare (Hiếm)
```yaml
rare:
  display: Hiếm
  enchant-display: "&#22c55e%enchant_display% %enchant_level%"
  book-display: "&#22c55e&l&n%enchant_display% %enchant_level%"
  prefix: "&#22c55e"
  success: 0->75
  success-sigma: 0.5
  destroy: 5->100
  destroy-sigma: 0.5
  valuable: 1000
  craft: true
  filter: true
```

| Property | Value |
|----------|-------|
| Color | Green (#22c55e) |
| Success Rate | 0-75% (avg ~19%) |
| Destroy Rate | 5-100% |
| Value | 1000 |

---

### Epic (Sử thi)
```yaml
epic:
  display: Sử thi
  enchant-display: "&#0dcaf0%enchant_display% %enchant_level%"
  book-display: "&#0dcaf0&l&n%enchant_display% %enchant_level%"
  prefix: "&#0dcaf0"
  success: 0->50
  success-sigma: 0.5
  destroy: 10->100
  destroy-sigma: 0.5
  valuable: 2000
  craft: true
  filter: true
```

| Property | Value |
|----------|-------|
| Color | Cyan (#0dcaf0) |
| Success Rate | 0-50% (avg ~15%) |
| Destroy Rate | 10-100% |
| Value | 2000 |

---

### Legendary (Huyền thoại)
```yaml
legendary:
  display: Huyền thoại
  enchant-display: "&#ffc107%enchant_display% %enchant_level%"
  book-display: "&#ffc107&l&n%enchant_display% %enchant_level%"
  prefix: "&#ffc107"
  success: 0->75
  success-sigma: 0.4
  destroy: 15->100
  destroy-sigma: 0.5
  valuable: 3000
  craft: true
  filter: true
```

| Property | Value |
|----------|-------|
| Color | Yellow/Gold (#ffc107) |
| Success Rate | 0-75% (avg ~11%) |
| Destroy Rate | 15-100% |
| Value | 3000 |

---

### Supreme (Tối thượng)
```yaml
supreme:
  display: Tối thượng
  enchant-display: "&#ff7b00%enchant_display% %enchant_level%"
  book-display: "&#ff7b00&l&n%enchant_display% %enchant_level%"
  prefix: "&#ff7b00"
  success: 0->100
  success-sigma: 0.3
  destroy: 20->100
  destroy-sigma: 0.5
  valuable: 4000
  craft: true
  filter: true
```

| Property | Value |
|----------|-------|
| Color | Orange (#ff7b00) |
| Success Rate | 0-100% (avg ~7%) |
| Destroy Rate | 20-100% |
| Value | 4000 |

---

### Ultimate (Chiến thần)
```yaml
ultimate:
  display: Chiến thần
  enchant-display: "&#ef4444%enchant_display% %enchant_level%"
  book-display: "&#ef4444&l&n%enchant_display% %enchant_level%"
  prefix: "&#ef4444"
  success: 0->100
  success-sigma: 0.25
  destroy: 25->100
  destroy-sigma: 0.5
  valuable: 5000
  craft: true
  filter: true
```

| Property | Value |
|----------|-------|
| Color | Red (#ef4444) |
| Success Rate | 0-100% (avg ~3%) |
| Destroy Rate | 25-100% |
| Value | 5000 |

---

## Prime Groups

Prime (Breakthrough) groups are enhanced versions of standard groups with guaranteed success.

### Prime Common (Thường Đột phá)
```yaml
primecommon:
  display: Thường (Đột phá)
  enchant-display: "&#94a3b8&k&l|&r <GRADIENT:94a3b8>&l%enchant_display_half_1%</GRADIENT:e2e8f0><GRADIENT:e2e8f0>&l%enchant_display_half_2% %enchant_level%</GRADIENT:94a3b8>&r &#94a3b8&k&l|"
  book-display: "&#94a3b8&k&l|&r <GRADIENT:94a3b8>&l&n%enchant_display_half_1%</GRADIENT:e2e8f0><GRADIENT:e2e8f0>&l&n%enchant_display_half_2% %enchant_level%</GRADIENT:94a3b8>&r &#94a3b8&k&l|"
  prefix: "&f"
  success: 100
  destroy: 0
  valuable: 0
  craft: false
```

| Property | Value |
|----------|-------|
| Color | Gradient gray |
| Success Rate | 100% |
| Destroy Rate | 0% |
| Craftable | No |

### Other Prime Groups

| Group | Display | Gradient Colors |
|-------|---------|-----------------|
| `primerare` | Hiếm (Đột phá) | #16a34a -> #bbf7d0 |
| `primeepic` | Sử thi (Đột phá) | #0891b2 -> #a5f3fc |
| `primelegendary` | Huyền thoại (Đột phá) | #facc15 -> #fef08a |
| `primesupreme` | Tối thượng (Đột phá) | #ff7b00 -> #fed7aa |
| `primeultimate` | Chiến thần (Đột phá) | #dc2626 -> #fecaca |

All prime groups have:
- 100% success rate
- 0% destroy rate
- Cannot be crafted
- Same valuable as base tier

---

## Special Groups

### Event (Sự kiện)
```yaml
event:
  display: Sự kiện
  enchant-display: "&#ec4899%enchant_display% %enchant_level%"
  book-display: "&#ec4899&l&n%enchant_display% %enchant_level%"
  prefix: "&#ec4899"
  success: 100
  destroy: 0
  valuable: 10000
  craft: true
```

| Property | Value |
|----------|-------|
| Color | Pink (#ec4899) |
| Success Rate | 100% |
| Destroy Rate | 0% |
| Value | 10000 |
| Purpose | Event-exclusive enchantments |

---

### Limit (Giới hạn)
```yaml
limit:
  display: Giới hạn
  enchant-display: "&#a855f7%enchant_display% %enchant_level%"
  book-display: "&#a855f7&l&n%enchant_display% %enchant_level%"
  prefix: "&#a855f7"
  success: 100
  destroy: 0
  valuable: 15000
  craft: true
```

| Property | Value |
|----------|-------|
| Color | Purple (#a855f7) |
| Success Rate | 100% |
| Destroy Rate | 0% |
| Value | 15000 |
| Purpose | Limited edition enchantments |

---

### Mask (Mặt nạ)
```yaml
mask:
  display: Mặt nạ
  enchant-display: "&5%enchant_display% %enchant_level%"
  book-display: "&5&l&n%enchant_display% %enchant_level%"
  prefix: "&5"
  success: 100
  destroy: 0
  valuable: 20000
  priority: HIGHEST
```

| Property | Value |
|----------|-------|
| Color | Dark Purple (&5) |
| Priority | HIGHEST |
| Purpose | Mask item enchantments |

---

### Artifact (Cổ vật)
```yaml
artifact:
  display: Cổ vật
  enchant-display: "&f%enchant_display% %enchant_level%"
  book-display: "&f&l&n%enchant_display% %enchant_level%"
  prefix: "&f"
  success: 100
  destroy: 0
  valuable: 20000
  priority: HIGHEST
```

| Property | Value |
|----------|-------|
| Color | White (&f) |
| Priority | HIGHEST |
| Purpose | Artifact/relic items |

---

### Set (Bộ trang bị)
```yaml
set:
  display: Bộ trang bị
  enchant-display: "&f%enchant_display% %enchant_level%"
  book-display: "&f&l&n%enchant_display% %enchant_level%"
  prefix: "&f"
  success: 100
  destroy: 0
  valuable: 50000
  priority: MORNITOR
```

| Property | Value |
|----------|-------|
| Priority | MORNITOR |
| Purpose | Set bonus enchantments |

---

### Tool (Bộ công cụ)
```yaml
tool:
  display: Bộ công cụ
  enchant-display: "&f%enchant_display% %enchant_level%"
  book-display: "&f&l&n%enchant_display% %enchant_level%"
  prefix: "&f"
  success: 100
  destroy: 0
  valuable: 50000
  priority: MORNITOR
```

| Property | Value |
|----------|-------|
| Purpose | Tool set enchantments |

---

### Disable (Không hoạt động)
```yaml
disable:
  display: Không hoạt động
  enchant-display: "&8%enchant_display% %enchant_level%"
  book-display: "&8&l&n%enchant_display% %enchant_level%"
  prefix: "&8&m"
  success: 0
  destroy: 100
  valuable: 0
```

| Property | Value |
|----------|-------|
| Color | Gray with strikethrough (&8&m) |
| Success Rate | 0% |
| Destroy Rate | 100% |
| Purpose | Disabled enchantments |

---

### Test (Thử nghiệm)
```yaml
test:
  display: Thử nghiệm
  enchant-display: "&8%enchant_display% %enchant_level%"
  book-display: "&8&l&n%enchant_display% %enchant_level%"
  prefix: "&8"
  success: 0
  destroy: 100
  valuable: 0
```

| Property | Value |
|----------|-------|
| Purpose | Testing enchantments (not for production) |

---

## Display Placeholders

### Available Placeholders

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%enchant_display%` | Full enchantment name | "Sharpness" |
| `%enchant_level%` | Level in Roman numerals | "III" |
| `%enchant_display_half_1%` | First half of name | "Sharp" |
| `%enchant_display_half_2%` | Second half of name | "ness" |

### Color Codes

| Code | Color | Hex Equivalent |
|------|-------|----------------|
| `&0` | Black | #000000 |
| `&1` | Dark Blue | #0000AA |
| `&2` | Dark Green | #00AA00 |
| `&3` | Dark Aqua | #00AAAA |
| `&4` | Dark Red | #AA0000 |
| `&5` | Dark Purple | #AA00AA |
| `&6` | Gold | #FFAA00 |
| `&7` | Gray | #AAAAAA |
| `&8` | Dark Gray | #555555 |
| `&9` | Blue | #5555FF |
| `&a` | Green | #55FF55 |
| `&b` | Aqua | #55FFFF |
| `&c` | Red | #FF5555 |
| `&d` | Light Purple | #FF55FF |
| `&e` | Yellow | #FFFF55 |
| `&f` | White | #FFFFFF |

### Formatting Codes

| Code | Effect |
|------|--------|
| `&l` | Bold |
| `&n` | Underline |
| `&o` | Italic |
| `&m` | Strikethrough |
| `&k` | Obfuscated (magic text) |
| `&r` | Reset |

### Hex Color Format
```
&#RRGGBB
```
Example: `&#ff7b00` for orange

### Gradient Format
```
<GRADIENT:start_color>text</GRADIENT:end_color>
```
Example: `<GRADIENT:94a3b8>&l%enchant_display%</GRADIENT:e2e8f0>`

---

## Success/Destroy Rates

### Rate Format

**Fixed Rate:**
```yaml
success: 100    # Always 100%
destroy: 0      # Never destroys
```

**Range (Uniform Distribution):**
```yaml
success: 0->50   # Random between 0-50%
```

### Gaussian Distribution

The `sigma` values control how rates cluster around the average:

- `sigma: 0.5` - Moderate clustering (default)
- `sigma: 0.4` - More clustering toward average
- `sigma: 0.3` - Strong clustering
- `sigma: 0.25` - Very strong clustering

**Example:**
```yaml
success: 0->100
success-sigma: 0.25   # Most rolls will be near 50%
```

### Rate Summary by Tier

| Group | Success Range | Avg Success | Destroy Range |
|-------|---------------|-------------|---------------|
| Common | 0-50% | ~25% | 0-100% |
| Rare | 0-75% | ~19% | 5-100% |
| Epic | 0-50% | ~15% | 10-100% |
| Legendary | 0-75% | ~11% | 15-100% |
| Supreme | 0-100% | ~7% | 20-100% |
| Ultimate | 0-100% | ~3% | 25-100% |
| Prime/Event/Limit | 100% | 100% | 0% |

---

## Priority System

Priority determines the order enchantments are processed.

| Priority | Order | Usage |
|----------|-------|-------|
| `LOWEST` | First | Pre-processing effects |
| `LOW` | Second | Early effects |
| `NORMAL` | Third | Standard enchantments (default) |
| `HIGH` | Fourth | Important effects |
| `HIGHEST` | Fifth | Critical effects (masks, artifacts) |
| `MORNITOR` | Last | Post-processing (sets, tools) |

**Note:** Higher priority enchantments are processed later, allowing them to override earlier effects.

---

## Custom Group Creation

### Basic Template
```yaml
mygroup:
  display: My Group
  enchant-display: "&6%enchant_display% %enchant_level%"
  book-display: "&6&l&n%enchant_display% %enchant_level%"
  prefix: "&6"
  success: 50
  destroy: 25
  valuable: 1000
  craft: true
  filter: true
```

### Gradient Template
```yaml
mygroup:
  display: My Group
  enchant-display: "<GRADIENT:ff0000>&l%enchant_display% %enchant_level%</GRADIENT:00ff00>"
  book-display: "<GRADIENT:ff0000>&l&n%enchant_display% %enchant_level%</GRADIENT:00ff00>"
  prefix: "&c"
  success: 100
  destroy: 0
  valuable: 5000
```

### Non-Craftable High-Value Template
```yaml
exclusive:
  display: Exclusive
  enchant-display: "&#gold%enchant_display% %enchant_level%"
  book-display: "&#gold&l&n%enchant_display% %enchant_level%"
  prefix: "&#gold"
  success: 100
  destroy: 0
  valuable: 100000
  craft: false
  filter: true
  priority: HIGHEST
```

---

## Examples

### Using Groups in Enchantments

```yaml
# In enchantment config file
my_enchant:
  group: legendary           # References the legendary group
  display: My Enchant
  # ... rest of enchantment config
```

### Custom Display Override

```yaml
my_enchant:
  group: rare
  display: Special Enchant
  custom-display-lore: "&b&l%enchant_display% %enchant_level%"  # Override group format
  # ...
```

### Group Hierarchy for Item Lore

When displaying enchantments on items, they are typically sorted by group rarity:
1. Ultimate (highest)
2. Supreme
3. Legendary
4. Epic
5. Rare
6. Common (lowest)

Special groups (Event, Limit, Mask, Artifact, Set) may appear separately or at the top.
