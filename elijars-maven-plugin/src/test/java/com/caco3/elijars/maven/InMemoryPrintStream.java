package com.caco3.elijars.maven;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class InMemoryPrintStream extends PrintStream {
    private InMemoryPrintStream() {
        super(new ByteArrayOutputStream(), /*autoFlush*/true, StandardCharsets.UTF_8);
    }

    public static InMemoryPrintStream create() {
        return new InMemoryPrintStream();
    }

    public String asString() {
        return getOutput().toString(StandardCharsets.UTF_8);
    }

    private ByteArrayOutputStream getOutput() {
        return ((ByteArrayOutputStream) out);
    }
}
