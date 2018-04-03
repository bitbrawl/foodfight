package org.bitbrawl.foodfight.runner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.controller.ControllerException;
import org.bitbrawl.foodfight.controller.JavaController;
import org.bitbrawl.foodfight.engine.field.DynamicField;
import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.field.InventoryState;
import org.bitbrawl.foodfight.engine.field.ScoreState;
import org.bitbrawl.foodfight.engine.field.TableState;
import org.bitbrawl.foodfight.engine.field.TeamState;
import org.bitbrawl.foodfight.engine.logging.EngineLogger;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public final class ControllerRunner implements AutoCloseable {

	private final Gson gson = new GsonBuilder().registerTypeAdapter(FieldState.class, FieldState.Deserializer.INSTANCE)
			.registerTypeAdapter(TeamState.class, TeamState.Deserializer.INSTANCE)
			.registerTypeAdapter(TableState.class, TableState.Deserializer.INSTANCE)
			.registerTypeAdapter(ScoreState.class, ScoreState.Deserializer.INSTANCE)
			.registerTypeAdapter(InventoryState.class, InventoryState.Deserializer.INSTANCE)
			.registerTypeAdapter(Vector.class, Vector.Deserializer.INSTANCE)
			.registerTypeAdapter(Direction.class, Direction.Deserializer.INSTANCE).create();
	private final Reader inReader = new InputStreamReader(System.in);
	private final Reader reader = new BufferedReader(inReader);
	private final JsonReader jsonReader = new JsonReader(reader);
	private final Writer outWriter = new OutputStreamWriter(System.out);
	private final Writer writer = new BufferedWriter(outWriter);
	private final Controller controller;
	private DynamicField field;

	public ControllerRunner(Path jar, String mainClass)
			throws IOException, ClassNotFoundException, ControllerException, InterruptedException {

		URL[] jarUrls = { jar.toUri().toURL() };
		Class<? extends JavaController> controllerClass;
		try (URLClassLoader loader = new URLClassLoader(jarUrls)) {
			controllerClass = loader.loadClass(mainClass).asSubclass(JavaController.class);
		}
		controller = new ControllerWrapper(controllerClass);

	}

	public void runTurn() throws IOException {

		FieldState input = gson.fromJson(jsonReader, FieldState.class);
		EngineLogger.INSTANCE.log(Level.INFO, "input: {0}", input);
		char teamSymbol = gson.fromJson(jsonReader, char.class);
		char playerSymbol = gson.fromJson(jsonReader, char.class);

		if (field == null)
			field = new DynamicField(input);
		else
			field.update(input);
		Team team = field.getTeam(teamSymbol);
		Player player = field.getPlayer(playerSymbol);
		Controller.Action action = controller.playAction(field, team, player);
		EngineLogger.INSTANCE.log(Level.INFO, "Returning action {0}", action);
		gson.toJson(action, writer);
		writer.write(" ");
		writer.flush();

	}

	@Override
	public void close() throws IOException {
		try {
			try {
				writer.close();
			} finally {
				outWriter.close();
			}
		} finally {
			try {
				jsonReader.close();
			} finally {
				try {
					reader.close();
				} finally {
					inReader.close();
				}
			}
		}
	}

	public static void main(String[] args) {
		if (args.length != 2)
			return;
		Logger logger = EngineLogger.INSTANCE;
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			logger.log(Level.SEVERE, "Problem running controller runner", e);
		});

		Path jar = Paths.get(args[0]);
		String mainClass = args[1];
		try (ControllerRunner runner = new ControllerRunner(jar, mainClass)) {
			for (int i = 1, n = Field.TOTAL_TURNS; i <= n; i++) {
				logger.log(Level.INFO, "Starting turn {0}", i);
				runner.runTurn();
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Unable to read from " + jar, e);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Unable to find class " + mainClass, e);
		} catch (ControllerException e) {
			logger.log(Level.SEVERE, "Problem with controller", e);
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "ControllerRunner interrupted", e);
		} catch (Throwable t) {
			logger.log(Level.SEVERE, "Unknown exception", t);
		} finally {
			logger.info("Complete");
		}

	}

}
