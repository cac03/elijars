package com.caco3.elijars.simpleapplication;

public class HelloWorld {
    private final String text;

    private HelloWorld(String text) {
        this.text = text;
    }

    public static HelloWorld create() {
        return new HelloWorld("Hello world");
    }

    @Override
    public String toString() {
        return text;
    }
}
