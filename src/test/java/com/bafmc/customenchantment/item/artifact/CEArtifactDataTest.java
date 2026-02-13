package com.bafmc.customenchantment.item.artifact;

import com.bafmc.customenchantment.item.CEItemData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEArtifactData Tests")
class CEArtifactDataTest {

    @Test
    @DisplayName("should extend CEItemData")
    void shouldExtendCEItemData() {
        CEArtifactData data = new CEArtifactData();
        assertTrue(data instanceof CEItemData);
    }

    @Test
    @DisplayName("should create CEArtifactData instance")
    void shouldCreateCEArtifactDataInstance() {
        CEArtifactData data = new CEArtifactData();
        assertNotNull(data);
    }

    @Test
    @DisplayName("should support cloning")
    void shouldSupportCloning() {
        CEArtifactData data = new CEArtifactData();
        CEArtifactData cloned = data.clone();

        assertNotNull(cloned);
        assertNotEquals(data, cloned);
    }
}
