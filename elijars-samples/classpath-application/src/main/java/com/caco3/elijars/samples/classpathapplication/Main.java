package com.caco3.elijars.samples.classpathapplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, " + Main.class.getModule());
        Manifest manifest = readManifest();
        manifest.getMainAttributes()
                .forEach((k, v) -> System.out.println("Manifest[" + k + "] = " + v));
    }

    private static Manifest readManifest() {
        ClassLoader classLoader = Main.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("META-INF/MANIFEST.MF")) {
            return new Manifest(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't find manifest", e);
        }
    }
}
