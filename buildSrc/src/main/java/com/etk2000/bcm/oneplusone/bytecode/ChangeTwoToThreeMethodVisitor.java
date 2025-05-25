package com.etk2000.bcm.oneplusone.bytecode;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nonnull;

public class ChangeTwoToThreeMethodVisitor extends MethodVisitor {
	public ChangeTwoToThreeMethodVisitor(@Nonnull MethodVisitor mv) {
		super(Opcodes.ASM9, mv);
	}

	@Override
	public void visitInsn(int opcode) {
		// note that this replaces all instances of 2 with 3, which might not be what you want,
		// but it's good enough for this example
		if (opcode == Opcodes.ICONST_2)
			super.visitInsn(Opcodes.ICONST_3);
		else
			super.visitInsn(opcode);
	}
}