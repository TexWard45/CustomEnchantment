package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeOperation;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.attribute.RangeAttribute;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerCustomAttribute")
@ExtendWith(MockitoExtension.class)
class PlayerCustomAttributeTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    private PlayerCustomAttribute playerCustomAttribute;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        playerCustomAttribute = new PlayerCustomAttribute(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerCustomAttribute);
            assertSame(mockCEPlayer, playerCustomAttribute.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerCustomAttribute instanceof CEPlayerExpansion);
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should not throw")
        void onJoinShouldNotThrow() {
            assertDoesNotThrow(() -> playerCustomAttribute.onJoin());
        }

        @Test
        @DisplayName("onQuit should not throw")
        void onQuitShouldNotThrow() {
            assertDoesNotThrow(() -> playerCustomAttribute.onQuit());
        }
    }

    @Nested
    @DisplayName("addCustomAttribute Tests")
    class AddCustomAttributeTests {

        @Test
        @DisplayName("Should add custom attribute by name")
        void shouldAddCustomAttributeByName() {
            RangeAttribute attr = mock(RangeAttribute.class);
            playerCustomAttribute.addCustomAttribute("test-attr", attr);

            List<NMSAttribute> list = playerCustomAttribute.getAttributeList();
            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("Should overwrite attribute with same name")
        void shouldOverwriteAttributeWithSameName() {
            RangeAttribute attr1 = mock(RangeAttribute.class);
            RangeAttribute attr2 = mock(RangeAttribute.class);
            playerCustomAttribute.addCustomAttribute("test-attr", attr1);
            playerCustomAttribute.addCustomAttribute("test-attr", attr2);

            List<NMSAttribute> list = playerCustomAttribute.getAttributeList();
            assertEquals(1, list.size());
            assertSame(attr2, list.get(0));
        }
    }

    @Nested
    @DisplayName("removeCustomAttribute Tests")
    class RemoveCustomAttributeTests {

        @Test
        @DisplayName("Should remove custom attribute by name")
        void shouldRemoveCustomAttributeByName() {
            RangeAttribute attr = mock(RangeAttribute.class);
            playerCustomAttribute.addCustomAttribute("test-attr", attr);
            playerCustomAttribute.removeCustomAttribute("test-attr");

            assertTrue(playerCustomAttribute.getAttributeList().isEmpty());
        }

        @Test
        @DisplayName("Should not throw when removing non-existing attribute")
        void shouldNotThrowWhenRemovingNonExisting() {
            assertDoesNotThrow(() -> playerCustomAttribute.removeCustomAttribute("nonexistent"));
        }
    }

    @Nested
    @DisplayName("getAttributeList Tests")
    class GetAttributeListTests {

        @Test
        @DisplayName("Should return empty list when no attributes")
        void shouldReturnEmptyListWhenNoAttributes() {
            assertTrue(playerCustomAttribute.getAttributeList().isEmpty());
        }

        @Test
        @DisplayName("Should return copy of attribute list")
        void shouldReturnCopyOfAttributeList() {
            RangeAttribute attr = mock(RangeAttribute.class);
            playerCustomAttribute.addCustomAttribute("test", attr);

            List<NMSAttribute> list1 = playerCustomAttribute.getAttributeList();
            List<NMSAttribute> list2 = playerCustomAttribute.getAttributeList();

            assertNotSame(list1, list2);
            assertEquals(list1.size(), list2.size());
        }

        @Test
        @DisplayName("Should return all added attributes")
        void shouldReturnAllAddedAttributes() {
            RangeAttribute attr1 = mock(RangeAttribute.class);
            RangeAttribute attr2 = mock(RangeAttribute.class);
            playerCustomAttribute.addCustomAttribute("attr1", attr1);
            playerCustomAttribute.addCustomAttribute("attr2", attr2);

            assertEquals(2, playerCustomAttribute.getAttributeList().size());
        }
    }

    @Nested
    @DisplayName("getValue by NMSAttributeType Tests")
    class GetValueByTypeTests {

        @Test
        @DisplayName("Should return 0.0 for unknown type")
        void shouldReturnZeroForUnknownType() {
            NMSAttributeType type = mock(NMSAttributeType.class);
            assertEquals(0.0, playerCustomAttribute.getValue(type), 0.001);
        }
    }

    @Nested
    @DisplayName("getValueMap Tests")
    class GetValueMapTests {

        @Test
        @DisplayName("Should return copy of value map")
        void shouldReturnCopyOfValueMap() {
            Map<NMSAttributeType, Double> map1 = playerCustomAttribute.getValueMap();
            Map<NMSAttributeType, Double> map2 = playerCustomAttribute.getValueMap();

            assertNotSame(map1, map2);
        }
    }

    @Nested
    @DisplayName("getRecalculateAttributeMap Tests")
    class GetRecalculateAttributeMapTests {

        @Test
        @DisplayName("Should return copy of recalculate attribute map")
        void shouldReturnCopyOfRecalculateAttributeMap() {
            var map1 = playerCustomAttribute.getRecalculateAttributeMap();
            var map2 = playerCustomAttribute.getRecalculateAttributeMap();

            assertNotSame(map1, map2);
        }

        @Test
        @DisplayName("Should return empty map initially")
        void shouldReturnEmptyMapInitially() {
            assertTrue(playerCustomAttribute.getRecalculateAttributeMap().isEmpty());
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("attributeMap should be ConcurrentHashMap")
        void attributeMapShouldBeConcurrentHashMap() throws Exception {
            var field = PlayerCustomAttribute.class.getDeclaredField("attributeMap");
            field.setAccessible(true);
            assertTrue(field.get(playerCustomAttribute) instanceof java.util.concurrent.ConcurrentHashMap);
        }

        @Test
        @DisplayName("valueMap should be ConcurrentHashMap")
        void valueMapShouldBeConcurrentHashMap() throws Exception {
            var field = PlayerCustomAttribute.class.getDeclaredField("valueMap");
            field.setAccessible(true);
            assertTrue(field.get(playerCustomAttribute) instanceof java.util.concurrent.ConcurrentHashMap);
        }
    }
}
