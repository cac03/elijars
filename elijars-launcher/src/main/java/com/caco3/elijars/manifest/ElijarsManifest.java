package com.caco3.elijars.manifest;

import com.caco3.elijars.utils.Assert;

import java.util.jar.Manifest;

/**
 * Manifest put to launcher jar.
 * <p>
 * The manifest is usual {@code META-INF/MANIFEST.MF} file.
 * The following attributes affect the execution of launcher:
 * <ul>
 *     <li>{@link ElijarsManifest#ELIJARS_START_CLASS} - is the name of the main class. Analogous to {@code Main-Class}</li>
 *     <li>{@link ElijarsManifest#ELIJARS_START_MODULE} - is the name of the module containing main class</li>
 * </ul>
 */
public class ElijarsManifest {
    private static final String ELIJARS_START_CLASS = "Elijars-Start-Class";
    private static final String ELIJARS_START_MODULE = "Elijars-Start-Module";

    private final Manifest manifest;

    private ElijarsManifest(Manifest manifest) {
        Assert.notNull(manifest, "manifest == null");
        this.manifest = manifest;
    }

    public static ElijarsManifest of(Manifest manifest) {
        return new ElijarsManifest(manifest);
    }

    public String getStartClassName() {
        return getValue(ELIJARS_START_CLASS);
    }

    private String getValue(String name) {
        return manifest.getMainAttributes().getValue(name);
    }

    public String getStartModule() {
        return getValue(ELIJARS_START_MODULE);
    }
}
