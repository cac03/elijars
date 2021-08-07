package com.caco3.elijars.simpleapplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.jar.Manifest;

public class Main {
    public static void main(String[] args) {
        System.out.println(HelloWorld.create());
        Module module = Main.class.getModule();
        System.out.println("Module = '" + module + "'");
        System.out.println("Arguments: " + Arrays.toString(args));
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
