package org.bitbrawl.foodfight.engine.config;

import java.nio.file.Path;

public final class ControllerConfig {

	private final String name;
	private final Path jar;
	private final String mainClass;

	ControllerConfig(String name, Path jar, String mainClass) {
		this.name = name;
		this.jar = jar;
		this.mainClass = mainClass;
	}

	@SuppressWarnings("unused")
	private ControllerConfig() {
		name = null;
		jar = null;
		mainClass = null;
	}

	public String getName() {
		return name;
	}

	public Path getJar() {
		return jar;
	}

	public String getMainClass() {
		return mainClass;
	}

}
