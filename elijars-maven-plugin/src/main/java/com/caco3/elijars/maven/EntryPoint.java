package com.caco3.elijars.maven;

public class EntryPoint {
    private final String moduleName;
    private final String mainClassName;

    public EntryPoint(String moduleName, String mainClassName) {
        this.moduleName = moduleName;
        this.mainClassName = mainClassName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getMainClassName() {
        return mainClassName;
    }
}
