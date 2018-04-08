package org.bitbrawl.foodfight.server;

import java.io.IOException;
import java.nio.file.Path;

import org.bitbrawl.foodfight.controller.Controller;
import org.bitbrawl.foodfight.engine.match.JarController;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;

public final class CompetitorController implements Controller, AutoCloseable {

	private final JarController wrapped;
	private final ControllerVersion version;

	public CompetitorController(Path jar, String mainClass, Path log, ControllerVersion version) throws IOException {
		this.wrapped = new JarController(jar, mainClass, log);
		this.version = version;
	}

	@Override
	public Action playAction(Field field, Team team, Player player) {
		return wrapped.playAction(field, team, player);
	}

	public ControllerVersion getVersion() {
		return version;
	}

	@Override
	public void close() {
		wrapped.close();
	}

	public Path getLog() {
		return wrapped.getLog();
	}

}
