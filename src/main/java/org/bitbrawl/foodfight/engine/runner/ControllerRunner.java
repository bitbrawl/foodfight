package org.bitbrawl.foodfight.engine.runner;

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

import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.controller.JavaController;
import org.bitbrawl.foodfight.engine.field.DynamicField;
import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.field.TableState;
import org.bitbrawl.foodfight.engine.field.TeamState;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ControllerRunner {

	private final Gson gson = new GsonBuilder().registerTypeAdapter(FieldState.class, FieldState.Deserializer.INSTANCE)
			.registerTypeAdapter(TeamState.class, TeamState.Deserializer.INSTANCE)
			.registerTypeAdapter(TableState.class, TableState.Deserializer.INSTANCE).create();
	private final Reader inReader = new InputStreamReader(System.in);
	private final Reader reader = new BufferedReader(inReader);
	private final Writer outWriter = new OutputStreamWriter(System.out);
	private final Writer writer = new BufferedWriter(outWriter);
	private final Controller controller;
	private DynamicField field;

	public ControllerRunner(Path jar, String mainClass)
			throws ClassNotFoundException, IOException, ControllerException, InterruptedException {

		URL[] jarUrls = { jar.toUri().toURL() };
		Class<? extends JavaController> controllerClass;
		try (URLClassLoader loader = new URLClassLoader(jarUrls)) {
			controllerClass = loader.loadClass(mainClass).asSubclass(JavaController.class);
		}
		controller = new ControllerWrapper(controllerClass);

	}

	public void runTurn() {

		FieldState input = gson.fromJson(reader, FieldState.class);
		char teamSymbol = gson.fromJson(reader, char.class);
		char playerSymbol = gson.fromJson(reader, char.class);

		if (field == null)
			field = new DynamicField(input);
		else
			field.update(input);
		Team team = field.getTeam(teamSymbol);
		Player player = field.getPlayer(playerSymbol);
		gson.toJson(controller.playAction(field, team, player), writer);

	}

	public void close() throws IOException {
		try {
			try {
				writer.close();
			} finally {
				outWriter.close();
			}
		} finally {
			try {
				reader.close();
			} finally {
				inReader.close();
			}
		}
	}

}
