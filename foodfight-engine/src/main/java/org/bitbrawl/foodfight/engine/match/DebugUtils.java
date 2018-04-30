package org.bitbrawl.foodfight.engine.match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.controller.Clock;
import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.controller.ControllerException;
import org.bitbrawl.foodfight.controller.JavaController;
import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.logging.EngineLogger;
import org.bitbrawl.foodfight.engine.video.FrameGenerator;
import org.bitbrawl.foodfight.engine.video.ImageFrame;
import org.bitbrawl.foodfight.field.MatchType;
import org.bitbrawl.foodfight.field.Player;

public final class DebugUtils {

	private DebugUtils() {
		throw new AssertionError("This class is not instantiable");
	}

	@SafeVarargs
	public static void runDebugMatch(MatchType matchType, Class<? extends JavaController>... classes) {
		Objects.requireNonNull(classes, "classes cannot be null");
		if (classes.length <= 0)
			throw new IllegalArgumentException("classes cannot be empty");

		// TODO logger class
		FieldState field = new FieldGenerator(matchType).get();

		List<Class<? extends JavaController>> classList = Arrays.asList(classes);
		List<Class<? extends JavaController>> allClasses = new ArrayList<>(classList);
		while (allClasses.size() < matchType.getNumberOfPlayers())
			allClasses.addAll(classList);
		Collections.shuffle(allClasses, ThreadLocalRandom.current());

		Map<Character, Controller> controllers = new HashMap<>();
		for (Player player : field.getPlayers()) {
			Logger playerLogger = Logger.getAnonymousLogger();
			Clock clock = unit -> Long.MAX_VALUE;
			Controller controller;
			Class<? extends JavaController> clazz = allClasses.remove(allClasses.size() - 1);
			try {
				@SuppressWarnings("deprecation")
				Controller tempController = JavaController.newInstance(clazz, playerLogger, clock);
				controller = tempController;
			} catch (ControllerException e) {
				EngineLogger.INSTANCE.log(Level.SEVERE, "Unable to instantiate new Controller", e);
				controller = (f, t, p) -> null;
			}
			controllers.put(player.getSymbol(), controller);
		}

		FrameGenerator generator = new FrameGenerator(field, c -> controllers.get(c).getClass().getSimpleName());
		ImageFrame frame = new ImageFrame(generator.apply(field));
		Match match = new Match.Builder(0, field, controllers::get, new DefaultTurnRunner())
				.uiConsumer(state -> frame.updateImage(generator.apply(state))).build();
		MatchHistory history = match.run();

	}

}
