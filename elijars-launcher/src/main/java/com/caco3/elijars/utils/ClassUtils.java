package com.caco3.elijars.utils;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;

public abstract class ClassUtils {

    private static final String FILE = "file";

    private ClassUtils() {
    }

    /**
     * Get class location as a {@link Path}
     * <p>
     * The location depends on where the class is located.
     * If it is in a {@code jar} file, then the method returns {@link Path} to the {@code jar}
     * <p>
     * Otherwise, the method returns {@link Path} to the root directory of the classpath or module-path,
     * containing the class
     *
     * <h3>Examples</h3>
     *
     * <h4>Class is in a module-path or a classpath</h4>
     *
     * <h5>Using {@code -p} or {@code --module-path} option</h5>
     *
     * <ol>
     *     <li>Application launched as {@code java -p /path/to/module1:/path/to/module2 -m module1/mymodule1.Main}</li>
     *     <li>The {@code getClassLocation(mymodule1.Main.class)} will return {@code /path/to/module1}</li>
     *     <li>
     *         Suppose a class {@code Foo} belongs to {@code module2} and its package is {@code module2},
     *         then {@code getClassLocation(module2.Foo.class)} will return {@code /path/to/module2}
     *     </li>
     * </ol>
     *
     * <h5>Using {@code -cp} or {@code -classpath} option</h5>
     *
     * <ol>
     *     <li>Application launched as {@code java -classpath /path/to/module1:/path/to/module2 mymodule1.Main}</li>
     *     <li>The {@code getClassLocation(mymodule1.Main.class)} will return {@code /path/to/module1}</li>
     *     <li>
     *         Suppose a class {@code Foo} belongs to {@code module2} and its package is {@code module2},
     *         then {@code getClassLocation(module2.Foo.class)} will return {@code /path/to/module2}
     *     </li>
     * </ol>
     *
     * <h5>Using {@code java -jar}</h5>
     *
     * <ol>
     *     <li>Application launched as {@code java -jar /tmp/myjar.jar}</li>
     *     <li>Application's main class is {@code module1.Main}</li>
     *     <li>Then the {@code getClassLocation(module1.Main.class)} call returns {@code /tmp/myjar.jar}</li>
     * </ol>
     *
     * @param applicationClass the class to get location for
     * @return {@link Path} to application location
     * @throws IllegalArgumentException if {@code applicationClass == null}
     */
    public static Path getClassLocation(Class<?> applicationClass) {
        Assert.notNull(applicationClass, "applicationClass == null");

        ProtectionDomain protectionDomain = applicationClass.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL url = codeSource.getLocation();
        Assert.state(FILE.equals(url.getProtocol()), "Protocol must be '" + FILE + "'");
        return Paths.get(url.getFile());
    }
}
