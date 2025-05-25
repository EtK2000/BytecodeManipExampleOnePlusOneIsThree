package com.etk2000.bcm.oneplusone.bytecode;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nonnull;

/**
 * A {@link ClassVisitor} for capturing the current and super class names.
 */
public class SuperNameVisitor extends ClassVisitor {
	@Nonnull
	private String name = "", superName = "";

	public SuperNameVisitor() {
		super(Opcodes.ASM9);
	}

	@Nonnull
	public String getClassName() {
		return name;
	}

	@Nonnull
	public String getSuperName() {
		return superName;
	}

	@Override
	public void visit(
			int version,
			int access,
			@Nonnull String name,
			String signature,
			@Nonnull String superName,
			@Nonnull String[] interfaces
	) {
		this.name = name;
		this.superName = superName;
	}
}

