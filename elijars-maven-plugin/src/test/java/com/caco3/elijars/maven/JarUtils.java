package com.caco3.elijars.maven;

import com.caco3.elijars.utils.Assert;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

public abstract class JarUtils {
    private JarUtils() {
    }

    public static ProcessResult runJar(Path jar, String... arguments) {
        Assert.notNull(arguments, "arguments == null");
        Assert.notNull(jar, "jar == null");

        String javaHome = System.getProperty("java.home");
        Path javaExecutable = Paths.get(javaHome, "bin", "java");
        try {
            List<String> command = new ArrayList<>();
            command.add(javaExecutable.toAbsolutePath().toString());
            command.add("-jar");
            command.add(jar.toAbsolutePath().toString());
            command.addAll(Arrays.asList(arguments));
            return new ProcessExecutor()
                    .command(command)
                    .redirectErrorStream(true)
                    .readOutput(true)
                    .execute();
        } catch (IOException | InterruptedException | TimeoutException e) {
            throw new IllegalStateException(e);
        }

    }
}
