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
public class ProjectWithDependenciesComposeMojoTest {
    private static final String MAVEN_MULTI_MODULE_PROJECT_DIRECTORY = "maven.multiModuleProjectDirectory";
    private final MavenCli mavenCli = new MavenCli();

    @BeforeAll
    void setUp() {
        installPlugin();
        installLauncher();
        buildSampleApplication();
    }

    @AfterAll
    void tearDown() {
        String workingDirectory = "../elijars-samples/application-with-guava";
        try (ScopedSystemProperty property = ScopedSystemProperty.create(
                MAVEN_MULTI_MODULE_PROJECT_DIRECTORY, workingDirectory)) {
            int returnCode = mavenCli.doMain(new String[]{"clean"}, workingDirectory, System.out, System.out);
            assertThat(returnCode).isEqualTo(0);
        }
    }

    private void installPlugin() {
        String workingDirectory = ".";
        try (ScopedSystemProperty scopedSystemProperty
                     = ScopedSystemProperty.create(MAVEN_MULTI_MODULE_PROJECT_DIRECTORY, workingDirectory)) {
            int returnCode = mavenCli.doMain(new String[]{"install"}, workingDirectory, System.out, System.out);
            if (returnCode != 0) {
                throw new IllegalStateException("Cannot install plugin");
            }
        }
    }

    private void installLauncher() {
        String workingDirectory = "../launcher";
        try (ScopedSystemProperty scopedSystemProperty
                     = ScopedSystemProperty.create(MAVEN_MULTI_MODULE_PROJECT_DIRECTORY, workingDirectory)) {
            int returnCode = mavenCli.doMain(new String[]{"install"}, workingDirectory, System.out, System.out);
            if (returnCode != 0) {
                throw new IllegalStateException("Cannot install plugin");
            }
        }
    }

    private void buildSampleApplication() {
        String workingDirectory = "../elijars-samples/application-with-guava";
        try (ScopedSystemProperty property = ScopedSystemProperty.create(
                MAVEN_MULTI_MODULE_PROJECT_DIRECTORY, workingDirectory)) {
            int returnCode = mavenCli.doMain(new String[]{"-e", "package"}, workingDirectory, System.out, System.out);
            assertThat(returnCode).isEqualTo(0);
        }
    }

    @Test
    void applicationSuccessfullyRuns() {
        Path jar = Paths.get("..", "elijars-samples", "application-with-guava", "target", "application-with-guava-1.0-SNAPSHOT.jar");
        String output = runJar(jar);

        assertThat(output)
                .contains("Hello from ListenableFuture");
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
