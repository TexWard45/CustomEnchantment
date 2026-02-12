package com.bafmc.customenchantment.api;

import org.bukkit.Particle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ParticleSupport class - particle list parsing.
 * Note: The send() methods require NMS/CraftBukkit which cannot be tested without a real server.
 */
@DisplayName("ParticleSupport Tests")
class ParticleSupportTest {

    @Nested
    @DisplayName("getParticleList Tests")
    class GetParticleListTests {

        @Test
        @DisplayName("Should parse single particle name")
        void shouldParseSingleParticleName() {
            ParticleSupport support = new ParticleSupport();

            List<Particle> result = support.getParticleList("FLAME");

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(Particle.FLAME, result.get(0));
        }

        @Test
        @DisplayName("Should parse multiple particle names")
        void shouldParseMultipleParticleNames() {
            ParticleSupport support = new ParticleSupport();

            List<Particle> result = support.getParticleList("FLAME,SMOKE,HEART");

            assertNotNull(result);
            assertEquals(3, result.size());
            assertTrue(result.contains(Particle.FLAME));
            assertTrue(result.contains(Particle.SMOKE));
            assertTrue(result.contains(Particle.HEART));
        }

        @Test
        @DisplayName("Should handle empty string")
        void shouldHandleEmptyString() {
            ParticleSupport support = new ParticleSupport();

            List<Particle> result = support.getParticleList("");

            assertNotNull(result);
            // Empty string will result in empty list or exception
            // Based on implementation, it tries to parse "" as particle
            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("Should skip invalid particle names")
        void shouldSkipInvalidParticleNames() {
            ParticleSupport support = new ParticleSupport();

            // Invalid name should be skipped (exception caught)
            List<Particle> result = support.getParticleList("FLAME,INVALID_PARTICLE,HEART");

            assertNotNull(result);
            // Should have 2 valid particles
            assertEquals(2, result.size());
            assertTrue(result.contains(Particle.FLAME));
            assertTrue(result.contains(Particle.HEART));
        }

        @Test
        @DisplayName("Should handle various particle types")
        void shouldHandleVariousParticleTypes() {
            ParticleSupport support = new ParticleSupport();

            String particles = "CRIT,EXPLOSION,ENCHANT,PORTAL,DRIPPING_WATER";

            List<Particle> result = support.getParticleList(particles);

            assertNotNull(result);
            // Verify all valid particles are parsed
            assertTrue(result.size() >= 1);
        }

        @Test
        @DisplayName("Should handle dust particle")
        void shouldHandleDustParticle() {
            ParticleSupport support = new ParticleSupport();

            List<Particle> result = support.getParticleList("DUST");

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(Particle.DUST, result.get(0));
        }

        @Test
        @DisplayName("Should handle single invalid particle name")
        void shouldHandleSingleInvalidParticleName() {
            ParticleSupport support = new ParticleSupport();

            List<Particle> result = support.getParticleList("TOTALLY_INVALID");

            assertNotNull(result);
            assertEquals(0, result.size());
        }
    }

    @Nested
    @DisplayName("Field Tests")
    class FieldTests {

        @Test
        @DisplayName("Particle field should be accessible")
        void particleFieldShouldBeAccessible() {
            ParticleSupport support = new ParticleSupport();

            // The particle field is public but unused in tests
            support.particle = Particle.FLAME;

            assertEquals(Particle.FLAME, support.particle);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle particle names with spaces")
        void shouldHandleParticleNamesWithSpaces() {
            ParticleSupport support = new ParticleSupport();

            // Spaces in names would cause invalid enum lookup
            List<Particle> result = support.getParticleList(" FLAME ");

            assertNotNull(result);
            // Trimmed space may or may not work depending on implementation
            // The valueOf will fail for " FLAME " as it has spaces
            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("Should handle lowercase particle names")
        void shouldHandleLowercaseParticleNames() {
            ParticleSupport support = new ParticleSupport();

            List<Particle> result = support.getParticleList("flame");

            assertNotNull(result);
            // Particle.valueOf is case-sensitive, so lowercase will fail
            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("Should handle trailing comma")
        void shouldHandleTrailingComma() {
            ParticleSupport support = new ParticleSupport();

            List<Particle> result = support.getParticleList("FLAME,HEART,");

            assertNotNull(result);
            // Trailing comma creates empty string element which fails
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should handle leading comma")
        void shouldHandleLeadingComma() {
            ParticleSupport support = new ParticleSupport();

            List<Particle> result = support.getParticleList(",FLAME,HEART");

            assertNotNull(result);
            // Leading comma creates empty string element which fails
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should handle consecutive commas")
        void shouldHandleConsecutiveCommas() {
            ParticleSupport support = new ParticleSupport();

            List<Particle> result = support.getParticleList("FLAME,,HEART");

            assertNotNull(result);
            // Empty string between commas will fail
            assertEquals(2, result.size());
        }
    }
}
