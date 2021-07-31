package com.caco3.elijars.resource;

import com.caco3.elijars.utils.Assert;
import com.caco3.elijars.utils.IoFunction;

import java.io.IOException;
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

    /**
     * Map {@link InputStream} of this {@link Resource} to {@code R}.
     * <p>
     * That is:
     * <ol>
     *     <li>Get {@link InputStream} for this {@link Resource}</li>
     *     <li>Apply given mapper to it</li>
     *     <li>Return result</li>
     * </ol>
     * <p>
     * The method closes {@link InputStream} after {@link IoFunction#apply(Object)} invocation
     * <p>
     * Example:
     * <pre>
     *     Resource resource = ...;
     *     byte[] bytes = resource.mapInputStream(inputStream -> inputStream.readAllBytes());
     *     // use bytes
     * </pre>
     *
     * @param mapper function to map {@link InputStream} to {@code R}
     * @param <R>    return type
     * @return application result of {@code mapper}
     * @throws IOException              if there was an IO error while getting {@link InputStream} or applying a {@code mapper}
     * @throws IllegalArgumentException if {@code mapper} is {@literal null}
     */
    default <R> R mapInputStream(IoFunction<? super InputStream, ? extends R> mapper) throws IOException {
        Assert.notNull(mapper, "mapper == null");
        try (InputStream inputStream = getInputStream()) {
            return mapper.apply(inputStream);
        }
    }
}
