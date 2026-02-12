---
description: Restate requirements, assess risks, and create step-by-step implementation plan. WAIT for user CONFIRM before touching any code.
---

# Plan Command

This command invokes the **planner** agent to create a comprehensive implementation plan before writing any code.

## What This Command Does

1. **Restate Requirements** - Clarify what needs to be built
2. **Identify Risks** - Surface potential issues and blockers
3. **Create Step Plan** - Break down implementation into phases
4. **Wait for Confirmation** - MUST receive user approval before proceeding

## When to Use

Use `/plan` when:
- Starting a new feature
- Making significant architectural changes
- Working on complex refactoring
- Multiple files/components will be affected
- Requirements are unclear or ambiguous

## How It Works

The planner agent will:

1. **Analyze the request** and restate requirements in clear terms
2. **Break down into phases** with specific, actionable steps
3. **Identify dependencies** between components
4. **Assess risks** and potential blockers
5. **Estimate complexity** (High/Medium/Low)
6. **Present the plan** and WAIT for your explicit confirmation

## Example Usage

```
User: /plan I need to add a custom enchant registration system

Agent (planner):
# Implementation Plan: Custom Enchant Registration System

## Requirements Restatement
- Allow registration of custom enchant behaviors
- Enchants trigger on combat/item events
- Config-driven enchant definitions
- Follow BafFramework ConditionHook/ExecuteHook patterns

## Implementation Phases

### Phase 1: Data Classes
- Create EnchantData with enchant name, level, and parameters
- Create EnchantConfig with @Configuration/@Path annotations

### Phase 2: Core Classes
- Create AbstractEnchant base class with getIdentifier() and apply()
- Create EnchantRegister extending SingletonRegister (strategy pattern)
- Register built-in enchants (damage boost, lifesteal, etc.)

### Phase 3: Event Integration
- Create EnchantListener for EntityDamageByEntityEvent and PlayerInteractEvent
- Check equipped items for custom enchant NBT tags
- Invoke registered enchant behaviors

### Phase 4: Module Registration
- Create EnchantModule extending PluginModule<YourPlugin>
- Register in YourPlugin.registerModules()
- Load enchant config in onReload()

## Dependencies
- BafFramework: StrategyRegister, ItemStackUtils
- External: None

## Risks
- MEDIUM: NMS compatibility for custom NBT tags across versions
- LOW: Performance with many enchants checked per combat event
- LOW: Enchant stacking/interaction edge cases

## Estimated Complexity: MEDIUM
- Core classes: 3-4 hours
- Event handling: 2-3 hours
- Testing: 2-3 hours
- Total: 7-10 hours

**WAITING FOR CONFIRMATION**: Proceed with this plan? (yes/no/modify)
```

## Important Notes

**CRITICAL**: The planner agent will **NOT** write any code until you explicitly confirm the plan with "yes" or "proceed" or similar affirmative response.

If you want changes, respond with:
- "modify: [your changes]"
- "different approach: [alternative]"
- "skip phase 2 and do phase 3 first"

## Integration with Other Commands

After planning:
- Use `/tdd` to implement with test-driven development
- Use `/gradle-build` if build errors occur
- Use `/code-review` to review completed implementation

## Related Agents

This command invokes the `planner` agent located at:
`.claude/agents/planner.md`
