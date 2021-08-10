package com.caco3.elijars.maven;

import com.caco3.elijars.resource.FileSystemResourceLoader;
import com.caco3.elijars.resource.Resource;
import com.caco3.elijars.resource.ResourceLoader;
import com.caco3.elijars.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.nio.file.Path;
import java.util.Optional;

public class EntryPointResolver {
    private static final Logger logger = LoggerFactory.getLogger(EntryPointResolver.class);

    public EntryPoint resolveEntryPoint(Path jar) {
        Assert.notNull(jar, "jar == null");
        try (FileSystemResourceLoader resourceLoader = FileSystemResourceLoader.forJar(jar)) {
            Resource resource = resourceLoader.loadByName("/module-info.class")
                    .orElseThrow();
            ModuleDescriptor moduleDescriptor = resource.mapInputStream(ModuleDescriptor::read);
            String moduleName = moduleDescriptor.name();
            String mainClassName = moduleDescriptor.mainClass().orElseThrow();
            return new EntryPoint(moduleName, mainClassName);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public EntryPoint resolveEntryPoint(ResourceLoader pathResourceLoader) {
        return pathResourceLoader.loadByName("/module-info.class")
                .map(this::readModuleDescriptor)
                .flatMap(this::toEntryPoint)
                .orElseThrow();
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
