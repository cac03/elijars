package com.caco3.elijars.maven;

public class ScopedSystemProperty implements AutoCloseable {
    private final String key;
    private final String oldValue;

    public ScopedSystemProperty(String key, String value) {
        this.key = key;
        this.oldValue = System.getProperty(key);

        System.setProperty(key, value);
    }

    public static ScopedSystemProperty create(String key, String value) {
        return new ScopedSystemProperty(key, value);
    }

    @Override
    public void close() {
        System.clearProperty(key);
    }
}
