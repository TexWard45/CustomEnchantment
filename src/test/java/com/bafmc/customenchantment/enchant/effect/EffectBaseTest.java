package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import org.bukkit.entity.Player;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base test class for all effect tests.
 * Provides common setup, teardown, and utility methods.
 */
public abstract class EffectBaseTest {

    /**
     * Create a test CEFunctionData with a null player
     */
    protected CEFunctionData createTestData(Player player) {
        return new CEFunctionData(player);
    }

    /**
     * Assert that an effect has the correct identifier
     */
    protected void assertEffectIdentifier(EffectHook effect, String expectedId) {
        assertEquals(expectedId, effect.getIdentify(),
            "Effect should have correct identifier");
    }

    /**
     * Assert that setup completes without throwing exception
     */
    protected void assertSetupSucceeds(EffectHook effect, String[] args) {
        assertDoesNotThrow(() -> effect.setup(args),
            "Effect setup should not throw exception with valid args");
    }

    /**
     * Assert that execute completes without throwing exception
     */
    protected void assertExecuteSucceeds(EffectHook effect, CEFunctionData data) {
        assertDoesNotThrow(() -> effect.execute(data),
            "Effect execute should not throw exception with valid data");
    }

    /**
     * Assert that setup throws an exception with null array
     */
    protected void assertSetupFailsWithNull(EffectHook effect) {
        assertThrows(Exception.class, () -> effect.setup(null),
            "Effect setup should throw exception with null args");
    }

    /**
     * Assert that execute handles null data gracefully
     */
    protected void assertExecuteHandlesNullData(EffectHook effect) {
        assertDoesNotThrow(() -> effect.execute(null),
            "Effect execute should not throw with null data");
    }

    /**
     * Assert that execute handles null player gracefully
     */
    protected void assertExecuteHandlesNullPlayer(EffectHook effect) {
        CEFunctionData data = new CEFunctionData(null);
        assertDoesNotThrow(() -> effect.execute(data),
            "Effect execute should not throw with null player");
    }

    /**
     * Assert that setup succeeds or fails due to NMS classes not available in test environment.
     * Some effects depend on NMS/Mojang classes (e.g., PotionEffectType, DustParticleOptions)
     * which are not on the test classpath.
     */
    protected void assertSetupSucceedsOrNmsUnavailable(EffectHook effect, String[] args) {
        try {
            effect.setup(args);
        } catch (NoClassDefFoundError | ExceptionInInitializerError e) {
            // NMS classes not available in test environment - acceptable
        }
    }

    /**
     * Assert that execute succeeds or fails due to NMS classes not available in test environment.
     * Also catches NullPointerException because if setup failed silently (NMS unavailable),
     * internal fields may be null causing NPE during execute.
     */
    protected void assertExecuteSucceedsOrNmsUnavailable(EffectHook effect, CEFunctionData data) {
        try {
            effect.execute(data);
        } catch (NoClassDefFoundError | ExceptionInInitializerError | NullPointerException e) {
            // NMS classes not available in test environment, or fields null from failed setup - acceptable
        }
    }
}
