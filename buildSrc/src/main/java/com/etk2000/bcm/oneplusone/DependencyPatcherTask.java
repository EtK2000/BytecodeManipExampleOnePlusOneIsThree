package com.etk2000.bcm.oneplusone;

import com.etk2000.bcm.oneplusone.util.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

public abstract class DependencyPatcherTask extends DefaultTask {
	public Configuration classpathConfiguration;

	@TaskAction
	public void patch() {
		if (classpathConfiguration == null)
			throw new IllegalStateException("classpathConfiguration needs to be set");

		final File dependencyPath = FileUtils.findDependencyPath(
				classpathConfiguration,
				"com.etk2000.bcm",
				"BytecodeManipExampleOnePlusOneIsThree"
		);
		if (dependencyPath == null) {
			System.out.printf(
					"Didn't find com.etk2000.bcm:BytecodeManipExampleOnePlusOneIsThree, not patching %s...\n",
					classpathConfiguration.getName()
			);
			return;
		}

		final File dependencyJar;
		if (dependencyPath.isDirectory())
			dependencyJar = FileUtils.findDependencyJarFromProjectPath(dependencyPath);
		else
			dependencyJar = dependencyPath;

		try {
			// build/tmp/BytecodeManipExampleOnePlusOneIsThree-extracted
			final File extractionDir = getProject().getLayout().getBuildDirectory()
					.dir("tmp/BytecodeManipExampleOnePlusOneIsThree-extracted").get().getAsFile();
			extractionDir.getParentFile().mkdirs();

			// build/generated/BytecodeManipExampleOnePlusOneIsThree-patched.jar
			final File patchedJar = getProject().getLayout().getBuildDirectory()
					.dir("generated/BytecodeManipExampleOnePlusOneIsThree-patched.jar").get().getAsFile();
			patchedJar.getParentFile().mkdirs();

			// extract -> patch to make everything public -> repackage -> cleanup -> update phase
			// note that we can skip extraction and directly write to the patched jar,
			// but this is easier for diagnosing problems

			FileUtils.extractJarToDirectory(dependencyJar, extractionDir);
			BytecodeManipExampleOnePlusOneIsThreeProcessor.processDirectory(extractionDir);
			FileUtils.packageJarFromDirectory(extractionDir, patchedJar);
			FileUtils.deleteDirectory(extractionDir);
			updateDependentTasksClasspath(dependencyJar, patchedJar);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	private void updateDependentTasksClasspath(@Nonnull File originalJar, @Nonnull File patchedJar) {
		// normal compiles
		getProject().getTasks().withType(JavaCompile.class).configureEach(task -> {
			task.setClasspath(
					task.getClasspath()
							.minus(getProject().files(originalJar))
							.plus(getProject().files(patchedJar))
			);
		});
		getProject().getTasks().withType(JavaExec.class).configureEach(task -> {
			task.setClasspath(
					task.getClasspath()
							.minus(getProject().files(originalJar))
							.plus(getProject().files(patchedJar))
			);
		});

		// update tests
		getProject().getTasks().withType(Test.class).configureEach(task -> {
			task.setClasspath(
					task.getClasspath()
							.minus(getProject().files(originalJar))
							.plus(getProject().files(patchedJar))
			);
		});
	}
}