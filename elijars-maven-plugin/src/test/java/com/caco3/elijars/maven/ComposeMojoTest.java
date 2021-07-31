package com.caco3.elijars.maven;

import org.apache.maven.cli.MavenCli;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ComposeMojoTest {
    private static final String MAVEN_MULTI_MODULE_PROJECT_DIRECTORY = "maven.multiModuleProjectDirectory";
    private final MavenCli mavenCli = new MavenCli();

    @BeforeAll
    void setUp() {
        clean();
        installProject();
    }

    @AfterAll
    void tearDown() {
        clean();
    }

    private void clean() {
        String workingDirectory = "../";
        try (ScopedSystemProperty property = ScopedSystemProperty.create(
                MAVEN_MULTI_MODULE_PROJECT_DIRECTORY, workingDirectory)) {
            int returnCode = mavenCli.doMain(new String[]{"clean"}, workingDirectory, System.out, System.out);
            assertThat(returnCode).isEqualTo(0);
        }
    }

    private void installProject() {
        String workingDirectory = "../";
        try (ScopedSystemProperty scopedSystemProperty
                     = ScopedSystemProperty.create(MAVEN_MULTI_MODULE_PROJECT_DIRECTORY, workingDirectory)) {
            int returnCode = mavenCli.doMain(new String[]{"install"}, workingDirectory, System.out, System.out);
            if (returnCode != 0) {
                throw new IllegalStateException("Cannot install plugin");
            }
        }
    }

    @Test
    void sampleApplicationJarCreated() {
        String workingDirectory = "../elijars-samples/sample-application";
        Path expectedJar = Paths.get(workingDirectory, "target", "sample-application-1.0-SNAPSHOT.jar");
        Path originalJar = Paths.get(workingDirectory, "target", "sample-application-1.0-SNAPSHOT.jar.original");

        assertThat(expectedJar).exists();
        assertThat(originalJar).exists();
    }

    @Test
    void jarSuccessfullyRuns() {
        String workingDirectory = "../elijars-samples/sample-application";
        Path expectedJar = Paths.get(workingDirectory, "target", "sample-application-1.0-SNAPSHOT.jar");

        String output = runJar(expectedJar);

        assertThat(output)
                .contains("Hello world")
                .contains("module elijars.simpleapplication");
    }

    private String runJar(Path jar) {
        String javaHome = System.getProperty("java.home");
        Path javaExecutable = Paths.get(javaHome, "bin", "java");
        try {
            ProcessResult processResult = new ProcessExecutor()
                    .command(javaExecutable.toAbsolutePath().toString(), "-jar", jar.toAbsolutePath().toString())
                    .redirectErrorStream(true)
                    .readOutput(true)
                    .execute();
            return processResult.outputUTF8();
        } catch (IOException | InterruptedException | TimeoutException e) {
            throw new IllegalStateException(e);
        }
    }
}