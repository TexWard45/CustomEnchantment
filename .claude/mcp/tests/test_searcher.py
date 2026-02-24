"""Tests for the searcher module (unit tests with mock data)."""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from models import Chunk, SearchResult
from searcher import apply_filters, rrf_fusion


class TestRRFFusion:
    def test_single_list(self):
        ranked = [(0, 1.0), (1, 0.5), (2, 0.3)]
        fused = rrf_fusion(ranked, k=60)
        assert len(fused) == 3
        # First item should have highest score: 1/(60+1)
        assert fused[0] > fused[1] > fused[2]

    def test_two_lists_boost_overlap(self):
        list1 = [(0, 1.0), (1, 0.5), (2, 0.3)]
        list2 = [(1, 1.0), (0, 0.5), (3, 0.3)]
        fused = rrf_fusion(list1, list2, k=60)
        # Items 0 and 1 appear in both lists, should have highest scores
        assert fused[0] > fused[3]
        assert fused[1] > fused[3]

    def test_empty_lists(self):
        fused = rrf_fusion([], [])
        assert fused == {}


class TestApplyFilters:
    def _make_chunks(self):
        return [
            Chunk(id="class:A", source="classes", embed_text="", display_text="",
                  metadata={"module": "EnchantModule", "type": "class", "tags": ["combat"]}),
            Chunk(id="class:B", source="classes", embed_text="", display_text="",
                  metadata={"module": "ItemModule", "type": "class", "tags": ["items"]}),
            Chunk(id="enchant:X", source="enchantments", embed_text="", display_text="",
                  metadata={"group": "legendary", "applies": ["SWORD"], "triggers": ["ATTACK"]}),
            Chunk(id="method:C.m", source="methods", embed_text="", display_text="",
                  metadata={"class": "C", "category": "hook", "tags": ["combat", "enchantment"]}),
        ]

    def test_filter_by_source_type(self):
        chunks = self._make_chunks()
        candidates = {0: 1.0, 1: 0.9, 2: 0.8, 3: 0.7}
        filtered = apply_filters(chunks, candidates, source_types=["classes"])
        assert set(filtered.keys()) == {0, 1}

    def test_filter_by_module(self):
        chunks = self._make_chunks()
        candidates = {0: 1.0, 1: 0.9}
        filtered = apply_filters(chunks, candidates, module="EnchantModule")
        assert set(filtered.keys()) == {0}

    def test_filter_by_tags(self):
        chunks = self._make_chunks()
        candidates = {0: 1.0, 1: 0.9, 3: 0.7}
        filtered = apply_filters(chunks, candidates, tags=["combat"])
        assert set(filtered.keys()) == {0, 3}

    def test_filter_by_enchantment_group(self):
        chunks = self._make_chunks()
        candidates = {2: 0.8}
        filtered = apply_filters(chunks, candidates, group="legendary")
        assert 2 in filtered

    def test_filter_by_applies_to(self):
        chunks = self._make_chunks()
        candidates = {2: 0.8}
        filtered = apply_filters(chunks, candidates, applies_to="SWORD")
        assert 2 in filtered
        filtered = apply_filters(chunks, candidates, applies_to="BOW")
        assert 2 not in filtered

    def test_filter_by_trigger(self):
        chunks = self._make_chunks()
        candidates = {2: 0.8}
        filtered = apply_filters(chunks, candidates, trigger="ATTACK")
        assert 2 in filtered
        filtered = apply_filters(chunks, candidates, trigger="DEFENSE")
        assert 2 not in filtered

    def test_no_filters(self):
        chunks = self._make_chunks()
        candidates = {0: 1.0, 1: 0.9, 2: 0.8, 3: 0.7}
        filtered = apply_filters(chunks, candidates)
        assert filtered == candidates
