package org.bitbrawl.foodfight.server;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bitbrawl.foodfight.engine.config.PathDeserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ServerConfig {

	private final String databaseUrl;
	private final Path repoFolder;
	private final Path dataFolder;
	private final Path mavenFolder;
	private final Authentication database;
	private final Authentication git;
	private final Authentication aws;

	private ServerConfig() {
		this.databaseUrl = null;
		this.repoFolder = null;
		this.dataFolder = null;
		this.mavenFolder = null;
		this.database = null;
		this.git = null;
		this.aws = null;
	}

	public static ServerConfig getInstance(Path file) throws IOException {
		Gson gson = new GsonBuilder().registerTypeAdapter(Path.class, PathDeserializer.INSTANCE).create();
		try (Reader reader = Files.newBufferedReader(file)) {
			return gson.fromJson(reader, ServerConfig.class);
		}
	}

	public String getDatabaseUrl() {
		return databaseUrl;
	}

	public Path getRepoFolder() {
		return repoFolder;
	}

	public Path getDataFolder() {
		return dataFolder;
	}

	public Path getMavenFolder() {
		return mavenFolder;
	}

	public Authentication getDatabase() {
		return database;
	}

	public Authentication getGit() {
		return git;
	}

	public Authentication getAws() {
		return aws;
	}

}
