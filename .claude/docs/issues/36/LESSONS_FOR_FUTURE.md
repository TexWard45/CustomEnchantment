# Lessons for Future Work (from Phase 5)

## BafFramework CustomMenu Migration — Complete

All 5 phases of the CustomMenu migration are complete. This document captures lessons applicable to any future BafFramework menu work.

---

## Pattern Catalog

### 1. PAPI Items Must Override setupItemStack()

**When:** Any YAML item has `%placeholder%` patterns in lore/display/skull-owner.

**Why:** `AbstractItem.setupItemStack()` calls `ItemData.getItemStack()` without player → PlaceholderAPI receives null player → NPE.

**Fix:**
```java
@Override
public ItemStack setupItemStack() {
    return null; // Rendered dynamically with player context
}
```

Then render in the menu's `updateMenu()` via `itemData.getItemStack(owner)`.

**Affected items:** Any with `%customenchantment_...%`, `%player_name%`, or other PAPI placeholders.

---

### 2. updateSlots(name, null) Resets to YAML Default

**Do:** `updateSlots("slot-name", null)` — BafFramework renders the YAML template.

**Don't:** `inventory.setItem(slot, null)` — leaves the slot empty (no glass pane).

**Exception:** Per-index updates (extra slots) must use `inventory.setItem(slot, getTemplateItemStack(name))` because `updateSlots` affects ALL slots of that name.

---

### 3. Static Singleton Map Lifecycle

Every menu with a static map must implement:
- `putMenu()` — mark old menu as removed before overwrite
- `removeMenu()` — mark as removed
- `clearAll()` — for plugin disable/reload

Leaked BukkitRunnables are the #1 risk from missing cleanup.

---

### 4. Sound Format

```yaml
# CORRECT (BafFramework)
sound:
  open: 'ENTITY_EXPERIENCE_ORB_PICKUP'

# WRONG
sound: 'ENTITY_EXPERIENCE_ORB_PICKUP'
```

---

### 5. Template Items (No Slot)

Items without a `slot:` field in YAML are templates — fetched by code for dynamic rendering. Used for skin display overlays (`-equip`, `-swap`, `-no-skin` suffixes).

Access via: `menuData.getItemMap().get(name).getItemStack(owner)`

---

### 6. 1:1 Port Strategy

When migrating complex menus, copy logic exactly rather than refactoring:
- Eliminates logic bugs
- Only framework-level API differences cause issues
- Refactoring can happen in a separate PR after migration is verified

---

## Migration Checklist (for future BafFramework menus)

- [ ] Map all legacy API calls to BafFramework equivalents
- [ ] Check for PAPI placeholders in YAML items → override `setupItemStack()`
- [ ] Use `updateSlots(name, null)` for resetting slots (not `inventory.setItem(slot, null)`)
- [ ] Sound format: `sound: open: 'SOUND_NAME'`
- [ ] Copy Vietnamese/localized text exactly from legacy YAML
- [ ] Template items: no `slot:` field, fetched via `menuData.getItemMap()`
- [ ] Static maps: implement `putMenu/removeMenu/clearAll` with removed flag
- [ ] BukkitRunnables: add `!owner.isOnline()` guard
- [ ] Code review before first test
- [ ] Per-index slots: use `inventory.setItem()` + `getTemplateItemStack()` for resets
