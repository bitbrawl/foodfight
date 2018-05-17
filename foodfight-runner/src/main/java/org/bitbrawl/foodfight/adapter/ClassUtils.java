package org.bitbrawl.foodfight.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;

public final class ClassUtils {

	public static String resourceName(String className) {
		return className.replace('.', '/') + ".class";
	}

	public static String readableName(String className) {
		return className.replaceAll("[/\\$]", ".");
	}

	public static String readableName(String className, String methodName) {
		return readableName(className) + '.' + methodName;
	}

	public static boolean isCustomClass(String className) {
		className = readableName(className);
		if (isBitBrawlClass(className))
			return false;
		if (isSystemClass(className))
			return false;
		return true;
	}

	private static boolean isBitBrawlClass(String className) {
		if (className.startsWith("org.bitbrawl.foodfight.sample."))
			return false;
		if (className.startsWith("org.bitbrawl.foodfighter."))
			return false;
		if (className.startsWith("org.bitbrawl."))
			return true;
		return false;
	}

	private static boolean isSystemClass(String className) {
		if (className.startsWith("java.") || className.startsWith("javax."))
			return true;
		if (className.startsWith("org.ietf.") || className.startsWith("org.omg.") || className.startsWith("org.w3c.")
				|| className.startsWith("org.xml."))
			return true;
		if (className.startsWith("sun."))
			return true;
		return false;
	}

	public static boolean isAllowed(String className, String methodName, String description) {

		if (isCustomClass(className))
			return true;

		String methodDots = readableName(className, methodName);
		if (ALLOWED_METHODS.contains(methodDots))
			return true;
		if (DISALLOWED_METHODS.contains(methodDots))
			return false;
		String classDots = readableName(className);
		if (ALLOWED_CLASSES.contains(classDots))
			return true;
		if (DISALLOWED_CLASSES.contains(classDots))
			return false;

		String packageSlashes = className.substring(0, className.lastIndexOf('/'));
		String packageDots = packageSlashes.replace('/', '.');
		return ALLOWED_PACKAGES.contains(packageDots);

	}

	private static final Set<String> generateSet(String resourceName) {

		Set<String> result = new HashSet<>();

		try (InputStream stream = ClassUtils.class.getResourceAsStream(resourceName);
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

			for (String line = reader.readLine(); line != null; line = reader.readLine())
				result.add(line);

		} catch (IOException e) {
			throw new UncheckedIOException("Unable to read file: " + resourceName, e);
		}

		return result;

	}

	private static final Set<String> ALLOWED_PACKAGES = generateSet("/allowed-packages.txt");
	private static final Set<String> ALLOWED_CLASSES = generateSet("/allowed-classes.txt");
	private static final Set<String> DISALLOWED_CLASSES = generateSet("/disallowed-classes.txt");
	private static final Set<String> ALLOWED_METHODS = generateSet("/allowed-methods.txt");
	private static final Set<String> DISALLOWED_METHODS = generateSet("/disallowed-methods.txt");

}