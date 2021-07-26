package com.caco3.elijars.resource;

import com.caco3.elijars.utils.Assert;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JarResourceLoader implements ResourceLoader {
    private final FileSystem fileSystem;

    private JarResourceLoader(FileSystem fileSystem) {
        Assert.notNull(fileSystem, "fileSystem == null");

        this.fileSystem = fileSystem;
    }

    public static JarResourceLoader forPath(Path path) {
        Assert.notNull(path, "path == null");
        Assert.isTrue(path.toString().endsWith(".jar"), () -> "path = '" + path + "' must be a .jar file");
        try {
            return new JarResourceLoader(FileSystems.newFileSystem(new URI("jar:" + path.toUri()), Collections.emptyMap()));
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }


    @Override
    public Optional<Resource> loadByName(String name) {
        throwIfClosed();
        Path path = fileSystem.getPath(name);
        if (Files.exists(path)) {
            return Optional.of(new PathResource(path));
        }
        return Optional.empty();
    }

    private void throwIfClosed() {
        if (fileSystem.isOpen()) {
            return;
        }
        throw new IllegalStateException(this + " is closed");
    }

    @Override
    public List<Resource> loadAll() {
        throwIfClosed();
        try (Stream<Path> stream = Files.walk(fileSystem.getPath("/"))) {
            return stream
                    .map(PathResource::new)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() {
        try {
            fileSystem.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return "JarResourceLoader{" +
               "fileSystem=" + fileSystem +
               '}';
    }
}
