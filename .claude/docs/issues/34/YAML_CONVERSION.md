# CE Anvil YAML Conversion Reference

## Legacy Layout → Slot Numbers

### Layout Grid

```
Row 0: "ooooooooo"  →  0  1  2  3  4  5  6  7  8
Row 1: "ooosoSooo"  →  9 10 11 [12] 13 [14] 15 16 17
Row 2: "opxxxxxno"  → 18 [19] [20][21][22][23][24] [25] 26
Row 3: "ooooaoooo"  → 27 28 29 30 [31] 32 33 34 35
Row 4: "ooooRoooo"  → 36 37 38 39 [40] 41 42 43 44
```

### Character → Slot Mapping

| Char | Name | Slot(s) |
|------|------|---------|
| `o` | border | 0,1,2,3,4,5,6,7,8,9,10,11,13,15,16,17,18,26,27,28,29,30,32,33,34,35,36,37,38,39,41,42,43,44 |
| `s` | slot1 | 12 |
| `S` | slot2 | 14 |
| `p` | previous-page | 19 |
| `x` (1st) | preview1 | 20 |
| `x` (2nd) | preview2 | 21 |
| `x` (3rd) | preview3 | 22 |
| `x` (4th) | preview4 | 23 |
| `x` (5th) | preview5 | 24 |
| `n` | next-page | 25 |
| `a` | confirm | 31 |
| `R` | return | 40 |

---

## New BafFramework YAML

```yaml
type: 'ce-anvil'
title: '&8&lLo ren'
row: 5
data:
  sound: ENTITY_EXPERIENCE_ORB_PICKUP
  default-view:
    enchant-group:
      - "common"
      - "rare"
      - "epic"
      - "legendary"
      - "supreme"
      - "ultimate"
      - "event"
  # Preview display order mapping: index 0→preview3, 1→preview2, etc.
  # This preserves the center-first spiral pattern
  preview-index-order: [3, 2, 4, 1, 5]

items:
  # ============================================================
  # BORDER - Static decorative items
  # ============================================================
  border:
    type: default
    item:
      type: RED_STAINED_GLASS_PANE
      amount: 1
      display: '&7'
    slot: 0,1,2,3,4,5,6,7,8,9,10,11,13,15,16,17,18,26,27,28,29,30,32,33,34,35,36,37,38,39,41,42,43,44

  # ============================================================
  # INTERACTIVE SLOTS - Items that respond to clicks
  # ============================================================
  slot1:
    type: anvil-slot
    item:
      type: LIGHT_GRAY_STAINED_GLASS_PANE
      amount: 1
      display: '&fO 1'
      lore:
        - ''
        - '&eNhap chon trang bi bat ki tu kho do'
    slot: 12
    data:
      slot-name: slot1

  slot2:
    type: anvil-slot
    item:
      type: LIGHT_GRAY_STAINED_GLASS_PANE
      amount: 1
      display: '&fO 2'
      lore:
        - ''
        - '&eNhap chon vat pham su dung tu kho do'
    slot: 14
    data:
      slot-name: slot2

  # ============================================================
  # PREVIEW SLOTS - Dynamic display area
  # ============================================================
  preview1:
    type: anvil-preview
    item:
      type: BLACK_STAINED_GLASS_PANE
      amount: 1
      display: '&fXem truoc'
    slot: 20
    data:
      preview-name: preview1

  preview2:
    type: anvil-preview
    item:
      type: BLACK_STAINED_GLASS_PANE
      amount: 1
      display: '&fXem truoc'
    slot: 21
    data:
      preview-name: preview2

  preview3:
    type: anvil-preview
    item:
      type: BLACK_STAINED_GLASS_PANE
      amount: 1
      display: '&fXem truoc'
    slot: 22
    data:
      preview-name: preview3

  preview4:
    type: anvil-preview
    item:
      type: BLACK_STAINED_GLASS_PANE
      amount: 1
      display: '&fXem truoc'
    slot: 23
    data:
      preview-name: preview4

  preview5:
    type: anvil-preview
    item:
      type: BLACK_STAINED_GLASS_PANE
      amount: 1
      display: '&fXem truoc'
    slot: 24
    data:
      preview-name: preview5

  # ============================================================
  # CONFIRM BUTTON - Active confirm slot
  # ============================================================
  confirm:
    type: anvil-confirm
    item:
      type: ANVIL
      amount: 1
      display: '&eHay chon vat pham va trang bi tuong ung'
      lore:
        - '&echo 2 o tren'
    slot: 31

  # ============================================================
  # CONFIRM TEMPLATES - No slot, appearance-only definitions
  # Views fetch these as ItemStack templates
  # ============================================================
  confirm-enchant-point:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eXac nhan ep ngoc dot pha'
      lore:
        - ''
        - '&fNhap de ep'

  confirm-book:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eXac nhan ep sach phu phep'
      lore:
        - ''
        - '&fNhap de ep'

  confirm-remove-enchant:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eXac nhan tach sach phu phep'
      lore:
        - ''
        - '&fNhap de tach'

  confirm-lore-format:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eXac nhan thay doi dinh dang vat pham'
      lore:
        - ''
        - '&fNhap de xac nhan'

  confirm-protect-dead:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eXac nhan ep ngoc bao ve'
      lore:
        - ''
        - '&fNhap de ep'

  confirm-gem-drill:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eXac nhan duc lo o ngoc'
      lore:
        - ''
        - "&aTi le thanh cong: &f100%'"
        - ''
        - '&fNhap de khoan'

  confirm-gem-drill-max:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&cDa dat so luong o ngoc toi da'

  confirm-gem-drill-with-chance:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eXac nhan duc lo o ngoc'
      lore:
        - ''
        - '&aTi le thanh cong: &f{chance}%'
        - ''
        - '&c&oThat bai se mat May Khoan'
        - ''
        - '&fNhap de khoan'

  confirm-gem:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eXac nhan kham ngoc'
      lore:
        - ''
        - '&fNhap de kham'

  confirm-remove-protect-dead:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eXac nhan tach ngoc bao ve'
      lore:
        - ''
        - '&fNhap de tach'

  confirm-remove-gem:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eXac nhan tach ngoc kham'
      lore:
        - ''
        - '&fNhap de tach'

  confirm-remove-enchant-point:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eXac nhan tach ngoc dot pha'
      lore:
        - ''
        - '&fNhap de tach'

  confirm-protect-destroy:
    type: default
    item:
      type: ANVIL
      amount: 1
      display: '&eXac nhan ep cuon giay bao ve'
      lore:
        - ''
        - '&fNhap de ep'

  # ============================================================
  # PAGINATION
  # ============================================================
  previous-page:
    type: anvil-page
    item:
      type: RED_STAINED_GLASS_PANE
      amount: 1
      display: '&7'
    slot: 19
    data:
      page-action: previous-page

  next-page:
    type: anvil-page
    item:
      type: RED_STAINED_GLASS_PANE
      amount: 1
      display: '&7'
    slot: 25
    data:
      page-action: next-page

  # Page indicator templates (no slot - used by views dynamically)
  has-previous-page:
    type: default
    item:
      type: YELLOW_STAINED_GLASS_PANE
      amount: 1
      display: '&cTrang truoc'

  has-next-page:
    type: default
    item:
      type: GREEN_STAINED_GLASS_PANE
      amount: 1
      display: '&aTrang ke'

  # ============================================================
  # RETURN BUTTON
  # ============================================================
  return:
    type: default
    item:
      type: GRAY_STAINED_GLASS_PANE
      amount: 1
      display: '&cTro ve'
    slot: 40
    execute:
      condition:
        - 'CLICK_TYPE LEFT'
      true-execute:
        - 'RETURN_MENU'
```

---

## Key Conversion Notes

### 1. Template Items (No Slot)

Items that have NO `slot:` field serve as **templates**. They define the appearance
of dynamic items that views place at specific slots at runtime.

In code, these are accessed via:
```java
menuData.getItemMap().get("confirm-book").getItemStackBuilder().getItemStack()
```

### 2. Sound Handling

Legacy: `settings.sound: ENTITY_EXPERIENCE_ORB_PICKUP`
New: Moved to `data.sound` - must be handled in `setupMenu()`:
```java
String soundName = menuData.getDataConfig().getString("sound");
if (soundName != null) {
    Sound sound = Sound.valueOf(soundName);
    owner.playSound(owner.getLocation(), sound, 1.0f, 1.0f);
}
```

### 3. Actions Removal

Legacy `settings.actions` controlled click cancellation. In BafFramework, the framework
automatically cancels all inventory actions by default. No configuration needed.

### 4. Vietnamese Text Encoding

The YAML uses Vietnamese text with diacritical marks. Ensure file is saved as UTF-8.
The color codes (`&8&l`, `&e`, `&f`, `&c`, etc.) are processed by `ColorUtils.t()`.

### 5. Return Button

The return button uses framework's built-in `RETURN_MENU` execute. This should work
with the new system's DefaultItem execute handling:

```yaml
return:
    type: default
    item: ...
    slot: 40
    execute:
      condition:
        - 'CLICK_TYPE LEFT'
      true-execute:
        - 'RETURN_MENU'
```
