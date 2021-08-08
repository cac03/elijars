package com.caco3.elijars;

import com.caco3.elijars.launcher.ApplicationDefinition;
import com.caco3.elijars.launcher.ApplicationDefinitionSource;
import com.caco3.elijars.launcher.JarApplicationDefinitionSource;
import com.caco3.elijars.launcher.Launcher;
import com.caco3.elijars.logging.Logger;
import com.caco3.elijars.resource.FileSystemResourceLoader;
import com.caco3.elijars.resource.ResourceLoader;
import com.caco3.elijars.utils.Assert;
import com.caco3.elijars.utils.ClassUtils;

public class Starter implements AutoCloseable {
    private static final Logger logger = Logger.forClass(Starter.class);

    private final ResourceLoader resourceLoader;
    private final ApplicationDefinitionSource configurationSource;

    public Starter(ResourceLoader resourceLoader, ApplicationDefinitionSource configurationSource) {
        Assert.notNull(resourceLoader, "resourceLoader == null");
        Assert.notNull(configurationSource, "configurationSource == null");
        this.configurationSource = configurationSource;
        this.resourceLoader = resourceLoader;
    }

    public static Starter createDefault() {
        FileSystemResourceLoader resourceLoader = FileSystemResourceLoader.forJar(ClassUtils.getClassLocation(Starter.class));
        ApplicationDefinitionSource configurationSource = new JarApplicationDefinitionSource(resourceLoader);
        return new Starter(resourceLoader, configurationSource);
    }

    public static void main(String[] args) throws Throwable {
        Starter starter = Starter.createDefault();
        starter.run(args);
    }

    private void run(String[] args) throws Throwable {
        ApplicationDefinition applicationDefinition = configurationSource.getApplicationDefinition();
        Launcher launcher = Launcher.create(applicationDefinition);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //noinspection EmptyTryBlock
            try (this; launcher) {
            } catch (Exception e) {
                logger.warn(() -> "Unable to close launcher = '" + launcher + "'", e);
            }
        }));
        launcher.run(args);
    }

    @Override
    public void close() {
        resourceLoader.close();
    }
}
