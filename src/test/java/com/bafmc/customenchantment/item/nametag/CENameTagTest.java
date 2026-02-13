package com.bafmc.customenchantment.item.nametag;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CENameTag Tests")
class CENameTagTest {

    @Test
    @DisplayName("should create CENameTag instance")
    void shouldCreateCENameTagInstance() {
        CENameTag nametag = new CENameTag();
        assertNotNull(nametag);
    }
}
