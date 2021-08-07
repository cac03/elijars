package com.caco3.elijars.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DeletablePathTest {

    @Test
    void temporaryDirectoryDeleted() throws Exception {
        Path directory = Files.createTempDirectory("my-prefix");

        try (DeletablePath ignore = DeletablePath.create(directory)) {
            assertThat(directory).exists();
        }
        assertThat(directory).doesNotExist();
    }

    @Test
    void directoryRecursivelyDeleted() throws IOException {
        Path directory = Files.createTempDirectory("my-prefix");
        Path abc = Files.createFile(directory.resolve("abc"));
        //noinspection EmptyTryBlock
        try (DeletablePath ignore = DeletablePath.create(directory)) {
        }

        assertThat(abc).doesNotExist();
        assertThat(directory).doesNotExist();
    }

}