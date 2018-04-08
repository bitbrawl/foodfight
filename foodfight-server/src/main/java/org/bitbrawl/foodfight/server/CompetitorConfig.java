package org.bitbrawl.foodfight.server;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

public final class CompetitorConfig {

	private final String mainClass;
	private final String version;

	private CompetitorConfig() {
		mainClass = "org.bitbrawl.SamplePlayer";
		version = "1.0.0";
	}

	public String getMainClass() {
		return mainClass;
	}

	public String getVersion() {
		return version;
	}

	public static CompetitorConfig getInstance(Path file) throws IOException {
		Gson gson = new Gson();
		try (Reader reader = Files.newBufferedReader(file)) {
			return gson.fromJson(reader, CompetitorConfig.class);
		}
	}

}
