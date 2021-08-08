package com.caco3.elijars.simpleapplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.jar.Manifest;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalStateException("No arguments");
        }
        String action = args[0];
        if (action.equals("read-manifest")) {
            Manifest manifest = readManifest();
            manifest.getMainAttributes()
                    .forEach((k, v) -> System.out.println("Manifest[" + k + "] = " + v));
        } else if (action.equals("hello-modular-world")) {
            System.out.println(HelloWorld.create());
            Module module = Main.class.getModule();
            System.out.println("Arguments: " + Arrays.toString(args));
            System.out.println("Module = '" + module + "'");
        } else {
            throw new IllegalStateException("Unknown action = '" + action + "'");
        }
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
