package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEUnifyData Tests")
class CEUnifyDataTest {

    @Test
    @DisplayName("should create CEUnifyData instance")
    void shouldCreateCEUnifyDataInstance() {
        CEUnifyData data = new CEUnifyData();
        assertNotNull(data);
    }
}
