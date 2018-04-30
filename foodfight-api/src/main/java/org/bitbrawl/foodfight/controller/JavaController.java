package org.bitbrawl.foodfight.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;

import net.jcip.annotations.ThreadSafe;

/**
 * A competitor-written controller implementation. The only abstract method that
 * a subclass needs to implement is the {@link #playAction(Field, Team, Player)}
 * method.
 * 
 * @author Finn
 */
@ThreadSafe
public abstract class JavaController implements Controller {

	private final Logger logger;
	private final Clock clock;

	/**
	 * Constructs a JavaController object, setting its logger and clock. This
	 * protected constructor can be used by subclasses in their parameterless
	 * constructors. This constructor ensures that the logger and clock are set
	 * before the subclass constructor runs.
	 */
	protected JavaController() {
		logger = loggerCopy.get();
		clock = clockCopy.get();
		if (logger == null || clock == null)
			throw new IllegalStateException("The constructor of a Controller should be called only by the game engine");
	}

	/**
	 * Gets the logger that this controller can use to log messages. A
	 * competitor should not use {@link System#out} for this purpose, as that
	 * could interfere with communication with the game engine.
	 * 
	 * @return a logger that can be used by this controller to log messages
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Gets the clock that this controller can use to determine how much time it
	 * has left to make its moves. Note that use of this method is optional; if
	 * a controller's algorithm is not time-intensive, it may not have a need
	 * for this method.
	 * 
	 * @return a clock that can be used by this controller to time itself
	 */
	public Clock getClock() {
		return clock;
	}

	/**
	 * Constructs a new instance of a JavaController, using the given class,
	 * logger, and clock. <b>This method should not be called by
	 * controllers</b>; it exists only to be internally called by the game
	 * engine. The class passed to this method must not be abstract, and must
	 * have a public parameterless constructor.
	 * 
	 * @param clazz
	 *            the class of the new controller
	 * @param logger
	 *            the logger that the new controller will use
	 * @param clock
	 *            the clock that the new controller will use
	 * @return the newly-constructed controller
	 * @throws ControllerException
	 *             if there was a problem with the controller class
	 */
	@Deprecated
	public static JavaController newInstance(Class<? extends JavaController> clazz, Logger logger, Clock clock)
			throws ControllerException {
		assert clazz != null;
		assert logger != null;
		assert clock != null;
		assert loggerCopy.get() == null;
		assert clockCopy.get() == null;

		loggerCopy.set(logger);
		try {
			clockCopy.set(clock);
			try {
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
