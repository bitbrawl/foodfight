package org.bitbrawl.foodfight.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.match.DefaultTurnRunner;
import org.bitbrawl.foodfight.engine.match.FieldGenerator;
import org.bitbrawl.foodfight.engine.match.Match;
import org.bitbrawl.foodfight.engine.match.MatchHistory;
import org.bitbrawl.foodfight.engine.runner.ControllerException;
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

	@SafeVarargs
	protected static void runDebugMatch(Match.Type matchType, Class<? extends JavaController>... classes) {
		Objects.requireNonNull(classes, "classes cannot be null");
		if (classes.length <= 0)
			throw new IllegalArgumentException("classes cannot be empty");

		// TODO logger class
		Logger matchLogger = Logger.getAnonymousLogger();
		FieldState field = new FieldGenerator(matchType).get();

		List<Class<? extends JavaController>> classList = Arrays.asList(classes);
		List<Class<? extends JavaController>> allClasses = new ArrayList<>(classList);
		while (allClasses.size() < matchType.getNumberOfPlayers())
			allClasses.addAll(classList);
		Collections.shuffle(allClasses, ThreadLocalRandom.current());

		Set<Set<Controller>> controllers = new LinkedHashSet<>();
		for (int i = 0, n = matchType.getNumberOfTeams(); i < n; i++) {
			Set<Controller> team = new LinkedHashSet<>();
			for (int j = 0, m = matchType.getNumberOfPlayers() / matchType.getNumberOfTeams(); j < m; j++) {
				Logger playerLogger = Logger.getAnonymousLogger();
				Clock clock = unit -> Long.MAX_VALUE;
				Controller controller;
				Class<? extends JavaController> clazz = allClasses.remove(allClasses.size() - 1);
				try {
					controller = newInstance(clazz, playerLogger, clock);
				} catch (ControllerException e) {
					matchLogger.log(Level.SEVERE, "Unable to instantiate new Controller", e);
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
