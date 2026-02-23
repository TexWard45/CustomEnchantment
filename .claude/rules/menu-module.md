---
paths:
  - "src/main/java/**/menu/**"
  - "src/main/resources/menu-new/**"
---
# MenuModule Conventions

## Menu Pattern
- Menus extend `AbstractMenu<MenuData, {Name}ExtraData>`
- Menu items extend `AbstractItem<{Menu}CustomMenu>`
- Register via `MenuRegister.instance().registerStrategy(MyCustomMenu.class)`
- Menu folder: `menu-new/` subdirectory

## Naming
- Menus: `{Name}CustomMenu` (e.g., `BookCraftCustomMenu`, `CEAnvilCustomMenu`)
- Extra data: `{Name}ExtraData` (e.g., `BookCraftExtraData`)
- Menu items: `{Name}Item` (e.g., `BookSlotItem`, `AnvilConfirmItem`)
- Handlers: `{Name}Handler` (e.g., `BookHandler`, `GemHandler`)

## Key Methods
- `getType()` — maps to YAML item name
- `registerItems()` — register all AbstractItem subclasses
- `setupItems()` — configure items from YAML
- `handlePlayerInventoryClick()` — handle clicks in player inventory
- `handleClose()` — cleanup on menu close

## CEAnvil Handler Pattern
- Handlers implement `Slot2Handler` interface
- Register: `CEAnvilCustomMenu.registerHandler(CEItemType.BOOK, BookHandler::new)`
- Methods: `isSuitable(CEItem)`, `updateView(menu)`, `apply(item1, item2)`

## Data Lifecycle
- ExtraData created per menu open, persists during interaction
- Menu item prep is async-safe; player interactions must use `owner` field
