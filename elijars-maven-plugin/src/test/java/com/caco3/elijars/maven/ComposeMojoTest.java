package com.caco3.elijars.maven;

import com.caco3.elijars.resource.FileSystemResourceLoader;
import com.caco3.elijars.resource.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.zeroturnaround.exec.ProcessResult;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ComposeMojoTest {
    private final Maven maven = Maven.createDefault();

    @BeforeAll
    void setUp() {
        maven.execute(Maven.Project.ELIJARS, List.of("clean", "verify"));
    }

    @AfterEach
    void tearDown() throws IOException {
        assertNoTemporaryFiles();
    }

    private static void assertNoTemporaryFiles() throws IOException {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:elijars*");
        Path path = Paths.get(System.getProperty("java.io.tmpdir"));
        int depth = 1;
        List<Path> temporaryElijarsFiles = Files.walk(path, depth)
                .filter(Files::isDirectory)
                .map(path::relativize)
                .filter(pathMatcher::matches)
                .collect(Collectors.toUnmodifiableList());
        assertThat(temporaryElijarsFiles).isEmpty();
    }

    @Nested
    class SampleApplicationTest {
        private final SampleApplication application = SampleApplication.SAMPLE_APPLICATION;

        @Test
        void sampleApplicationJarCreated() {
            Path expectedJar = application.getJar();
            Path originalJar = Paths.get(application.getJar().toAbsolutePath() + ".original");

            assertThat(expectedJar).exists();
            assertThat(originalJar).exists();
        }

        @Test
        void originalPomPreserved() {
            try (FileSystemResourceLoader resourceLoader = FileSystemResourceLoader.forJar(application.getJar())) {
                Optional<Resource> resource = resourceLoader.loadByName("META-INF/maven/com.caco3/sample-application/pom.xml");
                assertThat(resource)
                        .isPresent();
            }
        }

        @Test
        void applicationReadsItsOwnManifest() {
            ProcessResult processResult = JarUtils.runJar(application.getJar(), "read-manifest");
            String output = processResult.outputUTF8();

            assertThat(output)
                    .contains("Manifest[Custom-Entry] = Test value")
                    .doesNotContainPattern(Pattern.compile("Elijars-.*"));
        }

        @Test
        void applicationSuccessfullyAccessesResourcesAfterEarlyReturnFromMain() {
            ProcessResult processResult = JarUtils.runJar(application.getJar(), "early-main-return");
            String output = processResult.outputUTF8();

            assertThat(output)
                    .contains("Successfully read manifest");
        }
    }

    @Nested
    class JavaFxApplicationTest {
        @Test
        void javaFxApplicationRuns() {
            ProcessResult processResult = JarUtils.runJar(SampleApplication.JAVAFX_SAMPLE_APPLICATION.getJar());
            String output = processResult.outputUTF8();

            assertThat(output)
                    .contains("Found class 'javafx.geometry.Insets'")
                    .contains("End of main");
        }
    }

    @Nested
    class KotlinApplicationTest {
        @Test
        void kotlinApplicationRuns() {
            ProcessResult processResult = JarUtils.runJar(SampleApplication.KOTLIN_SAMPLE_APPLICATION.getJar());
            String output = processResult.outputUTF8();

            assertThat(output)
                    .contains("Hello(text=Hello from Kotlin Application)")
                    .doesNotContain("Duplicate class found")
                    .contains("class com.caco3.elijars.sample.kotlinapplication.Hello");
        }
    }

    @Nested
    class GuavaApplicationTest {
        @Test
        void applicationSuccessfullyRuns() {
            String output = JarUtils.runJar(SampleApplication.APPLICATION_WITH_GUAVA.getJar()).outputUTF8();

            assertThat(output)
                    .contains("Hello from ListenableFuture, " +
                              "my module = 'module elijars.guavaapplication', " +
                              "and the ListenableFuture's module = 'module com.google.common'");
        }
    }

    @Nested
    class ClassPathApplicationTest {
        private final SampleApplication application = SampleApplication.CLASSPATH_APPLICATION;

        @Test
        void classPathApplicationRuns() {
            String output = JarUtils.runJar(application.getJar()).outputUTF8();

            assertThat(output)
                    .contains("Hello, unnamed module");
        }

        @Test
        void applicationReadsItsOwnManifest() {
            ProcessResult processResult = JarUtils.runJar(application.getJar());
            String output = processResult.outputUTF8();

            assertThat(output)
                    .contains("Manifest[Custom-Entry] = Test entry for classpath application")
                    .doesNotContainPattern(Pattern.compile("Elijars-.*"));
        }
    }
}