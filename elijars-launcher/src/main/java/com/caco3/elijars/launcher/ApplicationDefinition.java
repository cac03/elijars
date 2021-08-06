package com.caco3.elijars.launcher;

import com.caco3.elijars.utils.Assert;

import java.nio.file.Path;
import java.util.List;

public class ApplicationDefinition {
    private final List<Path> dependencies;
    private final String mainClassName;
    private final String mainModuleName;

    public ApplicationDefinition(List<Path> dependencies, String mainClassName, String mainModuleName) {
        Assert.notNull(dependencies, "modulePath == null");
        Assert.notNull(mainClassName, "mainClassName == null");
        Assert.isTrue(!mainClassName.isBlank(), "mainClassName cannot be blank");

        this.dependencies = dependencies;
        this.mainClassName = mainClassName;
        this.mainModuleName = mainModuleName;
    }

    public static ApplicationDefinitionBuilder builder() {
        return new ApplicationDefinitionBuilder();
    }

    public List<Path> getDependencies() {
        return dependencies;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public String getMainModuleName() {
        return mainModuleName;
    }
}
