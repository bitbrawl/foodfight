package org.bitbrawl.foodfight.engine.config;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bitbrawl.foodfight.engine.match.Match;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

public final class Configuration {

	private final int numMatches;
	private final Match.Type matchType;
	private final List<ControllerConfig> controllers;

	private Configuration(int numMatches, Match.Type matchType, ControllerConfig[] players) {
		this.numMatches = numMatches;
		this.matchType = matchType;
		this.controllers = Collections.unmodifiableList(Arrays.asList(players));
	}

	public int getNumMatches() {
		return numMatches;
	}

	public Match.Type getMatchType() {
		return matchType;
	}

	public List<ControllerConfig> getControllers() {
		return controllers;
	}

	public static Configuration getInstance() throws IOException {
		synchronized (Configuration.class) {
			if (instance != null)
				return instance;

			if (Files.notExists(CONFIG_PATH))
				Files.copy(Configuration.class.getResourceAsStream("/config.json"), CONFIG_PATH);

			Gson gson = new GsonBuilder().registerTypeAdapter(Configuration.class, Deserializer.INSTANCE).create();
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
				instance = gson.fromJson(reader, Configuration.class);
			} catch (JsonIOException e) {
				throw new IOException("Unable to read configuration file", e);
			} catch (JsonSyntaxException e) {
				throw new IOException("Invalid JSON in configuration file", e);
			}

			if (instance.matchType == null)
				throw new IOException("The match type cannot be null");
			List<ControllerConfig> controllers = instance.controllers;
			if (controllers == null)
				throw new IOException("The list of controllers must be defined");
			if (controllers.isEmpty())
				throw new IOException("The list of controllers cannot be empty");

			return instance;

		}
	}

	private static volatile Configuration instance;
	private static final Path CONFIG_PATH = Paths.get("config.json");

	public enum Deserializer implements JsonDeserializer<Configuration> {
		INSTANCE;

		@Override
		public Configuration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			JsonObject object = json.getAsJsonObject();
			int numMatches = object.getAsJsonPrimitive("numMatches").getAsInt();
			Match.Type matchType = context.deserialize(object.getAsJsonPrimitive("matchType"), Match.Type.class);
			ControllerConfig[] players = context.deserialize(object.getAsJsonArray("players"),
					ControllerConfig[].class);
			return new Configuration(numMatches, matchType, players);

		}

	}

}
