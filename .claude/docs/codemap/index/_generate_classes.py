#!/usr/bin/env python3
"""Generate classes.json from CLASS-REGISTRY.md"""
import json, re, sys, os

BASE = "com.bafmc.customenchantment"
REGISTRY_PATH = os.path.join(os.path.dirname(__file__), "..", "CLASS-REGISTRY.md")

# Package mapping for each section header
SECTION_PACKAGES = {
    "Root": "",
    "API": ".api",
    "Attribute": ".attribute",
    "CombatLog": ".combatlog",
    "Command": ".command",
    "Config": ".config",
    "Constant": ".constant",
    "CustomMenu": ".custommenu",
    "Database": ".database",
    "Enchant": ".enchant",
    "Event": ".event",
    "Exception": ".exception",
    "Execute": ".execute",
    "Feature": ".feature",
    "Filter": ".filter",
    "Guard": ".guard",
    "Item": ".item",
    "Listener": ".listener",
    "Menu": ".menu",
    "NMS": ".nms",
    "Placeholder": ".placeholder",
    "Player": ".player",
    "Task": ".task",
    "Utils": ".utils",
}

# Sub-section package overrides
SUB_PACKAGES = {
    "ConditionHook": ".enchant.condition",
    "EffectHook": ".enchant.effect",
    "Core": None,  # uses parent section
    "Item Types": ".item",
    "Tinkerer": ".menu.tinkerer",
    "BookCraft": ".menu.bookcraft",
    "CEAnvil": ".menu.ceanvil",
    "BookUpgrade": ".menu.bookupgrade",
    "ArtifactUpgrade": ".menu.artifactupgrade",
    "Equipment": ".menu.equipment",
    "Attributes": ".player.attribute",
    "Bonuses": ".player",
    "Mining": ".player.mining",
    "Player Expansions": ".player",
    "Other": None,
}

# Module mapping by package
def get_module(pkg):
    if not pkg: return "Root"
    p = pkg.replace(BASE, "")
    if p.startswith(".enchant"): return "EnchantModule"
    if p.startswith(".item"): return "ItemModule"
    if p.startswith(".command"): return "CommandModule"
    if p.startswith(".config"): return "ConfigModule"
    if p.startswith(".listener"): return "ListenerModule"
    if p.startswith(".menu"): return "MenuModule"
    if p.startswith(".player"): return "PlayerModule"
    if p.startswith(".task"): return "TaskModule"
    if p.startswith(".guard"): return "GuardModule"
    if p.startswith(".database"): return "DatabaseModule"
    if p.startswith(".attribute"): return "AttributeModule"
    if p.startswith(".filter"): return "FilterModule"
    if p.startswith(".execute"): return "ExecuteModule"
    if p.startswith(".feature"): return "FeatureModule"
    if p.startswith(".custommenu"): return "CustomMenuModule"
    if p.startswith(".placeholder"): return "PlaceholderModule"
    if p.startswith(".combatlog"): return "CombatLogModule"
    if p.startswith(".api"): return "Root"
    if p.startswith(".constant"): return "Root"
    if p.startswith(".event"): return "Root"
    if p.startswith(".exception"): return "Root"
    if p.startswith(".nms"): return "Root"
    if p.startswith(".utils"): return "Root"
    return "Root"

# Auto-tagging rules
def get_tags(name, pkg_suffix, extends, implements, class_type):
    tags = set()
    # Domain tags from package
    if ".enchant.condition" in pkg_suffix: tags.update(["enchantment", "conditions"])
    elif ".enchant.effect" in pkg_suffix: tags.update(["enchantment", "effects"])
    elif ".enchant" in pkg_suffix: tags.add("enchantment")
    if ".item" in pkg_suffix: tags.add("items")
    if ".player.attribute" in pkg_suffix: tags.update(["player", "attributes"])
    elif ".player.mining" in pkg_suffix: tags.update(["player", "mining"])
    elif ".player" in pkg_suffix: tags.add("player")
    if ".menu" in pkg_suffix: tags.add("gui")
    if ".command" in pkg_suffix: tags.add("command")
    if ".config" in pkg_suffix: tags.add("config")
    if ".database" in pkg_suffix: tags.add("persistence")
    if ".listener" in pkg_suffix: tags.add("events")
    if ".task" in pkg_suffix: tags.add("scheduling")
    if ".attribute" in pkg_suffix and ".player" not in pkg_suffix: tags.add("attributes")
    if ".guard" in pkg_suffix: tags.add("mobs")
    if ".filter" in pkg_suffix: tags.add("filtering")
    if ".custommenu" in pkg_suffix or ".placeholder" in pkg_suffix: tags.add("integration")
    if ".execute" in pkg_suffix: tags.add("executes")
    if ".feature" in pkg_suffix: tags.add("items")
    if ".combatlog" in pkg_suffix: tags.add("combat")
    if ".api" in pkg_suffix: tags.add("api")
    if ".event" in pkg_suffix: tags.add("events")
    if ".nms" in pkg_suffix: tags.add("nms")
    if ".constant" in pkg_suffix: tags.add("config")

    # Pattern tags from extends/implements
    if extends:
        if "ConditionHook" in extends: tags.update(["hook", "conditions"])
        if "EffectHook" in extends: tags.update(["hook", "effects"])
        if "ExecuteHook" in extends: tags.update(["hook", "executes"])
        if "PluginModule" in extends: tags.add("module")
        if "StrategyRegister" in extends: tags.add("registry")
        if "PlayerPerTickTask" in extends: tags.update(["task", "per-tick"])
        if "CEItemFactory" in extends or "Factory" in extends: tags.add("factory")
        if "CEItem" in extends and "Factory" not in extends and "Storage" not in extends: tags.add("items")
        if "CEItemStorage" in extends or "StorageMap" in extends: tags.add("persistence")
        if "AbstractConfig" in extends: tags.add("config")
        if "AbstractAttributeMap" in extends: tags.add("attributes")
        if "AbstractSpecialMine" in extends: tags.add("mining")
        if "AbstractListHandler" in extends: tags.add("gui")
        if "AbstractTarget" in extends: tags.add("combat")
    if implements:
        if "Listener" in implements: tags.add("listener")
        if "Cloneable" in implements: tags.add("data-class")

    # Name-based tags
    if re.search(r"Damage|Combat|Attack", name): tags.add("combat")
    if re.search(r"Mine|Mining|Dig", name): tags.add("mining")
    if name.endswith("Data"): tags.add("data-class")
    if name.endswith("Factory"): tags.add("factory")
    if name.endswith("Manager"): tags.add("manager")
    if name.endswith("Register"): tags.add("registry")
    if name.endswith("Builder"): tags.add("builder")
    if name.endswith("Utils"): tags.add("utility")
    if name.endswith("Config"): tags.add("config-class")
    if name.endswith("Task"): tags.add("task")
    if name.endswith("Command"): tags.add("command")
    if name.startswith("Command"): tags.add("command")

    # class type tags
    if class_type == "abstract": tags.add("abstract")
    if class_type == "interface": tags.add("interface")
    if class_type == "enum": tags.add("enum")

    if not tags:
        tags.add("core")

    return sorted(tags)

def make_file_path(pkg_suffix, name):
    """Generate approximate file path from package."""
    # Determine module directory
    module_name = get_module(BASE + pkg_suffix)
    if module_name == "Root":
        module_dir = ""
    else:
        module_dir = module_name + "/"

    pkg_path = pkg_suffix.replace(".", "/")
    if pkg_path.startswith("/"):
        pkg_path = pkg_path[1:]

    # Clean generic types from name
    clean_name = re.sub(r"<.*>", "", name)
    return f"src/main/java/com/bafmc/customenchantment/{pkg_path}/{clean_name}.java"

def generate_summary(name, extends, class_type, pkg_suffix):
    """Generate one-line summary from class metadata."""
    clean = re.sub(r"([A-Z])", r" \1", name).strip()
    if extends and extends not in ["—", ""]:
        clean_ext = re.sub(r"\\?<.*>", "", extends)
        return f"{clean} ({clean_ext})"
    return clean

def parse_registry(filepath):
    classes = []
    current_section = "Root"
    current_sub = None
    current_pkg_suffix = ""

    with open(filepath, "r", encoding="utf-8") as f:
        lines = f.readlines()

    i = 0
    while i < len(lines):
        line = lines[i].strip()

        # Section headers
        m = re.match(r"^## (\w[\w/ ]*?) \(", line)
        if m:
            section_name = m.group(1).strip()
            if section_name in SECTION_PACKAGES:
                current_section = section_name
                current_pkg_suffix = SECTION_PACKAGES[section_name]
                current_sub = None
            i += 1
            continue

        # Sub-section headers
        m = re.match(r"^### (.+?)(?:\s*—|\s*\()", line)
        if m:
            sub_name = m.group(1).strip()
            if sub_name in SUB_PACKAGES:
                current_sub = sub_name
                if SUB_PACKAGES[sub_name] is not None:
                    current_pkg_suffix = SUB_PACKAGES[sub_name]
                else:
                    current_pkg_suffix = SECTION_PACKAGES.get(current_section, "")
            i += 1
            continue

        # Inline class lists (ConditionHook, EffectHook lists)
        if current_sub == "ConditionHook" and not line.startswith("|") and not line.startswith("#") and line and not line.startswith("```"):
            names = [n.strip() for n in line.split(",") if n.strip()]
            for n in names:
                clean_n = re.sub(r"\\?<.*?>", "", n)
                if clean_n and clean_n[0].isupper():
                    pkg = ".enchant.condition"
                    classes.append({
                        "name": clean_n,
                        "fqn": BASE + pkg + "." + clean_n,
                        "module": "EnchantModule",
                        "file": make_file_path(pkg, clean_n),
                        "type": "class",
                        "extends": "ConditionHook",
                        "implements": [],
                        "annotations": [],
                        "tags": get_tags(clean_n, pkg, "ConditionHook", "", "class"),
                        "summary": f"Condition hook implementation"
                    })
            i += 1
            continue

        if current_sub == "EffectHook" and not line.startswith("|") and not line.startswith("#") and line and not line.startswith("```"):
            # Parse bold-labeled lines like **Active abilities:** Name1, Name2
            m2 = re.match(r"\*\*(.+?)\*\*\s*(.+)", line)
            if m2:
                category = m2.group(1).strip().rstrip(":")
                names = [n.strip() for n in m2.group(2).split(",") if n.strip()]
                for n in names:
                    clean_n = re.sub(r"\\?<.*?>", "", n)
                    if clean_n and clean_n[0].isupper():
                        pkg = ".enchant.effect"
                        classes.append({
                            "name": clean_n,
                            "fqn": BASE + pkg + "." + clean_n,
                            "module": "EnchantModule",
                            "file": make_file_path(pkg, clean_n),
                            "type": "class",
                            "extends": "EffectHook",
                            "implements": [],
                            "annotations": [],
                            "tags": get_tags(clean_n, pkg, "EffectHook", "", "class"),
                            "summary": f"Effect hook — {category.lower()}"
                        })
            i += 1
            continue

        # Table rows
        m = re.match(r"^\|\s*(.+?)\s*\|", line)
        if m and not line.startswith("| Class") and not line.startswith("|---") and not line.startswith("| Type") and not line.startswith("| #"):
            cells = [c.strip() for c in line.split("|")[1:-1]]

            if len(cells) >= 2:
                name_raw = cells[0]
                # Clean markdown formatting
                name_clean = re.sub(r"\\?<.*?>", "", name_raw).replace("\\", "").strip()

                # Skip header-like rows
                if name_clean in ("Class", "---", "#", "Type", "Abstract Class", "Task", "Listener", "Command", "Subcommand"):
                    i += 1
                    continue

                # Determine class type and extends
                class_type = "class"
                extends = ""
                implements_str = ""
                summary = ""

                if len(cells) >= 4:  # Full table with Type, Extends, Summary
                    type_cell = cells[1].lower().strip()
                    if "abstract" in type_cell: class_type = "abstract"
                    elif "interface" in type_cell: class_type = "interface"
                    elif "enum" in type_cell: class_type = "enum"

                    ext_cell = cells[2] if len(cells) > 2 else ""
                    extends = re.sub(r"\\?<.*?>", "", ext_cell).replace("\\", "").strip()
                    if extends == "—" or extends == "-": extends = ""

                    summary = cells[3] if len(cells) > 3 else ""
                elif len(cells) == 3:  # Shorter tables
                    type_cell = cells[1].lower().strip() if cells[1].strip() not in ["—", "-"] else ""
                    if "abstract" in type_cell: class_type = "abstract"
                    elif "interface" in type_cell: class_type = "interface"
                    elif "enum" in type_cell: class_type = "enum"
                    summary = cells[2] if len(cells) > 2 else ""

                # Handle special sections
                # Item Types table has different format
                if current_sub == "Item Types" and len(cells) >= 3:
                    # Parse item type rows
                    item_type = cells[0]
                    item_classes_str = cells[1]
                    additional = cells[2] if len(cells) > 2 else ""

                    all_names = []
                    for part in (item_classes_str + ", " + additional).split(","):
                        n = part.strip()
                        if n and n != "—" and n[0].isupper():
                            all_names.append(re.sub(r"\\?<.*?>", "", n).replace("\\", ""))

                    for n in all_names:
                        pkg = ".item"
                        if "artifact" in item_type.lower(): pkg = ".item.artifact"
                        elif "book" in item_type.lower(): pkg = ".item.book"
                        elif "gem" in item_type.lower() and "drill" not in item_type.lower(): pkg = ".item.gem"
                        elif "gemdrill" in item_type.lower(): pkg = ".item.gemdrill"
                        elif "outfit" in item_type.lower(): pkg = ".item.outfit"
                        elif "sigil" in item_type.lower(): pkg = ".item.sigil"
                        elif "skin" in item_type.lower(): pkg = ".item.skin"
                        elif "randombook" in item_type.lower(): pkg = ".item.randombook"
                        elif "banner" in item_type.lower(): pkg = ".item"
                        elif "mask" in item_type.lower(): pkg = ".item"

                        ext = ""
                        if "Factory" in n: ext = "CEItemFactory"
                        elif "Storage" in n: ext = "CEItemStorage"
                        elif "Data" in n: ext = "CEItemData"
                        elif "Map" in n: ext = ""
                        elif "Simple" in n: ext = ""
                        elif "Filter" in n: ext = ""
                        elif "Group" in n: ext = ""
                        elif "Settings" in n: ext = ""
                        else: ext = "CEItem"

                        classes.append({
                            "name": n,
                            "fqn": BASE + pkg + "." + n,
                            "module": "ItemModule",
                            "file": make_file_path(pkg, n),
                            "type": "class",
                            "extends": ext,
                            "implements": [],
                            "annotations": [],
                            "tags": get_tags(n, pkg, ext, "", "class"),
                            "summary": f"{item_type} item type"
                        })
                    i += 1
                    continue

                # Menu subsection inline lists
                if current_section == "Menu" and current_sub in ("Tinkerer", "BookCraft", "CEAnvil", "BookUpgrade", "ArtifactUpgrade", "Equipment"):
                    # These are listed as inline text, not tables
                    pass

                # Regular table entry
                if name_clean and name_clean[0].isupper() and len(name_clean) > 1:
                    pkg = current_pkg_suffix
                    module = get_module(BASE + pkg)

                    classes.append({
                        "name": name_clean,
                        "fqn": BASE + pkg + "." + name_clean,
                        "module": module,
                        "file": make_file_path(pkg, name_clean),
                        "type": class_type,
                        "extends": extends,
                        "implements": [],
                        "annotations": [],
                        "tags": get_tags(name_clean, pkg, extends, "", class_type),
                        "summary": summary.replace("\\", "") if summary else generate_summary(name_clean, extends, class_type, pkg)
                    })

        # Parse inline class lists for Menu section
        if current_section == "Menu" and not line.startswith("|") and not line.startswith("#"):
            if current_sub in ("Tinkerer", "BookCraft", "CEAnvil", "BookUpgrade", "ArtifactUpgrade", "Equipment", "Core"):
                # Lines like: "MenuModule, MenuListenerAbstract (abstract), BookData"
                names = [n.strip() for n in line.split(",") if n.strip()]
                for n in names:
                    # Remove annotations like (abstract), (interface)
                    clean = re.sub(r"\s*\(.*?\)\s*", "", n).strip()
                    clean = re.sub(r"\\?<.*?>", "", clean).replace("\\", "")
                    if clean and clean[0].isupper() and len(clean) > 1:
                        ct = "abstract" if "(abstract)" in n else ("interface" if "(interface)" in n else "class")
                        pkg = SUB_PACKAGES.get(current_sub, ".menu") or ".menu"
                        classes.append({
                            "name": clean,
                            "fqn": BASE + pkg + "." + clean,
                            "module": "MenuModule",
                            "file": make_file_path(pkg, clean),
                            "type": ct,
                            "extends": "",
                            "implements": [],
                            "annotations": [],
                            "tags": get_tags(clean, pkg, "", "", ct),
                            "summary": f"Menu — {current_sub or 'core'}"
                        })

        # Player section inline lists
        if current_section == "Player" and not line.startswith("|") and not line.startswith("#"):
            if current_sub in ("Core", "Attributes", "Bonuses", "Mining", "Player Expansions", "Other"):
                names = [n.strip() for n in line.split(",") if n.strip()]
                for n in names:
                    clean = re.sub(r"\s*\(.*?\)\s*", "", n).strip()
                    clean = re.sub(r"\\?<.*?>", "", clean).replace("\\", "")
                    if clean and clean[0].isupper() and len(clean) > 1:
                        ct = "abstract" if "(abstract)" in n else ("interface" if "(interface)" in n else "class")
                        pkg = SUB_PACKAGES.get(current_sub) or ".player"
                        ext = ""
                        if "abstract" in n.lower() and "Abstract" in clean: ext = ""
                        elif "Expansion" in clean and clean != "CEPlayerExpansionRegister": ext = "CEPlayerExpansion"
                        elif "SpecialMine" in clean and "Abstract" not in clean: ext = "AbstractSpecialMine"
                        elif "AttributeMap" in clean and "Abstract" not in clean and "Register" not in clean and "Data" not in clean: ext = "AbstractAttributeMap"

                        classes.append({
                            "name": clean,
                            "fqn": BASE + pkg + "." + clean,
                            "module": "PlayerModule",
                            "file": make_file_path(pkg, clean),
                            "type": ct,
                            "extends": ext,
                            "implements": [],
                            "annotations": [],
                            "tags": get_tags(clean, pkg, ext, "", ct),
                            "summary": f"Player — {current_sub or 'core'}"
                        })

        i += 1

    return classes

def deduplicate(classes):
    seen = set()
    result = []
    for c in classes:
        key = c["fqn"]
        if key not in seen:
            seen.add(key)
            result.append(c)
    return result

if __name__ == "__main__":
    classes = parse_registry(REGISTRY_PATH)
    classes = deduplicate(classes)

    output = {
        "generated": "2026-02-23",
        "plugin": "CustomEnchantment",
        "count": len(classes),
        "classes": classes
    }

    out_path = os.path.join(os.path.dirname(__file__), "classes.json")
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(output, f, indent=2, ensure_ascii=False)

    print(f"Generated {len(classes)} class entries to classes.json")
