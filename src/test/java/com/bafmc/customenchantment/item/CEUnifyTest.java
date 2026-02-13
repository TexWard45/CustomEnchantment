package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CEUnify Tests")
class CEUnifyTest {

    @Test
    @DisplayName("should verify CEUnify is abstract")
    void shouldVerifyCEUnifyIsAbstract() {
        assertTrue(java.lang.reflect.Modifier.isAbstract(CEUnify.class.getModifiers()));
    }

    @Test
    @DisplayName("should verify CEUnify extends CEWeaponAbstract")
    void shouldVerifyCEUnifyExtendsCEWeaponAbstract() {
        assertTrue(CEWeaponAbstract.class.isAssignableFrom(CEUnify.class));
    }
}
