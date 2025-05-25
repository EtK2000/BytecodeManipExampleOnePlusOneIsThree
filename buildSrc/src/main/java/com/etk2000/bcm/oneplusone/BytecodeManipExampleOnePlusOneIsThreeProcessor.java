package com.etk2000.bcm.oneplusone;

import com.etk2000.bcm.oneplusone.bytecode.ChangeTwoToThreeClassVisitor;
import com.etk2000.bcm.oneplusone.bytecode.SuperNameVisitor;
import com.etk2000.bcm.oneplusone.util.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class BytecodeManipExampleOnePlusOneIsThreeProcessor {
	private static void processClassFile(@Nonnull File classFile) throws IOException {
		final ClassReader cr;
		try (FileInputStream fis = new FileInputStream(classFile)) {
			cr = new ClassReader(fis.readAllBytes());
		}

		final SuperNameVisitor scanner = new SuperNameVisitor();
		cr.accept(scanner, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

		// only patch the specific file
		if ("com/etk2000/bcm/oneplusone/Base".equals(scanner.getClassName())) {

			final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			cr.accept(new ChangeTwoToThreeClassVisitor(cw), 0);

			try (FileOutputStream fos = new FileOutputStream(classFile)) {
				fos.write(cw.toByteArray());
			}
		}
	}

	public static void processDirectory(@Nonnull File directory) throws IOException {
		FileUtils.processClassFilesInTree(directory, BytecodeManipExampleOnePlusOneIsThreeProcessor::processClassFile);
	}

	private BytecodeManipExampleOnePlusOneIsThreeProcessor() {
	}
}