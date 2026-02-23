---
name: codemap-generator
description: Analyze a Java/Bukkit codebase and generate architecture maps in .claude/docs/codemap/. Produces MODULE-MAP.md, CLASS-REGISTRY.md, DEPENDENCY-GRAPH.md, PATTERN-CATALOG.md, CONFIG-SCHEMA-MAP.md, DOMAIN-MAP.md, and ENTRY-POINTS.md.
---

# Codemap Generator

Automatically analyze a Java/Bukkit project and generate structured architecture maps.
Uses only Glob, Grep, and Read tools — no external dependencies.

## Output Directory

All files are generated in `.claude/docs/codemap/`.

## Generation Process

### Step 1: Discover Project Structure

```
Glob: **/build.gradle
→ Identify all Gradle modules (subprojects)

Glob: **/src/main/java/**/*.java
→ Count total Java files per module

Glob: **/src/main/resources/**/*.yml
→ Count config files
```

### Step 2: Find Main Plugin Class

```
Grep: "extends BafPlugin" or "extends JavaPlugin"
→ Identify the plugin entry point

Read: main plugin class
→ Extract registerModules() to get module load order
```

### Step 3: Extract All Class Declarations

For each module's source directory:

```
Grep: "^public (abstract )?(class|interface|enum) (\w+)"
→ Extract class name, type (class/interface/enum/abstract)

Grep: "extends (\w+)"
→ Extract parent class

Grep: "implements ([\w, ]+)"
→ Extract implemented interfaces
```

### Step 4: Analyze Each Module

For each PluginModule subclass:

```
Read: Module class file
→ Extract onEnable() contents: listener registrations, hook registrations, command registrations
→ Extract onReload() contents: config loading
→ Extract dependencies: getPlugin().getModule() calls
```

### Step 5: Detect Design Patterns

```
Grep: "static .+ instance()" → Singleton
Grep: "extends StrategyRegister" → Registry/Strategy
Grep: "extends ConditionHook" → Condition Hook
Grep: "extends EffectHook" → Effect Hook
Grep: "extends ExecuteHook" → Execute Hook
Grep: "extends PluginModule" → Module
Grep: "implements Listener" → Event Listener
Grep: "@Builder" → Builder pattern
Grep: "@Configuration" → Config class
Grep: "extends Abstract\w+" → Template Method
```

### Step 6: Map Config Schemas

```
Grep: "ConfigUtils.setupResource" → Find config file loading
Grep: "@Configuration" → Find config Java classes
Grep: "@Path" → Extract config field paths

Cross-reference: YAML filename ↔ Java class ↔ config paths
```

### Step 7: Scan Entry Points

```
Grep: "@EventHandler" → Event listeners (extract event type from method parameter)
Grep: "AdvancedCommandBuilder|registerCommand" → Commands
Grep: "runTaskTimer|runTaskAsynchronously|BukkitRunnable" → Scheduled tasks
```

### Step 8: Classify Domains

Tag each class based on package and naming:

| Signal | Domain |
|--------|--------|
| `.enchant` package | enchantment |
| `.menu` or `.gui` package | gui |
| `.command` package | command |
| `.listener` package | events |
| `.database` or `.storage` package | persistence |
| `.config` package | configuration |
| `.task` package | scheduling |
| `.player` package | player |
| `.item` package | items |
| `.attribute` package | attributes |
| `.guard` package | mobs |
| `.filter` package | filtering |
| `.feature` package | features |
| extends ConditionHook | conditions |
| extends EffectHook | effects |
| extends ExecuteHook | executes |
| "Damage/Combat/Attack" in name | combat |
| "Economy/Money/Cost" in name | economy |

### Step 9: Generate Output Files

Generate each file in `.claude/docs/codemap/`:

#### MODULE-MAP.md
```markdown
# Module Map — {ProjectName}

## Plugin: {PluginName} ({basePackage})
Minecraft: {version} | Framework: {framework} | Java {javaVersion}

### Module Load Order

| # | Module | Package | Purpose | Key Classes | Dependencies |
|---|--------|---------|---------|-------------|--------------|
| 1 | ModuleName | .package | Brief purpose | Class1, Class2 | OtherModule |
```

#### CLASS-REGISTRY.md
```markdown
# Class Registry — {ProjectName}

## {ModuleName} ({classCount} classes)

| Class | Type | Extends | Implements | Domain | Summary |
|-------|------|---------|------------|--------|---------|
| ClassName | class | ParentClass | Interface1 | domain | One-line summary |
```

#### DEPENDENCY-GRAPH.md
```markdown
# Dependency Graph — {ProjectName}

## Module Dependencies
ModuleA → ModuleB (reason)
ModuleA → ModuleC (reason)

## Key Class Dependencies
ClassA → ClassB (field type)
ClassA → ClassC (method parameter)
```

#### PATTERN-CATALOG.md
```markdown
# Pattern Catalog — {ProjectName}

## Singleton (N instances)
| Class | Access Method |
|-------|---------------|
| RegisterName | RegisterName.instance() |

## Registry/Strategy (N instances)
...
```

#### CONFIG-SCHEMA-MAP.md
```markdown
# Config Schema Map — {ProjectName}

| YAML File | Java Class | Key Paths | Module |
|-----------|------------|-----------|--------|
| config.yml | MainConfig | feature.enabled, debug.mode | ConfigModule |
```

#### DOMAIN-MAP.md
```markdown
# Domain Map — {ProjectName}

## enchantment (N classes)
ClassName1, ClassName2, ...

## combat (N classes)
...
```

#### ENTRY-POINTS.md
```markdown
# Entry Points — {ProjectName}

## Event Listeners (N listeners, M events)

| Listener | Events | Module |
|----------|--------|--------|
| PlayerListener | PlayerJoinEvent, PlayerQuitEvent | ListenerModule |

## Commands (N commands)

| Command | Handler | Module |
|---------|---------|--------|
| /ce | CommandModule | CommandModule |

## Scheduled Tasks (N tasks)

| Task | Type | Interval | Module |
|------|------|----------|--------|
| TpsTask | Repeating (sync) | 1 tick | TaskModule |
```

## Token Budget

All generated files combined MUST be under 2000 lines total.
Use concise one-line summaries, not verbose descriptions.
Abbreviate where possible without losing meaning.

## Validation

After generation, verify:
- [ ] MODULE-MAP.md lists all modules from registerModules()
- [ ] CLASS-REGISTRY.md covers 95%+ of public classes
- [ ] DEPENDENCY-GRAPH.md shows all getPlugin().getModule() relationships
- [ ] PATTERN-CATALOG.md identifies 10+ patterns
- [ ] CONFIG-SCHEMA-MAP.md maps all YAML files to Java classes
- [ ] DOMAIN-MAP.md tags classes into 8+ domains
- [ ] ENTRY-POINTS.md lists all listeners, commands, and tasks
- [ ] Total line count < 2000
