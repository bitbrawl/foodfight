package org.bitbrawl.foodfight.adapter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * Very slightly modified to use a different class loader.
 * 
 * @author Finn
 */
public final class JarClassWriter extends ClassWriter {

	private final ClassLoader loader;

	public JarClassWriter(ClassReader classReader, ClassLoader loader) {
		super(classReader, COMPUTE_FRAMES);
		this.loader = loader;
	}

	@Override
	protected String getCommonSuperClass(final String type1, final String type2) {
		Class<?> class1;
		try {
			class1 = Class.forName(type1.replace('/', '.'), false, loader);
		} catch (Exception e) {
			throw new TypeNotPresentException(type1, e);
		}
		Class<?> class2;
		try {
			class2 = Class.forName(type2.replace('/', '.'), false, loader);
		} catch (Exception e) {
			throw new TypeNotPresentException(type2, e);
		}
		if (class1.isAssignableFrom(class2)) {
			return type1;
		}
		if (class2.isAssignableFrom(class1)) {
			return type2;
		}
		if (class1.isInterface() || class2.isInterface()) {
			return "java/lang/Object";
		} else {
			do {
				class1 = class1.getSuperclass();
			} while (!class1.isAssignableFrom(class2));
			return class1.getName().replace('.', '/');
		}
	}

}
