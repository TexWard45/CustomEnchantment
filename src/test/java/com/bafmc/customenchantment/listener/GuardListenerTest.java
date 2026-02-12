package com.bafmc.customenchantment.listener;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.guard.Guard;
import com.bafmc.customenchantment.guard.GuardManager;
import com.bafmc.customenchantment.guard.GuardModule;
import com.bafmc.customenchantment.guard.PlayerGuard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for GuardListener class.
 * Tests guard-related event handling including spawn, target, death, and teleport.
 */
@DisplayName("GuardListener Tests")
class GuardListenerTest {

    private GuardListener guardListener;

    @Mock
    private CustomEnchantment mockPlugin;

    @Mock
    private Server mockServer;

    @Mock
    private PluginManager mockPluginManager;

    @Mock
    private GuardModule mockGuardModule;

    @Mock
    private GuardManager mockGuardManager;

    @Mock
    private Player mockPlayer;

    @Mock
    private PlayerGuard mockPlayerGuard;

    @Mock
    private Entity mockEntity;

    @Mock
    private LivingEntity mockLivingEntity;

    @Mock
    private Guard mockGuard;

    @Mock
    private World mockWorld;

    @Mock
    private World mockDifferentWorld;

    @Mock
    private Location mockFromLocation;

    @Mock
    private Location mockToLocation;

    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        playerUUID = UUID.randomUUID();

        when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
        when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");

        when(mockFromLocation.getWorld()).thenReturn(mockWorld);
        when(mockToLocation.getWorld()).thenReturn(mockWorld);

        when(mockGuardManager.getPlayerGuard(mockPlayer)).thenReturn(mockPlayerGuard);

        // Reset static field before each test
        GuardListener.guardSpawning = false;
    }

    private void setupGuardListener() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

            guardListener = new GuardListener(mockPlugin);
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

                guardListener = new GuardListener(mockPlugin);

                assertNotNull(guardListener);
                verify(mockPluginManager).registerEvents(guardListener, mockPlugin);
            }
        }
    }

    @Nested
    @DisplayName("guardSpawning Static Field Tests")
    class GuardSpawningFieldTests {

        @Test
        @DisplayName("should be false by default")
        void guardSpawning_isFalseByDefault() {
            assertFalse(GuardListener.guardSpawning);
        }

        @Test
        @DisplayName("should allow modification")
        void guardSpawning_allowsModification() {
            GuardListener.guardSpawning = true;
            assertTrue(GuardListener.guardSpawning);

            GuardListener.guardSpawning = false;
            assertFalse(GuardListener.guardSpawning);
        }
    }

    @Nested
    @DisplayName("onCreatureSpawn Tests")
    class OnCreatureSpawnTests {

        @Test
        @DisplayName("should not cancel event when guardSpawning is false")
        void onCreatureSpawn_doesNotCancelWhenGuardSpawningFalse() {
            setupGuardListener();

            CreatureSpawnEvent event = mock(CreatureSpawnEvent.class);
            GuardListener.guardSpawning = false;

            guardListener.onEntityTarget(event);

            verify(event, never()).setCancelled(false);
        }

        @Test
        @DisplayName("should uncancel event when guardSpawning is true")
        void onCreatureSpawn_uncancelsWhenGuardSpawningTrue() {
            setupGuardListener();

            CreatureSpawnEvent event = mock(CreatureSpawnEvent.class);
            GuardListener.guardSpawning = true;

            guardListener.onEntityTarget(event);

            verify(event).setCancelled(false);
        }
    }

    @Nested
    @DisplayName("onEntityTarget Tests")
    class OnEntityTargetTests {

        @Test
        @DisplayName("should return early when entity is not a guard")
        void onEntityTarget_returnsEarlyWhenNotGuard() {
            setupGuardListener();

            EntityTargetLivingEntityEvent event = mock(EntityTargetLivingEntityEvent.class);
            when(event.getEntity()).thenReturn(mockEntity);
            when(mockGuardManager.getGuard(mockEntity)).thenReturn(null);

            guardListener.onEntityTarget(event);

            verify(event, never()).setCancelled(anyBoolean());
        }

        @Test
        @DisplayName("should cancel event when guard is not currently targeting")
        void onEntityTarget_cancelsWhenGuardNotTargeting() {
            setupGuardListener();

            EntityTargetLivingEntityEvent event = mock(EntityTargetLivingEntityEvent.class);
            when(event.getEntity()).thenReturn(mockEntity);
            when(mockGuardManager.getGuard(mockEntity)).thenReturn(mockGuard);
            when(mockGuard.isNowTarget()).thenReturn(false);

            guardListener.onEntityTarget(event);

            verify(event).setCancelled(true);
        }

        @Test
        @DisplayName("should not cancel event when guard is currently targeting")
        void onEntityTarget_doesNotCancelWhenGuardTargeting() {
            setupGuardListener();

            EntityTargetLivingEntityEvent event = mock(EntityTargetLivingEntityEvent.class);
            when(event.getEntity()).thenReturn(mockEntity);
            when(mockGuardManager.getGuard(mockEntity)).thenReturn(mockGuard);
            when(mockGuard.isNowTarget()).thenReturn(true);

            guardListener.onEntityTarget(event);

            verify(event, never()).setCancelled(true);
        }
    }

    @Nested
    @DisplayName("onEntityDeath Tests")
    class OnEntityDeathTests {

        @Test
        @DisplayName("should return early when entity is not a guard")
        void onEntityDeath_returnsEarlyWhenNotGuard() {
            setupGuardListener();

            EntityDeathEvent event = mock(EntityDeathEvent.class);
            when(event.getEntity()).thenReturn(mockLivingEntity);
            when(mockGuardManager.getGuard(mockLivingEntity)).thenReturn(null);

            guardListener.onEntityTarget(event);

            verify(event, never()).setDroppedExp(anyInt());
        }

        @Test
        @DisplayName("should clear drops and exp when entity is a guard")
        void onEntityDeath_clearsDropsAndExpWhenGuard() {
            setupGuardListener();

            EntityDeathEvent event = mock(EntityDeathEvent.class);
            List<ItemStack> drops = new ArrayList<>();
            drops.add(mock(ItemStack.class));

            when(event.getEntity()).thenReturn(mockLivingEntity);
            when(mockGuardManager.getGuard(mockLivingEntity)).thenReturn(mockGuard);
            when(event.getDrops()).thenReturn(drops);

            guardListener.onEntityTarget(event);

            verify(event).setDroppedExp(0);
            assertTrue(drops.isEmpty());
        }
    }

    @Nested
    @DisplayName("onEntityTame Tests")
    class OnEntityTameTests {

        @Test
        @DisplayName("should return early when entity is not a guard")
        void onEntityTame_returnsEarlyWhenNotGuard() {
            setupGuardListener();

            EntityTameEvent event = mock(EntityTameEvent.class);
            when(event.getEntity()).thenReturn(mockLivingEntity);
            when(mockGuardManager.getGuard(mockLivingEntity)).thenReturn(null);

            guardListener.onTame(event);

            verify(event, never()).setCancelled(anyBoolean());
        }

        @Test
        @DisplayName("should cancel event when entity is a guard")
        void onEntityTame_cancelsWhenGuard() {
            setupGuardListener();

            EntityTameEvent event = mock(EntityTameEvent.class);
            when(event.getEntity()).thenReturn(mockLivingEntity);
            when(mockGuardManager.getGuard(mockLivingEntity)).thenReturn(mockGuard);

            guardListener.onTame(event);

            verify(event).setCancelled(true);
        }
    }

    @Nested
    @DisplayName("onTeleport Tests")
    class OnTeleportTests {

        @Test
        @DisplayName("should clear guards when player teleports to different world")
        void onTeleport_clearsGuardsWhenDifferentWorld() {
            setupGuardListener();

            PlayerTeleportEvent event = mock(PlayerTeleportEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(event.getFrom()).thenReturn(mockFromLocation);
            when(event.getTo()).thenReturn(mockToLocation);
            when(mockToLocation.getWorld()).thenReturn(mockDifferentWorld);

            guardListener.onTeleport(event);

            verify(mockPlayerGuard).clearGuards();
        }

        @Test
        @DisplayName("should not clear guards when player teleports in same world")
        void onTeleport_doesNotClearGuardsWhenSameWorld() {
            setupGuardListener();

            PlayerTeleportEvent event = mock(PlayerTeleportEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(event.getFrom()).thenReturn(mockFromLocation);
            when(event.getTo()).thenReturn(mockToLocation);
            // Both locations have mockWorld

            guardListener.onTeleport(event);

            verify(mockPlayerGuard, never()).clearGuards();
        }
    }
}
