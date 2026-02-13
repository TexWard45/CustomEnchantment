package com.bafmc.customenchantment.item.artifact;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEArtifactGroup Tests")
class CEArtifactGroupTest {

    @Test
    @DisplayName("should create CEArtifactGroup instance")
    void shouldCreateCEArtifactGroupInstance() {
        CEArtifactGroup group = new CEArtifactGroup();
        assertNotNull(group);
    }
}
