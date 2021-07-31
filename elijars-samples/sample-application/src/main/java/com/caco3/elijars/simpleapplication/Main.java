package com.caco3.elijars.simpleapplication;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println(HelloWorld.create());
        Module module = Main.class.getModule();
        System.out.println("Module = '" + module + "'");
        System.out.println("Arguments: " + Arrays.toString(args));
    }
}
