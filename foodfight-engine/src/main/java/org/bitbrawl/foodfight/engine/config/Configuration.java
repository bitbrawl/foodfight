package org.bitbrawl.foodfight.engine.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.bitbrawl.foodfight.field.MatchType;

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
	private final MatchType matchType;
	private final List<ControllerConfig> controllers;
	private final Path data;

	private Configuration(int numMatches, MatchType matchType, ControllerConfig[] controllers, Path data) {
		this.numMatches = numMatches;
		this.matchType = matchType;
		this.controllers = controllers == null ? null : Collections.unmodifiableList(Arrays.asList(controllers));
		this.data = data;
	}

	public static Configuration getConfig(Path configFile) throws IOException, ConfigException {
		Objects.requireNonNull(configFile, "configFile cannot be null");

		Configuration result;

		if (Files.notExists(configFile)) {

			result = getDefault();
			try (Writer writer = Files.newBufferedWriter(configFile, StandardOpenOption.CREATE_NEW)) {
				gson.toJson(result, writer);
			}

		} else {

			try (Reader reader = Files.newBufferedReader(configFile)) {
				return readConfig(reader);
			} catch (JsonIOException e) {
				throw new IOException("Unable to read configuration file", e);
			} catch (JsonSyntaxException e) {
				throw new ConfigException("Invalid JSON in configuration file", e);
			}

		}

		return result;

	}

	public static Configuration getConfig(InputStream configResource) throws IOException, ConfigException {
		Objects.requireNonNull(configResource, "configResource cannot be null");

		try (Reader reader = new InputStreamReader(configResource);
				BufferedReader buffered = new BufferedReader(reader)) {
			return readConfig(buffered);
		} catch (JsonIOException e) {
			throw new IOException("Unable to read configuration file", e);
		} catch (JsonSyntaxException e) {
			throw new ConfigException("Invalid JSON in configuration file", e);
		}

	}

	private static Configuration readConfig(Reader reader) throws ConfigException {

		Configuration result = gson.fromJson(reader, Configuration.class);

		if (result.numMatches <= 0)
			throw new ConfigException("numMatches must be at least 1");
		if (result.matchType == null)
			throw new ConfigException("matchType must be defined");
		List<ControllerConfig> controllers = result.controllers;
		if (controllers == null)
			throw new ConfigException("The list of controllers must be defined");
		if (controllers.isEmpty())
			throw new ConfigException("The list of controllers cannot be empty");
		if (result.data == null)
			throw new ConfigException("The data folder must be specified");

		return result;

	}

	public int getNumMatches() {
		return numMatches;
	}

	public MatchType getMatchType() {
		return matchType;
	}

	public List<ControllerConfig> getControllers() {
		return controllers;
	}

	public Path getData() {
		return data;
	}

	private static Configuration getDefault() {

		ControllerConfig[] players = new ControllerConfig[4];
		Path sampleJar = Paths.get("players", "sample-players.jar");
		players[0] = new ControllerConfig("sample-dummy", sampleJar, "org.bitbrawl.foodfight.sample.DummyController");
		players[1] = new ControllerConfig("sample-random", sampleJar, "org.bitbrawl.foodfight.sample.RandomController");
		players[2] = new ControllerConfig("sample-hiding", sampleJar, "org.bitbrawl.foodfight.sample.HidingController");
		players[3] = new ControllerConfig("sample-walls", sampleJar, "org.bitbrawl.foodfight.sample.WallsController");
		Path data = Paths.get("data");

		return new Configuration(3, MatchType.FREE_FOR_ALL, players, data);

	}

	private static final Gson gson = new GsonBuilder().registerTypeAdapter(Configuration.class, Deserializer.INSTANCE)
			.registerTypeAdapter(Path.class, PathSerializer.INSTANCE)
			.registerTypeAdapter(Path.class, PathDeserializer.INSTANCE).setPrettyPrinting().create();

	public enum Deserializer implements JsonDeserializer<Configuration> {
		INSTANCE;

		@Override
		public Configuration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			JsonObject object = json.getAsJsonObject();
			int numMatches = object.getAsJsonPrimitive("numMatches").getAsInt();
			MatchType matchType = context.deserialize(object.getAsJsonPrimitive("matchType"), MatchType.class);
			ControllerConfig[] controllers = context.deserialize(object.getAsJsonArray("controllers"),
					ControllerConfig[].class);
			Path data = context.deserialize(object.getAsJsonPrimitive("data"), Path.class);
			return new Configuration(numMatches, matchType, controllers, data);

		}

	}

}
