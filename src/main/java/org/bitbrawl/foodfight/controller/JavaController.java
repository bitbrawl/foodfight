package org.bitbrawl.foodfight.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.match.DefaultTurnRunner;
import org.bitbrawl.foodfight.engine.match.FieldGenerator;
import org.bitbrawl.foodfight.engine.match.Match;
import org.bitbrawl.foodfight.engine.match.MatchHistory;
import org.bitbrawl.foodfight.engine.video.FrameGenerator;
import org.bitbrawl.foodfight.engine.video.ImageFrame;

import com.google.gson.GsonBuilder;

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

	protected static void runDebugMatch(Class<? extends JavaController> clazz) {

		Logger matchLogger = Logger.getAnonymousLogger();

		FieldState field = new FieldGenerator(Match.Type.TEAM).get();
		Set<Set<Controller>> controllers = new HashSet<>();
		for (int i = 0; i < 2; i++) {
			Set<Controller> team = new HashSet<>();
			for (int j = 0; j < 2; j++) {
				Logger playerLogger = Logger.getAnonymousLogger();
				Clock clock = unit -> Long.MAX_VALUE;
				Controller controller;
				try {
					controller = newInstance(clazz, playerLogger, clock);
				} catch (NoSuchMethodException e) {
					matchLogger.log(Level.SEVERE, "A parameterless constructor was not found", e);
					controller = (f, t, p) -> null;
				} catch (InstantiationException e) {
					matchLogger.log(Level.SEVERE, "Your controller class cannot be abstract", e);
					controller = (f, t, p) -> null;
				} catch (IllegalAccessException e) {
					matchLogger.log(Level.SEVERE, "Your constructor must be public", e);
					controller = (f, t, p) -> null;
				} catch (InvocationTargetException e) {
					matchLogger.log(Level.SEVERE, "Your constructor threw an exception", e);
					controller = (f, t, p) -> null;
				}
				team.add(controller);
			}
			controllers.add(team);
		}

		FrameGenerator generator = new FrameGenerator(field);
		ImageFrame frame = new ImageFrame(generator.apply(field));
		MatchHistory history = new Match(field, controllers, new DefaultTurnRunner(matchLogger),
				state -> frame.updateImage(generator.apply(state))).run();

		matchLogger.info(new GsonBuilder().setPrettyPrinting().create().toJson(history));

	}

	static JavaController newInstance(Class<? extends JavaController> clazz, Logger logger, Clock clock)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
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
