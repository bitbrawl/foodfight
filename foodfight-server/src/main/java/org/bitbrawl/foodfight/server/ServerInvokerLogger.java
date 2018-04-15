package org.bitbrawl.foodfight.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.engine.logging.EngineLogger;

import org.apache.maven.shared.invoker.InvokerLogger;

public enum ServerInvokerLogger implements InvokerLogger {
	INSTANCE;

	@Override
	public void debug(String message) {
		logger.fine("[Maven] " + message);
	}

	@Override
	public void debug(String message, Throwable throwable) {
		logger.log(Level.FINE, "[Maven] " + message, throwable);
	}

	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	@Override
	public void info(String message) {
		logger.info("[Maven] " + message);
	}

	@Override
	public void info(String message, Throwable throwable) {
		logger.log(Level.INFO, "[Maven] " + message, throwable);
	}

	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public void warn(String message) {
		logger.warning("[Maven] " + message);
	}

	@Override
	public void warn(String message, Throwable throwable) {
		logger.log(Level.WARNING, "[Maven] " + message, throwable);
	}

	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	@Override
	public void error(String message) {
		logger.severe("[Maven] " + message);
	}

	@Override
	public void error(String message, Throwable throwable) {
		logger.log(Level.SEVERE, "[Maven] " + message, throwable);
	}

	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	@Override
	public void fatalError(String message) {
		logger.severe("[Maven] " + message);
	}

	@Override
	public void fatalError(String message, Throwable throwable) {
		logger.log(Level.SEVERE, "[Maven] " + message, throwable);
	}

	@Override
	public boolean isFatalErrorEnabled() {
		return true;
	}

	@Override
	public void setThreshold(int threshold) {
	}

	@Override
	public int getThreshold() {
		return ServerInvokerLogger.DEBUG;
	}

	private static final Logger logger = EngineLogger.INSTANCE;

}
