package org.bitbrawl.foodfight.adapter;

import java.util.List;
import java.util.Objects;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public final class ClassAdapter extends ClassNode {

	public ClassAdapter(ClassWriter writer) {
		super(Opcodes.ASM5);

		cv = Objects.requireNonNull(writer, "writer cannot be null");

	}

	@Override
	public void visitEnd() {

		List<MethodNode> methodList = methods;
		for (MethodNode method : methodList)
			adaptMethod(method);

		accept(cv);

	}

	private void adaptMethod(MethodNode method) {

		for (AbstractInsnNode instruction : method.instructions.toArray())
			adaptInstruction(instruction, method.instructions);

	}

	private InsnList throwException(String illegalMethod) {

		InsnList result = new InsnList();

		String className = "org/bitbrawl/foodfight/adapter/IllegalMethodException";
		result.add(new TypeInsnNode(Opcodes.NEW, className));
		result.add(new InsnNode(Opcodes.DUP));
		result.add(new LdcInsnNode(illegalMethod));
		result.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, className, "<init>", "(Ljava/lang/String;)V", false));
		result.add(new InsnNode(Opcodes.ATHROW));

		return result;
	}

	private void adaptInstruction(AbstractInsnNode instruction, InsnList list) {

		if (instruction.getType() != AbstractInsnNode.METHOD_INSN)
			return;

		MethodInsnNode method = (MethodInsnNode) instruction;
		if (!ClassUtils.isAllowed(method.owner, method.name, method.desc))
			list.insertBefore(instruction, throwException(ClassUtils.readableName(method.owner, method.name)));

	}

}