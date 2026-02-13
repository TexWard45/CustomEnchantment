package com.bafmc.customenchantment.item.artifact;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEArtifactGroupMap Tests")
class CEArtifactGroupMapTest {

    @Test
    @DisplayName("should create CEArtifactGroupMap instance")
    void shouldCreateCEArtifactGroupMapInstance() {
        CEArtifactGroupMap map = new CEArtifactGroupMap();
        assertNotNull(map);
    }
}
