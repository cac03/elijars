package com.caco3.elijars.launcher;

import com.caco3.elijars.resource.Resource;
import com.caco3.elijars.resource.ResourceLoader;
import com.caco3.elijars.utils.Assert;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

/**
 * {@link JarApplicationDefinitionSource} is a {@link ApplicationDefinitionSource} that loads
 * {@link ApplicationDefinition} from a given {@link ResourceLoader} with structure of a jar file.
 * <p>
 * That is, the source expects:
 *
 * <ol>
 *     <li>{@link #MANIFEST_PATH} to be present in the {@link ResourceLoader}</li>
 *     <li>{@code *.jar} files present in the {@link ResourceLoader}</li>
 * </ol>
 * <p>
 * The {@code *.jar} files form the {@link ApplicationDefinition#getDependencies()}.
 * <p>
 * The {@link #MANIFEST_PATH} should be a jar manifest file.
 * It must contain the following entries:
 * <ol>
 *     <li>{@link #ELIJARS_START_CLASS} - the class with the main method to run</li>
 *     <li>{@link #ELIJARS_START_MODULE} - the module containing the main class</li>
 * </ol>
 */
public class JarApplicationDefinitionSource implements ApplicationDefinitionSource {
    private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";
    private static final String ELIJARS_START_CLASS = "Elijars-Start-Class";
    private static final String ELIJARS_START_MODULE = "Elijars-Start-Module";

    private final ResourceLoader resourceLoader;

    public JarApplicationDefinitionSource(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "resourceLoader == null");
        this.resourceLoader = resourceLoader;
    }

    @Override
    public ApplicationDefinition getApplicationDefinition() {
        Manifest manifest = readManifest();
        String startClass = getStartClass(manifest);
        String startModule = getStartModule(manifest);
        List<Path> modulePath = collectModulePath();
        return ApplicationDefinition.builder()
                .dependencies(modulePath)
                .mainClassName(startClass)
                .mainModuleName(startModule)
                .build();
    }

    private Manifest readManifest() {
        try {
            return resourceLoader.loadByName(MANIFEST_PATH)
                    .orElseThrow(() -> new IllegalStateException("Cannot find '" + MANIFEST_PATH + "' in " + resourceLoader))
                    .mapInputStream(Manifest::new);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Cannot read '" + MANIFEST_PATH + "' in " + resourceLoader + " Reason:" + e.getMessage(), e);
        }
    }

    private static String getStartClass(Manifest manifest) {
        return manifest.getMainAttributes().getValue(ELIJARS_START_CLASS);
    }

    private static String getStartModule(Manifest manifest) {
        return manifest.getMainAttributes().getValue(ELIJARS_START_MODULE);
    }

    private List<Path> collectModulePath() {
        return resourceLoader
                .loadAll()
                .stream()
                .map(Resource::getPath)
                .filter(it -> it.toString().toLowerCase(Locale.US).endsWith(".jar"))
                .collect(Collectors.toList());
    }
}
