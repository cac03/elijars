package com.caco3.elijars.launcher;

import com.caco3.elijars.resource.FileSystemResourceLoader;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class JarApplicationDefinitionSourceTest {
    private static final List<String> DEPENDENCIES = List.of("my-dependency-1.jar", "my-dependency-2.jar");
    private static final String START_CLASS = "org.my.Main";
    private static final String START_MODULE = "org.mymodule";
    private static final String TEST_MANIFEST =
            "Manifest-Version: 1.0\n" +
            "Elijars-Start-Class: " + START_CLASS + "\n" +
            "Elijars-Start-Module: " + START_MODULE + "\n";
    private static final String ELIJARS_INF = "ELIJARS-INF";
    private static final String META_INF = "META-INF";
    private final FileSystem fileSystem = Jimfs.newFileSystem(
            Configuration.unix().toBuilder()
                    .setWorkingDirectory("/")
                    .build()
    );
    private final JarApplicationDefinitionSource configurationSource
            = new JarApplicationDefinitionSource(FileSystemResourceLoader.forFileSystem(fileSystem));

    @BeforeEach
    void setUp() throws IOException {
        createFakeJarStructureInFileSystem();
    }

    private void createFakeJarStructureInFileSystem() throws IOException {
        Files.createDirectory(fileSystem.getPath(ELIJARS_INF));
        for (String dependency : DEPENDENCIES) {
            Files.createFile(fileSystem.getPath(ELIJARS_INF, dependency));
        }
        Files.createDirectory(fileSystem.getPath(META_INF));
        Files.writeString(fileSystem.getPath(META_INF, "MANIFEST.MF"), TEST_MANIFEST, StandardOpenOption.CREATE);
    }

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void configurationRead() {
        List<Path> expectedModulePath = DEPENDENCIES
                .stream()
                .map(name -> fileSystem.getPath("/", ELIJARS_INF, name))
                .collect(Collectors.toList());

        assertThat(configurationSource.getApplicationDefinition())
                .isNotNull()
                .satisfies(configuration ->
                        assertThat(configuration.getMainModuleName()).isEqualTo(START_MODULE))
                .satisfies(configuration ->
                        assertThat(configuration.getMainClassName()).isEqualTo(START_CLASS))
                .satisfies(configuration ->
                        assertThat(configuration.getDependencies())
                                .containsExactlyInAnyOrderElementsOf(expectedModulePath));
    }
}