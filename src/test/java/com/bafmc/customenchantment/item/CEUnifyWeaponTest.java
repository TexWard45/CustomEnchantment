package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CEUnifyWeapon Tests")
class CEUnifyWeaponTest {

    @Test
    @DisplayName("should create CEUnifyWeapon instance")
    void shouldCreateCEUnifyWeaponInstance() {
        CEUnify mockUnify = mock(CEUnify.class);
        CEUnifyWeapon weapon = new CEUnifyWeapon(mockUnify);
        assertNotNull(weapon);
    }

    @Test
    @DisplayName("should not extend CEUnify")
    void shouldNotExtendCEUnify() {
        assertFalse(CEUnify.class.isAssignableFrom(CEUnifyWeapon.class));
    }
}
