package com.bafmc.customenchantment.player;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerVanillaAttribute")
@ExtendWith(MockitoExtension.class)
class PlayerVanillaAttributeTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    private PlayerVanillaAttribute playerVanillaAttribute;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        playerVanillaAttribute = new PlayerVanillaAttribute(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerVanillaAttribute);
            assertSame(mockCEPlayer, playerVanillaAttribute.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerVanillaAttribute instanceof CEPlayerExpansion);
        }
    }

    @Nested
    @DisplayName("Constants Tests")
    class ConstantsTests {

        @Test
        @DisplayName("PREFIX should be 'ce-'")
        void prefixShouldBeCE() {
            assertEquals("ce-", PlayerVanillaAttribute.PREFIX);
        }

        @Test
        @DisplayName("ATTRIBUTE_LIST should contain standard attributes")
        void attributeListShouldContainStandardAttributes() {
            assertTrue(PlayerVanillaAttribute.ATTRIBUTE_LIST.contains(Attribute.GENERIC_ARMOR));
            assertTrue(PlayerVanillaAttribute.ATTRIBUTE_LIST.contains(Attribute.GENERIC_ATTACK_DAMAGE));
            assertTrue(PlayerVanillaAttribute.ATTRIBUTE_LIST.contains(Attribute.GENERIC_MAX_HEALTH));
            assertTrue(PlayerVanillaAttribute.ATTRIBUTE_LIST.contains(Attribute.GENERIC_MOVEMENT_SPEED));
            assertTrue(PlayerVanillaAttribute.ATTRIBUTE_LIST.contains(Attribute.GENERIC_ATTACK_SPEED));
            assertTrue(PlayerVanillaAttribute.ATTRIBUTE_LIST.contains(Attribute.GENERIC_ARMOR_TOUGHNESS));
            assertTrue(PlayerVanillaAttribute.ATTRIBUTE_LIST.contains(Attribute.GENERIC_KNOCKBACK_RESISTANCE));
            assertTrue(PlayerVanillaAttribute.ATTRIBUTE_LIST.contains(Attribute.GENERIC_LUCK));
        }

        @Test
        @DisplayName("ATTRIBUTE_LIST should have 8 entries")
        void attributeListShouldHave8Entries() {
            assertEquals(8, PlayerVanillaAttribute.ATTRIBUTE_LIST.size());
        }
    }

    @Nested
    @DisplayName("getPrefix Tests")
    class GetPrefixTests {

        @Test
        @DisplayName("Should add prefix when not present")
        void shouldAddPrefixWhenNotPresent() {
            assertEquals("ce-test", PlayerVanillaAttribute.getPrefix("test"));
        }

        @Test
        @DisplayName("Should not duplicate prefix when already present")
        void shouldNotDuplicatePrefix() {
            assertEquals("ce-test", PlayerVanillaAttribute.getPrefix("ce-test"));
        }

        @Test
        @DisplayName("Should lowercase the name")
        void shouldLowercaseName() {
            assertEquals("ce-test", PlayerVanillaAttribute.getPrefix("TEST"));
        }

        @Test
        @DisplayName("Should handle mixed case with prefix")
        void shouldHandleMixedCaseWithPrefix() {
            assertEquals("ce-myattr", PlayerVanillaAttribute.getPrefix("CE-MyAttr"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"simple", "UPPER", "ce-already", "CE-ALREADY"})
        @DisplayName("Should always return lowercase prefixed name")
        void shouldAlwaysReturnLowercasePrefixed(String input) {
            String result = PlayerVanillaAttribute.getPrefix(input);
            assertTrue(result.startsWith("ce-"));
            assertEquals(result, result.toLowerCase());
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should call clearAllAttribute")
        void onJoinShouldCallClearAllAttribute() {
            // Setup mock for all attributes
            for (Attribute attr : Attribute.values()) {
                AttributeInstance mockInstance = mock(AttributeInstance.class);
                when(mockPlayer.getAttribute(attr)).thenReturn(mockInstance);
                when(mockInstance.getModifiers()).thenReturn(new ArrayList<>());
            }

            assertDoesNotThrow(() -> playerVanillaAttribute.onJoin());
        }

        @Test
        @DisplayName("onQuit should call clearAllAttribute")
        void onQuitShouldCallClearAllAttribute() {
            for (Attribute attr : Attribute.values()) {
                AttributeInstance mockInstance = mock(AttributeInstance.class);
                when(mockPlayer.getAttribute(attr)).thenReturn(mockInstance);
                when(mockInstance.getModifiers()).thenReturn(new ArrayList<>());
            }

            assertDoesNotThrow(() -> playerVanillaAttribute.onQuit());
        }
    }

    @Nested
    @DisplayName("getAttributeModifiers Tests")
    class GetAttributeModifiersTests {

        @Test
        @DisplayName("Should return empty list when no modifiers")
        void shouldReturnEmptyListWhenNoModifiers() {
            AttributeInstance mockInstance = mock(AttributeInstance.class);
            when(mockPlayer.getAttribute(Attribute.GENERIC_ARMOR)).thenReturn(mockInstance);
            when(mockInstance.getModifiers()).thenReturn(new ArrayList<>());

            List<AttributeModifier> result = playerVanillaAttribute.getAttributeModifiers(Attribute.GENERIC_ARMOR);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list when attribute instance is null")
        void shouldReturnEmptyListWhenInstanceIsNull() {
            when(mockPlayer.getAttribute(Attribute.GENERIC_ARMOR)).thenReturn(null);
            List<AttributeModifier> result = playerVanillaAttribute.getAttributeModifiers(Attribute.GENERIC_ARMOR);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("hasAttributeModifier Tests")
    class HasAttributeModifierTests {

        @Test
        @DisplayName("Should return false when modifier does not exist")
        void shouldReturnFalseWhenModifierDoesNotExist() {
            AttributeInstance mockInstance = mock(AttributeInstance.class);
            when(mockPlayer.getAttribute(Attribute.GENERIC_ARMOR)).thenReturn(mockInstance);
            when(mockInstance.getModifiers()).thenReturn(new ArrayList<>());

            assertFalse(playerVanillaAttribute.hasAttributeModifier(Attribute.GENERIC_ARMOR, "nonexistent"));
        }
    }

    @Nested
    @DisplayName("getAttributeModifierNameList Tests")
    class GetAttributeModifierNameListTests {

        @Test
        @DisplayName("Should return empty list when no modifiers with prefix")
        void shouldReturnEmptyListWhenNoModifiersWithPrefix() {
            AttributeInstance mockInstance = mock(AttributeInstance.class);
            when(mockPlayer.getAttribute(Attribute.GENERIC_ARMOR)).thenReturn(mockInstance);
            when(mockInstance.getModifiers()).thenReturn(new ArrayList<>());

            List<String> names = playerVanillaAttribute.getAttributeModifierNameList(Attribute.GENERIC_ARMOR);
            assertTrue(names.isEmpty());
        }

        @Test
        @DisplayName("Should filter modifiers by custom prefix")
        void shouldFilterModifiersByCustomPrefix() {
            AttributeInstance mockInstance = mock(AttributeInstance.class);
            when(mockPlayer.getAttribute(Attribute.GENERIC_ARMOR)).thenReturn(mockInstance);

            AttributeModifier mod = mock(AttributeModifier.class);
            when(mod.getName()).thenReturn("custom-test");
            Collection<AttributeModifier> mods = new ArrayList<>();
            mods.add(mod);
            when(mockInstance.getModifiers()).thenReturn(mods);

            List<String> names = playerVanillaAttribute.getAttributeModifierNameList(Attribute.GENERIC_ARMOR, "custom-");
            assertEquals(1, names.size());
            assertEquals("test", names.get(0));
        }
    }
}
