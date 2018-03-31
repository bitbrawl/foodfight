package org.bitbrawl.foodfight.engine.config;

public final class ConfigException extends Exception {

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
