#!/usr/bin/env python3
"""Generate search-tags.json from classes.json and enchantments.json."""
import json, os
from collections import defaultdict

INDEX_DIR = os.path.dirname(__file__)

def main():
    tags = defaultdict(set)

    # Load classes
    with open(os.path.join(INDEX_DIR, "classes.json"), "r", encoding="utf-8") as f:
        classes_data = json.load(f)
    for cls in classes_data["classes"]:
        for tag in cls["tags"]:
            tags[tag].add(cls["name"])

    # Load enchantments
    with open(os.path.join(INDEX_DIR, "enchantments.json"), "r", encoding="utf-8") as f:
        ench_data = json.load(f)
    for ench in ench_data["enchantments"]:
        for tag in ench["tags"]:
            tags[tag].add(ench["id"])

    # Sort everything
    result = {}
    for tag in sorted(tags.keys()):
        result[tag] = sorted(tags[tag])

    output = {
        "generated": "2026-02-23",
        "plugin": "CustomEnchantment",
        "tagCount": len(result),
        "tags": result
    }

    out_path = os.path.join(INDEX_DIR, "search-tags.json")
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(output, f, indent=2, ensure_ascii=False)

    total_entries = sum(len(v) for v in result.values())
    print(f"Generated {len(result)} tags with {total_entries} total entries")
    print(f"File size: {os.path.getsize(out_path)} bytes")
    print(f"Top 10 tags by count:")
    for tag in sorted(result.keys(), key=lambda t: -len(result[t]))[:10]:
        print(f"  {tag}: {len(result[tag])} entries")

if __name__ == "__main__":
    main()
