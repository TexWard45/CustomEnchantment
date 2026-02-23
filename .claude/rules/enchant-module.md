---
paths:
  - "src/main/java/**/enchant/**"
  - "src/main/resources/enchantment/**"
---
# EnchantModule Conventions

## Effect & Condition Hooks
- Effects extend `EffectHook`: implement `getIdentify()`, `setup(String[] args)`, `execute(CEFunctionData)`
- Conditions extend `ConditionHook`: implement `getIdentify()`, `setup(String[] args)`, `match(CEFunctionData)`
- Identifier format: `SCREAMING_SNAKE_CASE` (e.g., `ADD_POTION`, `ON_FIRE`)
- Register in `EnchantModule.onEnable()` via `new MyEffect().register()`
- Both hooks implement `Cloneable`
- `isAsync()` returns true by default in EffectHook (override to false for main-thread effects)

## Naming
- Effects: `Effect{What}` (e.g., `EffectAddPotion`, `EffectOnFire`, `EffectExplosion`)
- Conditions: `Condition{What}` (e.g., `ConditionHold`, `ConditionHealth`)
- Core data: `CE{Concept}` prefix (e.g., `CEEnchant`, `CEFunction`, `CELevel`)

## Data Flow
- `CEFunctionData` is passed to every Effect/Condition execution
- Use `CEFunctionData` methods to get player, enemy, living entity
- ArgumentLine splits on `:` delimiter (BafFramework convention)
- ConditionData is a HashMap with case-insensitive keys (all lowercased)

## Config
- Enchantment YAML files in `src/main/resources/enchantment/`
- Each file defines enchantments for an item type (sword.yml, bow.yml, etc.)
