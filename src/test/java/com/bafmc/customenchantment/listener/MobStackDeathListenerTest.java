package com.bafmc.customenchantment.listener;

import com.bafmc.bukkit.bafframework.api.BafFrameworkAPI;
import com.bafmc.bukkit.utils.ExpUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerMobBonus;
import com.bafmc.customenchantment.player.bonus.EntityTypeBonus;
import com.bafmc.mobslayer.object.SlayerMob;
import com.bafmc.mobslayer.object.SlayerPlayer;
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
import uk.antiperson.stackmob.events.StackDeathEvent;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for MobStackDeathListener class.
 * Tests StackMob integration for stacked mob death handling.
 */
@DisplayName("MobStackDeathListener Tests")
class MobStackDeathListenerTest {

    private MobStackDeathListener mobStackDeathListener;

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

    @Mock
    private EntityTypeBonus mockMobSlayerExpBonus;

    @Mock
    private EntityDeathEvent mockDeathEvent;

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
        when(mockMobBonus.getMobSlayerExpBonus()).thenReturn(mockMobSlayerExpBonus);

        when(mockEntity.getType()).thenReturn(EntityType.ZOMBIE);
        when(mockEntity.getKiller()).thenReturn(mockKiller);
    }

    private void setupMobStackDeathListener() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

            mobStackDeathListener = new MobStackDeathListener(mockPlugin);
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

                mobStackDeathListener = new MobStackDeathListener(mockPlugin);

                assertNotNull(mobStackDeathListener);
                verify(mockPluginManager).registerEvents(mobStackDeathListener, mockPlugin);
            }
        }
    }

    @Nested
    @DisplayName("onMobDeath Tests")
    class OnMobDeathTests {

        @Test
        @DisplayName("should not process when killer is null")
        void onMobDeath_doesNotProcessWhenKillerNull() {
            setupMobStackDeathListener();

            StackDeathEvent event = mock(StackDeathEvent.class);
            when(event.getEntityDeathEvent()).thenReturn(mockDeathEvent);
            when(mockDeathEvent.getEntity()).thenReturn(mockEntity);
            when(mockEntity.getKiller()).thenReturn(null);

            mobStackDeathListener.onMobDeath(event);

            // Should return early without processing
            verify(event, times(1)).getEntityDeathEvent();
        }

        @Test
        @DisplayName("should give exp bonus when exp bonus is not empty")
        void onMobDeath_givesExpBonusWhenNotEmpty() {
            setupMobStackDeathListener();

            StackDeathEvent event = mock(StackDeathEvent.class);
            when(event.getEntityDeathEvent()).thenReturn(mockDeathEvent);
            when(mockDeathEvent.getEntity()).thenReturn(mockEntity);
            when(event.getDeathStep()).thenReturn(5);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<ExpUtils> mockedExpUtils = mockStatic(ExpUtils.class);
                 MockedStatic<BafFrameworkAPI> mockedBafAPI = mockStatic(BafFrameworkAPI.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockKiller)).thenReturn(mockCEPlayer);
                when(mockExpBonus.isEmpty()).thenReturn(false);
                when(mockExpBonus.getBonus(EntityType.ZOMBIE, 5)).thenReturn(100.0);
                when(mockMoneyBonus.isEmpty()).thenReturn(true);
                when(mockMobSlayerExpBonus.isEmpty()).thenReturn(true);

                mockedExpUtils.when(() -> ExpUtils.getTotalExperience(mockKiller)).thenReturn(500);

                mobStackDeathListener.onMobDeath(event);

                mockedExpUtils.verify(() -> ExpUtils.setTotalExperience(mockKiller, 600));
            }
        }

        @Test
        @DisplayName("should not give exp bonus when exp bonus is empty")
        void onMobDeath_doesNotGiveExpBonusWhenEmpty() {
            setupMobStackDeathListener();

            StackDeathEvent event = mock(StackDeathEvent.class);
            when(event.getEntityDeathEvent()).thenReturn(mockDeathEvent);
            when(mockDeathEvent.getEntity()).thenReturn(mockEntity);
            when(event.getDeathStep()).thenReturn(5);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<ExpUtils> mockedExpUtils = mockStatic(ExpUtils.class);
                 MockedStatic<BafFrameworkAPI> mockedBafAPI = mockStatic(BafFrameworkAPI.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockKiller)).thenReturn(mockCEPlayer);
                when(mockExpBonus.isEmpty()).thenReturn(true);
                when(mockMoneyBonus.isEmpty()).thenReturn(true);
                when(mockMobSlayerExpBonus.isEmpty()).thenReturn(true);

                mobStackDeathListener.onMobDeath(event);

                mockedExpUtils.verify(() -> ExpUtils.setTotalExperience(any(), anyInt()), never());
            }
        }

        @Test
        @DisplayName("should give money bonus when money bonus is not empty")
        void onMobDeath_givesMoneyBonusWhenNotEmpty() {
            setupMobStackDeathListener();

            StackDeathEvent event = mock(StackDeathEvent.class);
            when(event.getEntityDeathEvent()).thenReturn(mockDeathEvent);
            when(mockDeathEvent.getEntity()).thenReturn(mockEntity);
            when(event.getDeathStep()).thenReturn(3);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<BafFrameworkAPI> mockedBafAPI = mockStatic(BafFrameworkAPI.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockKiller)).thenReturn(mockCEPlayer);
                when(mockExpBonus.isEmpty()).thenReturn(true);
                when(mockMoneyBonus.isEmpty()).thenReturn(false);
                when(mockMoneyBonus.getBonus(EntityType.ZOMBIE, 3)).thenReturn(150.0);
                when(mockMobSlayerExpBonus.isEmpty()).thenReturn(true);

                mobStackDeathListener.onMobDeath(event);

                mockedBafAPI.verify(() -> BafFrameworkAPI.giveMoneyLater(mockKiller, 150.0));
            }
        }

        @Test
        @DisplayName("should not give money bonus when money bonus is empty")
        void onMobDeath_doesNotGiveMoneyBonusWhenEmpty() {
            setupMobStackDeathListener();

            StackDeathEvent event = mock(StackDeathEvent.class);
            when(event.getEntityDeathEvent()).thenReturn(mockDeathEvent);
            when(mockDeathEvent.getEntity()).thenReturn(mockEntity);
            when(event.getDeathStep()).thenReturn(3);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<BafFrameworkAPI> mockedBafAPI = mockStatic(BafFrameworkAPI.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockKiller)).thenReturn(mockCEPlayer);
                when(mockExpBonus.isEmpty()).thenReturn(true);
                when(mockMoneyBonus.isEmpty()).thenReturn(true);
                when(mockMobSlayerExpBonus.isEmpty()).thenReturn(true);

                mobStackDeathListener.onMobDeath(event);

                mockedBafAPI.verify(() -> BafFrameworkAPI.giveMoneyLater(any(), anyDouble()), never());
            }
        }

        @Test
        @DisplayName("should multiply bonuses by death step")
        void onMobDeath_multipliesBonusesByDeathStep() {
            setupMobStackDeathListener();

            StackDeathEvent event = mock(StackDeathEvent.class);
            when(event.getEntityDeathEvent()).thenReturn(mockDeathEvent);
            when(mockDeathEvent.getEntity()).thenReturn(mockEntity);
            when(event.getDeathStep()).thenReturn(10);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<ExpUtils> mockedExpUtils = mockStatic(ExpUtils.class);
                 MockedStatic<BafFrameworkAPI> mockedBafAPI = mockStatic(BafFrameworkAPI.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockKiller)).thenReturn(mockCEPlayer);
                when(mockExpBonus.isEmpty()).thenReturn(false);
                when(mockExpBonus.getBonus(EntityType.ZOMBIE, 10)).thenReturn(500.0);
                when(mockMoneyBonus.isEmpty()).thenReturn(false);
                when(mockMoneyBonus.getBonus(EntityType.ZOMBIE, 10)).thenReturn(250.0);
                when(mockMobSlayerExpBonus.isEmpty()).thenReturn(true);

                mockedExpUtils.when(() -> ExpUtils.getTotalExperience(mockKiller)).thenReturn(0);

                mobStackDeathListener.onMobDeath(event);

                mockedExpUtils.verify(() -> ExpUtils.setTotalExperience(mockKiller, 500));
                mockedBafAPI.verify(() -> BafFrameworkAPI.giveMoneyLater(mockKiller, 250.0));
            }
        }

        @Test
        @DisplayName("should handle mob slayer exp bonus when not empty")
        void onMobDeath_handlesMobSlayerExpBonusWhenNotEmpty() {
            setupMobStackDeathListener();

            StackDeathEvent event = mock(StackDeathEvent.class);
            when(event.getEntityDeathEvent()).thenReturn(mockDeathEvent);
            when(mockDeathEvent.getEntity()).thenReturn(mockEntity);
            when(event.getDeathStep()).thenReturn(2);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<BafFrameworkAPI> mockedBafAPI = mockStatic(BafFrameworkAPI.class);
                 MockedStatic<SlayerMob> mockedSlayerMob = mockStatic(SlayerMob.class);
                 MockedStatic<SlayerPlayer> mockedSlayerPlayer = mockStatic(SlayerPlayer.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockKiller)).thenReturn(mockCEPlayer);
                when(mockExpBonus.isEmpty()).thenReturn(true);
                when(mockMoneyBonus.isEmpty()).thenReturn(true);
                when(mockMobSlayerExpBonus.isEmpty()).thenReturn(false);

                // SlayerMob returns null, so should return early
                mockedSlayerMob.when(() -> SlayerMob.getSlayerMob(EntityType.ZOMBIE)).thenReturn(null);

                mobStackDeathListener.onMobDeath(event);

                // Should not try to give exp if no SlayerMob found
                verify(mockKiller, never()).giveExp(anyInt());
            }
        }

        @Test
        @DisplayName("should handle different entity types")
        void onMobDeath_handlesDifferentEntityTypes() {
            setupMobStackDeathListener();

            StackDeathEvent event = mock(StackDeathEvent.class);
            when(event.getEntityDeathEvent()).thenReturn(mockDeathEvent);
            when(mockDeathEvent.getEntity()).thenReturn(mockEntity);
            when(mockEntity.getType()).thenReturn(EntityType.CREEPER);
            when(event.getDeathStep()).thenReturn(1);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<BafFrameworkAPI> mockedBafAPI = mockStatic(BafFrameworkAPI.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockKiller)).thenReturn(mockCEPlayer);
                when(mockExpBonus.isEmpty()).thenReturn(true);
                when(mockMoneyBonus.isEmpty()).thenReturn(false);
                when(mockMoneyBonus.getBonus(EntityType.CREEPER, 1)).thenReturn(100.0);
                when(mockMobSlayerExpBonus.isEmpty()).thenReturn(true);

                mobStackDeathListener.onMobDeath(event);

                mockedBafAPI.verify(() -> BafFrameworkAPI.giveMoneyLater(mockKiller, 100.0));
            }
        }
    }
}
