package com.bafmc.customenchantment.guard;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for GuardManager class.
 * Tests global guard tracking and player guard management.
 */
public class GuardManagerTest {

    private GuardManager guardManager;

    @Mock
    private Player mockPlayer;

    @Mock
    private Player mockPlayer2;

    @Mock
    private Entity mockEntity;

    @Mock
    private Guard mockGuard;

    @Mock
    private com.bafmc.customenchantment.nms.EntityInsentientNMS mockEntityInsentientNMS;

    private UUID playerUUID;
    private UUID player2UUID;
    private UUID entityUUID;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        guardManager = new GuardManager();

        playerUUID = UUID.randomUUID();
        player2UUID = UUID.randomUUID();
        entityUUID = UUID.randomUUID();

        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");
        when(mockPlayer2.getUniqueId()).thenReturn(player2UUID);
        when(mockPlayer2.getName()).thenReturn("TestPlayer2");
        when(mockEntity.getUniqueId()).thenReturn(entityUUID);

        when(mockGuard.getEntityInsentient()).thenReturn(mockEntityInsentientNMS);
        when(mockEntityInsentientNMS.getEntity()).thenReturn(mockEntity);
    }

    // ==================== getPlayerGuard Tests ====================

    @Test
    void getPlayerGuard_createsNewPlayerGuardIfNotExists() {
        PlayerGuard playerGuard = guardManager.getPlayerGuard(mockPlayer);

        assertNotNull(playerGuard);
        assertEquals("TestPlayer", playerGuard.getName());
    }

    @Test
    void getPlayerGuard_returnsSameInstanceForSamePlayer() {
        PlayerGuard first = guardManager.getPlayerGuard(mockPlayer);
        PlayerGuard second = guardManager.getPlayerGuard(mockPlayer);

        assertSame(first, second);
    }

    @Test
    void getPlayerGuard_createsDifferentInstancesForDifferentPlayers() {
        PlayerGuard pg1 = guardManager.getPlayerGuard(mockPlayer);
        PlayerGuard pg2 = guardManager.getPlayerGuard(mockPlayer2);

        assertNotSame(pg1, pg2);
        assertEquals("TestPlayer", pg1.getName());
        assertEquals("TestPlayer2", pg2.getName());
    }

    // ==================== removePlayerGuard Tests ====================

    @Test
    void removePlayerGuard_removesExistingPlayerGuard() {
        // First create a player guard
        guardManager.getPlayerGuard(mockPlayer);

        // Then remove it
        guardManager.removePlayerGuard(mockPlayer);

        // Getting player guard again should create a new one
        PlayerGuard newPlayerGuard = guardManager.getPlayerGuard(mockPlayer);
        assertNotNull(newPlayerGuard);
    }

    @Test
    void removePlayerGuard_doesNothingIfPlayerGuardDoesNotExist() {
        // Should not throw exception
        assertDoesNotThrow(() -> guardManager.removePlayerGuard(mockPlayer));
    }

    // ==================== isGuard Tests ====================

    @Test
    void isGuard_returnsFalseWhenNoGuardsExist() {
        assertFalse(guardManager.isGuard(mockEntity));
    }

    @Test
    void isGuard_returnsTrueWhenEntityIsGuard() {
        guardManager.addEntityGuard(mockGuard);

        assertTrue(guardManager.isGuard(mockEntity));
    }

    @Test
    void isGuard_returnsFalseForDifferentEntity() {
        guardManager.addEntityGuard(mockGuard);

        Entity differentEntity = mock(Entity.class);
        when(differentEntity.getUniqueId()).thenReturn(UUID.randomUUID());

        assertFalse(guardManager.isGuard(differentEntity));
    }

    // ==================== getGuard Tests ====================

    @Test
    void getGuard_returnsNullWhenNoGuardsExist() {
        assertNull(guardManager.getGuard(mockEntity));
    }

    @Test
    void getGuard_returnsGuardForEntity() {
        guardManager.addEntityGuard(mockGuard);

        Guard result = guardManager.getGuard(mockEntity);

        assertSame(mockGuard, result);
    }

    @Test
    void getGuard_returnsNullForDifferentEntity() {
        guardManager.addEntityGuard(mockGuard);

        Entity differentEntity = mock(Entity.class);
        when(differentEntity.getUniqueId()).thenReturn(UUID.randomUUID());

        assertNull(guardManager.getGuard(differentEntity));
    }

    // ==================== addEntityGuard Tests ====================

    @Test
    void addEntityGuard_addsGuardToMap() {
        guardManager.addEntityGuard(mockGuard);

        assertTrue(guardManager.isGuard(mockEntity));
        assertEquals(1, guardManager.getGuards().size());
    }

    @Test
    void addEntityGuard_allowsMultipleGuards() {
        Guard mockGuard2 = mock(Guard.class);
        Entity mockEntity2 = mock(Entity.class);
        UUID entity2UUID = UUID.randomUUID();
        com.bafmc.customenchantment.nms.EntityInsentientNMS mockNMS2 =
            mock(com.bafmc.customenchantment.nms.EntityInsentientNMS.class);

        when(mockEntity2.getUniqueId()).thenReturn(entity2UUID);
        when(mockGuard2.getEntityInsentient()).thenReturn(mockNMS2);
        when(mockNMS2.getEntity()).thenReturn(mockEntity2);

        guardManager.addEntityGuard(mockGuard);
        guardManager.addEntityGuard(mockGuard2);

        assertEquals(2, guardManager.getGuards().size());
        assertTrue(guardManager.isGuard(mockEntity));
        assertTrue(guardManager.isGuard(mockEntity2));
    }

    // ==================== removeEntityGuard Tests ====================

    @Test
    void removeEntityGuard_removesGuardFromMap() {
        guardManager.addEntityGuard(mockGuard);
        assertTrue(guardManager.isGuard(mockEntity));

        guardManager.removeEntityGuard(mockGuard);

        assertFalse(guardManager.isGuard(mockEntity));
        assertEquals(0, guardManager.getGuards().size());
    }

    @Test
    void removeEntityGuard_doesNothingIfGuardDoesNotExist() {
        assertDoesNotThrow(() -> guardManager.removeEntityGuard(mockGuard));
    }

    // ==================== getPlayerGuards Tests ====================

    @Test
    void getPlayerGuards_returnsEmptyListWhenNoPlayerGuards() {
        List<PlayerGuard> playerGuards = guardManager.getPlayerGuards();

        assertNotNull(playerGuards);
        assertTrue(playerGuards.isEmpty());
    }

    @Test
    void getPlayerGuards_returnsAllPlayerGuards() {
        guardManager.getPlayerGuard(mockPlayer);
        guardManager.getPlayerGuard(mockPlayer2);

        List<PlayerGuard> playerGuards = guardManager.getPlayerGuards();

        assertEquals(2, playerGuards.size());
    }

    @Test
    void getPlayerGuards_returnsDefensiveCopy() {
        guardManager.getPlayerGuard(mockPlayer);

        List<PlayerGuard> playerGuards = guardManager.getPlayerGuards();
        playerGuards.clear();

        // Original list should not be affected
        assertEquals(1, guardManager.getPlayerGuards().size());
    }

    // ==================== getGuards Tests ====================

    @Test
    void getGuards_returnsEmptyListWhenNoGuards() {
        List<Guard> guards = guardManager.getGuards();

        assertNotNull(guards);
        assertTrue(guards.isEmpty());
    }

    @Test
    void getGuards_returnsAllGuards() {
        Guard mockGuard2 = mock(Guard.class);
        Entity mockEntity2 = mock(Entity.class);
        UUID entity2UUID = UUID.randomUUID();
        com.bafmc.customenchantment.nms.EntityInsentientNMS mockNMS2 =
            mock(com.bafmc.customenchantment.nms.EntityInsentientNMS.class);

        when(mockEntity2.getUniqueId()).thenReturn(entity2UUID);
        when(mockGuard2.getEntityInsentient()).thenReturn(mockNMS2);
        when(mockNMS2.getEntity()).thenReturn(mockEntity2);

        guardManager.addEntityGuard(mockGuard);
        guardManager.addEntityGuard(mockGuard2);

        List<Guard> guards = guardManager.getGuards();

        assertEquals(2, guards.size());
    }

    @Test
    void getGuards_returnsDefensiveCopy() {
        guardManager.addEntityGuard(mockGuard);

        List<Guard> guards = guardManager.getGuards();
        guards.clear();

        // Original list should not be affected
        assertEquals(1, guardManager.getGuards().size());
    }
}
