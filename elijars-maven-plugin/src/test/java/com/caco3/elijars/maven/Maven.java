package com.caco3.elijars.maven;

import com.caco3.elijars.utils.Assert;
import org.apache.maven.cli.MavenCli;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapped maven to provide convenient interface in tests
 */
public class Maven {
    private static final String MAVEN_MULTI_MODULE_PROJECT_DIRECTORY = "maven.multiModuleProjectDirectory";
    private static final String WORKING_DIRECTORY = "..";

    public enum Project {
        ELIJARS("elijars"),
        ELIJARS_MAVEN_PLUGIN("elijars-maven-plugin"),
        ELIJARS_SAMPLES("elijars-samples"),
        SAMPLE_APPLICATION("sample-application"),
        ELIJARS_LAUNCHER("elijars-launcher"),
        SAMPLE_APPLICATION_WITH_GUAVA("application-with-guava");

        Project(String mavenModuleName) {
            this.mavenModuleName = mavenModuleName;
        }


        public String getMavenModuleName() {
            return mavenModuleName;
        }

        private final String mavenModuleName;

    }

    private final MavenCli mavenCli = new MavenCli();
    private final InMemoryPrintStream outputStream;
    private final InMemoryPrintStream errorStream;

    private Maven(InMemoryPrintStream outputStream, InMemoryPrintStream errorStream) {
        this.outputStream = outputStream;
        this.errorStream = errorStream;
    }

    public static Maven createDefault() {
        return new Maven(InMemoryPrintStream.create(), InMemoryPrintStream.create());
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
        try (ScopedSystemProperty ignored =
                     new ScopedSystemProperty(MAVEN_MULTI_MODULE_PROJECT_DIRECTORY, WORKING_DIRECTORY)) {
            String[] arguments = appendProject(skipTests(goals), project)
                    .toArray(new String[0]);
            int returnCode = mavenCli.doMain(arguments, WORKING_DIRECTORY, outputStream, errorStream);
            if (returnCode != 0) {
                throw new IllegalStateException("Maven return codes is not zero, but = "
                                                + returnCode + ", error output "
                                                + System.lineSeparator() + errorStream.asString()
                                                + System.lineSeparator() + ", output = " + outputStream.asString());
            }
        }
    }

    private static List<String> skipTests(List<String> arguments) {
        List<String> newArguments = new ArrayList<>();
        newArguments.add("-DskipTests=true");
        newArguments.addAll(arguments);
        return newArguments;
    }

    private static List<String> appendProject(List<String> arguments, Project project) {
        if (project == Project.ELIJARS) {
            return arguments;
        }
        List<String> newArguments = new ArrayList<>();
        newArguments.add("--projects");
        newArguments.add(project.getMavenModuleName());
        newArguments.addAll(arguments);
        return newArguments;
    }
}
