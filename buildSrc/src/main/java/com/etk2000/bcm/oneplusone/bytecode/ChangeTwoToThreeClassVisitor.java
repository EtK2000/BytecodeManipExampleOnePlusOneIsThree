package com.etk2000.bcm.oneplusone.bytecode;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nonnull;

public class ChangeTwoToThreeClassVisitor extends ClassVisitor {
	public ChangeTwoToThreeClassVisitor(@Nonnull ClassWriter cw) {
		super(Opcodes.ASM9, cw);
	}

	@Override
	public MethodVisitor visitMethod(int access, @Nonnull String name, @Nonnull String descriptor, String signature, String[] exceptions) {
		// only manipulate `main(String[])`
		if ("main".equals(name) && "([Ljava/lang/String;)V".equals(descriptor))
			return new ChangeTwoToThreeMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions));

		// otherwise, skip it
		return super.visitMethod(access, name, descriptor, signature, exceptions);
	}
}