package com.bafmc.customenchantment.listener;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerCustomAttribute;
import com.bafmc.customfarm.ore.event.OreBreakEvent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CustomFarmListener class.
 * Tests CustomFarm plugin integration for ore mining mechanics.
 */
@DisplayName("CustomFarmListener Tests")
class CustomFarmListenerTest {

    private CustomFarmListener customFarmListener;

    @Mock
    private CustomEnchantment mockPlugin;

    @Mock
    private Server mockServer;

    @Mock
    private PluginManager mockPluginManager;

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private PlayerCustomAttribute mockCustomAttribute;

    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        playerUUID = UUID.randomUUID();

        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");

        when(mockCEPlayer.getCustomAttribute()).thenReturn(mockCustomAttribute);
    }

    private void setupCustomFarmListener() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

            customFarmListener = new CustomFarmListener(mockPlugin);
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should initialize with plugin reference and register events")
        void constructor_initializesWithPluginAndRegistersEvents() {
            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
                mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

                customFarmListener = new CustomFarmListener(mockPlugin);

                assertNotNull(customFarmListener);
                verify(mockPluginManager).registerEvents(customFarmListener, mockPlugin);
            }
        }
    }

    @Nested
    @DisplayName("onOreBreak Tests")
    class OnOreBreakTests {

        @Test
        @DisplayName("should return early when player is null")
        void onOreBreak_returnsEarlyWhenPlayerNull() {
            setupCustomFarmListener();

            OreBreakEvent event = mock(OreBreakEvent.class);
            when(event.getPlayer()).thenReturn(null);

            customFarmListener.onOreBreak(event);

            verify(event, never()).setCancelled(anyBoolean());
        }

        @Test
        @DisplayName("should cancel event when mining power is zero")
        void onOreBreak_cancelsWhenMiningPowerZero() {
            setupCustomFarmListener();

            OreBreakEvent event = mock(OreBreakEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<CustomEnchantmentMessage> mockedMessage = mockStatic(CustomEnchantmentMessage.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCustomAttribute.getValue(CustomAttributeType.MINING_POWER)).thenReturn(0.0);
                List<String> noMiningPowerMessage = Collections.singletonList("No mining power!");
                mockedMessage.when(() -> CustomEnchantmentMessage.getMessageConfig("ore.no-mining-power"))
                    .thenReturn(noMiningPowerMessage);

                customFarmListener.onOreBreak(event);

                verify(event).setCancelled(true);
                verify(event).setCancelledMessages(noMiningPowerMessage);
            }
        }

        @Test
        @DisplayName("should cancel event when mining power is negative")
        void onOreBreak_cancelsWhenMiningPowerNegative() {
            setupCustomFarmListener();

            OreBreakEvent event = mock(OreBreakEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<CustomEnchantmentMessage> mockedMessage = mockStatic(CustomEnchantmentMessage.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCustomAttribute.getValue(CustomAttributeType.MINING_POWER)).thenReturn(-5.0);
                List<String> noMiningPowerMessage = Collections.singletonList("No mining power!");
                mockedMessage.when(() -> CustomEnchantmentMessage.getMessageConfig("ore.no-mining-power"))
                    .thenReturn(noMiningPowerMessage);

                customFarmListener.onOreBreak(event);

                verify(event).setCancelled(true);
                verify(event).setCancelledMessages(noMiningPowerMessage);
            }
        }

        @Test
        @DisplayName("should set damage when mining power is positive")
        void onOreBreak_setsDamageWhenMiningPowerPositive() {
            setupCustomFarmListener();

            OreBreakEvent event = mock(OreBreakEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCustomAttribute.getValue(CustomAttributeType.MINING_POWER)).thenReturn(25.0);

                customFarmListener.onOreBreak(event);

                verify(event, never()).setCancelled(true);
                verify(event).setDamage(25.0);
            }
        }

        @Test
        @DisplayName("should set damage with decimal mining power")
        void onOreBreak_setsDamageWithDecimalMiningPower() {
            setupCustomFarmListener();

            OreBreakEvent event = mock(OreBreakEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCustomAttribute.getValue(CustomAttributeType.MINING_POWER)).thenReturn(15.75);

                customFarmListener.onOreBreak(event);

                verify(event).setDamage(15.75);
            }
        }

        @Test
        @DisplayName("should handle very small positive mining power")
        void onOreBreak_handlesVerySmallPositiveMiningPower() {
            setupCustomFarmListener();

            OreBreakEvent event = mock(OreBreakEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCustomAttribute.getValue(CustomAttributeType.MINING_POWER)).thenReturn(0.001);

                customFarmListener.onOreBreak(event);

                verify(event, never()).setCancelled(true);
                verify(event).setDamage(0.001);
            }
        }

        @Test
        @DisplayName("should handle large mining power values")
        void onOreBreak_handlesLargeMiningPower() {
            setupCustomFarmListener();

            OreBreakEvent event = mock(OreBreakEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockCustomAttribute.getValue(CustomAttributeType.MINING_POWER)).thenReturn(1000000.0);

                customFarmListener.onOreBreak(event);

                verify(event).setDamage(1000000.0);
            }
        }
    }
}
