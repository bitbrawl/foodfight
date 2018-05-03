package org.bitbrawl.foodfight.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;

public final class ClassUtils {

	private static String dotsToSlashes(String className) {
		return className.replace('.', '/');
	}

	private static String slashesToDots(String className) {
		return className.replace('/', '.');
	}

	public static String resourceName(String className) {
		return dotsToSlashes(className) + ".class";
	}

	public static String readableName(String className, String methodName) {
		return slashesToDots(className) + '.' + methodName;
	}

	private static String packageName(String className) {
		return className.substring(0, className.lastIndexOf('.'));
	}

	public static boolean isCustomClass(String className) {
		className = dotsToSlashes(className);
		if (isBitBrawlClass(className))
			return false;
		if (isSystemClass(className))
			return false;
		return true;
	}

	public static boolean isBitBrawlClass(String className) {
		className = dotsToSlashes(className);
		return className.startsWith("org/bitbrawl/") && !className.startsWith("org/bitbrawl/foodfight/sample/");
	}

	private static boolean isSystemClass(String className) {
		if (isBitBrawlClass(className))
			return false;
		if (className.startsWith("java/") || className.startsWith("javax/"))
			return true;
		if (className.startsWith("org/ietf/") || className.startsWith("org/omg/") || className.startsWith("org/w3c/")
				|| className.startsWith("org/xml/"))
			return true;
		if (className.startsWith("sun/"))
			return true;
		return false;
	}

	public static boolean isAllowed(String className, String methodName, String description) {

		String dotsClassName = slashesToDots(className);
		if (!ALLOWED_PACKAGES.contains(packageName(dotsClassName)))
			return false;
		if (DISALLOWED_CLASSES.contains(dotsClassName))
			return false;
		if (DISALLOWED_METHODS.contains(dotsClassName + '.' + methodName))
			return false;
		return true;
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

	private static final Set<String> ALLOWED_PACKAGES = generateSet("/allowed_packages.txt");
	private static final Set<String> DISALLOWED_CLASSES = generateSet("/disallowed_classes.txt");
	private static final Set<String> DISALLOWED_METHODS = generateSet("/disallowed_methods.txt");

}