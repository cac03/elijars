package com.caco3.elijars;

import com.caco3.elijars.launcher.ElijarsLaunchException;
import com.caco3.elijars.launcher.LaunchConfiguration;
import com.caco3.elijars.launcher.Launcher;
import com.caco3.elijars.manifest.ElijarsManifest;
import com.caco3.elijars.resource.JarResourceLoader;
import com.caco3.elijars.resource.Resource;
import com.caco3.elijars.resource.ResourceLoader;
import com.caco3.elijars.utils.Assert;
import com.caco3.elijars.utils.ExecutionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

public class Starter implements AutoCloseable {

    private final ResourceLoader resourceLoader;

    public Starter(ResourceLoader resourceLoader) {
        Assert.notNull(resourceLoader, "resourceLoader == null");
        this.resourceLoader = resourceLoader;
    }

    public static Starter createDefault() {
        return new Starter(JarResourceLoader.forPath(ExecutionUtils.getRunningApplicationLocation()));
    }

    public static void main(String[] args) throws Throwable {
        try (Starter starter = Starter.createDefault()) {
            starter.run(args);
        }
    }

    private void run(String[] args) throws Throwable {
        try {
            List<Path> modulePath = createModulePath();
            LaunchConfiguration launchConfiguration = readConfiguration(modulePath);
            Launcher.create(launchConfiguration).run(args);
        } catch (IOException e) {
            throw new ElijarsLaunchException("Got IOException while reading manifest", e);
        }
    }

    private LaunchConfiguration readConfiguration(List<Path> modulePath) throws IOException {
        Manifest manifest = resourceLoader.loadByName("META-INF/MANIFEST.MF")
                .orElseThrow()
                .mapInputStream(this::readManifest);
        ElijarsManifest elijarsManifest = ElijarsManifest.of(manifest);

        return new LaunchConfiguration(modulePath, elijarsManifest.getStartClassName(), elijarsManifest.getStartModule());
    }

    private List<Path> createModulePath() {
        return resourceLoader.loadAll()
                .stream()
                .map(Resource::getPath)
                .filter(path -> path.toString().endsWith(".jar"))
                .collect(Collectors.toList());
    }

    private Manifest readManifest(InputStream it) throws IOException {
        Manifest manifest = new Manifest();
        manifest.read(it);
        return manifest;
    }

    @Override
    public void close() {
        resourceLoader.close();
    }
}
