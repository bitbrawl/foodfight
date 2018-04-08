package org.bitbrawl.foodfight.server;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

public final class Authentication {

	private final String username, password;

	private Authentication() {
		this.username = "user";
		this.password = "pass";
	}

	public static Authentication getInstance(Path path) throws IOException {
		try (Reader reader = Files.newBufferedReader(path)) {
			return gson.fromJson(reader, Authentication.class);
		}
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	private static final Gson gson = new Gson();

}
