package com.caco3.elijars.maven;

import com.caco3.elijars.resource.Resource;
import com.caco3.elijars.resource.ResourceLoader;

import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class EntryPointResolver {
    public EntryPoint resolveEntryPoint(ResourceLoader pathResourceLoader) {
        return pathResourceLoader.loadByName("/module-info.class")
                .map(this::readModuleDescriptor)
                .map(this::toEntryPoint)
                .orElseGet(() -> readFromManifest(pathResourceLoader))
                .orElseThrow();
    }

    private Optional<EntryPoint> readFromManifest(ResourceLoader pathResourceLoader) {
        return pathResourceLoader.loadByName("/META-INF/MANIFEST.MF")
                .map(this::readManifest)
                .map(it -> new EntryPoint(null, it.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS)));
    }

    private Manifest readManifest(Resource it) {
        try {
            return it.mapInputStream(Manifest::new);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Optional<EntryPoint> toEntryPoint(ModuleDescriptor moduleDescriptor) {
        String moduleName = moduleDescriptor.name();
        return moduleDescriptor
                .mainClass()
                .map(mainClass -> new EntryPoint(moduleName, mainClass));
    }

    private ModuleDescriptor readModuleDescriptor(Resource it) {
        try {
            return it.mapInputStream(ModuleDescriptor::read);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
