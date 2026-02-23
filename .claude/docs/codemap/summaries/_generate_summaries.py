#!/usr/bin/env python3
"""
Generate hierarchical markdown summaries (Levels 1-5) from Phase 2 JSON indexes + Java source.

Levels:
  5 - ECOSYSTEM.md: Plugin in its external context (~20 lines)
  4 - PLUGIN-SUMMARY.md: All modules at a glance (~50 lines)
  3 - {module}.md: One file per module with class summaries
  2 - Class summaries embedded in Level 3 files (key classes detailed)
  1 - Method signatures embedded in Level 2 summaries (key methods as bullets)

Usage:
  python _generate_summaries.py
"""
import json, re
from collections import defaultdict
from pathlib import Path

# === Paths ===
SCRIPT_DIR = Path(__file__).resolve().parent
INDEX_DIR = SCRIPT_DIR.parent / "index"
PROJECT_ROOT = SCRIPT_DIR.parents[3]  # summaries -> codemap -> docs -> .claude -> project root
SRC_ROOT = PROJECT_ROOT / "src" / "main" / "java" / "com" / "bafmc" / "customenchantment"

# Module name -> output filename
MODULE_FILES = {
    "Root": "root.md",
    "EnchantModule": "enchant.md",
    "ItemModule": "item.md",
    "PlayerModule": "player.md",
    "MenuModule": "menu.md",
    "CommandModule": "command.md",
    "ListenerModule": "listener.md",
    "TaskModule": "task.md",
    "ConfigModule": "config.md",
    "GuardModule": "guard.md",
    "DatabaseModule": "database.md",
    "AttributeModule": "attribute.md",
    "FilterModule": "filter.md",
    "ExecuteModule": "execute.md",
    "FeatureModule": "feature.md",
    "CustomMenuModule": "custommenu.md",
    "PlaceholderModule": "placeholder.md",
}

# How many classes get detailed Level 2 summaries per module
DETAILED_BUDGET = {
    "EnchantModule": 15,
    "ItemModule": 12,
    "MenuModule": 10,
    "PlayerModule": 12,
    "CommandModule": 8,
    "TaskModule": 8,
    "ConfigModule": 8,
    "ListenerModule": 10,
    "Root": 10,
    "GuardModule": 5,
    "DatabaseModule": 2,
    "AttributeModule": 4,
    "FilterModule": 3,
    "ExecuteModule": 3,
    "FeatureModule": 5,
    "CustomMenuModule": 4,
    "PlaceholderModule": 2,
}

# === Load Phase 2 indexes ===

def load_json(name):
    path = INDEX_DIR / name
    if not path.exists():
        print(f"WARNING: {path} not found, using empty data")
        return {}
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)

def load_indexes():
    return {
        "classes": load_json("classes.json"),
        "modules": load_json("modules.json"),
        "methods": load_json("methods.json"),
        "dependencies": load_json("dependencies.json"),
        "entry_points": load_json("entry-points.json"),
        "configs": load_json("configs.json"),
        "enchantments": load_json("enchantments.json"),
    }

# === Java source parser ===

def parse_java_file(filepath):
    """Extract structural info from a Java source file."""
    info = {
        "line_count": 0,
        "class_decl": "",
        "annotations": [],
        "fields": [],
        "methods": [],
        "imports": [],
        "has_lombok_getter": False,
        "has_lombok_setter": False,
        "has_configuration": False,
        "implements_listener": False,
    }

    if not filepath.exists():
        return info

    try:
        lines = filepath.read_text(encoding="utf-8").splitlines()
    except Exception:
        return info

    info["line_count"] = len(lines)
    in_comment = False

    for line in lines:
        stripped = line.strip()

        # Track block comments
        if "/*" in stripped:
            in_comment = True
        if "*/" in stripped:
            in_comment = False
            continue
        if in_comment or stripped.startswith("//"):
            continue

        # Imports
        if stripped.startswith("import "):
            info["imports"].append(stripped)
            continue

        # Annotations
        if stripped.startswith("@"):
            ann = stripped.split("(")[0].lstrip("@")
            info["annotations"].append(ann)
            if "Getter" in ann:
                info["has_lombok_getter"] = True
            if "Setter" in ann:
                info["has_lombok_setter"] = True
            if ann == "Configuration":
                info["has_configuration"] = True
            continue

        # Class declaration
        class_match = re.match(
            r"^(public\s+)?(abstract\s+)?(class|interface|enum)\s+(\w+)(<[^>]+>)?\s*(extends\s+\S+)?\s*(implements\s+.+?)?\s*\{?",
            stripped,
        )
        if class_match and not info["class_decl"]:
            info["class_decl"] = stripped.rstrip("{").strip()
            if "Listener" in (class_match.group(7) or ""):
                info["implements_listener"] = True

        # Fields (private/protected with type and name)
        field_match = re.match(
            r"^(private|protected)\s+(static\s+)?(final\s+)?(\S+(?:<[^>]+>)?)\s+(\w+)\s*[;=]",
            stripped,
        )
        if field_match:
            modifier = "static " if field_match.group(2) else ""
            ftype = field_match.group(4)
            fname = field_match.group(5)
            info["fields"].append(f"{modifier}{ftype} {fname}")

        # Public/protected methods
        method_match = re.match(
            r"^(public|protected|@Override\s+public|@Override\s+protected|@EventHandler[^)]*\)\s*public)\s+"
            r"(static\s+)?([\w<>\[\],\s]+?)\s+(\w+)\s*\(([^)]*)\)\s*\{?",
            stripped,
        )
        if method_match:
            ret = method_match.group(3).strip()
            mname = method_match.group(4)
            params = method_match.group(5).strip()
            # Simplify param types
            simple_params = simplify_params(params)
            info["methods"].append({
                "name": mname,
                "returns": ret,
                "params": simple_params,
                "is_event": "@EventHandler" in stripped,
            })

    return info


def simplify_params(params):
    """Simplify parameter string for display."""
    if not params:
        return ""
    parts = []
    for p in params.split(","):
        p = p.strip()
        # Remove annotations
        p = re.sub(r"@\w+\s+", "", p)
        # Take just type and name
        tokens = p.split()
        if len(tokens) >= 2:
            parts.append(f"{tokens[-2]} {tokens[-1]}")
        elif tokens:
            parts.append(tokens[0])
    return ", ".join(parts)


# === Classification & scoring ===

# Pattern templates for grouped classes
PATTERN_TEMPLATES = {
    "condition": {"parent": "ConditionHook", "prefix": "Condition", "desc": "Config-driven condition check"},
    "effect": {"parent": "EffectHook", "prefix": "Effect", "desc": "Config-driven enchantment effect"},
    "factory": {"parent": "CEItemFactory", "prefix": "", "desc": "Item factory implementation"},
    "item_impl": {"parent": "CEItem", "prefix": "", "desc": "Item type implementation"},
    "expansion": {"parent": "CEPlayerExpansion", "prefix": "", "desc": "Player stat expansion"},
    "special_mine": {"parent": "AbstractSpecialMine", "prefix": "", "desc": "Special mining behavior"},
    "command": {"parent": "", "prefix": "Command", "desc": "Command handler"},
}


def classify_class(cls):
    """Classify a class entry into a template category."""
    extends = cls.get("extends", "")
    name = cls.get("name", "")
    tags = cls.get("tags", [])

    if "ConditionHook" in extends:
        return "condition"
    if "EffectHook" in extends:
        return "effect"
    if "CEItemFactory" in extends:
        return "factory"
    if extends in ("CEItem", "CEWeapon", "CEArmor") or (
        "items" in tags and extends and "Factory" not in name and "Storage" not in name
        and "Data" not in name and "Config" not in name and "Map" not in name
        and "Register" not in name and "Module" not in name
    ):
        return "item_impl"
    if "CEPlayerExpansion" in extends:
        return "expansion"
    if "AbstractSpecialMine" in extends:
        return "special_mine"
    return "detailed"  # Gets full summary


def importance_score(cls, java_info):
    """Score a class for importance ranking. Higher = more important."""
    score = 0
    name = cls.get("name", "")
    extends = cls.get("extends", "")
    tags = cls.get("tags", [])
    ctype = cls.get("type", "class")

    # Line count (proxy for complexity)
    lines = java_info.get("line_count", 0)
    if lines > 300:
        score += 5
    elif lines > 150:
        score += 3
    elif lines > 50:
        score += 1

    # Entry points: modules, main class, listeners
    if "module" in tags:
        score += 10
    if name == "CustomEnchantment":
        score += 15
    if java_info.get("implements_listener", False):
        score += 6
    if "listener" in tags:
        score += 4

    # Abstract/interface = architectural
    if ctype in ("abstract", "interface"):
        score += 4

    # Core infrastructure
    if "registry" in tags or "Register" in name:
        score += 5
    if "manager" in tags or "Manager" in name:
        score += 4
    if "config" in tags or "Config" in name:
        score += 3
    if "factory" in tags and "CEItemFactory" not in extends:
        score += 3

    # Data flow: key classes
    if name in ("CECaller", "CEEnchant", "CEFunction", "CEFunctionData", "CEPlayer",
                 "CEDisplay", "CELevel", "CEItemRegister", "PlayerStorage",
                 "CEEnchantMap", "CEGroupMap", "CEItemStorageMap"):
        score += 8

    # Builder / utility (medium)
    if "builder" in tags:
        score += 2
    if "utility" in tags:
        score += 1

    # Penalize simple hook implementations
    if "ConditionHook" in extends or "EffectHook" in extends:
        score -= 5

    return score


def resolve_file_path(cls):
    """Try to find the actual Java source file for a class."""
    # Try the file path from classes.json
    rel_path = cls.get("file", "")
    if rel_path:
        # Clean up double slashes
        rel_path = rel_path.replace("//", "/")
        full = PROJECT_ROOT / rel_path
        if full.exists():
            return full

    # Fallback: search by class name
    name = cls.get("name", "")
    for java_file in SRC_ROOT.rglob(f"{name}.java"):
        return java_file

    return None


# === Summary generators ===

def generate_method_bullets(java_info, max_methods=8):
    """Generate Level 1 method bullet points."""
    bullets = []
    methods = java_info.get("methods", [])

    # Prioritize: non-getter/setter, non-trivial methods
    important = []
    event_handlers = []
    others = []

    for m in methods:
        name = m["name"]
        if m.get("is_event"):
            event_handlers.append(m)
        elif name.startswith("get") or name.startswith("set") or name.startswith("is"):
            others.append(m)
        else:
            important.append(m)

    # Take important first, then events, then others
    selected = (important + event_handlers + others)[:max_methods]

    for m in selected:
        params = m["params"]
        ret = m["returns"]
        prefix = "[event] " if m.get("is_event") else ""
        if ret and ret != "void":
            bullets.append(f"- `{m['name']}({params})` -> `{ret}`: {prefix}")
        else:
            bullets.append(f"- `{m['name']}({params})`: {prefix}")

    return bullets


def generate_field_summary(java_info, max_fields=6):
    """Summarize key fields."""
    fields = java_info.get("fields", [])
    if not fields:
        return ""

    # Filter out trivially named fields
    shown = fields[:max_fields]
    summary = ", ".join(f"`{f}`" for f in shown)
    if len(fields) > max_fields:
        summary += f" (+{len(fields) - max_fields} more)"
    return summary


def generate_class_summary(cls, java_info, methods_index):
    """Generate a Level 2 class summary."""
    name = cls["name"]
    ctype = cls.get("type", "class")
    extends = cls.get("extends", "")
    summary_text = cls.get("summary", "")
    tags = cls.get("tags", [])
    lines = java_info.get("line_count", 0)
    class_decl = java_info.get("class_decl", "")

    # Header
    out = []
    line_info = f" ({lines} lines)" if lines > 0 else ""
    out.append(f"### {name}{line_info}")

    # Type line
    type_parts = [ctype]
    if extends:
        type_parts.append(f"extends {extends}")
    out.append(f"**Type:** {' '.join(type_parts)}")

    # Purpose
    if summary_text:
        out.append(f"**Purpose:** {summary_text}")

    # Key fields
    field_summary = generate_field_summary(java_info)
    if field_summary:
        out.append(f"**Fields:** {field_summary}")

    # Key methods (Level 1)
    method_bullets = generate_method_bullets(java_info)
    if method_bullets:
        out.append("**Key Methods:**")
        out.extend(method_bullets)

    # Annotations of note
    annotations = java_info.get("annotations", [])
    notable = [a for a in annotations if a in ("Configuration", "Getter", "Setter", "Builder", "AllArgsConstructor")]
    if notable:
        out.append(f"**Annotations:** {', '.join('@' + a for a in notable)}")

    # Notes (thread safety, edge cases)
    notes = []
    if java_info.get("implements_listener"):
        events = [m["name"] for m in java_info.get("methods", []) if m.get("is_event")]
        if events:
            notes.append(f"Handles {len(events)} events")
    if any("ConcurrentHashMap" in f for f in java_info.get("fields", [])):
        notes.append("Thread-safe collections")
    if java_info.get("has_configuration"):
        notes.append("YAML-bound config")
    if notes:
        out.append(f"**Notes:** {'; '.join(notes)}")

    out.append("")
    return "\n".join(out)


def generate_pattern_group(group_name, classes, parent_class, methods_index):
    """Generate a grouped summary for pattern classes."""
    count = len(classes)

    # Collect unique identifiers from methods index
    identifiers = []
    for cls in classes:
        name = cls["name"]
        for m in methods_index:
            if m.get("class") == name and m.get("category") == "hook-identifier":
                identifiers.append(m["returns"])

    out = []
    out.append(f"### {group_name} ({count} classes)")
    out.append(f"**Pattern:** Each extends `{parent_class}`, implements `getIdentify()` + `setup()` + action method")

    if identifiers:
        # Show first few identifiers
        shown = identifiers[:10]
        id_str = ", ".join(f"`{i}`" for i in shown)
        if len(identifiers) > 10:
            id_str += f" ... (+{len(identifiers) - 10} more)"
        out.append(f"**Identifiers:** {id_str}")

    # Group by name prefix patterns
    name_list = sorted(c["name"] for c in classes)
    out.append(f"**Classes:** {', '.join(name_list[:8])}")
    if len(name_list) > 8:
        out.append(f"  ... and {len(name_list) - 8} more")

    out.append("")
    return "\n".join(out)


def generate_item_pattern_group(classes):
    """Group item implementations by sub-type."""
    by_type = defaultdict(list)
    for cls in classes:
        name = cls["name"]
        tags = cls.get("tags", [])
        fqn = cls.get("fqn", "")

        if ".artifact" in fqn:
            by_type["Artifact"].append(name)
        elif ".outfit" in fqn:
            by_type["Outfit"].append(name)
        elif ".sigil" in fqn:
            by_type["Sigil"].append(name)
        elif ".book" in fqn:
            by_type["Book"].append(name)
        elif ".gem" in fqn and ".gemdrill" not in fqn:
            by_type["Gem"].append(name)
        elif ".gemdrill" in fqn:
            by_type["GemDrill"].append(name)
        elif ".skin" in fqn:
            by_type["Skin"].append(name)
        elif ".randombook" in fqn:
            by_type["RandomBook"].append(name)
        else:
            by_type["Other"].append(name)

    out = []
    total = sum(len(v) for v in by_type.values())
    out.append(f"### Item Implementations ({total} classes)")
    out.append("**Pattern:** Each item type has Factory + Item + Data + Storage classes")
    out.append("")

    for type_name, names in sorted(by_type.items()):
        out.append(f"- **{type_name}** ({len(names)}): {', '.join(sorted(names)[:5])}")
        if len(names) > 5:
            out[-1] += f" (+{len(names) - 5} more)"

    out.append("")
    return "\n".join(out)


def generate_module_file(module_name, module_info, classes, methods_index, deps, entry_points, configs):
    """Generate a Level 3 module summary file."""
    out = []

    # Header
    pkg = module_info.get("package", "com.bafmc.customenchantment")
    class_count = module_info.get("classCount", len(classes))
    phase = module_info.get("phase", "")
    purpose = module_info.get("purpose", "")

    display_name = module_name.replace("Module", "") if module_name != "Root" else "Root (Core)"
    out.append(f"# {display_name} Module Summary")
    out.append("")

    meta_parts = [f"**Package:** `{pkg}`", f"**Classes:** {class_count}"]
    if phase:
        meta_parts.append(f"**Init Phase:** {phase}")
    out.append(" | ".join(meta_parts))

    if purpose:
        out.append(f"**Purpose:** {purpose}")

    # Dependencies
    depends_on = module_info.get("dependsOn", [])
    depended_by = module_info.get("dependedBy", [])
    if depends_on or depended_by:
        dep_parts = []
        if depends_on:
            dep_parts.append(f"**Depends On:** {', '.join(depends_on)}")
        if depended_by:
            dep_parts.append(f"**Depended By:** {', '.join(depended_by)}")
        out.append(" | ".join(dep_parts))

    out.append("")

    # Execution flow (from dependencies data)
    flows = find_module_flows(module_name, deps)
    if flows:
        out.append("## Execution Flow")
        for flow in flows:
            out.append(f"- {flow}")
        out.append("")

    # Config files for this module
    module_configs = [c for c in configs if c.get("module") == module_name or
                      (module_name == "ConfigModule" and c.get("module") == "ConfigModule")]
    if module_configs:
        out.append("## Configuration")
        for cfg in module_configs:
            yml = cfg.get("yamlFile", "")
            java = cfg.get("javaClass", "")
            keys = cfg.get("keyPaths", [])
            out.append(f"- `{yml}` -> `{java}`")
            if keys:
                out.append(f"  Keys: {', '.join(keys[:5])}")
                if len(keys) > 5:
                    out[-1] += f" (+{len(keys) - 5} more)"
        out.append("")

    # Listeners for this module
    module_listeners = find_module_listeners(module_name, entry_points)
    if module_listeners:
        out.append("## Event Listeners")
        for listener in module_listeners:
            cls_name = listener.get("class", "")
            events = listener.get("events", [])
            notes = listener.get("priorityNotes", "")
            out.append(f"- **{cls_name}**: {', '.join(events[:4])}")
            if len(events) > 4:
                out[-1] += f" (+{len(events) - 4} more)"
            if notes:
                out.append(f"  Priority: {notes}")
        out.append("")

    # Classify classes
    detailed_classes = []
    condition_classes = []
    effect_classes = []
    factory_classes = []
    item_impl_classes = []
    expansion_classes = []
    special_mine_classes = []
    other_grouped = []

    for cls in classes:
        category = classify_class(cls)
        if category == "condition":
            condition_classes.append(cls)
        elif category == "effect":
            effect_classes.append(cls)
        elif category == "factory":
            factory_classes.append(cls)
        elif category == "item_impl":
            item_impl_classes.append(cls)
        elif category == "expansion":
            expansion_classes.append(cls)
        elif category == "special_mine":
            special_mine_classes.append(cls)
        else:
            detailed_classes.append(cls)

    # Score and select top N for detailed summaries
    budget = DETAILED_BUDGET.get(module_name, 5)
    scored = []
    for cls in detailed_classes:
        filepath = resolve_file_path(cls)
        java_info = parse_java_file(filepath) if filepath else {"line_count": 0}
        score = importance_score(cls, java_info)
        scored.append((score, cls, java_info))

    scored.sort(key=lambda x: -x[0])
    top_classes = scored[:budget]
    remaining = scored[budget:]

    # Output detailed summaries
    if top_classes:
        out.append("## Key Classes")
        out.append("")
        for _score, cls, java_info in top_classes:
            out.append(generate_class_summary(cls, java_info, methods_index))

    # Output pattern groups
    if condition_classes:
        out.append(generate_pattern_group("Condition Hooks", condition_classes, "ConditionHook", methods_index))

    if effect_classes:
        out.append(generate_pattern_group("Effect Hooks", effect_classes, "EffectHook", methods_index))

    if factory_classes:
        out.append(generate_pattern_group("Item Factories", factory_classes, "CEItemFactory", methods_index))

    if item_impl_classes:
        out.append(generate_item_pattern_group(item_impl_classes))

    if expansion_classes:
        out.append(generate_pattern_group("Player Expansions", expansion_classes, "CEPlayerExpansion", methods_index))

    if special_mine_classes:
        out.append(generate_pattern_group("Special Mining", special_mine_classes, "AbstractSpecialMine", methods_index))

    # Remaining ungrouped classes
    if remaining:
        out.append(f"## Other Classes ({len(remaining)})")
        out.append("")
        for _score, cls, java_info in remaining:
            name = cls["name"]
            lines = java_info.get("line_count", 0)
            summary = cls.get("summary", "")
            extends = cls.get("extends", "")
            line_info = f" ({lines}L)" if lines > 0 else ""
            ext_info = f" extends {extends}" if extends else ""
            out.append(f"- **{name}**{line_info}{ext_info}: {summary}")
        out.append("")

    return "\n".join(out)


def find_module_flows(module_name, deps):
    """Extract relevant data flows for a module."""
    flows = []
    data_flows = deps.get("dataFlows", [])
    for flow in data_flows:
        flow_steps = flow.get("flow", [])
        # Check if this module is mentioned in the flow
        for step in flow_steps:
            if module_name.replace("Module", "") in step:
                flows.append(f"**{flow['name']}**: {' -> '.join(flow_steps)}")
                break
    return flows


def find_module_listeners(module_name, entry_points):
    """Find listeners belonging to this module."""
    listeners = []
    all_listeners = entry_points.get("listeners", {})

    for group in ("core", "conditional"):
        for listener in all_listeners.get(group, []):
            if listener.get("module") == module_name:
                listeners.append(listener)

    return listeners


def generate_plugin_summary(modules_data, deps, classes_data):
    """Generate Level 4 plugin summary."""
    out = []
    out.append("# CustomEnchantment - Plugin Summary")
    out.append("")
    out.append("**Type:** Bukkit Plugin (Paper/Leaf 1.21.1) | **Java:** 21 | **Framework:** BafFramework")
    out.append(f"**Classes:** {classes_data.get('count', 0)} | **Modules:** {modules_data.get('count', 0)}")
    out.append("")
    out.append("## What It Does")
    out.append("")
    out.append("Custom enchantment system for Minecraft. Adds config-driven enchantments with")
    out.append("conditions (when to trigger) and effects (what happens). Supports custom items")
    out.append("(artifacts, outfits, sigils, gems), player stat expansions, and menu-based UIs.")
    out.append("")
    out.append("## Architecture")
    out.append("")
    out.append("Three-phase initialization:")
    out.append("1. **Phase 1 - Hooks:** Register ConditionHook/EffectHook strategies (EnchantModule, AttributeModule, FilterModule, ExecuteModule, FeatureModule)")
    out.append("2. **Phase 2 - Config:** Load YAML configs that reference registered hooks (ConfigModule)")
    out.append("3. **Phase 3 - Features:** Start listeners, commands, menus, tasks (remaining 10 modules)")
    out.append("")

    # Module table
    out.append("## Module Overview")
    out.append("")
    out.append("| Module | Classes | Purpose |")
    out.append("|--------|---------|---------|")

    for mod in modules_data.get("modules", []):
        name = mod["name"]
        count = mod.get("classCount", 0)
        purpose = mod.get("purpose", "")
        file_link = MODULE_FILES.get(name, "")
        if file_link:
            out.append(f"| [{name}]({file_link}) | {count} | {purpose} |")
        else:
            out.append(f"| {name} | {count} | {purpose} |")

    out.append("")

    # Data flows
    out.append("## Key Data Flows")
    out.append("")
    data_flows = deps.get("dataFlows", [])
    for flow in data_flows:
        name = flow.get("name", "")
        steps = flow.get("flow", [])
        out.append(f"**{name}:** {' -> '.join(steps)}")
        out.append("")

    # External dependencies
    out.append("## External Dependencies")
    out.append("")
    out.append("**Required:** BafFramework (compileOnly)")
    out.append("**Optional:** CustomMenu, StackMob, Vouchers, ItemDropManager, mcMMO, PlaceholderAPI, CustomShop, DamageIndicator, CustomFarm, Citizens")
    out.append("")

    return "\n".join(out)


def generate_ecosystem_summary():
    """Generate Level 5 ecosystem summary."""
    out = []
    out.append("# CustomEnchantment - Ecosystem Context")
    out.append("")
    out.append("## Position in Server Stack")
    out.append("")
    out.append("```")
    out.append("Minecraft Server (Paper/Leaf 1.21.1)")
    out.append("  |-- BafFramework (core framework - module system, config, commands)")
    out.append("  |     |-- CustomEnchantment (this plugin - enchants, items, player stats)")
    out.append("  |     |-- CustomMenu (optional - external menu integration)")
    out.append("  |     |-- CustomShop (optional - shop integration)")
    out.append("  |     |-- CustomFarm (optional - farm integration)")
    out.append("  |-- PlaceholderAPI (optional - placeholder support)")
    out.append("  |-- mcMMO (optional - MMO skill integration)")
    out.append("  |-- Citizens (optional - NPC integration)")
    out.append("  |-- StackMob (optional - mob stacking compatibility)")
    out.append("```")
    out.append("")
    out.append("## Plugin Scope")
    out.append("")
    out.append("CustomEnchantment adds a YAML-driven enchantment system to Minecraft items.")
    out.append("Enchantments are defined by conditions (when they trigger) and effects (what they do).")
    out.append("The plugin also manages custom item types, player stat tracking, and inventory menus.")
    out.append("")
    out.append("## Key Integration Points")
    out.append("")
    out.append("- **BafFramework**: Module lifecycle, config system, command builder, hook registry")
    out.append("- **Bukkit Events**: 50+ event handlers across 15 listeners")
    out.append("- **YAML Config**: 15+ config files defining enchantments, items, menus")
    out.append("- **SQLite Database**: Player data persistence")
    out.append("- **PlaceholderAPI**: Player stat placeholders for other plugins")
    out.append("")

    return "\n".join(out)


# === Main ===

def main():
    print("Loading Phase 2 indexes...")
    indexes = load_indexes()

    classes_data = indexes["classes"]
    modules_data = indexes["modules"]
    methods_list = indexes["methods"].get("methods", [])
    deps = indexes["dependencies"]
    entry_points = indexes["entry_points"]
    configs_list = indexes["configs"].get("configs", [])

    # Group classes by module
    by_module = defaultdict(list)
    for cls in classes_data.get("classes", []):
        mod = cls.get("module", "Root")
        by_module[mod].append(cls)

    # Build module info lookup
    module_info_map = {}
    for mod in modules_data.get("modules", []):
        module_info_map[mod["name"]] = mod

    # Root module info (not in modules.json)
    if "Root" not in module_info_map:
        module_info_map["Root"] = {
            "name": "Root",
            "package": "com.bafmc.customenchantment",
            "purpose": "Main plugin class, storage maps, logging, messages, utilities",
            "classCount": len(by_module.get("Root", [])),
            "dependsOn": [],
            "dependedBy": ["all modules"],
            "phase": "",
        }

    # Generate Level 5
    print("Generating ECOSYSTEM.md...")
    ecosystem = generate_ecosystem_summary()
    (SCRIPT_DIR / "ECOSYSTEM.md").write_text(ecosystem, encoding="utf-8")

    # Generate Level 4
    print("Generating PLUGIN-SUMMARY.md...")
    plugin_summary = generate_plugin_summary(modules_data, deps, classes_data)
    (SCRIPT_DIR / "PLUGIN-SUMMARY.md").write_text(plugin_summary, encoding="utf-8")

    # Generate Level 3 module files
    total_detailed = 0
    total_grouped = 0

    for module_name, filename in MODULE_FILES.items():
        classes = by_module.get(module_name, [])
        if not classes and module_name not in module_info_map:
            continue

        info = module_info_map.get(module_name, {
            "name": module_name,
            "package": f"com.bafmc.customenchantment.{module_name.lower().replace('module', '')}",
            "purpose": "",
            "classCount": len(classes),
            "dependsOn": [],
            "dependedBy": [],
        })

        print(f"Generating {filename} ({len(classes)} classes)...")
        content = generate_module_file(module_name, info, classes, methods_list, deps, entry_points, configs_list)
        (SCRIPT_DIR / filename).write_text(content, encoding="utf-8")

        # Count detailed vs grouped
        for cls in classes:
            cat = classify_class(cls)
            if cat == "detailed":
                total_detailed += 1
            else:
                total_grouped += 1

    # Summary stats
    print(f"\nGeneration complete!")
    print(f"  Total classes: {classes_data.get('count', 0)}")
    print(f"  Detailed candidates: {total_detailed}")
    print(f"  Pattern-grouped: {total_grouped}")
    print(f"  Output files: {2 + len(MODULE_FILES)} (ECOSYSTEM.md + PLUGIN-SUMMARY.md + {len(MODULE_FILES)} module files)")

    # Size check
    total_size = 0
    for f in SCRIPT_DIR.glob("*.md"):
        total_size += f.stat().st_size
    print(f"  Total size: {total_size / 1024:.1f} KB")


if __name__ == "__main__":
    main()
