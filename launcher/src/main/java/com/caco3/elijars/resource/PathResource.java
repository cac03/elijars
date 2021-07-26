package com.caco3.elijars.resource;

import com.caco3.elijars.utils.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class PathResource implements Resource {
    private final Path path;

    public PathResource(Path path) {
        Assert.notNull(path, "path == null");
        Assert.isTrue(Files.exists(path), () -> "path = '" + path + "' must exist");

        this.path = path;
    }

    @Override
    public InputStream getInputStream() {
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Path getPath() {
        return path;
    }
}
