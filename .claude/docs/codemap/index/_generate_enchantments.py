#!/usr/bin/env python3
"""Generate enchantments.json from enchantment YAML files."""
import json, os, re, yaml, io

ENCHANT_DIR = os.path.join(os.path.dirname(__file__), "..", "..", "..", "..", "src", "main", "resources", "enchantment")

# Auto-tagging by applies field
APPLIES_TAGS = {
    "ALL_SWORD": "sword", "ALL_AXE": "axe", "ALL_BOW": "bow",
    "ALL_ARMOR": "armor", "ALL_PICKAXE": "pickaxe", "ALL_HOE": "hoe",
    "ALL_BOOTS": "boots", "ALL_TRIDENT": "trident", "TRIDENT": "trident",
    "ALL_SHOVEL": "shovel", "PLAYER_HEAD": "mask", "ALL_GEM": "gem",
    "SHIELD": "shield", "ALL_HELMET": "helmet", "ALL_CHESTPLATE": "chestplate",
    "ALL_LEGGINGS": "leggings",
}

# Group to tag mapping
GROUP_TAGS = {
    "common": "common", "rare": "rare", "epic": "epic",
    "legendary": "legendary", "supreme": "supreme", "ultimate": "ultimate",
    "event": "event", "artifact": "artifact", "mask": "mask",
    "tool": "tool", "set": "set",
}

def extract_identifiers(value_list, prefix_type="condition"):
    """Extract unique identifiers from condition/effect lists."""
    ids = set()
    if not value_list or not isinstance(value_list, list):
        return sorted(ids)
    for item in value_list:
        if not isinstance(item, str):
            continue
        # Handle OR conditions: "TARGET=ENEMY | ENTITY_TYPE:PLAYER"
        parts = [p.strip() for p in item.split("|")]
        for part in parts:
            # Extract the identifier before : or =
            m = re.match(r"([A-Z_]+)", part)
            if m:
                ids.add(m.group(1))
    return sorted(ids)

def extract_effect_ids(value_list):
    """Extract effect identifiers from effect lists."""
    ids = set()
    if not value_list or not isinstance(value_list, list):
        return sorted(ids)
    for item in value_list:
        if not isinstance(item, str):
            continue
        m = re.match(r"([A-Z_]+)", item)
        if m:
            ids.add(m.group(1))
    return sorted(ids)

def get_trigger_types(levels_data):
    """Extract unique trigger types from all levels."""
    types = set()
    if not levels_data or not isinstance(levels_data, dict):
        return sorted(types)
    for level_key, level_val in levels_data.items():
        if not isinstance(level_val, dict):
            continue
        for trigger_name, trigger_data in level_val.items():
            if isinstance(trigger_data, dict) and "type" in trigger_data:
                types.add(trigger_data["type"])
    return sorted(types)

def get_conditions_effects(levels_data):
    """Extract conditions and effects from first available level."""
    conditions = set()
    effects = set()
    if not levels_data or not isinstance(levels_data, dict):
        return sorted(conditions), sorted(effects)

    for level_key, level_val in levels_data.items():
        if not isinstance(level_val, dict):
            continue
        for trigger_name, trigger_data in level_val.items():
            if not isinstance(trigger_data, dict):
                continue
            if "conditions" in trigger_data:
                for c in extract_identifiers(trigger_data["conditions"]):
                    conditions.add(c)
            if "effects" in trigger_data:
                for e in extract_effect_ids(trigger_data["effects"]):
                    effects.add(e)
        break  # Only first level

    return sorted(conditions), sorted(effects)

def get_tags(enchant_id, group, applies_list, triggers, file_name):
    """Generate tags for an enchantment."""
    tags = set(["enchantment"])

    # Group tag
    group_lower = (group or "").lower()
    for prefix, tag in GROUP_TAGS.items():
        if group_lower.startswith(prefix):
            tags.add(tag)

    # Prime tag
    if group_lower.startswith("prime"):
        tags.add("prime")

    # Applies tags
    for a in (applies_list or []):
        a_upper = a.upper() if isinstance(a, str) else ""
        if a_upper in APPLIES_TAGS:
            tags.add(APPLIES_TAGS[a_upper])

    # Combat-related
    if any(t in (triggers or []) for t in ["ATTACK", "DEFENSE"]):
        tags.add("combat")

    # File-based tags
    fname = file_name.replace(".yml", "")
    if fname.startswith("artifact"):
        tags.add("artifact")
    if fname == "mask":
        tags.add("mask")
    if fname in ("sword", "axe", "bow", "trident"):
        tags.add("combat")
    if fname in ("pickaxe", "hoe", "tool"):
        tags.add("mining")

    return sorted(tags)

def _fallback_parse(filepath):
    """Fallback parser for YAML files with unquoted color codes."""
    data = {}
    current_key = None
    current_data = {}
    with open(filepath, "r", encoding="utf-8") as f:
        for line in f:
            # Top-level key (no indentation)
            m = re.match(r'^([a-z][a-z0-9_-]*):\s*$', line)
            if m:
                if current_key:
                    data[current_key] = current_data
                current_key = m.group(1)
                current_data = {}
                continue
            if current_key and line.startswith("  "):
                stripped = line.strip()
                if stripped.startswith("group:"):
                    current_data["group"] = stripped.split(":", 1)[1].strip()
                elif stripped.startswith("display:"):
                    current_data["display"] = stripped.split(":", 1)[1].strip()
                elif stripped.startswith("max-level:"):
                    try:
                        current_data["max-level"] = int(stripped.split(":", 1)[1].strip())
                    except ValueError:
                        current_data["max-level"] = 1
                elif stripped.startswith("- ALL_") or stripped.startswith("- PLAYER_") or stripped.startswith("- TRIDENT") or stripped.startswith("- SHIELD"):
                    current_data.setdefault("applies", []).append(stripped.lstrip("- ").strip())
    if current_key:
        data[current_key] = current_data
    return data

def parse_enchantments():
    enchantments = []
    yml_files = sorted([f for f in os.listdir(ENCHANT_DIR) if f.endswith(".yml")])

    for yml_file in yml_files:
        filepath = os.path.join(ENCHANT_DIR, yml_file)
        try:
            with open(filepath, "r", encoding="utf-8") as f:
                content = f.read()
            # Pre-process: quote unquoted & color codes that break YAML anchors
            content = re.sub(r'(?<!")&([0-9a-fklmnor])', r'"&\1', content)
            # This is a rough fix; for robustness, wrap problematic lines
            try:
                data = yaml.safe_load(content)
            except yaml.YAMLError:
                # Fallback: try line-by-line key extraction
                data = _fallback_parse(filepath)
        except Exception as e:
            print(f"Warning: Failed to parse {yml_file}: {e}")
            continue

        if not isinstance(data, dict):
            continue

        for ench_id, ench_data in data.items():
            if not isinstance(ench_data, dict):
                continue

            group = str(ench_data.get("group", ""))
            display = str(ench_data.get("display", ench_id))
            max_level = ench_data.get("max-level", 1)
            applies = ench_data.get("applies", [])
            if not isinstance(applies, list):
                applies = [applies] if applies else []
            applies = [str(a) for a in applies]

            levels = ench_data.get("levels", {})
            triggers = get_trigger_types(levels)
            conditions, effects = get_conditions_effects(levels)
            tags = get_tags(ench_id, group, applies, triggers, yml_file)

            entry = {
                "id": ench_id,
                "file": yml_file,
                "group": group,
                "display": display,
                "maxLevel": max_level,
            }
            if applies: entry["applies"] = applies
            if triggers: entry["triggers"] = triggers
            if conditions: entry["conditions"] = conditions
            if effects: entry["effects"] = effects
            entry["tags"] = tags
            enchantments.append(entry)

    return enchantments

def deduplicate(enchantments):
    """Remove duplicates keeping first occurrence per (id, file)."""
    seen = set()
    result = []
    for e in enchantments:
        key = (e["id"], e["file"])
        if key not in seen:
            seen.add(key)
            result.append(e)
    return result

if __name__ == "__main__":
    enchantments = parse_enchantments()
    enchantments = deduplicate(enchantments)

    output = {
        "generated": "2026-02-23",
        "plugin": "CustomEnchantment",
        "count": len(enchantments),
        "files": sorted(set(e["file"] for e in enchantments)),
        "enchantments": enchantments
    }

    out_path = os.path.join(os.path.dirname(__file__), "enchantments.json")
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(output, f, indent=2, ensure_ascii=False)

    print(f"Generated {len(enchantments)} enchantment entries from {len(output['files'])} files")
    print(f"File size: {os.path.getsize(out_path)} bytes")
