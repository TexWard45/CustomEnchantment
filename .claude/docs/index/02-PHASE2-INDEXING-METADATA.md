# Phase 2 — Indexing & Metadata System

## Purpose

Create a universal, searchable index of every code entity (class, method, field, config key, resource file) with rich metadata. This is the "database" that all retrieval systems query against.

## Design

### Index Schema

Each indexed entity stores:

```json
{
  "id": "com.bafmc.customenchantment.enchant.condition.ConditionHealth",
  "type": "class",
  "name": "ConditionHealth",
  "package": "com.bafmc.customenchantment.enchant.condition",
  "module": "EnchantModule",
  "plugin": "CustomEnchantment",
  "file": "src/main/java/.../enchant/condition/ConditionHealth.java",
  "line": 15,

  "extends": "ConditionHook",
  "implements": [],
  "annotations": ["@Getter"],

  "responsibility": "Checks if player/entity health matches a threshold condition",
  "tags": ["enchantment", "conditions", "combat", "health"],
  "dependencies": ["ConditionHook", "ConditionData", "Player", "LivingEntity"],
  "usedBy": ["CECaller", "enchantment YAML configs"],

  "methods": [
    {
      "name": "getIdentifier",
      "signature": "String getIdentifier()",
      "returns": "\"HEALTH\"",
      "summary": "Returns condition identifier for YAML config matching"
    },
    {
      "name": "check",
      "signature": "boolean check(ConditionData data, String value)",
      "summary": "Compares entity health against threshold value from config"
    }
  ],

  "configUsage": "Used in enchantment YAML as: condition: \"HEALTH:>50%\"",
  "lastModified": "2025-12-15T10:30:00Z",
  "hash": "a1b2c3d4..."
}
```

### Entity Types to Index

| Entity Type | Count (CE) | Key Metadata |
|-------------|-----------|--------------|
| Classes | 550+ | extends, implements, annotations, domain tags |
| Interfaces | ~20 | implementors list |
| Methods (public) | ~2000 | signature, return type, summary |
| Fields (annotated) | ~500 | @Path values, types, defaults |
| Config files (YAML) | 40+ | schema, bound Java class |
| Resources | 100+ | type (yml, properties), purpose |
| Enchantment defs | 200+ | conditions, effects, materials, groups |
| Item definitions | 50+ | type, rarity, stats |

### Index Storage Format

Store as JSON files in `.claude/docs/codemap/index/`:

```
.claude/docs/codemap/index/
├── modules.json           # All modules with metadata
├── classes.json           # All classes (compact: name, file, tags, one-line summary)
├── methods.json           # Public method signatures (for API surface queries)
├── configs.json           # YAML → Java class mappings
├── enchantments.json      # All enchantment definitions
├── dependencies.json      # Dependency graph (adjacency list)
├── entry-points.json      # Listeners, commands, tasks
└── search-tags.json       # Tag → entity ID reverse index
```

### Search Tag Taxonomy

```
Domain Tags:
  enchantment, artifact, combat, defense, mining, farming,
  economy, gui, command, config, persistence, events,
  player, items, mobs, scheduling, integration, api

Pattern Tags:
  singleton, registry, hook, module, listener, builder,
  data-class, config-class, manager, task, filter, expansion

Framework Tags:
  bafframework, bukkit-api, paper-api, nms, custommenu,
  placeholder-api, mcmmo

Lifecycle Tags:
  on-enable, on-disable, on-reload, on-save,
  per-tick, per-player, on-join, on-quit
```

## Technical Implementation

### Indexing Pipeline

```pseudocode
function buildIndex(projectRoot):
  index = new ProjectIndex()

  # Step 1: Discover files
  javaFiles = glob("src/main/java/**/*.java")
  yamlFiles = glob("src/main/resources/**/*.yml")
  gradleFiles = glob("**/build.gradle*")

  # Step 2: Extract class-level metadata
  for file in javaFiles:
    content = read(file)
    classInfo = extractClassInfo(content)
    classInfo.module = detectModule(file.path)
    classInfo.tags = autoTag(classInfo)
    classInfo.hash = sha256(content)
    index.classes.add(classInfo)

  # Step 3: Extract method signatures
  for classInfo in index.classes:
    methods = extractMethods(classInfo.content)
    for method in methods:
      if method.isPublic or method.hasAnnotation("@EventHandler"):
        index.methods.add(method)

  # Step 4: Build dependency graph
  for classInfo in index.classes:
    imports = extractImports(classInfo.content)
    fieldTypes = extractFieldTypes(classInfo.content)
    for dep in imports + fieldTypes:
      if dep in index.classes:
        index.dependencies.addEdge(classInfo.id, dep.id)

  # Step 5: Index configs
  for file in yamlFiles:
    configInfo = detectConfigBinding(file, index.classes)
    index.configs.add(configInfo)

  # Step 6: Build reverse tag index
  for entity in index.allEntities:
    for tag in entity.tags:
      index.searchTags[tag].add(entity.id)

  # Step 7: Persist
  writeJson(index, ".claude/docs/codemap/index/")
  return index
```

### Incremental Update Strategy

```pseudocode
function updateIndex(projectRoot, existingIndex):
  changedFiles = gitDiff("--name-only", "HEAD~1")

  for file in changedFiles:
    if file.endsWith(".java"):
      newHash = sha256(read(file))
      if newHash != existingIndex.getHash(file):
        # Re-index this file
        removeFromIndex(existingIndex, file)
        addToIndex(existingIndex, file)

    if file.endsWith(".yml"):
      updateConfigIndex(existingIndex, file)

  # Rebuild reverse index for changed entities only
  rebuildAffectedTags(existingIndex, changedFiles)
  persist(existingIndex)
```

### Query Interface

```pseudocode
# Find all classes tagged with "combat" and "conditions"
results = index.query(tags=["combat", "conditions"])
# → [ConditionHealth, ConditionDamage, ConditionEntityType, ...]

# Find what uses ConditionHealth
results = index.query(usedBy="ConditionHealth")
# → [CECaller, sword.yml, armor.yml, ...]

# Find all entry points in ListenerModule
results = index.query(module="ListenerModule", type="method", annotation="@EventHandler")
# → [onBlockBreak, onEntityDamage, onPlayerJoin, ...]

# Search by responsibility keyword
results = index.search("damage reduction")
# → [ConditionHealth, EffectDamageModifier, PlayerVanillaAttribute, ...]
```

## Applied to Minecraft Plugins

### Enchantment System Index Entry

```json
{
  "id": "enchant:lifesteal",
  "type": "enchantment-definition",
  "name": "Life Steal",
  "file": "src/main/resources/enchantment/sword.yml",
  "group": "legendary",
  "maxLevel": 5,
  "materials": ["DIAMOND_SWORD", "IRON_SWORD", "NETHERITE_SWORD"],
  "conditions": ["EQUIP_SLOT:MAIN_HAND"],
  "effects": ["ADD_POTION:REGENERATION"],
  "tags": ["enchantment", "combat", "sword", "healing", "legendary"],
  "javaClasses": ["CEEnchant", "ConditionEquipSlot", "EffectAddPotion"]
}
```

### Config-to-Code Mapping

```json
{
  "yamlFile": "src/main/resources/config.yml",
  "javaClass": "com.bafmc.customenchantment.config.MainConfig",
  "paths": {
    "setting.debug": { "field": "debug", "type": "boolean", "default": false },
    "setting.language": { "field": "language", "type": "String", "default": "en" },
    "setting.auto-save": { "field": "autoSave", "type": "int", "default": 300 }
  }
}
```

## Dependencies

- **Phase 1** — Architecture maps provide the skeleton that the index fills with detail

## Success Criteria

- [ ] All 550+ classes indexed with metadata
- [ ] Public method signatures indexed (~2000 methods)
- [ ] All YAML configs mapped to Java classes
- [ ] Enchantment definitions indexed (200+)
- [ ] Tag-based queries return results in < 1 second
- [ ] Dependency graph covers class→class and module→module
- [ ] Incremental update re-indexes only changed files
- [ ] Index JSON files total < 500KB (stays manageable)
- [ ] Schema works for any Java project, not just Bukkit plugins

---

## GitHub Issue

### Title
`feat: Phase 2 — Universal Indexing & Metadata System`

### Description

Build a comprehensive indexing system that catalogs every code entity (class, method, config, resource) with rich metadata including responsibility summaries, domain tags, dependency links, and usage locations. This index becomes the queryable knowledge base for all AI operations.

### Background / Motivation

Phase 1 generates high-level architecture maps. Phase 2 fills in the details — every class, method, and config entry becomes searchable. Without this index, Claude must use brute-force grep/glob patterns to find relevant code, which is slow and often misses indirect relationships.

The index must be plugin-agnostic so it works for enchantment systems, economy plugins, RPG systems, and any future Java project.

### Tasks

- [ ] Define JSON schema for indexed entities (classes, methods, fields, configs, resources)
- [ ] Design tag taxonomy (domain tags, pattern tags, framework tags, lifecycle tags)
- [ ] Implement Java class extractor (name, package, extends, implements, annotations)
- [ ] Implement method signature extractor (public methods with return types)
- [ ] Implement field extractor (annotated fields: @Path, @Getter)
- [ ] Implement YAML config binder (map YAML files → Java @Configuration classes)
- [ ] Implement dependency graph builder (imports + field types → adjacency list)
- [ ] Implement auto-tagger (assign domain/pattern tags from class characteristics)
- [ ] Implement reverse tag index (tag → entity ID lookup)
- [ ] Implement enchantment definition indexer (parse YAML enchant configs)
- [ ] Implement incremental update (re-index only git-changed files)
- [ ] Create index query interface (tag-based, keyword, dependency traversal)
- [ ] Generate full index for CustomEnchantment (550+ classes)
- [ ] Validate index completeness (>95% coverage)
- [ ] Store index at `.claude/docs/codemap/index/`

### Technical Notes

- JSON format for easy parsing by Claude Code (Read tool)
- Keep individual JSON files < 100KB for fast loading
- Use SHA-256 file hashes for change detection
- Tag taxonomy should be extensible (new tags don't break existing index)
- Enchantment definitions need special handling: parse YAML condition/effect syntax
- Method signatures should include parameter names for better search

### Acceptance Criteria

- [ ] Index contains all public classes and interfaces in the project
- [ ] Each class has at least one domain tag and one pattern tag
- [ ] YAML→Java config mapping covers all @Configuration classes
- [ ] Dependency graph accurately reflects import/field relationships
- [ ] `query(tags=["combat", "conditions"])` returns correct results
- [ ] Incremental update completes in < 30 seconds for typical commits
- [ ] Index format documented and reusable across projects

### Future Improvements

- LSP integration for call hierarchy (incoming/outgoing calls)
- Cross-project index merging (BafFramework + CustomEnchantment)
- Index versioning (track how API surface changes over time)
- Automated staleness detection (flag when index diverges from code)

---
**Priority:** P1 | **Effort:** L | **Labels:** `feature`, `infrastructure`, `phase-2`, `effort:L`
