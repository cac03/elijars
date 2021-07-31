package com.caco3.elijars.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo(name = "compose")
public class ComposeMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject mavenProject;
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;

    @SuppressWarnings("RedundantThrows")
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!mavenProject.getPackaging().equals("jar")) {
            getLog().debug("Skipping execution for non-jar artifacts");
            return;
        }

        Artifact artifact = mavenProject.getArtifact();
        getLog().info("artifact.getFile() = " + artifact.getFile());
    }
}
