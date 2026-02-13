package com.bafmc.customenchantment.item;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@DisplayName("ApplyReason Tests")
class ApplyReasonTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        try {
            if (MockBukkit.isMocked()) {
                MockBukkit.unmock();
            }
            server = MockBukkit.mock();
        } catch (Exception | NoClassDefFoundError | ExceptionInInitializerError e) {
            // MockBukkit may fail to initialize due to missing netty/NMS classes
            server = null;
        }
    }

    @Test
    @DisplayName("should create ApplyReason with reason and result")
    void shouldCreateApplyReason() {
        ApplyReason reason = new ApplyReason("test_reason", ApplyResult.SUCCESS);

        assertEquals("test_reason", reason.getReason());
        assertEquals(ApplyResult.SUCCESS, reason.getResult());
    }

    @Test
    @DisplayName("should update reason after creation")
    void shouldUpdateReason() {
        ApplyReason reason = new ApplyReason("initial", ApplyResult.FAIL);
        reason.setReason("updated");

        assertEquals("updated", reason.getReason());
    }

    @Test
    @DisplayName("should update result after creation")
    void shouldUpdateResult() {
        ApplyReason reason = new ApplyReason("test", ApplyResult.FAIL);
        reason.setResult(ApplyResult.SUCCESS);

        assertEquals(ApplyResult.SUCCESS, reason.getResult());
    }

    @Test
    @DisplayName("should manage placeholder map")
    void shouldManagePlaceholderMap() {
        ApplyReason reason = new ApplyReason("test", ApplyResult.SUCCESS);
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("key1", "value1");

        reason.setPlaceholder(placeholder);
        assertEquals(placeholder, reason.getPlaceholder());
    }

    @Test
    @DisplayName("should manage data map")
    void shouldManageDataMap() {
        ApplyReason reason = new ApplyReason("test", ApplyResult.SUCCESS);

        reason.putData("key1", "value1");
        reason.putData("key2", 42);

        assertEquals("value1", reason.getData().get("key1"));
        assertEquals(42, reason.getData().get("key2"));
    }

    @AfterAll
    static void tearDownAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
    }

    @Test
    @DisplayName("should set and get player")
    void shouldSetAndGetPlayer() {
        assumeTrue(server != null, "MockBukkit not available");
        ApplyReason reason = new ApplyReason("test", ApplyResult.SUCCESS);
        Player player = server.addPlayer();

        reason.setPlayer(player);
        assertEquals(player, reason.getPlayer());
    }

    @Test
    @DisplayName("should set and get CEItem references")
    void shouldSetAndGetCEItems() {
        ApplyReason reason = new ApplyReason("test", ApplyResult.SUCCESS);

        assertNull(reason.getCEItem1());
        assertNull(reason.getCEItem2());
    }

    @Test
    @DisplayName("should manage source CEItem")
    void shouldManageSourceCEItem() {
        ApplyReason reason = new ApplyReason("test", ApplyResult.SUCCESS);

        assertFalse(reason.isChangeSource());
        assertNull(reason.getSource());

        reason.setSource(null);
        assertFalse(reason.isChangeSource());
    }

    @Test
    @DisplayName("should set and get write logs flag")
    void shouldSetAndGetWriteLogs() {
        ApplyReason reason = new ApplyReason("test", ApplyResult.SUCCESS);

        assertFalse(reason.isWriteLogs());
        reason.setWriteLogs(true);
        assertTrue(reason.isWriteLogs());
    }

    @Test
    @DisplayName("should verify predefined constants")
    void shouldVerifyPredefinedConstants() {
        assertEquals("nothing", ApplyReason.NOTHING.getReason());
        assertEquals(ApplyResult.NOTHING, ApplyReason.NOTHING.getResult());

        assertEquals("cancel", ApplyReason.CANCEL.getReason());
        assertEquals(ApplyResult.CANCEL, ApplyReason.CANCEL.getResult());

        assertEquals("success", ApplyReason.SUCCESS.getReason());
        assertEquals(ApplyResult.SUCCESS, ApplyReason.SUCCESS.getResult());

        assertEquals("fail", ApplyReason.FAIL.getReason());
        assertEquals(ApplyResult.FAIL, ApplyReason.FAIL.getResult());
    }

    @Test
    @DisplayName("should handle null values gracefully")
    void shouldHandleNullValues() {
        ApplyReason reason = new ApplyReason("test", ApplyResult.SUCCESS);

        reason.setPlaceholder(null);
        assertNull(reason.getPlaceholder());

        reason.setPlayer(null);
        assertNull(reason.getPlayer());

        reason.setRewards(null);
        assertNull(reason.getRewards());
    }
}
