package com.caco3.elijars.utils;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ExecutionUtilsTest {
    @Test
    void runningApplicationLocationGot() {
        assertThat(ExecutionUtils.getRunningApplicationLocation())
                .isNotNull()
                .satisfies(it -> assertThat(Files.exists(it)).withFailMessage(() -> it + " does not exist").isTrue());
    }
}