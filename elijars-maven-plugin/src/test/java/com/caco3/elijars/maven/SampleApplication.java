package com.caco3.elijars.maven;

import com.caco3.elijars.utils.Assert;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public enum SampleApplication {
    APPLICATION_WITH_GUAVA("application-with-guava"),
    JAVAFX_SAMPLE_APPLICATION("javafx-sample-application"),
    SAMPLE_APPLICATION("sample-application");


    private static Path findProjectRoot() {
        Path startPath = Paths.get(".").toAbsolutePath();
        Path path = startPath;
        while (path != null) {
            if (path.getNameCount() > 0 && path.getName(path.getNameCount() - 1).equals(Paths.get("elijars"))) {
                return path;
            }
            path = path.getParent();
        }
        throw new IllegalStateException(
                "Unable to find elijars root project directory, tried to descend from root = '" + startPath + "'");
    }

    SampleApplication(String mavenModuleName) {
        this.root = findProjectRoot()
                .resolve("elijars-samples")
                .resolve(mavenModuleName);
        this.mavenModuleName = mavenModuleName;
        Assert.state(Files.exists(this.root), () -> "Couldn't find project at " + this.root);
    }

    public Path getJar() {
        Path jar = root
                .resolve("target")
                .resolve(mavenModuleName + "-1.0-SNAPSHOT.jar");
        Assert.state(Files.exists(jar), () -> jar + " must exist");
        return jar;
    }

    private final Path root;
    private final String mavenModuleName;
}
