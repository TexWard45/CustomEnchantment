"""Chunk builders that convert Phase 2/3 data into searchable Chunk objects."""

from __future__ import annotations

import json
import re

from models import Chunk
from config import INDEX_DIR, SUMMARIES_DIR


def load_json(filename: str) -> dict:
    """Load a JSON file from the index directory."""
    path = INDEX_DIR / filename
    with open(path, encoding="utf-8") as f:
        return json.load(f)


# ── 1. Classes ──────────────────────────────────────────────────────────

def build_class_chunks() -> list[Chunk]:
    """Build chunks from classes.json (541 classes)."""
    data = load_json("classes.json")
    # Detect duplicate names to use FQN for disambiguation
    from collections import Counter
    name_counts = Counter(c["name"] for c in data["classes"])
    chunks = []
    for cls in data["classes"]:
        name = cls["name"]
        module = cls.get("module", "")
        cls_type = cls.get("type", "class")
        summary = cls.get("summary", "")
        tags = cls.get("tags", [])
        extends = cls.get("extends", "")
        implements = cls.get("implements", [])

        parts = [f"{name} is a {cls_type} in {module}"]
        if summary:
            parts[0] += f" that {summary}"
        if extends:
            parts.append(f"Extends: {extends}")
        if implements:
            parts.append(f"Implements: {', '.join(implements)}")
        if tags:
            parts.append(f"Tags: {', '.join(tags)}")

        embed_text = ". ".join(parts)

        display_parts = [f"**{name}** ({cls_type}) — {module}"]
        if summary:
            display_parts.append(summary)
        if cls.get("fqn"):
            display_parts.append(f"`{cls['fqn']}`")

        # Use FQN for ID if class name is duplicated
        chunk_id = f"class:{cls.get('fqn', name)}" if name_counts[name] > 1 else f"class:{name}"
        chunks.append(Chunk(
            id=chunk_id,
            source="classes",
            embed_text=embed_text,
            display_text="\n".join(display_parts),
            metadata={
                "module": module,
                "type": cls_type,
                "tags": tags,
                "fqn": cls.get("fqn", ""),
                "file": cls.get("file", ""),
                "name": name,
            },
        ))
    return chunks


# ── 2. Enchantments ────────────────────────────────────────────────────

def build_enchantment_chunks() -> list[Chunk]:
    """Build chunks from enchantments.json (579 enchantments)."""
    data = load_json("enchantments.json")
    chunks = []
    for ench in data["enchantments"]:
        eid = ench["id"]
        display = ench.get("display", eid)
        group = ench.get("group", "")
        applies = ench.get("applies", [])
        triggers = ench.get("triggers", [])
        effects = ench.get("effects", [])
        conditions = ench.get("conditions", [])
        max_level = ench.get("maxLevel", 1)
        source_file = ench.get("file", "")

        embed_text = (
            f"{display} is a {group} enchantment. "
            f"Applies to: {', '.join(applies)}. "
            f"Triggers: {', '.join(triggers)}. "
            f"Effects: {', '.join(effects)}"
        )
        if conditions:
            embed_text += f". Conditions: {', '.join(conditions)}"

        display_text = (
            f"**{display}** ({eid}) — {group} | Max Level: {max_level}\n"
            f"Applies: {', '.join(applies)}\n"
            f"Triggers: {', '.join(triggers)}\n"
            f"Effects: {', '.join(effects)}"
        )

        # Include source file in ID to handle enchantments in multiple YAML files
        file_stem = source_file.replace(".yml", "") if source_file else "unknown"
        chunks.append(Chunk(
            id=f"enchant:{file_stem}:{eid}",
            source="enchantments",
            embed_text=embed_text,
            display_text=display_text,
            metadata={
                "group": group,
                "applies": applies,
                "triggers": triggers,
                "effects": effects,
                "conditions": conditions,
                "maxLevel": max_level,
                "file": source_file,
                "name": display,
            },
        ))
    return chunks


# ── 3. Methods ──────────────────────────────────────────────────────────

def build_method_chunks() -> list[Chunk]:
    """Build chunks from methods.json (257 methods)."""
    data = load_json("methods.json")
    chunks = []
    for meth in data["methods"]:
        cls_name = meth["class"]
        method_name = meth["name"]
        returns = meth.get("returns", "")
        category = meth.get("category", "")
        tags = meth.get("tags", [])

        embed_text = (
            f"{cls_name}.{method_name}() returns '{returns}'. "
            f"Category: {category}. Tags: {', '.join(tags)}"
        )

        display_text = (
            f"**{cls_name}.{method_name}()** → `{returns}`\n"
            f"Category: {category}"
        )

        chunks.append(Chunk(
            id=f"method:{cls_name}.{method_name}",
            source="methods",
            embed_text=embed_text,
            display_text=display_text,
            metadata={
                "class": cls_name,
                "name": method_name,
                "returns": returns,
                "category": category,
                "tags": tags,
            },
        ))
    return chunks


# ── 4. Configs ──────────────────────────────────────────────────────────

def build_config_chunks() -> list[Chunk]:
    """Build chunks from configs.json."""
    data = load_json("configs.json")
    chunks = []
    for cfg in data["configs"]:
        java_class = cfg["javaClass"]
        yaml_file = cfg.get("yamlFile", "")
        module = cfg.get("module", "")
        key_paths = cfg.get("keyPaths", [])

        keys_str = ", ".join(key_paths) if key_paths else "(no key paths)"
        embed_text = (
            f"{java_class} reads from {yaml_file}. "
            f"Module: {module}. Keys: {keys_str}"
        )

        display_text = (
            f"**{java_class}** — `{yaml_file}` ({module})\n"
            f"Keys: {keys_str}"
        )

        chunks.append(Chunk(
            id=f"config:{java_class}",
            source="configs",
            embed_text=embed_text,
            display_text=display_text,
            metadata={
                "module": module,
                "yamlFile": yaml_file,
                "keyPaths": key_paths,
                "name": java_class,
                "fqn": cfg.get("fqn", ""),
            },
        ))
    return chunks


# ── 5. Modules ──────────────────────────────────────────────────────────

def build_module_chunks() -> list[Chunk]:
    """Build chunks from modules.json (16 modules)."""
    data = load_json("modules.json")
    chunks = []
    for mod in data["modules"]:
        name = mod["name"]
        purpose = mod.get("purpose", "")
        depends_on = mod.get("dependsOn", [])
        depended_by = mod.get("dependedBy", [])
        tags = mod.get("tags", [])

        deps_str = ", ".join(depends_on) if depends_on else "none"
        embed_text = (
            f"{name} provides: {purpose}. "
            f"Depends on: {deps_str}"
        )
        if depended_by:
            embed_text += f". Depended by: {', '.join(depended_by)}"

        display_text = (
            f"**{name}** — {purpose}\n"
            f"Depends on: {deps_str}\n"
            f"Depended by: {', '.join(depended_by) if depended_by else 'none'}"
        )

        chunks.append(Chunk(
            id=f"module:{name}",
            source="modules",
            embed_text=embed_text,
            display_text=display_text,
            metadata={
                "name": name,
                "purpose": purpose,
                "dependsOn": depends_on,
                "dependedBy": depended_by,
                "tags": tags,
                "package": mod.get("package", ""),
                "classCount": mod.get("classCount", 0),
            },
        ))
    return chunks


# ── 6. Entry Points ────────────────────────────────────────────────────

def build_entry_point_chunks() -> list[Chunk]:
    """Build chunks from entry-points.json (listeners + commands)."""
    data = load_json("entry-points.json")
    chunks = []

    # Listeners (core + conditional)
    for section in ["core", "conditional"]:
        for listener in data.get("listeners", {}).get(section, []):
            cls = listener["class"]
            module = listener.get("module", "")
            events = listener.get("events", [])
            tags = listener.get("tags", [])
            conditional = listener.get("conditional", False)

            events_str = ", ".join(events) if events else "none"
            embed_text = f"{cls} handles: {events_str}. Module: {module}"
            if conditional:
                embed_text += f". Condition: {listener.get('condition', '')}"

            display_text = (
                f"**{cls}** ({module})\n"
                f"Events: {events_str}"
            )

            chunks.append(Chunk(
                id=f"listener:{cls}",
                source="entry-points",
                embed_text=embed_text,
                display_text=display_text,
                metadata={
                    "module": module,
                    "events": events,
                    "tags": tags,
                    "conditional": conditional,
                    "name": cls,
                },
            ))

    # Commands
    for cmd in data.get("commands", {}).get("root", []):
        name = cmd["name"]
        handler = cmd.get("handler", "")
        module = cmd.get("module", "")
        subcommands = cmd.get("subcommands", [])
        tags = cmd.get("tags", [])

        subs_str = ", ".join(subcommands) if subcommands else "none"
        embed_text = (
            f"Command {name} handled by {handler}. "
            f"Module: {module}. Subcommands: {subs_str}"
        )

        display_text = (
            f"**{name}** → {handler} ({module})\n"
            f"Subcommands: {subs_str}"
        )

        chunks.append(Chunk(
            id=f"command:{handler}",
            source="entry-points",
            embed_text=embed_text,
            display_text=display_text,
            metadata={
                "module": module,
                "name": handler,
                "command": name,
                "subcommands": subcommands,
                "tags": tags,
            },
        ))

    return chunks


# ── 7. Summaries ────────────────────────────────────────────────────────

def build_summary_chunks() -> list[Chunk]:
    """Build chunks from Phase 3 markdown summaries, split at ### headings."""
    chunks = []

    for md_path in sorted(SUMMARIES_DIR.glob("*.md")):
        filename = md_path.stem  # e.g. "enchant", "PLUGIN-SUMMARY"
        text = md_path.read_text(encoding="utf-8")

        # Split on ### headings (level 3)
        sections = re.split(r"(?=^### )", text, flags=re.MULTILINE)

        for i, section in enumerate(sections):
            section = section.strip()
            if not section or len(section) < 30:
                continue

            # Extract heading if present
            heading_match = re.match(r"^###\s+(.+)", section)
            heading = heading_match.group(1).strip() if heading_match else f"section-{i}"

            # Clean heading for ID
            clean_heading = re.sub(r"[^a-zA-Z0-9]+", "-", heading).strip("-").lower()
            chunk_id = f"summary:{filename}:{clean_heading}"

            # Truncate very long sections for embedding (keep first 500 chars)
            embed_text = section[:500] if len(section) > 500 else section

            chunks.append(Chunk(
                id=chunk_id,
                source="summaries",
                embed_text=embed_text,
                display_text=section,
                metadata={
                    "module": filename,
                    "section": heading,
                    "file": md_path.name,
                },
            ))

    return chunks


# ── Build all ───────────────────────────────────────────────────────────

ALL_BUILDERS = [
    ("classes", build_class_chunks),
    ("enchantments", build_enchantment_chunks),
    ("methods", build_method_chunks),
    ("configs", build_config_chunks),
    ("modules", build_module_chunks),
    ("entry-points", build_entry_point_chunks),
    ("summaries", build_summary_chunks),
]


def build_all_chunks() -> list[Chunk]:
    """Build all chunks from all sources."""
    all_chunks = []
    for _name, builder in ALL_BUILDERS:
        chunks = builder()
        all_chunks.extend(chunks)
    return all_chunks


if __name__ == "__main__":
    chunks = build_all_chunks()
    print(f"Total chunks: {len(chunks)}")
    # Print breakdown
    from collections import Counter
    counts = Counter(c.source for c in chunks)
    for source, count in sorted(counts.items()):
        print(f"  {source}: {count}")
