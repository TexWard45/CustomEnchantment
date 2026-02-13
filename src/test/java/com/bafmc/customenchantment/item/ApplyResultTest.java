package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApplyResult Tests")
class ApplyResultTest {

    @Test
    @DisplayName("should have NOTHING result")
    void shouldHaveNothingResult() {
        ApplyResult result = ApplyResult.NOTHING;
        assertNotNull(result);
    }

    @Test
    @DisplayName("should have CANCEL result")
    void shouldHaveCancelResult() {
        ApplyResult result = ApplyResult.CANCEL;
        assertNotNull(result);
    }

    @Test
    @DisplayName("should have SUCCESS result")
    void shouldHaveSuccessResult() {
        ApplyResult result = ApplyResult.SUCCESS;
        assertNotNull(result);
    }

    @Test
    @DisplayName("should have FAIL result")
    void shouldHaveFailResult() {
        ApplyResult result = ApplyResult.FAIL;
        assertNotNull(result);
    }

    @Test
    @DisplayName("should have FAIL_AND_UPDATE result")
    void shouldHaveFailAndUpdateResult() {
        ApplyResult result = ApplyResult.FAIL_AND_UPDATE;
        assertNotNull(result);
    }

    @Test
    @DisplayName("should have DESTROY result")
    void shouldHaveDestroyResult() {
        ApplyResult result = ApplyResult.DESTROY;
        assertNotNull(result);
    }

    @Test
    @DisplayName("should distinguish between different result types")
    void shouldDistinguishBetweenResults() {
        assertNotEquals(ApplyResult.SUCCESS, ApplyResult.FAIL);
        assertNotEquals(ApplyResult.NOTHING, ApplyResult.CANCEL);
        assertNotEquals(ApplyResult.DESTROY, ApplyResult.SUCCESS);
    }
}
