package com.caco3.elijars.resource;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Descriptor of a binary resource.
 * <p>
 * {@link ResourceLoader} is a primary interface to load the {@link Resource}s.
 * When the {@link ResourceLoader} is {@link ResourceLoader#close() closed} all {@link Resource}s
 * loaded by it become invalid and throw {@link IllegalStateException} on access
 * <p>
 * The primary implementation is {@link PathResource}
 * <p>
 * Inspired by {@code org.springframework.core.io.Resource}
 */
public interface Resource {
    /**
     * Get {@link InputStream} to read the resource.
     * The returned {@link InputStream} should be closed, after its use
     * The {@link InputStream} remains valid unless the {@link ResourceLoader} is closed
     *
     * @return {@link InputStream}, never {@code null}
     * @throws IllegalStateException if originating {@link ResourceLoader} is {@link ResourceLoader#close() closed}
     */
    InputStream getInputStream();

    /**
     * Get path for the current resource.
     * The {@link Path} is valid in the scope of the {@link ResourceLoader} used to load the resource.
     *
     * @return path for the resource, never {@code null}
     * @throws IllegalStateException if originating {@link ResourceLoader} is {@link ResourceLoader#close() closed}
     */
    Path getPath();
}
