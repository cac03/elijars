package com.caco3.elijars.maven;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.zeroturnaround.exec.ProcessResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectWithDependenciesComposeMojoTest {
    private final Maven maven = Maven.createDefault();
    private final SampleApplication application = SampleApplication.APPLICATION_WITH_GUAVA;
    private ProcessResult processResult;

    @BeforeAll
    void setUp() {
        maven.execute(Maven.Project.ELIJARS, List.of("verify"));
        processResult = JarUtils.runJar(application.getJar());
    }

    @Test
    void applicationSuccessfullyRuns() {
        String output = processResult.outputUTF8();

        assertThat(output)
                .contains("Hello from ListenableFuture, " +
                          "my module = 'module elijars.guavaapplication', " +
                          "and the ListenableFuture's module = 'module com.google.common'");
    }
}
