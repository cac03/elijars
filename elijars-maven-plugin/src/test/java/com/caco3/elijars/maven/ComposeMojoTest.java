package com.caco3.elijars.maven;

import org.junit.jupiter.api.BeforeAll;
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
    private final SampleApplication sampleApplication = SampleApplication.SAMPLE_APPLICATION;

    @BeforeAll
    void setUp() {
        maven.execute(Maven.Project.ELIJARS, List.of("verify"));
    }

    @Test
    void sampleApplicationJarCreated() {
        Path expectedJar = sampleApplication.getJar();
        Path originalJar = Paths.get(sampleApplication.getJar().toAbsolutePath() + ".original");

        assertThat(expectedJar).exists();
        assertThat(originalJar).exists();
    }

    @Test
    void jarSuccessfullyRuns() {
        ProcessResult processResult = JarUtils.runJar(sampleApplication.getJar());
        String output = processResult.outputUTF8();

        assertThat(output)
                .contains("Hello world")
                .contains("module elijars.simpleapplication");
    }
}