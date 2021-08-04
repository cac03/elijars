package com.caco3.elijars.maven;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

public abstract class JarUtils {
    private JarUtils() {
    }

    public static ProcessResult runJar(Path jar) {
        String javaHome = System.getProperty("java.home");
        Path javaExecutable = Paths.get(javaHome, "bin", "java");
        try {
            return new ProcessExecutor()
                    .command(javaExecutable.toAbsolutePath().toString(), "-jar", jar.toAbsolutePath().toString())
                    .redirectErrorStream(true)
                    .readOutput(true)
                    .execute();
        } catch (IOException | InterruptedException | TimeoutException e) {
            throw new IllegalStateException(e);
        }

    }
}
