package com.caco3.elijars.maven;

import com.caco3.elijars.utils.Assert;
import org.apache.maven.cli.MavenCli;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapped maven to provide convenient interface in tests
 */
public class Maven {
    private static final String MAVEN_MULTI_MODULE_PROJECT_DIRECTORY = "maven.multiModuleProjectDirectory";
    private static final String RECURSIVE_TEST_RUN_PROFILE = "recursive-test-run";

    public enum Project {
        ELIJARS(".."),
        ELIJARS_MAVEN_PLUGIN("."),
        SAMPLE_APPLICATION("../elijars-samples/sample-application"),
        ELIJARS_LAUNCHER("../elijars-launcher"),
        SAMPLE_APPLICATION_WITH_GUAVA("../elijars-samples/application-with-guava");

        Project(String workingDirectory) {
            this.workingDirectory = workingDirectory;
        }

        private final String workingDirectory;

        public String getWorkingDirectory() {
            return workingDirectory;
        }
    }

    private final MavenCli mavenCli = new MavenCli();
    private final PrintStream outputStream;
    private final PrintStream errorStream;

    private Maven(PrintStream outputStream, PrintStream errorStream) {
        this.outputStream = outputStream;
        this.errorStream = errorStream;
    }

    public static Maven createDefault() {
        return new Maven(System.out, System.out);
    }

    /**
     * Execute given list of goals against {@link Project}
     *
     * @param project to execute against
     * @param goals   to execute
     */
    public void execute(Project project, List<String> goals) {
        Assert.notNull(project, "project == null");
        Assert.notNull(goals, "goals == null");
        String workingDirectory = project.getWorkingDirectory();
        try (ScopedSystemProperty ignored =
                     new ScopedSystemProperty(MAVEN_MULTI_MODULE_PROJECT_DIRECTORY, workingDirectory)) {
            int returnCode = mavenCli.doMain(appendProfile(goals).toArray(new String[0]), workingDirectory, outputStream, errorStream);
            if (returnCode != 0) {
                throw new IllegalStateException("Maven return codes is not zero, but = " + returnCode);
            }
        }
    }

    private static List<String> appendProfile(List<String> arguments) {
        List<String> newArguments = new ArrayList<>();
        newArguments.add("-P");
        newArguments.add(RECURSIVE_TEST_RUN_PROFILE);
        newArguments.addAll(arguments);
        return newArguments;
    }
}
