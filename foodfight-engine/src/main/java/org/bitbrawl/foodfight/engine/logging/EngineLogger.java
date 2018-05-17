package org.bitbrawl.foodfight.engine.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public final class EngineLogger extends Logger {

	private EngineLogger() {
		super("org.bitbrawl", null);
		setUseParentHandlers(false);
		Handler handler = new ConsoleHandler();
		handler.setFormatter(new EngineFormatter((formatter, time, level, line) -> {
			formatter.format("%tT.%tL [%s] %s%n", time, time, level, line);
		}));
		addHandler(handler);
	}

	public static final EngineLogger INSTANCE = new EngineLogger();

}
