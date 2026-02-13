package com.bafmc.customenchantment.event;

import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.enchant.ModifyType;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CEPlayerStatsModifyEvent - player stats modification event
 * Covers event creation, stat calculations, modification types, and cancellation
 */
@DisplayName("CEPlayerStatsModifyEvent Tests")
class CEPlayerStatsModifyEventTest {

    private CEPlayerStatsModifyEvent event;
    private CEPlayer mockCEPlayer;
    private CustomAttributeType mockAttributeType;

    @BeforeEach
    void setUp() {
        mockCEPlayer = mock(CEPlayer.class);
        mockAttributeType = CustomAttributeType.HEALTH_REGENERATION;
    }

    @Nested
    @DisplayName("Event Creation Tests")
    class EventCreationTests {

        @Test
        @DisplayName("should create event with required parameters")
        void shouldCreateEventSync() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
            assertNotNull(event);
        }

        @Test
        @DisplayName("should create async event")
        void shouldCreateEventAsync() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0,
                true
            );
            assertNotNull(event);
        }

        @Test
        @DisplayName("should extend Event")
        void shouldExtendEvent() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
            assertTrue(event instanceof Event);
        }

        @Test
        @DisplayName("should implement Cancellable")
        void shouldImplementCancellable() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
            assertTrue(event instanceof Cancellable);
        }
    }

    @Nested
    @DisplayName("CEPlayer Accessor Tests")
    class CEPlayerAccessorTests {

        @BeforeEach
        void setUpEvent() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
        }

        @Test
        @DisplayName("should store CEPlayer")
        void shouldStoreCEPlayer() {
            assertNotNull(event.getCEPlayer());
        }

        @Test
        @DisplayName("should return same CEPlayer")
        void shouldReturnSameCEPlayer() {
            assertEquals(mockCEPlayer, event.getCEPlayer());
        }

        @Test
        @DisplayName("should handle null CEPlayer")
        void shouldHandleNullCEPlayer() {
            event = new CEPlayerStatsModifyEvent(
                null,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
            assertNull(event.getCEPlayer());
        }
    }

    @Nested
    @DisplayName("Stat Type Tests")
    class StatTypeTests {

        @BeforeEach
        void setUpEvent() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
        }

        @Test
        @DisplayName("should store stat type")
        void shouldStoreStatType() {
            assertNotNull(event.getStatsType());
        }

        @Test
        @DisplayName("should return correct stat type")
        void shouldReturnCorrectType() {
            assertEquals(mockAttributeType, event.getStatsType());
        }

        @Test
        @DisplayName("should support different attribute types")
        void shouldSupportDifferentTypes() {
            // Test with basic attribute type
            CustomAttributeType type = CustomAttributeType.HEALTH_REGENERATION;
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                type,
                ModifyType.ADD,
                100.0,
                10.0
            );
            assertEquals(type, event.getStatsType());
        }
    }

    @Nested
    @DisplayName("Modify Type Tests")
    class ModifyTypeTests {

        @Test
        @DisplayName("should store ADD modify type")
        void shouldStoreAddType() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
            assertEquals(ModifyType.ADD, event.getModifyType());
        }

        @Test
        @DisplayName("should store REMOVE modify type")
        void shouldStoreRemoveType() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.REMOVE,
                100.0,
                10.0
            );
            assertEquals(ModifyType.REMOVE, event.getModifyType());
        }

        @Test
        @DisplayName("should store SET modify type")
        void shouldStoreSetType() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.SET,
                100.0,
                50.0
            );
            assertEquals(ModifyType.SET, event.getModifyType());
        }
    }

    @Nested
    @DisplayName("Value Tests")
    class ValueTests {

        @BeforeEach
        void setUpEvent() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
        }

        @Test
        @DisplayName("should store current value")
        void shouldStoreCurrentValue() {
            assertEquals(100.0, event.getCurrentValue());
        }

        @Test
        @DisplayName("should store change value")
        void shouldStoreChangeValue() {
            assertEquals(10.0, event.getChangeValue());
        }

        @Test
        @DisplayName("should handle zero values")
        void shouldHandleZeroValues() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                0.0,
                0.0
            );
            assertEquals(0.0, event.getCurrentValue());
            assertEquals(0.0, event.getChangeValue());
        }

        @Test
        @DisplayName("should handle negative values")
        void shouldHandleNegativeValues() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                -50.0,
                -10.0
            );
            assertEquals(-50.0, event.getCurrentValue());
            assertEquals(-10.0, event.getChangeValue());
        }

        @Test
        @DisplayName("should handle large values")
        void shouldHandleLargeValues() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100000.0,
                50000.0
            );
            assertEquals(100000.0, event.getCurrentValue());
            assertEquals(50000.0, event.getChangeValue());
        }
    }

    @Nested
    @DisplayName("Final Value Calculation Tests")
    class FinalValueCalculationTests {

        @Test
        @DisplayName("should calculate ADD result")
        void shouldCalculateAddResult() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
            assertEquals(110.0, event.getFinalValue());
        }

        @Test
        @DisplayName("should calculate REMOVE result")
        void shouldCalculateRemoveResult() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.REMOVE,
                100.0,
                10.0
            );
            assertEquals(90.0, event.getFinalValue());
        }

        @Test
        @DisplayName("should calculate SET result")
        void shouldCalculateSetResult() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.SET,
                100.0,
                50.0
            );
            assertEquals(50.0, event.getFinalValue());
        }

        @Test
        @DisplayName("should handle ADD resulting in negative")
        void shouldHandleAddNegative() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                10.0,
                -20.0
            );
            assertEquals(-10.0, event.getFinalValue());
        }

        @Test
        @DisplayName("should handle REMOVE resulting in negative")
        void shouldHandleRemoveNegative() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.REMOVE,
                10.0,
                20.0
            );
            assertEquals(-10.0, event.getFinalValue());
        }

        @Test
        @DisplayName("should handle SET to zero")
        void shouldHandleSetZero() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.SET,
                100.0,
                0.0
            );
            assertEquals(0.0, event.getFinalValue());
        }
    }

    @Nested
    @DisplayName("Value Modification Tests")
    class ValueModificationTests {

        @BeforeEach
        void setUpEvent() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
        }

        @Test
        @DisplayName("should modify change value")
        void shouldModifyChangeValue() {
            event.setValue(20.0);
            assertEquals(20.0, event.getChangeValue());
        }

        @Test
        @DisplayName("should recalculate final value after modification")
        void shouldRecalculateAfterModification() {
            event.setValue(20.0);
            assertEquals(120.0, event.getFinalValue());
        }

        @Test
        @DisplayName("should allow setting to zero")
        void shouldAllowZeroValue() {
            event.setValue(0.0);
            assertEquals(0.0, event.getChangeValue());
        }

        @Test
        @DisplayName("should allow negative values after modification")
        void shouldAllowNegativeAfterModification() {
            event.setValue(-15.0);
            assertEquals(-15.0, event.getChangeValue());
        }
    }

    @Nested
    @DisplayName("Cancellation Tests")
    class CancellationTests {

        @BeforeEach
        void setUpEvent() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
        }

        @Test
        @DisplayName("should not be cancelled initially")
        void shouldNotBeCancelledInitially() {
            assertFalse(event.isCancelled());
        }

        @Test
        @DisplayName("should allow cancellation")
        void shouldAllowCancellation() {
            event.setCancelled(true);
            assertTrue(event.isCancelled());
        }

        @Test
        @DisplayName("should allow uncancel")
        void shouldAllowUncancel() {
            event.setCancelled(true);
            event.setCancelled(false);
            assertFalse(event.isCancelled());
        }

        @Test
        @DisplayName("should maintain cancel state")
        void shouldMaintainCancelState() {
            event.setCancelled(true);
            assertTrue(event.isCancelled());
            assertTrue(event.isCancelled());
        }
    }

    @Nested
    @DisplayName("Event Handler Tests")
    class EventHandlerTests {

        @BeforeEach
        void setUpEvent() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
        }

        @Test
        @DisplayName("should have handler list")
        void shouldHaveHandlerList() {
            HandlerList handlers = event.getHandlers();
            assertNotNull(handlers);
        }

        @Test
        @DisplayName("should have static handler list")
        void shouldHaveStaticHandlerList() {
            HandlerList handlers = CEPlayerStatsModifyEvent.getHandlerList();
            assertNotNull(handlers);
        }

        @Test
        @DisplayName("should register with plugin manager")
        void shouldRegisterWithPluginManager() {
            assertNotNull(event.getHandlers());
        }
    }

    @Nested
    @DisplayName("Async Event Tests")
    class AsyncEventTests {

        @Test
        @DisplayName("should create sync event by default")
        void shouldCreateSyncEvent() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
            assertNotNull(event);
        }

        @Test
        @DisplayName("should create async event")
        void shouldCreateAsyncEvent() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0,
                true
            );
            assertNotNull(event);
        }

        @Test
        @DisplayName("should handle async flag correctly")
        void shouldHandleAsyncFlag() {
            CEPlayerStatsModifyEvent asyncEvent = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0,
                true
            );
            assertNotNull(asyncEvent);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("should be usable in event listener")
        void shouldBeUsableInListener() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
            assertNotNull(event.getCEPlayer());
            assertNotNull(event.getStatsType());
            assertNotNull(event.getModifyType());
        }

        @Test
        @DisplayName("should support listener cancellation")
        void shouldSupportListenerCancellation() {
            event = new CEPlayerStatsModifyEvent(
                mockCEPlayer,
                mockAttributeType,
                ModifyType.ADD,
                100.0,
                10.0
            );
            event.setCancelled(true);
            assertTrue(event.isCancelled());
        }
    }
}
