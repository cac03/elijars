package com.caco3.elijars.resource;

import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PathResourceTest {
    private final FileSystem fileSystem = Jimfs.newFileSystem();

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void getInputSteamThrowsIfFileSystemIsClosed() throws IOException {
        PathResource pathResource = newTestPathResource();

        fileSystem.close();

        assertThatThrownBy(pathResource::getInputStream)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getPathThrowsIfFileSystemIsClosed() throws IOException {
        PathResource pathResource = newTestPathResource();

        fileSystem.close();

        assertThatThrownBy(pathResource::getPath)
                .isInstanceOf(IllegalStateException.class);
    }

    private PathResource newTestPathResource() throws IOException {
        Path path = fileSystem.getPath("abc");
        Files.createFile(path);
        return new PathResource(path);
    }
}