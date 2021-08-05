package com.caco3.elijars.launcher;

/**
 * Source for {@link LaunchConfigurationSource}.
 * <p>
 * It abstracts a way to load the {@link LaunchConfiguration}.
 * <p>
 * The {@link LaunchConfiguration} for example could be sourced by:
 *
 * <ol>
 *     <li>Reading {@code MANIFEST.MF} and composing module path from a jar file</li>
 *     <li>Reading arbitrary file from the classpath or module path to create the {@link LaunchConfiguration}</li>
 * </ol>
 *
 * @see JarLaunchConfigurationSource
 * @see LaunchConfiguration
 */
public interface LaunchConfigurationSource {
    /**
     * Get the {@link LaunchConfiguration} from this source
     *
     * @return {@link LaunchConfiguration}, never {@literal null}
     */
    LaunchConfiguration getConfiguration();
}
