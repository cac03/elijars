package com.caco3.elijars.resource;

import java.util.List;
import java.util.Optional;

/**
 * {@link ResourceLoader} loads resources available to it.
 * <p>
 * For example {@link ResourceLoader} can encapsulate a {@link java.nio.file.FileSystem}.
 * <p>
 * The {@link ResourceLoader} should be properly {@link #close() closed} after its usage.
 * All {@link Resource}s become invalid after the {@link ResourceLoader} {@link #close() closed}
 * <p>
 * The primary implementation is {@link JarResourceLoader}
 */
public interface ResourceLoader extends AutoCloseable {

    /**
     * Load the resource by given name.
     * The {@link Resource} resolution depends on the implementation
     *
     * @param name of the resource to load
     * @return resource if it is found, or {@link Optional#empty()} otherwise
     */
    Optional<Resource> loadByName(String name);

    /**
     * Load all resources available in this {@link ResourceLoader}
     *
     * @return list of all {@link Resource}s
     */
    List<Resource> loadAll();

    /**
     * Close this {@link ResourceLoader} and release all resources held.
     * <p>
     * All {@link Resource}s previously loaded by this {@link ResourceLoader} become invalid
     */
    @Override
    void close();
}
