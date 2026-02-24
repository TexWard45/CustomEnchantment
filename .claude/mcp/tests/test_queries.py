"""Canonical query tests — 10 queries with expected results in top-5.

These tests require a built index (run `python indexer.py` first).
"""

from __future__ import annotations

import sys
from pathlib import Path

import pytest

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from searcher import search


def _result_ids(results) -> list[str]:
    """Extract chunk IDs from search results."""
    return [r.chunk.id for r in results]


def _any_contains(results, substring: str) -> bool:
    """Check if any result ID contains the substring."""
    return any(substring.lower() in r.chunk.id.lower() for r in results)


def _any_display_contains(results, substring: str) -> bool:
    """Check if any result's display text contains the substring."""
    return any(substring.lower() in r.chunk.display_text.lower() for r in results)


# ── Test cases ──────────────────────────────────────────────────────────

class TestCanonicalQueries:
    """10 canonical queries that validate the search quality."""

    def test_damage_reduction(self, search_components):
        """'damage reduction' should find EffectModifyDamage or similar damage classes."""
        chunks, index, bm25, model = search_components
        results = search("damage reduction", chunks, index, bm25, model,
                         top_k=10, source_types=["classes", "methods"])
        assert len(results) > 0
        # Should find damage-related classes
        assert _any_contains(results, "damage") or _any_display_contains(results, "damage")

    def test_cactus_enchantment(self, search_components):
        """'cactus legendary armor defense' should find cactus enchantment."""
        chunks, index, bm25, model = search_components
        results = search("cactus legendary armor defense", chunks, index, bm25, model,
                         top_k=10, source_types=["enchantments"])
        assert _any_contains(results, "cactus") or _any_display_contains(results, "cactus")

    def test_player_join_event(self, search_components):
        """'player join event' should find PlayerListener."""
        chunks, index, bm25, model = search_components
        results = search("player join event", chunks, index, bm25, model,
                         top_k=10, source_types=["entry-points"])
        assert _any_contains(results, "PlayerListener") or _any_display_contains(results, "PlayerJoinEvent")

    def test_config_combat_settings(self, search_components):
        """'combat settings' should find MainConfig."""
        chunks, index, bm25, model = search_components
        results = search("combat settings", chunks, index, bm25, model,
                         top_k=10, source_types=["configs"])
        assert _any_contains(results, "MainConfig") or _any_display_contains(results, "combat")

    def test_enchant_module(self, search_components):
        """'enchantment system' should find EnchantModule."""
        chunks, index, bm25, model = search_components
        results = search("enchantment system", chunks, index, bm25, model,
                         top_k=10, source_types=["modules"])
        assert _any_contains(results, "EnchantModule")

    def test_cecaller_trigger(self, search_components):
        """'enchantment trigger executor' should find CECaller."""
        chunks, index, bm25, model = search_components
        results = search("enchantment trigger executor", chunks, index, bm25, model,
                         top_k=10, source_types=["classes", "summaries"])
        assert _any_contains(results, "CECaller") or _any_display_contains(results, "CECaller")

    def test_sword_enchantments(self, search_components):
        """'sword enchantment for fire' should find fire-related sword enchantments."""
        chunks, index, bm25, model = search_components
        results = search("sword enchantment for fire", chunks, index, bm25, model,
                         top_k=10, source_types=["enchantments"])
        assert len(results) > 0
        # Should find at least one enchant with SWORD in applies or fire in effects
        assert any(
            "sword" in str(r.chunk.metadata.get("applies", [])).lower() or
            "fire" in r.chunk.embed_text.lower()
            for r in results
        )

    def test_inventory_listener(self, search_components):
        """'inventory click event' should find InventoryListener."""
        chunks, index, bm25, model = search_components
        results = search("inventory click event", chunks, index, bm25, model,
                         top_k=10, source_types=["entry-points"])
        assert _any_contains(results, "InventoryListener") or _any_display_contains(results, "InventoryClick")

    def test_database_module(self, search_components):
        """'database persistence' should find DatabaseModule."""
        chunks, index, bm25, model = search_components
        results = search("database persistence", chunks, index, bm25, model,
                         top_k=10, source_types=["modules", "classes"])
        assert (_any_contains(results, "Database") or
                _any_display_contains(results, "database"))

    def test_potion_effect(self, search_components):
        """'add potion effect' should find potion-related effects."""
        chunks, index, bm25, model = search_components
        results = search("add potion effect", chunks, index, bm25, model,
                         top_k=10, source_types=["classes", "methods"])
        assert (_any_contains(results, "Potion") or
                _any_display_contains(results, "potion"))
