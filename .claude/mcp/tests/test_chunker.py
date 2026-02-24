"""Tests for the chunker module."""

from __future__ import annotations

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent.parent))

from chunker import (
    build_class_chunks,
    build_config_chunks,
    build_enchantment_chunks,
    build_entry_point_chunks,
    build_method_chunks,
    build_module_chunks,
    build_summary_chunks,
    build_all_chunks,
)


class TestClassChunks:
    def test_produces_chunks(self):
        chunks = build_class_chunks()
        assert len(chunks) > 500

    def test_chunk_has_required_fields(self):
        chunks = build_class_chunks()
        c = chunks[0]
        assert c.id.startswith("class:")
        assert c.source == "classes"
        assert c.embed_text
        assert c.display_text
        assert "module" in c.metadata
        assert "type" in c.metadata

    def test_unique_ids(self):
        chunks = build_class_chunks()
        ids = [c.id for c in chunks]
        assert len(ids) == len(set(ids))


class TestEnchantmentChunks:
    def test_produces_chunks(self):
        chunks = build_enchantment_chunks()
        assert len(chunks) > 500

    def test_chunk_has_domain_metadata(self):
        chunks = build_enchantment_chunks()
        c = chunks[0]
        assert c.id.startswith("enchant:")
        assert c.source == "enchantments"
        assert "group" in c.metadata
        assert "applies" in c.metadata
        assert "triggers" in c.metadata
        assert "effects" in c.metadata

    def test_cactus_enchantment_exists(self):
        chunks = build_enchantment_chunks()
        # ID now includes file stem: enchant:{file}:{id}
        assert any("cactus" in c.id for c in chunks)


class TestMethodChunks:
    def test_produces_chunks(self):
        chunks = build_method_chunks()
        assert len(chunks) > 100

    def test_chunk_format(self):
        chunks = build_method_chunks()
        c = chunks[0]
        assert c.id.startswith("method:")
        assert c.source == "methods"
        assert "class" in c.metadata
        assert "category" in c.metadata


class TestConfigChunks:
    def test_produces_chunks(self):
        chunks = build_config_chunks()
        assert len(chunks) > 10

    def test_main_config_exists(self):
        chunks = build_config_chunks()
        ids = {c.id for c in chunks}
        assert "config:MainConfig" in ids


class TestModuleChunks:
    def test_produces_16_modules(self):
        chunks = build_module_chunks()
        assert len(chunks) == 16

    def test_enchant_module_exists(self):
        chunks = build_module_chunks()
        ids = {c.id for c in chunks}
        assert "module:EnchantModule" in ids


class TestEntryPointChunks:
    def test_produces_chunks(self):
        chunks = build_entry_point_chunks()
        assert len(chunks) > 15

    def test_has_listeners_and_commands(self):
        chunks = build_entry_point_chunks()
        sources = {c.id.split(":")[0] for c in chunks}
        assert "listener" in sources
        assert "command" in sources


class TestSummaryChunks:
    def test_produces_chunks(self):
        chunks = build_summary_chunks()
        assert len(chunks) > 50

    def test_chunk_has_module_metadata(self):
        chunks = build_summary_chunks()
        c = chunks[0]
        assert c.source == "summaries"
        assert "module" in c.metadata
        assert "section" in c.metadata


class TestBuildAllChunks:
    def test_total_count(self):
        chunks = build_all_chunks()
        assert len(chunks) > 1000

    def test_all_sources_present(self):
        chunks = build_all_chunks()
        sources = {c.source for c in chunks}
        expected = {"classes", "enchantments", "methods", "configs", "modules", "entry-points", "summaries"}
        assert sources == expected

    def test_all_ids_unique(self):
        chunks = build_all_chunks()
        ids = [c.id for c in chunks]
        assert len(ids) == len(set(ids)), f"Duplicate IDs found: {len(ids) - len(set(ids))}"
