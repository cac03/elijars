package com.caco3.elijars.maven;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectWithDependenciesComposeMojoTest {
    private final Maven maven = Maven.createDefault();
    private final SampleApplication application = SampleApplication.APPLICATION_WITH_GUAVA;

    @BeforeAll
    void setUp() {
        maven.execute(Maven.Project.ELIJARS, List.of("verify"));
    }

    @Test
    void applicationSuccessfullyRuns() {
        Path jar = application.getJar();

        ProcessResult processResult = JarUtils.runJar(jar);
        String output = processResult.outputUTF8();

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
