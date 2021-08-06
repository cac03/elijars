package com.caco3.elijars.launcher;

/**
 * Source for {@link ApplicationDefinitionSource}.
 * <p>
 * It abstracts a way to load the {@link ApplicationDefinition}.
 * <p>
 * The {@link ApplicationDefinition} for example could be sourced by:
 *
 * <ol>
 *     <li>Reading {@code MANIFEST.MF} and composing module path from a jar file</li>
 *     <li>Reading arbitrary file from the classpath or module path to create the {@link ApplicationDefinition}</li>
 * </ol>
 *
 * @see JarApplicationDefinitionSource
 * @see ApplicationDefinition
 */
public interface ApplicationDefinitionSource {
    /**
     * Get the {@link ApplicationDefinition} from this source
     *
     * @return {@link ApplicationDefinition}, never {@literal null}
     */
    ApplicationDefinition getApplicationDefinition();
}
