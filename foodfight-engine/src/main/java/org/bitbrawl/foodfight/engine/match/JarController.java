package org.bitbrawl.foodfight.engine.match;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.field.InventoryState;
import org.bitbrawl.foodfight.engine.field.ScoreState;
import org.bitbrawl.foodfight.engine.logging.EngineLogger;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;

public final class JarController implements Controller {

	private final Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
			.registerTypeAdapter(ScoreState.class, ScoreState.Serializer.INSTANCE)
			.registerTypeAdapter(InventoryState.class, InventoryState.Serializer.INSTANCE)
			.registerTypeAdapter(Vector.class, Vector.Serializer.INSTANCE)
			.registerTypeAdapter(Direction.class, Direction.Serializer.INSTANCE).create();
	private final Path log;
	private final Process process;
	private final Writer outWriter;
	private final Writer writer;
	private final Reader inReader;
	private final Reader reader;
	private final JsonReader jsonReader;
	private boolean isClosed;

	public JarController(Path jar, String className, Path log) throws IOException {

		this.log = log;

		String runnerJar = Paths.get("lib", "foodfight-runner-0.1.0-jar-with-dependencies.jar").toAbsolutePath()
				.toString();
		String jarString = jar.toAbsolutePath().toString();
		ProcessBuilder builder = new ProcessBuilder("java", "-jar", runnerJar, jarString, className);
		Files.createFile(log);
		builder.redirectError(log.toAbsolutePath().toFile());
		process = builder.start();
		outWriter = new OutputStreamWriter(process.getOutputStream());
		writer = new BufferedWriter(outWriter);
		inReader = new InputStreamReader(process.getInputStream());
		reader = new BufferedReader(inReader);
		jsonReader = new JsonReader(reader);

	}

	@Override
	public Action playAction(Field field, Team team, Player player) {

		if (isClosed)
			return null;

		try {
			gson.toJson(FieldState.fromField(field), writer);
			gson.toJson(team.getSymbol(), writer);
			gson.toJson(player.getSymbol(), writer);
			writer.flush();
			Action result = gson.fromJson(jsonReader, Action.class);
			return result;
		} catch (IOException | JsonIOException e) {
			EngineLogger.INSTANCE.log(Level.SEVERE, "Problem communicating with controller", e);
			isClosed = true;
			return null;
		}

	}

	public Path getLog() {
		return log;
	}

}
