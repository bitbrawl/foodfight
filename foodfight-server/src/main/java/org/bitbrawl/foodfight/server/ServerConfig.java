package org.bitbrawl.foodfight.server;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

public final class ServerConfig {

	private final Authentication database;
	private final Authentication git;
	private final Authentication aws;

	private ServerConfig() {
		this.database = null;
		this.git = null;
		this.aws = null;
	}

	public static ServerConfig getInstance(Path file) throws IOException {
		Gson gson = new Gson();
		try (Reader reader = Files.newBufferedReader(file)) {
			return gson.fromJson(reader, ServerConfig.class);
		}
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
