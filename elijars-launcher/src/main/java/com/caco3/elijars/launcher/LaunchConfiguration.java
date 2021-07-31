package com.caco3.elijars.launcher;

import com.caco3.elijars.utils.Assert;

import java.nio.file.Path;
import java.util.List;

public class LaunchConfiguration {
    private final List<Path> modulePath;
    private final String mainClassName;
    private final String mainModuleName;

    public LaunchConfiguration(List<Path> modulePath, String mainClassName, String mainModuleName) {
        Assert.notNull(modulePath, "modulePath == null");
        Assert.notNull(mainClassName, "mainClassName == null");
        Assert.notNull(mainModuleName, "mainModuleName == null");

        Assert.isTrue(!mainClassName.isBlank(), "mainClassName cannot be blank");
        Assert.isTrue(!mainModuleName.isBlank(), "mainModuleName cannot be blank");

        this.modulePath = modulePath;
        this.mainClassName = mainClassName;
        this.mainModuleName = mainModuleName;
    }

    public List<Path> getModulePath() {
        return modulePath;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public String getMainModuleName() {
        return mainModuleName;
    }
}
