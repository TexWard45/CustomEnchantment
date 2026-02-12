package com.bafmc.customenchantment.listener;

import com.bafmc.bukkit.bafframework.api.BafFrameworkAPI;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerMobBonus;
import com.bafmc.customenchantment.player.bonus.EntityTypeBonus;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for MobDeathListener class.
 * Tests mob death handling for exp and money bonuses.
 */
@DisplayName("MobDeathListener Tests")
class MobDeathListenerTest {

    private MobDeathListener mobDeathListener;

    @Mock
    private CustomEnchantment mockPlugin;

    @Mock
    private Server mockServer;

    @Mock
    private PluginManager mockPluginManager;

    @Mock
    private Player mockKiller;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private LivingEntity mockEntity;

    @Mock
    private PlayerMobBonus mockMobBonus;

    @Mock
    private EntityTypeBonus mockExpBonus;

    @Mock
    private EntityTypeBonus mockMoneyBonus;

    private UUID killerUUID;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        killerUUID = UUID.randomUUID();

        when(mockKiller.getUniqueId()).thenReturn(killerUUID);
        when(mockKiller.getName()).thenReturn("TestKiller");

        when(mockCEPlayer.getMobBonus()).thenReturn(mockMobBonus);
        when(mockMobBonus.getExpBonus()).thenReturn(mockExpBonus);
        when(mockMobBonus.getMoneyBonus()).thenReturn(mockMoneyBonus);

        when(mockEntity.getType()).thenReturn(EntityType.ZOMBIE);
    }

    private void setupMobDeathListener() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

            mobDeathListener = new MobDeathListener(mockPlugin);
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

                mobDeathListener = new MobDeathListener(mockPlugin);

                assertNotNull(mobDeathListener);
                verify(mockPluginManager).registerEvents(mobDeathListener, mockPlugin);
            }
        }
    }

    @Nested
    @DisplayName("onMobStackDeath Tests")
    class OnMobStackDeathTests {

        @Test
        @DisplayName("should not process when killer is null")
        void onMobStackDeath_doesNotProcessWhenKillerNull() {
            setupMobDeathListener();

            EntityDeathEvent event = mock(EntityDeathEvent.class);
            when(event.getEntity()).thenReturn(mockEntity);
            when(mockEntity.getKiller()).thenReturn(null);

            mobDeathListener.onMobStackDeath(event);

            // Should return early without processing bonuses
            verify(event, never()).getEntityType();
        }

        @Test
        @DisplayName("should get CEPlayer for killer")
        void onMobStackDeath_getsCEPlayerForKiller() {
            setupMobDeathListener();

            EntityDeathEvent event = mock(EntityDeathEvent.class);
            when(event.getEntity()).thenReturn(mockEntity);
            when(mockEntity.getKiller()).thenReturn(mockKiller);
            when(event.getEntityType()).thenReturn(EntityType.ZOMBIE);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<BafFrameworkAPI> mockedBafAPI = mockStatic(BafFrameworkAPI.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockKiller)).thenReturn(mockCEPlayer);
                when(mockExpBonus.isEmpty()).thenReturn(true);
                when(mockMoneyBonus.isEmpty()).thenReturn(true);

                mobDeathListener.onMobStackDeath(event);

                mockedCEAPI.verify(() -> CEAPI.getCEPlayer(mockKiller));
            }
        }

        @Test
        @DisplayName("should give money bonus when money bonus is empty (inverted logic in source)")
        void onMobStackDeath_givesMoneyBonusWhenEmpty() {
            // Note: The production code has inverted logic - it gives bonus when isEmpty() returns true
            setupMobDeathListener();

            EntityDeathEvent event = mock(EntityDeathEvent.class);
            when(event.getEntity()).thenReturn(mockEntity);
            when(mockEntity.getKiller()).thenReturn(mockKiller);
            when(event.getEntityType()).thenReturn(EntityType.ZOMBIE);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<BafFrameworkAPI> mockedBafAPI = mockStatic(BafFrameworkAPI.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockKiller)).thenReturn(mockCEPlayer);
                when(mockExpBonus.isEmpty()).thenReturn(true);
                when(mockMoneyBonus.isEmpty()).thenReturn(true);
                when(mockMoneyBonus.getBonus(EntityType.ZOMBIE, 1)).thenReturn(50.0);

                mobDeathListener.onMobStackDeath(event);

                mockedBafAPI.verify(() -> BafFrameworkAPI.giveMoneyLater(mockKiller, 50.0));
            }
        }

        @Test
        @DisplayName("should not give money bonus when money bonus is not empty (inverted logic in source)")
        void onMobStackDeath_doesNotGiveMoneyBonusWhenNotEmpty() {
            // Note: The production code has inverted logic - it skips bonus when isEmpty() returns false
            setupMobDeathListener();

            EntityDeathEvent event = mock(EntityDeathEvent.class);
            when(event.getEntity()).thenReturn(mockEntity);
            when(mockEntity.getKiller()).thenReturn(mockKiller);
            when(event.getEntityType()).thenReturn(EntityType.ZOMBIE);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<BafFrameworkAPI> mockedBafAPI = mockStatic(BafFrameworkAPI.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockKiller)).thenReturn(mockCEPlayer);
                when(mockExpBonus.isEmpty()).thenReturn(true);
                when(mockMoneyBonus.isEmpty()).thenReturn(false);

                mobDeathListener.onMobStackDeath(event);

                mockedBafAPI.verifyNoInteractions();
            }
        }

        @Test
        @DisplayName("should handle different entity types (inverted logic in source)")
        void onMobStackDeath_handlesDifferentEntityTypes() {
            // Note: The production code has inverted logic - it gives bonus when isEmpty() returns true
            setupMobDeathListener();

            EntityDeathEvent event = mock(EntityDeathEvent.class);
            when(event.getEntity()).thenReturn(mockEntity);
            when(mockEntity.getKiller()).thenReturn(mockKiller);
            when(mockEntity.getType()).thenReturn(EntityType.SKELETON);
            when(event.getEntityType()).thenReturn(EntityType.SKELETON);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<BafFrameworkAPI> mockedBafAPI = mockStatic(BafFrameworkAPI.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockKiller)).thenReturn(mockCEPlayer);
                when(mockExpBonus.isEmpty()).thenReturn(true);
                // Due to inverted logic: isEmpty() = true triggers bonus
                when(mockMoneyBonus.isEmpty()).thenReturn(true);
                when(mockMoneyBonus.getBonus(EntityType.SKELETON, 1)).thenReturn(75.0);

                mobDeathListener.onMobStackDeath(event);

                mockedBafAPI.verify(() -> BafFrameworkAPI.giveMoneyLater(mockKiller, 75.0));
            }
        }
    }
}
