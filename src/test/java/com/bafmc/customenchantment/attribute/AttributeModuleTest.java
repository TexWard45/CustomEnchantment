package com.bafmc.customenchantment.attribute;

import com.bafmc.customenchantment.CustomEnchantment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for AttributeModule class.
 * Tests module initialization and lifecycle.
 */
class AttributeModuleTest {

    private CustomEnchantment mockPlugin;
    private AttributeModule attributeModule;

    @BeforeEach
    void setUp() {
        mockPlugin = mock(CustomEnchantment.class);
        attributeModule = new AttributeModule(mockPlugin);
    }

    @Test
    @DisplayName("Constructor initializes with plugin reference")
    void constructor_initializesWithPlugin() {
        assertNotNull(attributeModule);
    }

    @Test
    @DisplayName("onEnable initializes CustomAttributeType")
    void onEnable_initializesCustomAttributeType() {
        // CustomAttributeType.init() is called in onEnable
        // We verify by checking that attribute types are registered
        assertDoesNotThrow(() -> attributeModule.onEnable());

        // Verify CustomAttributeType values are populated
        CustomAttributeType[] values = CustomAttributeType.getValues();
        assertNotNull(values);
        assertTrue(values.length > 0, "Should have registered attribute types");
    }

    @Test
    @DisplayName("onEnable initializes AttributeMapRegister")
    void onEnable_initializesAttributeMapRegister() {
        // This test verifies that onEnable doesn't throw when initializing
        // the AttributeMapRegister
        assertDoesNotThrow(() -> attributeModule.onEnable());
    }

    @Test
    @DisplayName("Module can be created multiple times")
    void module_canBeCreatedMultipleTimes() {
        AttributeModule module1 = new AttributeModule(mockPlugin);
        AttributeModule module2 = new AttributeModule(mockPlugin);

        assertNotNull(module1);
        assertNotNull(module2);
        assertNotSame(module1, module2);
    }
}
