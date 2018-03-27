package org.bitbrawl.foodfight.engine.runner;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.controller.Clock;
import org.bitbrawl.foodfight.controller.JavaController;
import org.bitbrawl.foodfight.engine.config.ControllerConfig;
import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.field.TableState;
import org.bitbrawl.foodfight.engine.field.TeamState;
import org.bitbrawl.foodfight.engine.logging.EngineLogger;
import org.bitbrawl.foodfight.field.Field;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ControllerRunner {

	private final Gson gson = new GsonBuilder().registerTypeAdapter(FieldState.class, FieldState.Deserializer.INSTANCE)
			.registerTypeAdapter(TeamState.class, TeamState.Deserializer.INSTANCE)
			.registerTypeAdapter(TableState.class, TableState.Deserializer.INSTANCE).create();

	public ControllerRunner(Path jar, String mainClass)
			throws ClassNotFoundException, IOException, ControllerException {
		URL[] jarUrls = { jar.toUri().toURL() };
		Class<? extends JavaController> controllerClass;
		try (URLClassLoader loader = new URLClassLoader(jarUrls)) {
			controllerClass = loader.loadClass(mainClass).asSubclass(JavaController.class);
		}
		Logger logger = EngineLogger.INSTANCE;
		Clock clock = new ControllerClock(Field.TIME_LIMIT_NANOS, TimeUnit.NANOSECONDS);
		JavaController controller = JavaController.newInstance(controllerClass, logger, clock);

	}

	public void run(ControllerConfig controller) {

	}

}
