package com.bafmc.customenchantment.item.artifact;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEArtifactGroup Tests")
class CEArtifactGroupTest {

    @Test
    @DisplayName("should create CEArtifactGroup instance via builder")
    void shouldCreateCEArtifactGroupInstance() {
        CEArtifactGroup group = CEArtifactGroup.builder()
                .name("test")
                .display("Test Display")
                .itemDisplay("Test Item Display")
                .itemLore(new ArrayList<>())
                .build();
        assertNotNull(group);
    }
}
