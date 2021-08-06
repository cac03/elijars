package com.caco3.elijars.launcher;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class ApplicationDefinitionBuilder {
    private List<Path> dependencies = Collections.emptyList();
    private String mainClassName;
    private String mainModuleName;

    public ApplicationDefinitionBuilder dependencies(List<Path> dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public ApplicationDefinitionBuilder mainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
        return this;
    }

    public ApplicationDefinitionBuilder mainModuleName(String mainModuleName) {
        this.mainModuleName = mainModuleName;
        return this;
    }

    public ApplicationDefinition build() {
        return new ApplicationDefinition(dependencies, mainClassName, mainModuleName);
    }
}