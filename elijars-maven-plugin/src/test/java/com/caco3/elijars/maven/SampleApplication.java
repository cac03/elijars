package com.caco3.elijars.maven;

import com.caco3.elijars.utils.Assert;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public enum SampleApplication {
    APPLICATION_WITH_GUAVA(
            "application-with-guava",
            "elijars.guavaapplication",
            "com.caco3.elijars.guavaapplication.GuavaApplication"
    ),
    JAVAFX_SAMPLE_APPLICATION(
            "javafx-sample-application",
            "elijars.javafx.sample.application",
            "com.caco3.elijars.javafxsample.Sample"
    ),
    KOTLIN_SAMPLE_APPLICATION(
            "kotlin-application",
            "com.caco3.elijars.samples.kotlin.application",
            "com.caco3.elijars.sample.kotlinapplication.Main"
    ),
    CLASSPATH_APPLICATION(
            "classpath-application",
            null,
            "com.caco3.elijars.samples.classpathapplication.Main"
    ),
    SAMPLE_APPLICATION(
            "sample-application",
            "elijars.simpleapplication",
            "com.caco3.elijars.simpleapplication.Main"
    );


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

    SampleApplication(String mavenModuleName, String moduleName, String mainClassName) {
        this.root = findProjectRoot()
                .resolve("elijars-samples")
                .resolve(mavenModuleName);
        this.mavenModuleName = mavenModuleName;
        this.moduleName = moduleName;
        this.mainClassName = mainClassName;
        Assert.state(Files.exists(this.root), () -> "Couldn't find project at " + this.root);
    }

    public Path getJar() {
        Path jar = root
                .resolve("target")
                .resolve(mavenModuleName + ".jar");
        Assert.state(Files.exists(jar), () -> jar + " must exist");
        return jar;
    }

    public Path getOriginalJar() {
        Path path = root.resolve("target").resolve(mavenModuleName + "-original.jar");
        Assert.state(Files.exists(path), () -> path + " must exist");
        return path;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    private final Path root;
    private final String mavenModuleName;
    private final String moduleName;
    private final String mainClassName;
}
