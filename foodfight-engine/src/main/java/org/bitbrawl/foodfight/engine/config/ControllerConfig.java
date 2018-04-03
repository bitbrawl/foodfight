package org.bitbrawl.foodfight.engine.config;

import java.nio.file.Path;

public final class ControllerConfig {

	private final Path jar;
	private final String mainClass;

	ControllerConfig(Path jar, String mainClass) {
		this.jar = jar;
		this.mainClass = mainClass;
	}

	@SuppressWarnings("unused")
	private ControllerConfig() {
		jar = null;
		mainClass = null;
	}

	public Path getJar() {
		return jar;
	}

	public String getMainClass() {
		return mainClass;
	}

}
