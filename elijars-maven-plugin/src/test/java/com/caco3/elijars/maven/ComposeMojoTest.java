package com.caco3.elijars.maven;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.zeroturnaround.exec.ProcessResult;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ComposeMojoTest {
    private final Maven maven = Maven.createDefault();

    @BeforeAll
    void setUp() {
        maven.execute(Maven.Project.ELIJARS, List.of("clean", "verify"));
    }

    @Nested
    class SampleApplicationTest {
        @Test
        void sampleApplicationJarCreated() {
            SampleApplication application = SampleApplication.SAMPLE_APPLICATION;
            Path expectedJar = application.getJar();
            Path originalJar = Paths.get(application.getJar().toAbsolutePath() + ".original");

            assertThat(expectedJar).exists();
            assertThat(originalJar).exists();
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
        @Test
        void classPathApplicationRuns() {
            String output = JarUtils.runJar(SampleApplication.CLASSPATH_APPLICATION.getJar()).outputUTF8();

            assertThat(output)
                    .contains("Hello, unnamed module");
        }
    }
}