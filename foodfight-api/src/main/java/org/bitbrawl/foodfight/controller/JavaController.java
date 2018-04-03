package org.bitbrawl.foodfight.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public abstract class JavaController implements Controller {

	private final Logger logger;
	private final Clock clock;

	protected JavaController() {
		logger = loggerCopy.get();
		clock = clockCopy.get();
		if (logger == null || clock == null)
			throw new IllegalStateException("The constructor of a Controller should be called only by the game engine");
	}

	public Logger getLogger() {
		return logger;
	}

	public Clock getClock() {
		return clock;
	}

	// TODO warning note
	public static JavaController newInstance(Class<? extends JavaController> clazz, Logger logger, Clock clock)
			throws ControllerException {
		assert clazz != null;
		assert logger != null;
		assert clock != null;
		assert loggerCopy.get() == null;
		assert clockCopy.get() == null;

		try {
			loggerCopy.set(logger);
			try {
				clockCopy.set(clock);
				return clazz.getConstructor().newInstance();
			} catch (NoSuchMethodException e) {
				String message = "A parameterless constructor was not found for class " + clazz.getSimpleName();
				throw new ControllerException(message, e);
			} catch (InstantiationException e) {
				throw new ControllerException(clazz.getSimpleName() + " cannot be abstract", e);
			} catch (IllegalAccessException e) {
				String message = "The constructor for " + clazz.getSimpleName() + " must be public";
				throw new ControllerException(message, e);
			} catch (InvocationTargetException e) {
				String message = "The constructor for " + clazz.getSimpleName() + " threw an exception";
				throw new ControllerException(message, e);
			} finally {
				clockCopy.set(null);
			}
		} finally {
			loggerCopy.set(null);
		}

	}

	private static final ThreadLocal<Logger> loggerCopy = new ThreadLocal<>();
	private static final ThreadLocal<Clock> clockCopy = new ThreadLocal<>();

}
