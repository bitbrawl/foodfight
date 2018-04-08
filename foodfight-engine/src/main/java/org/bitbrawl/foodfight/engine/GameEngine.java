package org.bitbrawl.foodfight.engine;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.engine.config.ConfigException;
import org.bitbrawl.foodfight.engine.config.Configuration;
import org.bitbrawl.foodfight.engine.config.ControllerConfig;
import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.field.InventoryState;
import org.bitbrawl.foodfight.engine.field.ScoreState;
import org.bitbrawl.foodfight.engine.logging.EngineLogger;
import org.bitbrawl.foodfight.engine.match.DefaultTurnRunner;
import org.bitbrawl.foodfight.engine.match.FieldGenerator;
import org.bitbrawl.foodfight.engine.match.JarController;
import org.bitbrawl.foodfight.engine.match.Match;
import org.bitbrawl.foodfight.engine.match.MatchHistory;
import org.bitbrawl.foodfight.engine.video.FrameGenerator;
import org.bitbrawl.foodfight.engine.video.ImageEncoder;
import org.bitbrawl.foodfight.field.MatchType;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public enum GameEngine {

	INSTANCE;

	public void runMatches() throws InterruptedException {

		Configuration config;

		try {
			config = Configuration.getConfig(Paths.get("config.json"));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Unable to read configuration file", e);
			return;
		} catch (ConfigException e) {
			logger.log(Level.SEVERE, "Invalid configuration file", e);
			return;
		}

		Path data = config.getData();
		try {
			Files.createDirectories(data);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Unable to create data folder", e);
			return;
		}

		int matchNumber;
		try {
			matchNumber = getNextMatchNumber(config.getData());
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Unable to determine next match number", e);
			return;
		}

		for (int i = 0, n = config.getNumMatches(); i < n; i++) {
			runMatch(matchNumber, config);
			matchNumber++;
		}

		logger.info("Finished");

	}

	private static int getNextMatchNumber(Path data) throws IOException {
		assert data != null;
		int prefixLength = "match-".length();
		int lastMatch = -1;
		for (Path path : Files.newDirectoryStream(data, "match-*")) {
			if (!Files.isDirectory(path))
				continue;
			String dirName = path.getFileName().toString();
			if (dirName.length() != prefixLength + 6)
				continue;
			int matchNumber;
			try {
				matchNumber = Integer.parseInt(dirName.substring(prefixLength), 16);
			} catch (NumberFormatException ignore) {
				continue;
			}
			if (matchNumber > lastMatch)
				lastMatch = matchNumber;
		}
		return lastMatch + 1;
	}

	public void runMatch(int matchNumber, Configuration config) throws InterruptedException {

		String matchName = String.format("match-%06x", matchNumber);

		logger.log(Level.INFO, "Setting up {0}", matchName);

		MatchType matchType = config.getMatchType();
		Path matchData = config.getData().resolve(matchName);
		try {
			Files.createDirectories(matchData);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Unable to create match data directory", e);
			return;
		}
		Queue<ControllerConfig> controllerConfigs = orderRandomly(config.getControllers(),
				matchType.getNumberOfPlayers());

		FieldState field = new FieldGenerator(matchType).get();

		Map<Character, Controller> controllers = new HashMap<>();
		Map<Character, String> names = new HashMap<>();
		Collection<JarController> jarsToClose = new LinkedList<>();
		try {
			for (Player player : field.getPlayers()) {
				char symbol = player.getSymbol();
				ControllerConfig controllerConfig = controllerConfigs.remove();
				names.put(symbol, controllerConfig.getName());
				Path jar = controllerConfig.getJar();
				String className = controllerConfig.getMainClass();
				Path log = matchData.resolve("player-" + symbol + ".log");
				Controller controller;
				try {
					JarController jarController = new JarController(jar, className, log);
					jarsToClose.add(jarController);
					controller = jarController;
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Unable to create controller", e);
					controller = (f, t, p) -> null;
				}
				controllers.put(symbol, controller);
			}

			FrameGenerator generator = new FrameGenerator(field, names::get);
			Consumer<FieldState> videoConsumer;
			ImageEncoder encoder = null;
			try {
				encoder = new ImageEncoder(matchData.resolve("video.mp4"));
				ImageEncoder encCopy = encoder;
				videoConsumer = state -> encCopy.encode(generator.apply(state));
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Unable to generate video file", e);
				videoConsumer = state -> {
				};
			}

			logger.info("Running match");
			// TODO move video generation later

			MatchHistory history;
			try {
				Match match = new Match(matchNumber, field, controllers::get, new DefaultTurnRunner(), videoConsumer);
				history = match.run();
			} finally {
				if (encoder != null)
					try {
						encoder.close();
					} catch (IOException e) {
						logger.log(Level.SEVERE, "Unable to finish generating video file", e);
					}
			}

			Path traceFile = matchData.resolve("trace.json");

			logger.log(Level.INFO, "Writing match history to {0}", traceFile);

			Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
					.registerTypeAdapter(ScoreState.class, ScoreState.Serializer.INSTANCE)
					.registerTypeAdapter(InventoryState.class, InventoryState.Serializer.INSTANCE)
					.registerTypeAdapter(Vector.class, Vector.Serializer.INSTANCE)
					.registerTypeAdapter(Direction.class, Direction.Serializer.INSTANCE).setPrettyPrinting().create();
			try (Writer writer = Files.newBufferedWriter(traceFile, StandardOpenOption.CREATE_NEW)) {
				gson.toJson(history, writer);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Unable to create trace file", e);
			}

		} finally {
			for (JarController jar : jarsToClose)
				jar.close();
		}

	}

	private static <E> Queue<E> orderRandomly(Collection<E> collection, int minNumber) {
		List<E> result = new ArrayList<>(collection);
		while (result.size() < minNumber)
			result.addAll(collection);
		Collections.shuffle(result, ThreadLocalRandom.current());
		return new LinkedList<>(result);
	}

	public static final void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			logger.log(Level.SEVERE, "Problem running game engine", e);
		});
		try {
			INSTANCE.runMatches();
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "Main thread interrupted", e);
		}
	}

	private static final Logger logger = EngineLogger.INSTANCE;

}
