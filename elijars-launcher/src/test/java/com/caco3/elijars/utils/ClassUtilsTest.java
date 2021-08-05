package com.caco3.elijars.utils;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ClassUtilsTest {
    @Test
    void classLocationFound() {
        Path location = ClassUtils.getClassLocation(ClassUtils.class);

        assertThat(location)
                .isNotNull()
                .satisfies(it -> assertThat(Files.exists(it)).withFailMessage(() -> it + " does not exist").isTrue());
    }
}