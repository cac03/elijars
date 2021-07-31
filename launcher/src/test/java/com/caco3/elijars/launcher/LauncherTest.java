package com.caco3.elijars.launcher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LauncherTest {
    private final SampleApplicationProject sampleApplicationProject = SampleApplicationProject
            .forSourcesDirectory(Paths.get("..", "elijars-samples", "sample-application").toAbsolutePath());
    private PrintStream originalSystemOut;
    private final ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
    private final PrintStream interceptedOutput = new PrintStream(outputBytes);
    private final Path jar = sampleApplicationProject.jar();

    @BeforeEach
    void setUp() {
        originalSystemOut = System.out;
        System.setOut(interceptedOutput);
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setOut(originalSystemOut);
        sampleApplicationProject.close();
    }

    @Test
    void helloWorldPrinted() throws Throwable {
        LaunchConfiguration configuration = new LaunchConfiguration(
                List.of(jar),
                "com.caco3.elijars.simpleapplication.Main",
                "elijars.simpleapplication"
        );
        Launcher launcher = Launcher.create(configuration);
        launcher.run(new String[]{"abc"});

        assertThat(getOutput())
                .contains("Hello world")
                .contains("Module = 'module elijars.simpleapplication'")
                .contains("Arguments: [abc]");
    }

    private String getOutput() {
        interceptedOutput.flush();
        return outputBytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}