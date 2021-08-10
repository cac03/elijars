package com.caco3.elijars.maven;

import com.caco3.elijars.resource.FileSystemResourceLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntryPointResolverTest {
    private final Maven maven = Maven.createDefault();

    @BeforeAll
    void setUp() {
        maven.execute(Maven.Project.ELIJARS, List.of("clean", "verify"));
    }

    @Test
    void entryPointResolvedUsingModuleDescriptor() {
        EntryPointResolver resolver = new EntryPointResolver();
        SampleApplication application = SampleApplication.APPLICATION_WITH_GUAVA;
        try (FileSystemResourceLoader resourceLoader = FileSystemResourceLoader.forJar(application.getOriginalJar())) {
            EntryPoint entryPoint = resolver.resolveEntryPoint(resourceLoader);
            assertThat(entryPoint.getModuleName())
                    .isEqualTo(application.getModuleName());
            assertThat(entryPoint.getMainClassName())
                    .isEqualTo(application.getMainClassName());
        }
    }

    @Test
    void entryPointResolvedFromManifest() {
        EntryPointResolver resolver = new EntryPointResolver();
        SampleApplication application = SampleApplication.CLASSPATH_APPLICATION;
        try (FileSystemResourceLoader resourceLoader = FileSystemResourceLoader.forJar(application.getOriginalJar())) {
            EntryPoint entryPoint = resolver.resolveEntryPoint(resourceLoader);
            assertThat(entryPoint.getModuleName()).isNull();
            assertThat(entryPoint.getMainClassName())
                    .isEqualTo(application.getMainClassName());
        }
    }
}