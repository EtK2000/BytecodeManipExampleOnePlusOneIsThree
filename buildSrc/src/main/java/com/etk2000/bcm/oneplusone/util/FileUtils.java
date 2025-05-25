package com.etk2000.bcm.oneplusone.util;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;

public class FileUtils {
	public static void deleteDirectory(@Nonnull File dir) throws IOException {
		try (Stream<Path> paths = Files.walk(dir.toPath())) {
			paths.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(File::delete);
		}
	}

	public static void extractJarToDirectory(@Nonnull File jarFile, @Nonnull File directory) throws IOException {
		try (JarFile jar = new JarFile(jarFile)) {
			final Enumeration<JarEntry> entries = jar.entries();

			while (entries.hasMoreElements()) {
				final JarEntry entry = entries.nextElement();
				final File file = new File(directory, entry.getName());

				if (entry.isDirectory())
					file.mkdirs();
				else {
					file.getParentFile().mkdirs();
					try (InputStream in = jar.getInputStream(entry); OutputStream out = new FileOutputStream(file)) {
						in.transferTo(out);
					}
				}
			}
		}
	}

	@Nonnull
	public static File findDependencyJarFromProjectPath(@Nonnull File dependencyPath) {
		/*
		 * if dependencyPath is a project in our env, find the libs folder:
		 * dependencyPath = <name>/build/classes/java/main
		 * we need to find <name>/build/libs/<name>-<version>.jar
		 */

		final File buildFolder = dependencyPath // <name>/build/classes/java/main
				.getParentFile() // <name>/build/classes/java
				.getParentFile() // <name>/build/classes
				.getParentFile(); // <name>/build

		// currently assuming it'll be the only file in said directory
		return Objects.requireNonNull(new File(buildFolder, "libs").listFiles())[0];
	}

	public static File findDependencyPath(@Nonnull Configuration config, @Nonnull String group, @Nonnull String name) {
		for (ResolvedArtifact artifact : config.getResolvedConfiguration().getResolvedArtifacts()) {
			final ModuleVersionIdentifier id = artifact.getModuleVersion().getId();

			if (group.equals(id.getGroup()) && name.equals(id.getName())) {
				config.getResolvedConfiguration().getResolvedArtifacts().remove(artifact);
				return artifact.getFile();
			}
		}
		return null;
	}

	public static void packageJarFromDirectory(@Nonnull File directory, @Nonnull File jarFile) throws IOException {
		try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile))) {
			packageJarFromDirectoryInner(directory, directory, jos);
		}
	}

	private static void packageJarFromDirectoryInner(@Nonnull File baseDir, @Nonnull File currentFile, @Nonnull JarOutputStream jos) throws IOException {
		if (currentFile.isDirectory()) {
			for (File file : Objects.requireNonNull(currentFile.listFiles()))
				packageJarFromDirectoryInner(baseDir, file, jos);
		}
		else {
			final String entryName = baseDir.toURI().relativize(currentFile.toURI()).getPath();
			jos.putNextEntry(new JarEntry(entryName));

			try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(currentFile))) {
				in.transferTo(jos);
			}
			jos.closeEntry();
		}
	}

	public static void processClassFilesInTree(
			@Nonnull File dir,
			@Nonnull ThrowingConsumer<File, IOException> processClassFileFunc
	) throws IOException {
		for (File file : Objects.requireNonNull(dir.listFiles())) {
			if (file.isDirectory())
				processClassFilesInTree(file, processClassFileFunc);

			else if (file.getName().endsWith(".class"))
				processClassFileFunc.accept(file);
		}
	}

	private FileUtils() {
	}
}