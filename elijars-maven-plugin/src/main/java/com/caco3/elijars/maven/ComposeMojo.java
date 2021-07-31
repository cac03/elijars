package com.caco3.elijars.maven;

import com.caco3.elijars.utils.Assert;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.Manifest;
import org.codehaus.plexus.archiver.jar.ManifestException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Primary plugin goal.
 * It creates an executable modular fat jar. That is:
 * <ol>
 *     <li>Executable means the jar could be run as {@code java -jar myjar.jar}</li>
 *     <li>Modular means the application runs in its module described in {@code module-info.java}.
 *     Regular jars run in unnamed module</li>
 *     <li>Fat means the jar contains all dependencies required to run it</li>
 * </ol>
 * <p>
 * The plugin requires two parameters:
 *
 * <ol>
 *     <li>{@code startClass} - is the class' name with main method</li>
 *     <li>{@code startModule} - is the the name of the module with the main class</li>
 * </ol>
 */
@Mojo(name = "compose", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ComposeMojo extends AbstractMojo {
    private static final String SEPARATOR = "/";
    private static final String ELIJARS_INF = "ELIJARS-INF";
    private static final String LAUNCHER_GROUP_ID = "com.caco3";
    private static final String LAUNCHER_ARTIFACT_ID = "elijars-launcher";

    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject mavenProject;
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;
    @Parameter(required = true)
    private String startModule;
    @Parameter(required = true)
    private String startClass;
    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession mavenSession;
    @Component
    private ArtifactHandler artifactHandler;
    @Component
    private Map<String, Archiver> archivers;

    @SuppressWarnings("RedundantThrows")
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!mavenProject.getPackaging().equals("jar")) {
            getLog().debug("Skipping execution for non-jar artifacts");
            return;
        }
        composeJar();
    }

    private void composeJar() throws MojoFailureException {
        Artifact artifact = mavenProject.getArtifact();
        try {
            JarArchiver jarArchiver = getJarArchiver();
            addArtifact(jarArchiver, artifact.getFile());
            includeLauncher(jarArchiver);
            includeDependencies(jarArchiver);
            jarArchiver.addConfiguredManifest(createManifest());
            createArchiveAndKeepOriginal(artifact, jarArchiver);
        } catch (IOException | ManifestException e) {
            throw new MojoFailureException("Cannot create jar", e);
        }
    }

    private void createArchiveAndKeepOriginal(Artifact artifact, JarArchiver jarArchiver) throws IOException {
        File repackagedFile = new File(outputDirectory, artifact.getFile().getName() + ".repackaged");
        jarArchiver.setDestFile(repackagedFile);
        jarArchiver.createArchive();
        Files.move(artifact.getFile().toPath(), Paths.get(artifact.getFile().toString() + ".original"));
        Files.move(repackagedFile.toPath(), artifact.getFile().toPath());
    }

    private void includeLauncher(JarArchiver jarArchiver) {
        jarArchiver.addArchivedFileSet(getLauncherJar());
    }

    private void addArtifact(JarArchiver jarArchiver, File file) {
        jarArchiver.addFile(file, destinationNameForDependency(file));
    }

    private void includeDependencies(JarArchiver jarArchiver) {
        mavenProject.
                getArtifacts()
                .stream().map(Artifact::getFile)
                .forEach(it -> addArtifact(jarArchiver, it));
    }

    private static String destinationNameForDependency(File file) {
        Assert.notNull(file, "file == null");
        return ELIJARS_INF + SEPARATOR + file.getName();
    }

    private JarArchiver getJarArchiver() {
        JarArchiver archiver = (JarArchiver) archivers.get("jar");
        Assert.state(archiver != null, "No jar archiver, archiver = " + archivers);
        return archiver;
    }

    private Manifest createManifest() {
        Manifest manifest = new Manifest();
        try {
            manifest.addConfiguredAttribute(new Manifest.Attribute("Main-Class", "com.caco3.elijars.Starter"));
            manifest.addConfiguredAttribute(new Manifest.Attribute("Elijars-Start-Class", startClass));
            manifest.addConfiguredAttribute(new Manifest.Attribute("Elijars-Start-Module", startModule));
            return manifest;
        } catch (ManifestException e) {
            throw new IllegalStateException(e);
        }
    }

    private File getLauncherJar() {
        Artifact launcherJar = findLauncherJar();
        return new File(launcherJar.getFile() + ".jar");
    }

    private Plugin findElijarsMavenPlugin() {
        return mavenProject.getBuildPlugins()
                .stream()
                .filter(it -> "elijars-maven-plugin".equals(it.getArtifactId()) && "com.caco3".equals(it.getGroupId()))
                .findFirst()
                .orElseThrow();
    }

    private Artifact findLauncherJar() {
        Plugin plugin = findElijarsMavenPlugin();

        return mavenSession.getLocalRepository()
                .find(new DefaultArtifact(LAUNCHER_GROUP_ID, LAUNCHER_ARTIFACT_ID,
                        plugin.getVersion(), "compile", "jar", null, artifactHandler));
    }
}
