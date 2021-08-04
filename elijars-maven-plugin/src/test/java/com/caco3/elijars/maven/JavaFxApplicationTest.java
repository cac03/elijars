package com.caco3.elijars.maven;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.zeroturnaround.exec.ProcessResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JavaFxApplicationTest {
    private final Maven maven = Maven.createDefault();
    private final SampleApplication application = SampleApplication.JAVAFX_SAMPLE_APPLICATION;

    @BeforeAll
    void setUp() {
        maven.execute(Maven.Project.ELIJARS, List.of("verify"));
    }

    @Test
    void javaFxApplicationRuns() {
        ProcessResult processResult = JarUtils.runJar(application.getJar());
        String output = processResult.outputUTF8();

        assertThat(output)
                .contains("Found class 'javafx.geometry.Insets'")
                .contains("End of main");
    }
}
