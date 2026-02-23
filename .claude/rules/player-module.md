---
paths:
  - "src/main/java/**/player/**"
---
# PlayerModule Conventions

## Player Expansion System
- All player data extensions extend `CEPlayerExpansion`
- Constructor MUST be `MyExpansion(CEPlayer cePlayer)` — instantiated via reflection
- Lifecycle: `onJoin()` to load data, `onQuit()` to save/cleanup
- Register class in `PlayerModule.setupPlayerExpansions()`: `CEPlayerExpansionRegister.register(MyExpansion.class)`
- Access: `CEAPI.getCEPlayer(player).getExpansion(MyExpansion.class)`

## Naming
- Expansions: `Player{Feature}` (e.g., `PlayerStorage`, `PlayerEquipment`, `PlayerPotion`)
- Registries: `{Feature}Register` (e.g., `CEPlayerExpansionRegister`, `PlayerSpecialMiningRegister`)

## Thread Safety
- Use `ConcurrentHashMap` for data accessed from both main and async threads
- CEPlayer map is accessed from async tasks — use thread-safe collections

## Important
- Expansions are per-player instances, not global singletons
- onJoin order = registration order; onQuit = reverse order
- `CEPlayerExpansionRegister` uses reflection: constructor signature must match exactly
