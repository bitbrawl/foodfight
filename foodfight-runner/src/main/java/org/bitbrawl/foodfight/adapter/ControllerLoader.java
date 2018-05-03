package org.bitbrawl.foodfight.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public final class ControllerLoader extends ClassLoader implements AutoCloseable {

	private final Map<String, Class<?>> loadedClasses = new HashMap<>();

	public ControllerLoader(Path jar) throws MalformedURLException {
		super(parent(jar));
	}

	private static ClassLoader parent(Path jar) throws MalformedURLException {
		if (jar == null)
			throw new NullPointerException();

		URL[] urls = { jar.toUri().toURL() };
		return new URLClassLoader(urls, ControllerLoader.class.getClassLoader());
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (name == null)
			throw new NullPointerException();

		if (!ClassUtils.isCustomClass(name))
			return ControllerLoader.class.getClassLoader().loadClass(name);

		if (loadedClasses.containsKey(name))
			return loadedClasses.get(name);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassAdapter adapter = new ClassAdapter(writer);

		ClassReader reader;
		try (InputStream classStream = getParent().getResourceAsStream(ClassUtils.resourceName(name))) {
			reader = new ClassReader(classStream);
		} catch (IOException e) {
			throw new ClassNotFoundException("Unable to find class: " + name, e);
		}

		reader.accept(adapter, ClassReader.SKIP_FRAMES | ClassReader.EXPAND_FRAMES);

		byte[] result = writer.toByteArray();

		// try (PrintWriter printer = new PrintWriter(name + "_debug.txt")) {
		// ClassReader rereader = new ClassReader(result);
		// ClassVisitor trace = new TraceClassVisitor(printer);
		// rereader.accept(trace, 0);
		// } catch (FileNotFoundException e) {
		// throw new AssertionError(e);

		Class<?> finishedClass = defineClass(name, result, 0, result.length);

		if (resolve)
			resolveClass(finishedClass);

		loadedClasses.put(name, finishedClass);

		return finishedClass;

	}

	@Override
	public void close() throws IOException {
		((URLClassLoader) getParent()).close();
	}

}
