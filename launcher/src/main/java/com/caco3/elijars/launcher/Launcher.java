package com.caco3.elijars.launcher;

import com.caco3.elijars.utils.Assert;

import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.List;

/**
 * {@code Launcher} is the class real entry point of the jar.
 * <p>
 * The {@code run} method proceeds as follows:
 * <ol>
 *     <li>Collect module path</li>
 *     <li>Define a {@link ModuleLayer} with given module path</li>
 *     <li>Find the main module, that is, the module containing main class</li>
 *     <li>Find main class in the module</li>
 *     <li>Find {@code public static void main(String[])} method</li>
 *     <li>Invoke it</li>
 * </ol>
 */
public class Launcher {
    private final LaunchConfiguration launchConfiguration;

    private Launcher(LaunchConfiguration configuration) {
        Assert.notNull(configuration, "configuration == null");

        this.launchConfiguration = configuration;
    }

    public static Launcher create(LaunchConfiguration launchConfiguration) {
        return new Launcher(launchConfiguration);
    }

    public void run() throws Throwable {
        Module module = defineModule();
        Class<?> clazz = findMainClass(module);
        Method mainMethod = findMainMethod(clazz);
        invokeMain(mainMethod);
    }

    private Module defineModule() {
        ModuleFinder moduleFinder = ModuleFinder.of(launchConfiguration.getModulePath().toArray(Path[]::new));
        ModuleLayer bootLayer = ModuleLayer.boot();
        Configuration configuration = bootLayer.configuration()
                .resolveAndBind(moduleFinder, ModuleFinder.of(), List.of(launchConfiguration.getMainModuleName()));
        ModuleLayer moduleLayer = bootLayer.defineModulesWithOneLoader(configuration, ClassLoader.getPlatformClassLoader());
        return moduleLayer.findModule(launchConfiguration.getMainModuleName())
                .orElseThrow(() -> new ElijarsLaunchException(
                        "Module name = '" + launchConfiguration.getMainModuleName() + "' not found"));
    }

    private Class<?> findMainClass(Module module) {
        Assert.notNull(module, "module == null");
        ClassLoader classLoader = module.getClassLoader();
        try {
            return classLoader.loadClass(launchConfiguration.getMainClassName());
        } catch (ClassNotFoundException e) {
            throw new ElijarsLaunchException("Unable to find main class = '"
                                             + launchConfiguration.getMainClassName()
                                             + "' in module = '" + launchConfiguration.getMainModuleName() + "'");
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

    private void invokeMain(Method method) throws Throwable {
        try {
            Object[] arguments = new Object[]{new String[0]};
            method.invoke(null, arguments);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } catch (IllegalAccessException e) {
            throw new ElijarsLaunchException("Cannot call main method in class = '" + getMainClassDescription()
                                             + "'. Check if the module = '" + launchConfiguration.getMainModuleName()
                                             + "' is open", e);
        }
    }

    private String getMainClassDescription() {
        return launchConfiguration.getMainModuleName() + "/" + launchConfiguration.getMainClassName();
    }
}
