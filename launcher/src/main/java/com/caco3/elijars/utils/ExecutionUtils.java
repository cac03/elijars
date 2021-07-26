package com.caco3.elijars.utils;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;

public abstract class ExecutionUtils {

    private static final String FILE = "file";

    private ExecutionUtils() {
    }

    /**
     * Get current application location.
     * <p>
     * If the application started as a jar file using {@code java -jar myjar.jar} command
     * then this method returns {@link Path} to the jar file.
     * <p>
     * TODO: if not java -jar
     *
     * @return {@link Path} to application location
     */
    public static Path getRunningApplicationLocation() {
        ProtectionDomain protectionDomain = ExecutionUtils.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL url = codeSource.getLocation();
        Assert.state(FILE.equals(url.getProtocol()), "Protocol must be '" + FILE + "'");
        return Paths.get(url.getFile());
    }
}
