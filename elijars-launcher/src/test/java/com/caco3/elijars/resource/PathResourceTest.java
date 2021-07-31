package com.caco3.elijars.resource;

import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void mapInputStreamReturnsMappedObject() throws IOException {
        PathResource pathResource = newTestPathResource();
        String expectedContent = "abc";
        Files.writeString(pathResource.getPath(), expectedContent);

        String actualContent = pathResource.mapInputStream(it -> new String(it.readAllBytes(), StandardCharsets.UTF_8));
        assertThat(actualContent)
                .isEqualTo(actualContent);
    }

    private PathResource newTestPathResource() throws IOException {
        Path path = fileSystem.getPath("abc");
        Files.createFile(path);
        return new PathResource(path);
    }
}