package com.bafmc.customenchantment.guard;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.nms.EntityInsentientNMS;
import com.bafmc.customenchantment.nms.EntityLivingNMS;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for PlayerGuard class.
 * Tests per-player guard management including wildcard removal.
 */
public class PlayerGuardTest {

    private PlayerGuard playerGuard;

    @Mock
    private Player mockPlayer;

    @Mock
    private Guard mockGuard;

    @Mock
    private Guard mockGuard2;

    @Mock
    private Guard mockGuard3;

    @Mock
    private Entity mockEntity;

    @Mock
    private Entity mockEntity2;

    @Mock
    private Entity mockEntity3;

    @Mock
    private EntityInsentientNMS mockEntityInsentientNMS;

    @Mock
    private EntityInsentientNMS mockEntityInsentientNMS2;

    @Mock
    private EntityInsentientNMS mockEntityInsentientNMS3;

    @Mock
    private CustomEnchantment mockPlugin;

    @Mock
    private GuardModule mockGuardModule;

    @Mock
    private GuardManager mockGuardManager;

    @Mock
    private Server mockServer;

    private UUID playerUUID;
    private UUID entityUUID;
    private UUID entity2UUID;
    private UUID entity3UUID;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        playerUUID = UUID.randomUUID();
        entityUUID = UUID.randomUUID();
        entity2UUID = UUID.randomUUID();
        entity3UUID = UUID.randomUUID();

        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");

        when(mockEntity.getUniqueId()).thenReturn(entityUUID);
        when(mockEntity2.getUniqueId()).thenReturn(entity2UUID);
        when(mockEntity3.getUniqueId()).thenReturn(entity3UUID);

        when(mockGuard.getName()).thenReturn("guard1");
        when(mockGuard.getEntityInsentient()).thenReturn(mockEntityInsentientNMS);
        when(mockEntityInsentientNMS.getEntity()).thenReturn(mockEntity);

        when(mockGuard2.getName()).thenReturn("guard2");
        when(mockGuard2.getEntityInsentient()).thenReturn(mockEntityInsentientNMS2);
        when(mockEntityInsentientNMS2.getEntity()).thenReturn(mockEntity2);

        when(mockGuard3.getName()).thenReturn("wolf_alpha");
        when(mockGuard3.getEntityInsentient()).thenReturn(mockEntityInsentientNMS3);
        when(mockEntityInsentientNMS3.getEntity()).thenReturn(mockEntity3);

        playerGuard = new PlayerGuard(mockPlayer);
    }

    // ==================== Constructor Tests ====================

    @Test
    void constructor_initializesCorrectly() {
        assertEquals("TestPlayer", playerGuard.getName());
        assertNotNull(playerGuard.getGuards());
        assertTrue(playerGuard.getGuards().isEmpty());
    }

    // ==================== addGuard Tests ====================

    @Test
    void addGuard_addsGuardSuccessfully() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            playerGuard.addGuard(mockGuard);

            assertEquals(1, playerGuard.getGuards().size());
            assertTrue(playerGuard.containsGuardName("guard1"));
            verify(mockGuardManager).addEntityGuard(mockGuard);
        }
    }

    @Test
    void addGuard_doesNotAddDuplicateByName() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            playerGuard.addGuard(mockGuard);

            // Try to add another guard with same name
            Guard duplicateGuard = mock(Guard.class);
            when(duplicateGuard.getName()).thenReturn("guard1");

            playerGuard.addGuard(duplicateGuard);

            assertEquals(1, playerGuard.getGuards().size());
            verify(mockGuardManager, times(1)).addEntityGuard(mockGuard);
        }
    }

    @Test
    void addGuard_allowsMultipleGuardsWithDifferentNames() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            playerGuard.addGuard(mockGuard);
            playerGuard.addGuard(mockGuard2);

            assertEquals(2, playerGuard.getGuards().size());
            assertTrue(playerGuard.containsGuardName("guard1"));
            assertTrue(playerGuard.containsGuardName("guard2"));
        }
    }

    // ==================== removeGuard Tests ====================

    @Test
    void removeGuard_removesGuardSuccessfully() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            playerGuard.addGuard(mockGuard);
            assertEquals(1, playerGuard.getGuards().size());

            playerGuard.removeGuard(mockGuard);

            assertEquals(0, playerGuard.getGuards().size());
            assertFalse(playerGuard.containsGuardName("guard1"));
            verify(mockGuardManager).removeEntityGuard(mockGuard);
            verify(mockGuard).remove();
        }
    }

    // ==================== removeGuardByName Tests ====================

    @Test
    void removeGuardByName_removesExactMatch() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            playerGuard.addGuard(mockGuard);
            playerGuard.addGuard(mockGuard2);

            playerGuard.removeGuardByName("guard1");

            assertEquals(1, playerGuard.getGuards().size());
            assertFalse(playerGuard.containsGuardName("guard1"));
            assertTrue(playerGuard.containsGuardName("guard2"));
        }
    }

    @Test
    void removeGuardByName_doesNothingForNonExistentName() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            playerGuard.addGuard(mockGuard);

            playerGuard.removeGuardByName("nonexistent");

            assertEquals(1, playerGuard.getGuards().size());
            assertTrue(playerGuard.containsGuardName("guard1"));
        }
    }

    @Test
    void removeGuardByName_wildcardRemovesMatchingGuards() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            // Add guards: guard1, guard2, wolf_alpha
            playerGuard.addGuard(mockGuard);
            playerGuard.addGuard(mockGuard2);
            playerGuard.addGuard(mockGuard3);

            assertEquals(3, playerGuard.getGuards().size());

            // Remove all guards starting with "guard"
            playerGuard.removeGuardByName("guard*");

            // Only wolf_alpha should remain
            assertEquals(1, playerGuard.getGuards().size());
            assertFalse(playerGuard.containsGuardName("guard1"));
            assertFalse(playerGuard.containsGuardName("guard2"));
            assertTrue(playerGuard.containsGuardName("wolf_alpha"));
        }
    }

    @Test
    void removeGuardByName_wildcardWithNoMatchesDoesNothing() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            playerGuard.addGuard(mockGuard);

            playerGuard.removeGuardByName("xyz*");

            assertEquals(1, playerGuard.getGuards().size());
        }
    }

    // ==================== clearGuards Tests ====================

    @Test
    void clearGuards_removesAllGuards() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            playerGuard.addGuard(mockGuard);
            playerGuard.addGuard(mockGuard2);
            assertEquals(2, playerGuard.getGuards().size());

            playerGuard.clearGuards();

            assertEquals(0, playerGuard.getGuards().size());
        }
    }

    @Test
    void clearGuards_doesNothingWhenNoGuards() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            assertDoesNotThrow(() -> playerGuard.clearGuards());
            assertEquals(0, playerGuard.getGuards().size());
        }
    }

    // ==================== getGuardByName Tests ====================

    @Test
    void getGuardByName_returnsGuardForExistingName() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            playerGuard.addGuard(mockGuard);

            Guard result = playerGuard.getGuardByName("guard1");

            assertSame(mockGuard, result);
        }
    }

    @Test
    void getGuardByName_returnsNullForNonExistentName() {
        Guard result = playerGuard.getGuardByName("nonexistent");

        assertNull(result);
    }

    // ==================== containsGuardName Tests ====================

    @Test
    void containsGuardName_returnsTrueForExistingName() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            playerGuard.addGuard(mockGuard);

            assertTrue(playerGuard.containsGuardName("guard1"));
        }
    }

    @Test
    void containsGuardName_returnsFalseForNonExistentName() {
        assertFalse(playerGuard.containsGuardName("nonexistent"));
    }

    // ==================== tickGuards Tests ====================

    @Test
    void tickGuards_callsTickOnAllGuards() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            playerGuard.addGuard(mockGuard);
            playerGuard.addGuard(mockGuard2);

            playerGuard.tickGuards();

            verify(mockGuard).tick();
            verify(mockGuard2).tick();
        }
    }

    @Test
    void tickGuards_doesNothingWhenNoGuards() {
        assertDoesNotThrow(() -> playerGuard.tickGuards());
    }

    // ==================== getPlayer Tests ====================

    @Test
    void getPlayer_returnsBukkitPlayer() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getPlayer(playerUUID)).thenReturn(mockPlayer);

            Player result = playerGuard.getPlayer();

            assertSame(mockPlayer, result);
        }
    }

    @Test
    void getPlayer_returnsNullIfPlayerOffline() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getPlayer(playerUUID)).thenReturn(null);

            Player result = playerGuard.getPlayer();

            assertNull(result);
        }
    }

    // ==================== isPlayerOnline Tests ====================

    @Test
    void isPlayerOnline_returnsTrueWhenPlayerOnline() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getPlayer(playerUUID)).thenReturn(mockPlayer);

            assertTrue(playerGuard.isPlayerOnline());
        }
    }

    @Test
    void isPlayerOnline_returnsFalseWhenPlayerOffline() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(() -> Bukkit.getPlayer(playerUUID)).thenReturn(null);

            assertFalse(playerGuard.isPlayerOnline());
        }
    }

    // ==================== getGuard Tests ====================

    @Test
    void getGuard_returnsGuardForEntity() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            playerGuard.addGuard(mockGuard);

            Guard result = playerGuard.getGuard(mockEntity);

            assertSame(mockGuard, result);
        }
    }

    @Test
    void getGuard_returnsNullForUnknownEntity() {
        Entity unknownEntity = mock(Entity.class);
        when(unknownEntity.getUniqueId()).thenReturn(UUID.randomUUID());

        Guard result = playerGuard.getGuard(unknownEntity);

        assertNull(result);
    }

    // ==================== getGuards Tests ====================

    @Test
    void getGuards_returnsDefensiveCopy() {
        try (MockedStatic<CustomEnchantment> mockedStatic = mockStatic(CustomEnchantment.class)) {
            mockedStatic.when(CustomEnchantment::instance).thenReturn(mockPlugin);
            when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
            when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);

            playerGuard.addGuard(mockGuard);

            List<Guard> guards = playerGuard.getGuards();
            guards.clear();

            // Original should not be affected
            assertEquals(1, playerGuard.getGuards().size());
        }
    }

    // ==================== lastTarget Tests ====================

    @Test
    void setTarget_methodExists() {
        // Note: This test cannot fully test setTarget because EntityLivingNMS
        // constructor requires a CraftEntity (NMS) which cannot be mocked with Mockito.
        // Testing with real entity requires full server integration test.

        // Verify the method exists and has correct signature
        try {
            var method = PlayerGuard.class.getDeclaredMethod("setTarget", Entity.class);
            assertNotNull(method, "setTarget method should exist");
            assertEquals(void.class, method.getReturnType());
        } catch (NoSuchMethodException e) {
            fail("setTarget(Entity) method should exist");
        }
    }

    @Test
    void getLastTarget_returnsNullInitially() {
        assertNull(playerGuard.getLastTarget());
    }

    // ==================== lastEnemy Tests ====================

    @Test
    void setLastEnemy_methodExists() {
        // Note: This test cannot fully test setLastEnemy because EntityLivingNMS
        // constructor requires a CraftEntity (NMS) which cannot be mocked with Mockito.
        // Testing with real entity requires full server integration test.

        // Verify the method exists and has correct signature
        try {
            var method = PlayerGuard.class.getDeclaredMethod("setLastEnemy", Entity.class);
            assertNotNull(method, "setLastEnemy method should exist");
            assertEquals(void.class, method.getReturnType());
        } catch (NoSuchMethodException e) {
            fail("setLastEnemy(Entity) method should exist");
        }
    }

    @Test
    void getLastEnemy_returnsNullInitially() {
        assertNull(playerGuard.getLastEnemy());
    }

    // ==================== getName Tests ====================

    @Test
    void getName_returnsPlayerName() {
        assertEquals("TestPlayer", playerGuard.getName());
    }
}
