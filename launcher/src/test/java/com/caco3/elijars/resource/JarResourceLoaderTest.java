package com.caco3.elijars.resource;

import com.caco3.elijars.launcher.SampleApplicationProject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JarResourceLoaderTest {
    private final SampleApplicationProject sampleApplicationProject = SampleApplicationProject
            .forSourcesDirectory(Paths.get("..", "elijars-samples", "sample-application"));
    private final Path jar = sampleApplicationProject.jar();
    private final JarResourceLoader jarResourceLoader = JarResourceLoader.forPath(jar);

    @AfterEach
    void tearDown() throws Exception {
        jarResourceLoader.close();
        sampleApplicationProject.close();
    }

    @Test
    void manifestLoaded() {
        Optional<Resource> resource = jarResourceLoader.loadByName("META-INF/MANIFEST.MF");

        assertThat(resource)
                .isNotEmpty();
    }

    @Test
    void allResourcesLoaded() {
        List<Resource> resources = jarResourceLoader.loadAll();

        assertThat(resources)
                .isNotEmpty()
                .map(Resource::getPath)
                .map(Objects::toString)
                .contains("/com/caco3/elijars/simpleapplication/HelloWorld.class")
                .contains("/com/caco3/elijars/simpleapplication/Main.class");
    }

    @Test
    void throwsIllegalStateExceptionWhenClosed() {
        jarResourceLoader.close();

        assertThatThrownBy(() -> jarResourceLoader.loadByName("abc"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("is closed");
    }

    @Test
    void returnEmptyOptionalWhenAbsentResourceRequested() {
        assertThat(jarResourceLoader.loadByName("/does/not/Exist.class"))
                .isEmpty();
    }
}