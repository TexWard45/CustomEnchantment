# Phase 10 — Multi-Plugin & Cross-Project Support

## Purpose

Enable a shared knowledge layer across all plugins in the ecosystem. Detect reusable components, recommend code sharing, handle version differences, and provide a unified search across all projects.

## Design

### Ecosystem Architecture

```
┌─────────────────────────────────────────────────┐
│              SHARED KNOWLEDGE LAYER              │
│                                                  │
│  BafFramework API    Bukkit/Paper patterns       │
│  Shared conventions  Reusable component registry │
│  Cross-project deps  Version compatibility map   │
└────────────────┬────────────────────────────────┘
                 │
    ┌────────────┼────────────────┐
    │            │                │
    ▼            ▼                ▼
┌─────────┐ ┌──────────┐ ┌─────────────┐
│ Custom   │ │ Custom   │ │ Custom      │
│ Enchant  │ │ Menu     │ │ Shop        │
│          │ │          │ │             │
│ Index    │ │ Index    │ │ Index       │
│ Summaries│ │ Summaries│ │ Summaries   │
│ Cache    │ │ Cache    │ │ Cache       │
└─────────┘ └──────────┘ └─────────────┘
```

### Shared Knowledge Components

#### 1. BafFramework API Reference (Shared)

```
~/.claude/shared/bafframework/
├── API-REFERENCE.md            # Full API surface
├── MODULE-LIFECYCLE.md         # PluginModule, onEnable/onReload/etc.
├── CONFIG-SYSTEM.md            # @Configuration, @Path patterns
├── COMMAND-SYSTEM.md           # AdvancedCommandBuilder
├── FEATURE-SYSTEM.md           # ConditionHook, EffectHook, ExecuteHook
├── UTILITIES.md                # ColorUtils, ItemStackBuilder, etc.
└── VERSION-CHANGELOG.md        # API changes between versions
```

Distribution methods:
- **Symlink**: `ln -s ~/.claude/shared/bafframework .claude/docs/bafframework`
- **Git submodule**: Shared docs as a separate repo
- **User-level CLAUDE.md**: Reference in `~/.claude/CLAUDE.md`

#### 2. Bukkit/Paper Pattern Library (Shared)

```
~/.claude/shared/bukkit-patterns/
├── THREADING.md                # Main thread rules, async patterns
├── EVENTS.md                   # Event handling best practices
├── ITEMS.md                    # ItemStack creation, NBT, PersistentDataContainer
├── INVENTORY.md                # Inventory management patterns
├── COMMANDS.md                 # Command registration (Brigadier vs legacy)
├── PERSISTENCE.md              # Database, file, config patterns
├── NMS.md                      # NMS access patterns (version-specific)
└── PERFORMANCE.md              # Server performance considerations
```

#### 3. Cross-Project Convention Registry

```json
// ~/.claude/shared/conventions.json
{
  "naming": {
    "modules": "{Feature}Module",
    "configs": "{Feature}Config",
    "listeners": "{Feature}Listener",
    "managers": "{Feature}Manager"
  },
  "patterns": {
    "singleton": "instance() method + null in onDisable()",
    "config-loading": "ConfigUtils.setupResource() in onReload()",
    "listener-registration": "self-register in constructor"
  },
  "testing": {
    "framework": "JUnit 5 + MockBukkit 4.x",
    "package": "org.mockbukkit.mockbukkit",
    "setup": "@BeforeAll with isMocked() guard"
  }
}
```

#### 4. Reusable Component Registry

Automatically detect components used across multiple plugins:

```json
// ~/.claude/shared/reusable-components.json
{
  "components": [
    {
      "name": "ConditionHook pattern",
      "source": "BafFramework",
      "usedBy": ["CustomEnchantment", "CustomMenu"],
      "description": "Extensible condition check system",
      "howToUse": "Extend ConditionHook, implement getIdentifier() + check()"
    },
    {
      "name": "AbstractMenu pattern",
      "source": "CustomMenu",
      "usedBy": ["CustomEnchantment", "CustomShop"],
      "description": "Type-safe menu system with data binding",
      "howToUse": "Extend AbstractMenu<D, E>, implement buildMenu()"
    },
    {
      "name": "PlayerExpansion pattern",
      "source": "CustomEnchantment",
      "candidates": ["CustomShop", "any RPG plugin"],
      "description": "Extensible player data without modifying player class",
      "howToUse": "Extend CEPlayerExpansion, register in expansion register"
    }
  ]
}
```

### Cross-Project Search

When working on Plugin A, search across Plugin B's index:

```pseudocode
function crossProjectSearch(query, projects):
  allResults = []
  for project in projects:
    index = loadIndex(project.path)
    results = index.search(query)
    for r in results:
      r.project = project.name
      allResults.append(r)

  # Rank by relevance, with current project boosted
  return rank(allResults, boost_current_project=1.5)
```

### Cross-Project Context Loading

Using Claude Code's `--add-dir`:

```bash
# Working on CustomEnchantment, need BafFramework context
claude --add-dir ../BafFramework

# Working on CustomShop, need both frameworks
claude --add-dir ../BafFramework --add-dir ../CustomMenu
```

With `CLAUDE_CODE_ADDITIONAL_DIRECTORIES_CLAUDE_MD=1`, CLAUDE.md files from additional directories are loaded automatically.

### Version Compatibility Map

Track API compatibility across plugin versions:

```json
// ~/.claude/shared/version-map.json
{
  "bafframework": {
    "1.0.0": {
      "minecraft": "1.21.1",
      "breaking_changes": [],
      "new_apis": ["AdvancedCommandBuilder", "CommandRegistrar"]
    }
  },
  "customenchantment": {
    "1.0.0": {
      "bafframework": ">=1.0.0",
      "minecraft": "1.21.1",
      "api_surface": ["CEAPI", "CEEnchant", "CEPlayer"]
    }
  }
}
```

### Reuse Detection Algorithm

```pseudocode
function detectReusableComponents(projects):
  # Find patterns that appear in 2+ projects
  allPatterns = {}
  for project in projects:
    patterns = project.index.getPatterns()
    for pattern in patterns:
      allPatterns[pattern.signature].add(project.name)

  reusable = []
  for pattern, projects in allPatterns:
    if len(projects) >= 2:
      reusable.append({
        "pattern": pattern,
        "usedBy": projects,
        "recommendation": suggestExtraction(pattern, projects)
      })

  return reusable

function suggestExtraction(pattern, projects):
  if pattern.isUtility:
    return f"Extract to shared utility library"
  if pattern.isFrameworkExtension:
    return f"Consider adding to BafFramework"
  if pattern.isPluginSpecific:
    return f"Document as reusable pattern, keep in each plugin"
```

## Technical Implementation

### Shared Knowledge Directory

```bash
# Create shared knowledge directory
mkdir -p ~/.claude/shared/bafframework
mkdir -p ~/.claude/shared/bukkit-patterns
mkdir -p ~/.claude/shared/project-registry

# Symlink into each project
ln -s ~/.claude/shared .claude/shared
```

### Cross-Project MCP Server

A single MCP server that indexes multiple projects:

```json
// ~/.claude/mcp.json (user-level, applies to all projects)
{
  "mcpServers": {
    "ecosystem-search": {
      "command": "node",
      "args": ["~/.claude/shared/mcp-server/index.js"],
      "env": {
        "PROJECTS": "D:/IntellijWorkspace/3FMC/Bukkit/CustomEnchantment,D:/IntellijWorkspace/3FMC/Bukkit/BafFramework,D:/IntellijWorkspace/3FMC/Bukkit/CustomMenu"
      }
    }
  }
}
```

### Project Registry

```json
// ~/.claude/shared/project-registry/registry.json
{
  "projects": [
    {
      "name": "CustomEnchantment",
      "path": "D:/IntellijWorkspace/3FMC/Bukkit/CustomEnchantment",
      "type": "bukkit-plugin",
      "framework": "BafFramework",
      "minecraft": "1.21.1",
      "modules": 16,
      "lastIndexed": "2026-02-23"
    },
    {
      "name": "BafFramework",
      "path": "D:/IntellijWorkspace/3FMC/Bukkit/BafFramework",
      "type": "framework",
      "minecraft": "1.21.1",
      "lastIndexed": "2026-02-23"
    },
    {
      "name": "CustomMenu",
      "path": "D:/IntellijWorkspace/3FMC/Bukkit/CustomMenu",
      "type": "bukkit-plugin",
      "framework": "BafFramework",
      "minecraft": "1.21.1",
      "lastIndexed": "2026-02-23"
    }
  ]
}
```

## Applied Example: Adding Economy to CustomEnchantment

```
Task: "Add money cost to enchantment upgrades"

Cross-project search:
1. Search CustomEnchantment → BookUpgradeMenu, ArtifactUpgradeMenu
2. Search CustomShop → economy API, vault integration, player balance
3. Search BafFramework → RequirementHook (cost system)

Shared knowledge:
- BafFramework provides RequirementHook pattern
- CustomShop has a VaultEconomyManager that wraps Vault API
- Convention: costs go through RequirementHook.check() + pay()

Context loaded:
- CustomEnchantment: BookUpgradeMenu summary, RequirementHook docs
- CustomShop: VaultEconomyManager summary (from cross-project search)
- BafFramework: FEATURE_SYSTEM.md RequirementHook section

Result: Claude implements MoneyRequirement following the established pattern,
compatible with CustomShop's economy system, without reading the full
CustomShop codebase.
```

## Dependencies

- **Phase 6** — Cross-session memory provides the persistence layer
- **Phase 7** — Agent tools provide cross-project search capability

## Success Criteria

- [ ] Shared knowledge directory created with BafFramework docs
- [ ] Project registry tracks all plugins in ecosystem
- [ ] Cross-project search finds relevant code across 2+ projects
- [ ] Reusable component registry identifies shared patterns
- [ ] Version compatibility map prevents incompatible changes
- [ ] `--add-dir` workflow documented for cross-project sessions
- [ ] New plugin bootstraps with shared knowledge in < 5 minutes

---

## GitHub Issue

### Title
`feat: Phase 10 — Multi-Plugin & Cross-Project Knowledge Layer`

### Description

Build a shared knowledge layer that spans all plugins in the ecosystem. Enable cross-project search, reusable component detection, shared convention enforcement, and version compatibility tracking.

### Background / Motivation

The 3FMC ecosystem has multiple interdependent plugins (CustomEnchantment, BafFramework, CustomMenu, CustomShop, etc.). Currently, knowledge about one plugin doesn't transfer to another. A developer working on CustomEnchantment who needs to integrate with CustomShop must manually find and read the relevant code.

A shared knowledge layer means context built for one plugin immediately benefits all others.

### Tasks

- [ ] Create shared knowledge directory structure at `~/.claude/shared/`
- [ ] Extract BafFramework API reference into shared docs
- [ ] Extract Bukkit/Paper pattern library into shared docs
- [ ] Create project registry tracking all ecosystem plugins
- [ ] Create cross-project convention registry
- [ ] Implement reusable component detection algorithm
- [ ] Create version compatibility map
- [ ] Configure cross-project MCP server (if Phase 4 complete)
- [ ] Document `--add-dir` workflow for cross-project sessions
- [ ] Implement symlink-based shared docs distribution
- [ ] Test: search from CustomEnchantment finds CustomShop economy code
- [ ] Test: new plugin bootstraps with shared knowledge

### Acceptance Criteria

- [ ] BafFramework API available to all plugins via shared docs
- [ ] Cross-project search returns results from 2+ projects
- [ ] Reusable component registry identifies 5+ shared patterns
- [ ] New plugin setup includes shared knowledge in < 5 minutes
- [ ] No duplicate documentation across projects

### Future Improvements

- Automatic cross-project index merging on git push
- Shared test fixtures library
- Plugin compatibility testing framework
- API change detection (flag breaking changes across plugins)

---
**Priority:** P3 | **Effort:** L | **Labels:** `feature`, `infrastructure`, `phase-10`, `effort:L`
