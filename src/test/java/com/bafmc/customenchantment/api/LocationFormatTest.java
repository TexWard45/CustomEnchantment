package com.bafmc.customenchantment.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for LocationFormat class - location parsing and formatting.
 */
@DisplayName("LocationFormat Tests")
class LocationFormatTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create LocationFormat with format string")
        void shouldCreateLocationFormatWithFormatString() {
            LocationFormat format = new LocationFormat("world_0_64_0");

            assertNotNull(format);
        }

        @Test
        @DisplayName("Should create LocationFormat with empty string")
        void shouldCreateLocationFormatWithEmptyString() {
            LocationFormat format = new LocationFormat("");

            assertNotNull(format);
        }

        @Test
        @DisplayName("Should create LocationFormat with complex format")
        void shouldCreateLocationFormatWithComplexFormat() {
            LocationFormat format = new LocationFormat("~_xP_yP_zP_yawP_pitchP");

            assertNotNull(format);
        }
    }

    @Nested
    @DisplayName("getLocation with Two Locations Tests")
    class GetLocationWithTwoLocationsTests {

        @Test
        @DisplayName("Should return null when both locations are null")
        void shouldReturnNullWhenBothLocationsAreNull() {
            LocationFormat format = new LocationFormat("~_0_64_0");

            Location result = format.getLocation((Location) null, (Location) null);

            assertNull(result);
        }

        @Test
        @DisplayName("Should handle format with player location variables")
        void shouldHandleFormatWithPlayerLocationVariables() {
            World world = mock(World.class);
            when(world.getName()).thenReturn("world");

            Location pLocation = new Location(world, 100.0, 64.0, 200.0, 45.0f, 0.0f);

            LocationFormat format = new LocationFormat("~_xP_yP_zP");

            Location result = format.getLocation(pLocation, null);

            assertNotNull(result);
            assertEquals(100.0, result.getX(), 0.001);
            assertEquals(64.0, result.getY(), 0.001);
            assertEquals(200.0, result.getZ(), 0.001);
        }

        @Test
        @DisplayName("Should handle format with enemy location variables")
        void shouldHandleFormatWithEnemyLocationVariables() {
            World world = mock(World.class);
            when(world.getName()).thenReturn("world");

            Location eLocation = new Location(world, 50.0, 70.0, 100.0, 90.0f, -45.0f);

            LocationFormat format = new LocationFormat("~_xE_yE_zE");

            Location result = format.getLocation(null, eLocation);

            // With only enemy location, the world reference uses null check
            // which means ~ can't resolve, so result may be null
            // This tests the current behavior
            assertNotNull(result);
        }

        @Test
        @DisplayName("Should calculate distance between locations")
        void shouldCalculateDistanceBetweenLocations() {
            World world = mock(World.class);
            when(world.getName()).thenReturn("world");

            Location pLocation = new Location(world, 0.0, 64.0, 0.0);
            Location eLocation = new Location(world, 3.0, 64.0, 4.0);

            // Distance should be 5 (3-4-5 triangle)
            LocationFormat format = new LocationFormat("~_distance_0_0");

            Location result = format.getLocation(pLocation, eLocation);

            assertNotNull(result);
            // X should be the distance (5.0)
            assertEquals(5.0, result.getX(), 0.001);
        }
    }

    @Nested
    @DisplayName("getLocation with Full Coordinates Tests")
    class GetLocationWithFullCoordinatesTests {

        @Test
        @DisplayName("Should parse format with yaw and pitch")
        void shouldParseFormatWithYawAndPitch() {
            World world = mock(World.class);
            when(world.getName()).thenReturn("world");

            Location pLocation = new Location(world, 100.0, 64.0, 200.0, 45.0f, 30.0f);

            LocationFormat format = new LocationFormat("~_0_64_0_~_~");

            Location result = format.getLocation(pLocation, null);

            assertNotNull(result);
            assertEquals(45.0f, result.getYaw(), 0.001);
            assertEquals(30.0f, result.getPitch(), 0.001);
        }

        @Test
        @DisplayName("Should handle relative coordinates with tilde")
        void shouldHandleRelativeCoordinatesWithTilde() {
            World world = mock(World.class);
            when(world.getName()).thenReturn("world");

            Location pLocation = new Location(world, 100.0, 64.0, 200.0);

            // ~+10 means add 10 to current position
            LocationFormat format = new LocationFormat("~_~+10_~_~");

            Location result = format.getLocation(pLocation, null);

            assertNotNull(result);
            assertEquals(110.0, result.getX(), 0.001);
        }
    }

    @Nested
    @DisplayName("getExactYaw Tests")
    class GetExactYawTests {

        @Test
        @DisplayName("Should convert negative yaw to positive")
        void shouldConvertNegativeYawToPositive() {
            float result = LocationFormat.getExactYaw(-90.0f);

            // -90 -> 360 + (-90) = 270, then 270 - 270 = 0
            // Actually: yaw = 360 + yaw = 270, then yaw = 270 - yaw
            // First: yaw = 360 + (-90) = 270
            // Then: yaw = 270 - 270 = 0
            assertEquals(0.0f, result, 0.001);
        }

        @Test
        @DisplayName("Should handle zero yaw")
        void shouldHandleZeroYaw() {
            float result = LocationFormat.getExactYaw(0.0f);

            // yaw = 270 - 0 = 270
            assertEquals(270.0f, result, 0.001);
        }

        @Test
        @DisplayName("Should handle positive yaw")
        void shouldHandlePositiveYaw() {
            float result = LocationFormat.getExactYaw(90.0f);

            // yaw = 270 - 90 = 180
            assertEquals(180.0f, result, 0.001);
        }

        @Test
        @DisplayName("Should handle 180 degree yaw")
        void shouldHandle180DegreeYaw() {
            float result = LocationFormat.getExactYaw(180.0f);

            // yaw = 270 - 180 = 90
            assertEquals(90.0f, result, 0.001);
        }

        @Test
        @DisplayName("Should handle -180 degree yaw")
        void shouldHandleNegative180DegreeYaw() {
            float result = LocationFormat.getExactYaw(-180.0f);

            // yaw < 0: yaw = 360 + (-180) = 180
            // yaw = 270 - 180 = 90
            assertEquals(90.0f, result, 0.001);
        }

        @Test
        @DisplayName("Should handle 270 degree yaw")
        void shouldHandle270DegreeYaw() {
            float result = LocationFormat.getExactYaw(270.0f);

            // yaw = 270 - 270 = 0
            assertEquals(0.0f, result, 0.001);
        }

        @Test
        @DisplayName("Should handle 360 degree yaw")
        void shouldHandle360DegreeYaw() {
            float result = LocationFormat.getExactYaw(360.0f);

            // yaw = 270 - 360 = -90
            assertEquals(-90.0f, result, 0.001);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle format with only world")
        void shouldHandleFormatWithOnlyWorld() {
            World world = mock(World.class);
            when(world.getName()).thenReturn("world");

            Location pLocation = new Location(world, 0, 0, 0);

            LocationFormat format = new LocationFormat("~");

            Location result = format.getLocation(pLocation, null);

            // With only world in format (size <= 3), x/y/z are null
            // This may cause issues in Location constructor
            assertNull(result);
        }

        @Test
        @DisplayName("Should handle yaw variable replacement")
        void shouldHandleYawVariableReplacement() {
            World world = mock(World.class);
            when(world.getName()).thenReturn("world");

            Location pLocation = new Location(world, 100.0, 64.0, 200.0, 45.0f, 30.0f);

            // eyawP is exact yaw, yawP is regular yaw
            LocationFormat format = new LocationFormat("~_100_64_200_yawP_pitchP");

            Location result = format.getLocation(pLocation, null);

            assertNotNull(result);
            assertEquals(45.0f, result.getYaw(), 0.001);
            assertEquals(30.0f, result.getPitch(), 0.001);
        }

        @Test
        @DisplayName("Should handle exact yaw variable replacement")
        void shouldHandleExactYawVariableReplacement() {
            World world = mock(World.class);
            when(world.getName()).thenReturn("world");

            Location pLocation = new Location(world, 100.0, 64.0, 200.0, 90.0f, 0.0f);

            LocationFormat format = new LocationFormat("~_100_64_200_eyawP_0");

            Location result = format.getLocation(pLocation, null);

            assertNotNull(result);
            // Exact yaw of 90 should be 180 (270 - 90)
            assertEquals(180.0f, result.getYaw(), 0.001);
        }
    }

    @Nested
    @DisplayName("Format String Parsing Tests")
    class FormatStringParsingTests {

        @Test
        @DisplayName("Should parse underscore-separated format")
        void shouldParseUnderscoreSeparatedFormat() {
            World world = mock(World.class);
            when(world.getName()).thenReturn("test_world");

            Location pLocation = new Location(world, 0, 0, 0);

            LocationFormat format = new LocationFormat("~_10_20_30");

            Location result = format.getLocation(pLocation, null);

            assertNotNull(result);
            assertEquals(10.0, result.getX(), 0.001);
            assertEquals(20.0, result.getY(), 0.001);
            assertEquals(30.0, result.getZ(), 0.001);
        }

        @Test
        @DisplayName("Should handle mathematical expressions")
        void shouldHandleMathematicalExpressions() {
            World world = mock(World.class);
            when(world.getName()).thenReturn("world");

            Location pLocation = new Location(world, 100.0, 64.0, 200.0);

            // Using math expression in format
            LocationFormat format = new LocationFormat("~_xP+50_yP_zP-100");

            Location result = format.getLocation(pLocation, null);

            assertNotNull(result);
            assertEquals(150.0, result.getX(), 0.001);
            assertEquals(64.0, result.getY(), 0.001);
            assertEquals(100.0, result.getZ(), 0.001);
        }
    }
}
