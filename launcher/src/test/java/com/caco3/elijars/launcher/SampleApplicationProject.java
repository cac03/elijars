package com.caco3.elijars.launcher;

import com.caco3.elijars.utils.Assert;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class SampleApplicationProject implements AutoCloseable {
    private final Path sourcesDirectory;
    private final Path workingDirectory;
    private final Path compilationOutputDirectory;
    private final Path jarOutputDirectory;
    private final Project project;


    private SampleApplicationProject(
            Path sourcesDirectory,
            Path workingDirectory,
            Path compilationOutputDirectory,
            Path jarOutputDirectory
    ) {
        this.sourcesDirectory = sourcesDirectory;
        this.workingDirectory = workingDirectory;
        this.compilationOutputDirectory = compilationOutputDirectory;
        this.jarOutputDirectory = jarOutputDirectory;
        this.project = configureProject();
    }

    public static SampleApplicationProject forSourcesDirectory(Path sourcesDirectory) {
        Assert.notNull(sourcesDirectory, "sourcesDirectory == null");
        Assert.isTrue(Files.isDirectory(sourcesDirectory), () -> "'" + sourcesDirectory + "' must be directory");

        try {
            Path workingDirectory = Files.createTempDirectory("elijars");
            Path compilationOutputDirectory = workingDirectory.resolve("build");
            Path jarOutputDirectory = workingDirectory.resolve("jar");
            Files.createDirectory(compilationOutputDirectory);
            Files.createDirectory(jarOutputDirectory);
            return new SampleApplicationProject(sourcesDirectory,
                    workingDirectory,
                    compilationOutputDirectory,
                    jarOutputDirectory
            );
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Project configureProject() {
        Project project = new Project();
        configureLogging(project);
        createCompileTarget(project);
        createJarTarget(project);
        return project;
    }

    private void createCompileTarget(Project project) {
        Target compile = new Target();
        compile.setName("compile");
        compile.setProject(project);
        Javac javac = new Javac();
        javac.setProject(project);
        javac.setSrcdir(new org.apache.tools.ant.types.Path(project, sourcesDirectory.toAbsolutePath().toString()));
        javac.setDestdir(compilationOutputDirectory.toFile());
        compile.addTask(javac);
        project.addTarget("compile", compile);
    }

    private void createJarTarget(Project project) {
        Target jarTarget = new Target();
        jarTarget.setProject(project);
        jarTarget.setDepends("compile");
        jarTarget.setName("jar");
        Jar jar = new Jar();
        jar.setProject(project);
        jar.setBasedir(compilationOutputDirectory.toAbsolutePath().toFile());
        addManifest(jar);
        jar.setDestFile(jarOutputDirectory.toAbsolutePath().resolve("sample-application.jar").toFile());
        jarTarget.addTask(jar);
        project.addTarget("jar", jarTarget);
    }

    private void addManifest(Jar jar) {
        Manifest manifest = new Manifest();
        try {
            manifest.addConfiguredAttribute(new Manifest.Attribute("Main-Class", "com.caco3.elijars.simpleapplication.Main"));
            jar.addConfiguredManifest(manifest);
        } catch (ManifestException e) {
            throw new IllegalStateException(e);
        }
    }

    private void configureLogging(Project project) {
        DefaultLogger logger = new DefaultLogger();
        logger.setErrorPrintStream(System.out);
        logger.setOutputPrintStream(System.out);
        logger.setMessageOutputLevel(Project.MSG_VERBOSE);
        project.addBuildListener(logger);
    }

    /**
     * Compile the sample project.
     * <p>
     * That is take all {@code .java} sources in {@link #sourcesDirectory} and invoke {@link Javac}.
     * The compiler writes {@code .class} files to the {@link #compilationOutputDirectory}
     */
    public void compile() {
        project.executeTarget("compile");
    }

    /**
     * Create {@code jar} file for the sample project.
     * <p>
     * That is:
     * <ol>
     *     <li>Compile project</li>
     *     <li>Prepare manifest: specify main class</li>
     *     <li>Create jar to {@link #jarOutputDirectory}</li>
     * </ol>
     *
     * @return path to created jar
     */
    public Path jar() {
        project.executeTarget("jar");
        return jarOutputDirectory.resolve("sample-application.jar");
    }

    /**
     * Delete the temporary working directory
     *
     * @throws IOException if error occurs while deleting
     */
    @Override
    public void close() throws IOException {
        Files.walkFileTree(workingDirectory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
