package org.bitbrawl.foodfight.engine.runner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.field.TableState;
import org.bitbrawl.foodfight.engine.field.TeamState;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JarController implements Controller, AutoCloseable {

	private final Gson gson = new GsonBuilder().registerTypeAdapter(FieldState.class, FieldState.Deserializer.INSTANCE)
			.registerTypeAdapter(TeamState.class, TeamState.Deserializer.INSTANCE)
			.registerTypeAdapter(TableState.class, TableState.Deserializer.INSTANCE).create();
	private final Process process;
	private final Writer outWriter;
	private final Writer writer;
	private final Reader inReader;
	private final Reader reader;

	public JarController(Path jar, String className, Path log) throws IOException {

		String jarString = jar.toAbsolutePath().toString();
		ProcessBuilder builder = new ProcessBuilder("java", "-jar", jarString, className);
		Files.createFile(log);
		builder.redirectError(log.toAbsolutePath().toFile());
		process = builder.start();
		outWriter = new OutputStreamWriter(process.getOutputStream());
		writer = new BufferedWriter(outWriter);
		inReader = new InputStreamReader(process.getInputStream());
		reader = new BufferedReader(inReader);

	}

	@Override
	public Action playAction(Field field, Team team, Player player) {

		gson.toJson(FieldState.fromField(field), writer);
		gson.toJson(team.getSymbol(), writer);
		gson.toJson(player.getSymbol(), writer);

		return gson.fromJson(reader, Action.class);

	}

	@Override
	public void close() throws IOException {
		try {
			try {
				try {
					reader.close();
				} finally {
					inReader.close();
				}
			} finally {
				try {
					writer.close();
				} finally {
					outWriter.close();
				}
			}
		} finally {
			process.destroy();
		}
	}

}
