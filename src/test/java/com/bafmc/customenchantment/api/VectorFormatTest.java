package com.bafmc.customenchantment.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for VectorFormat class - vector parsing and formatting.
 */
@DisplayName("VectorFormat Tests")
class VectorFormatTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create VectorFormat with format string")
        void shouldCreateVectorFormatWithFormatString() {
            VectorFormat format = new VectorFormat("1_0_0");

            assertNotNull(format);
        }

        @Test
        @DisplayName("Should create VectorFormat with empty string")
        void shouldCreateVectorFormatWithEmptyString() {
            VectorFormat format = new VectorFormat("");

            assertNotNull(format);
        }

        @Test
        @DisplayName("Should create VectorFormat with variable format")
        void shouldCreateVectorFormatWithVariableFormat() {
            VectorFormat format = new VectorFormat("xP_yP_zP");

            assertNotNull(format);
        }
    }

    @Nested
    @DisplayName("getVector with Two Locations Tests")
    class GetVectorWithTwoLocationsTests {

        @Test
        @DisplayName("Should return null when both locations are null")
        void shouldReturnNullWhenBothLocationsAreNull() {
            VectorFormat format = new VectorFormat("1_0_0");

            Vector result = format.getVector((Location) null, (Location) null);

            assertNull(result);
        }

        @Test
        @DisplayName("Should handle format with player direction variables")
        void shouldHandleFormatWithPlayerDirectionVariables() {
            World world = mock(World.class);

            // Create location with specific direction
            Location pLocation = new Location(world, 0, 64, 0, 0, 0);
            // Direction for yaw=0, pitch=0 is (0, 0, 1) (looking south in Minecraft)

            VectorFormat format = new VectorFormat("xP_yP_zP");

            Vector result = format.getVector(pLocation, null);

            assertNotNull(result);
            // The direction vector components should be from the location's direction
        }

        @Test
        @DisplayName("Should handle format with enemy direction variables")
        void shouldHandleFormatWithEnemyDirectionVariables() {
            World world = mock(World.class);

            Location eLocation = new Location(world, 0, 64, 0, 90, 0);
            // Direction for yaw=90 is (-1, 0, 0) (looking west)

            VectorFormat format = new VectorFormat("xE_yE_zE");

            Vector result = format.getVector(null, eLocation);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle constant vector values")
        void shouldHandleConstantVectorValues() {
            World world = mock(World.class);
            Location pLocation = new Location(world, 0, 64, 0);

            VectorFormat format = new VectorFormat("1_2_3");

            Vector result = format.getVector(pLocation, null);

            assertNotNull(result);
            assertEquals(1.0, result.getX(), 0.001);
            assertEquals(2.0, result.getY(), 0.001);
            assertEquals(3.0, result.getZ(), 0.001);
        }
    }

    @Nested
    @DisplayName("getVector with Relative Direction Tests")
    class GetVectorWithRelativeDirectionTests {

        @Test
        @DisplayName("Should handle relative vector with R suffix")
        void shouldHandleRelativeVectorWithRSuffix() {
            World world = mock(World.class);

            Location pLocation = new Location(world, 0, 64, 0, 0, 0);
            Location eLocation = new Location(world, 10, 64, 10, 90, 0);

            // R suffix indicates relative direction (enemy direction - player direction)
            VectorFormat format = new VectorFormat("xR_yR_zR");

            Vector result = format.getVector(pLocation, eLocation);

            assertNotNull(result);
            // The relative vector should be enemy direction minus player direction
        }

        @Test
        @DisplayName("Should handle tilde for current value")
        void shouldHandleTildeForCurrentValue() {
            World world = mock(World.class);

            // yaw=0, pitch=0 gives direction vector pointing south (0, 0, 1)
            Location pLocation = new Location(world, 0, 64, 0, 0, 0);
            Vector expectedDirection = pLocation.getDirection();

            VectorFormat format = new VectorFormat("~_~_~");

            Vector result = format.getVector(pLocation, null);

            assertNotNull(result);
            assertEquals(expectedDirection.getX(), result.getX(), 0.001);
            assertEquals(expectedDirection.getY(), result.getY(), 0.001);
            assertEquals(expectedDirection.getZ(), result.getZ(), 0.001);
        }
    }

    @Nested
    @DisplayName("getExactYaw Tests")
    class GetExactYawTests {

        @Test
        @DisplayName("Should return absolute value for negative yaw")
        void shouldReturnAbsoluteValueForNegativeYaw() {
            VectorFormat format = new VectorFormat("0_0_0");

            float result = format.getExactYaw(-90.0f);

            assertEquals(90.0f, result, 0.001);
        }

        @Test
        @DisplayName("Should return inverted value for positive yaw")
        void shouldReturnInvertedValueForPositiveYaw() {
            VectorFormat format = new VectorFormat("0_0_0");

            float result = format.getExactYaw(90.0f);

            // 360 - 90 = 270
            assertEquals(270.0f, result, 0.001);
        }

        @Test
        @DisplayName("Should return 0 for zero yaw")
        void shouldReturnZeroForZeroYaw() {
            VectorFormat format = new VectorFormat("0_0_0");

            float result = format.getExactYaw(0.0f);

            assertEquals(0.0f, result, 0.001);
        }

        @Test
        @DisplayName("Should handle 180 degree yaw")
        void shouldHandle180DegreeYaw() {
            VectorFormat format = new VectorFormat("0_0_0");

            float result = format.getExactYaw(180.0f);

            // 360 - 180 = 180
            assertEquals(180.0f, result, 0.001);
        }

        @Test
        @DisplayName("Should handle -180 degree yaw")
        void shouldHandleNegative180DegreeYaw() {
            VectorFormat format = new VectorFormat("0_0_0");

            float result = format.getExactYaw(-180.0f);

            // abs(-180) = 180
            assertEquals(180.0f, result, 0.001);
        }
    }

    @Nested
    @DisplayName("Mathematical Expression Tests")
    class MathematicalExpressionTests {

        @Test
        @DisplayName("Should evaluate addition expression")
        void shouldEvaluateAdditionExpression() {
            World world = mock(World.class);
            Location pLocation = new Location(world, 0, 64, 0, 0, 0);

            VectorFormat format = new VectorFormat("1+1_2+2_3+3");

            Vector result = format.getVector(pLocation, null);

            assertNotNull(result);
            assertEquals(2.0, result.getX(), 0.001);
            assertEquals(4.0, result.getY(), 0.001);
            assertEquals(6.0, result.getZ(), 0.001);
        }

        @Test
        @DisplayName("Should evaluate subtraction expression")
        void shouldEvaluateSubtractionExpression() {
            World world = mock(World.class);
            Location pLocation = new Location(world, 0, 64, 0, 0, 0);

            VectorFormat format = new VectorFormat("5-2_10-3_8-5");

            Vector result = format.getVector(pLocation, null);

            assertNotNull(result);
            assertEquals(3.0, result.getX(), 0.001);
            assertEquals(7.0, result.getY(), 0.001);
            assertEquals(3.0, result.getZ(), 0.001);
        }

        @Test
        @DisplayName("Should evaluate multiplication expression")
        void shouldEvaluateMultiplicationExpression() {
            World world = mock(World.class);
            Location pLocation = new Location(world, 0, 64, 0, 0, 0);

            VectorFormat format = new VectorFormat("2*3_4*5_6*7");

            Vector result = format.getVector(pLocation, null);

            assertNotNull(result);
            assertEquals(6.0, result.getX(), 0.001);
            assertEquals(20.0, result.getY(), 0.001);
            assertEquals(42.0, result.getZ(), 0.001);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty format string")
        void shouldHandleEmptyFormatString() {
            World world = mock(World.class);
            Location pLocation = new Location(world, 0, 64, 0);

            VectorFormat format = new VectorFormat("");

            Vector result = format.getVector(pLocation, null);

            // Empty format should result in null vector since size < 3
            assertNull(result);
        }

        @Test
        @DisplayName("Should handle insufficient format parts")
        void shouldHandleInsufficientFormatParts() {
            World world = mock(World.class);
            Location pLocation = new Location(world, 0, 64, 0);

            VectorFormat format = new VectorFormat("1_2");

            Vector result = format.getVector(pLocation, null);

            // Less than 3 parts should result in null
            assertNull(result);
        }

        @Test
        @DisplayName("Should handle negative vector values")
        void shouldHandleNegativeVectorValues() {
            World world = mock(World.class);
            Location pLocation = new Location(world, 0, 64, 0);

            VectorFormat format = new VectorFormat("-1_-2_-3");

            Vector result = format.getVector(pLocation, null);

            assertNotNull(result);
            assertEquals(-1.0, result.getX(), 0.001);
            assertEquals(-2.0, result.getY(), 0.001);
            assertEquals(-3.0, result.getZ(), 0.001);
        }

        @Test
        @DisplayName("Should handle decimal vector values")
        void shouldHandleDecimalVectorValues() {
            World world = mock(World.class);
            Location pLocation = new Location(world, 0, 64, 0);

            VectorFormat format = new VectorFormat("0.5_1.5_2.5");

            Vector result = format.getVector(pLocation, null);

            assertNotNull(result);
            assertEquals(0.5, result.getX(), 0.001);
            assertEquals(1.5, result.getY(), 0.001);
            assertEquals(2.5, result.getZ(), 0.001);
        }

        @Test
        @DisplayName("Should handle zero vector")
        void shouldHandleZeroVector() {
            World world = mock(World.class);
            Location pLocation = new Location(world, 0, 64, 0);

            VectorFormat format = new VectorFormat("0_0_0");

            Vector result = format.getVector(pLocation, null);

            assertNotNull(result);
            assertEquals(0.0, result.getX(), 0.001);
            assertEquals(0.0, result.getY(), 0.001);
            assertEquals(0.0, result.getZ(), 0.001);
        }
    }
}
