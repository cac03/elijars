package com.caco3.elijars.launcher;

import com.caco3.elijars.classpath.ElijarsClassLoader;
import com.caco3.elijars.logging.Logger;
import com.caco3.elijars.utils.Assert;
import com.caco3.elijars.utils.DeletablePath;

import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@code Launcher} is the class real entry point of the jar.
 * <p>
 * The {@code run} method proceeds as follows:
 *
 * <h4>When {@link ApplicationDefinition#getMainModuleName()} is specified</h4>
 * <ol>
 *     <li>Collect dependencies</li>
 *     <li>Put all of them onto the module path</li>
 *     <li>Define a {@link ModuleLayer} with given module path</li>
 *     <li>Find the main module, that is, the module containing main class</li>
 *     <li>Find main class in the module</li>
 *     <li>Find {@code public static void main(String[])} method</li>
 *     <li>Invoke it</li>
 * </ol>
 *
 * <h4>When {@link ApplicationDefinition#getMainModuleName()} is not specified, that is the application is not modular</h4>
 * <ol>
 *     <li>Collect dependencies</li>
 *     <li>Create a new class loader - {@link ElijarsClassLoader}. It will load all dependency classes</li>
 *     <li>Find main class</li>
 *     <li>Find main method</li>
 *     <li>Invoke main method</li>
 * </ol>
 */
public class Launcher implements AutoCloseable {
    private static final Logger logger = Logger.forClass(Launcher.class);
    private static final ClassLoader parentClassLoader = ClassLoader.getPlatformClassLoader();

    private final ApplicationDefinition applicationDefinition;
    private final Path explodedDependenciesDirectory;

    private Launcher(ApplicationDefinition configuration) throws IOException {
        Assert.notNull(configuration, "configuration == null");

        this.applicationDefinition = configuration;
        this.explodedDependenciesDirectory = Files.createTempDirectory("elijars");
    }

    public static Launcher create(ApplicationDefinition applicationDefinition) throws IOException {
        return new Launcher(applicationDefinition);
    }

    public void run(String[] arguments) throws Throwable {
        Assert.notNull(arguments, "arguments == null");

        Module module = defineModule();
        logger.info(() -> "Defined " + module);
        Class<?> clazz = findMainClass(module);
        logger.debug(() -> "Found main class = '" + clazz + "'");
        Method mainMethod = findMainMethod(clazz);
        logger.debug(() -> "Found main method = '" + mainMethod + "'");
        Thread.currentThread().setContextClassLoader(module.getClassLoader());
        logger.info(() -> "Calling = '" + mainMethod + "' with arguments = '" + Arrays.toString(arguments) + "'");
        invokeMain(mainMethod, arguments);
        logger.info(() -> mainMethod + " returned");
    }

    private Module defineModule() throws IOException {
        if (applicationDefinition.getMainModuleName() == null) {
            return putToNewClassLoader().getUnnamedModule();
        }
        return createNewLayerAndDefineModule();
    }

    private ClassLoader putToNewClassLoader() throws IOException {
        List<Path> explodedDependencies = explode(applicationDefinition.getDependencies());
        URL[] urls = explodedDependencies.stream()
                .map(this::toUrl)
                .toArray(URL[]::new);
        logger.debug(() -> "ClassLoader urls = '" + Arrays.toString(urls) + "'");
        return new ElijarsClassLoader(urls, parentClassLoader);
    }

    private Module createNewLayerAndDefineModule() throws IOException {
        List<Path> explodedDependencies = explode(applicationDefinition.getDependencies());
        ModuleFinder moduleFinder = ModuleFinder.of(explodedDependencies.toArray(Path[]::new));
        ModuleLayer bootLayer = ModuleLayer.boot();

        List<String> rootModules = List.of(applicationDefinition.getMainModuleName());

        Configuration configuration = bootLayer.configuration()
                .resolveAndBind(moduleFinder, ModuleFinder.of(), rootModules);
        ModuleLayer moduleLayer = bootLayer.defineModulesWithOneLoader(configuration, parentClassLoader);

        return moduleLayer.findModule(applicationDefinition.getMainModuleName())
                .orElseThrow(() -> new ElijarsLaunchException(
                        "Module name = '" + applicationDefinition.getMainModuleName() + "' not found"));
    }

    private URL toUrl(Path url) {
        Assert.state(url != null, "url == null");
        try {
            return url.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<Path> explode(List<Path> paths) throws IOException {
        List<Path> explodedPaths = new ArrayList<>();
        for (Path path : paths) {
            Path explodedPath = explodedDependenciesDirectory.resolve(path.getFileName().toString());
            Files.copy(path, explodedPath);
            explodedPaths.add(explodedPath);
        }
        return explodedPaths;
    }

    private Class<?> findMainClass(Module module) {
        Assert.notNull(module, "module == null");
        ClassLoader classLoader = module.getClassLoader();
        try {
            return classLoader.loadClass(applicationDefinition.getMainClassName());
        } catch (ClassNotFoundException e) {
            throw new ElijarsLaunchException(
                    "Unable to find main class = '" + applicationDefinition.getMainClassName()
                    + "' in module = '" + applicationDefinition.getMainModuleName() + "'", e);
        }
    }

    private Method findMainMethod(Class<?> clazz) {
        try {
            Method method = clazz.getDeclaredMethod("main", String[].class);
            if (!Modifier.isStatic(method.getModifiers())) {
                throw new ElijarsLaunchException("Main method is not static in class = " + clazz);
            }
            return method;
        } catch (NoSuchMethodException e) {
            throw new ElijarsLaunchException(
                    "'public static void main(String[])' method not found, in class = " + clazz, e);
        }
    }

    private void invokeMain(Method method, String[] mainArguments) throws Throwable {
        try {
            Object[] arguments = new Object[]{mainArguments};
            method.invoke(null, arguments);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } catch (IllegalAccessException e) {
            throw new ElijarsLaunchException("Cannot call main method in class = '" + getMainClassDescription()
                                             + "'. Check if the module = '" + applicationDefinition.getMainModuleName()
                                             + "' is open", e);
        }
    }

    private String getMainClassDescription() {
        return applicationDefinition.getMainModuleName() + "/" + applicationDefinition.getMainClassName();
    }

    @Override
    public void close() throws Exception {
        //noinspection EmptyTryBlock
        try (DeletablePath ignored = DeletablePath.create(explodedDependenciesDirectory)) {
        }
    }
}
