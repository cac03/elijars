package com.caco3.elijars.maven;

import com.caco3.elijars.utils.Assert;
import lombok.Data;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.Objects;

/**
 * Exclusion of artifact into a final jar.
 * <p>
 * This class maps {@code exclusion} xml elements of the plugin configuration.
 * E.g.:
 *
 * <pre>{@code
 * <exclusions>
 *     <exclusion>
 *         <groupId>org.projectlombok</groupId>
 *         <artifactId>lombok</artifactId>
 *     </exclusion>
 * </exclusions>
 * }</pre>
 */
@Data
public class Exclusion {
    /**
     * {@code artifactId} of the exclusion
     */
    @Parameter(required = true)
    private String artifactId;
    /**
     * {@code groupId} of the exclusion
     */
    @Parameter(required = true)
    private String groupId;

    /**
     * Checks if this {@link Exclusion} excludes the {@code artifact}.
     * <p>
     * The {@link Exclusion} excludes the artifact if its {@code groupId} and {@code artifactId} are equal to the
     * artifact's ones
     *
     * @param artifact to check match against
     * @return {@code true} if this {@link Exclusion} excludes the {@code artifact}, {@code false} otherwise
     * @throws IllegalArgumentException of {@code artifact == null}
     */
    public boolean excludes(Artifact artifact) {
        Assert.notNull(artifact, "artifact == null");
        return Objects.equals(artifact.getArtifactId(), artifactId)
               && Objects.equals(artifact.getGroupId(), groupId);
    }
}
