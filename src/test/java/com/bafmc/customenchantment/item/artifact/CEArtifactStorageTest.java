package com.bafmc.customenchantment.item.artifact;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEArtifactStorage Tests")
class CEArtifactStorageTest {

    @Test
    @DisplayName("should create CEArtifactStorage instance")
    void shouldCreateCEArtifactStorageInstance() {
        CEArtifactStorage storage = new CEArtifactStorage();
        assertNotNull(storage);
    }
}
