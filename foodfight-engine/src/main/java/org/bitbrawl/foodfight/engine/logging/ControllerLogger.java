package org.bitbrawl.foodfight.engine.logging;

import java.util.function.IntSupplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public final class ControllerLogger extends Logger {

	public ControllerLogger(IntSupplier turnNumberSupplier) {
		super("org.bitbrawl", null);
		setUseParentHandlers(false);
		Handler handler = new ConsoleHandler();
		handler.setFormatter(new EngineFormatter((formatter, time, level, line) -> {
			formatter.format("%tT.%tL [%s] [turn %d] %s%n", time, time, level, turnNumberSupplier.getAsInt(), line);
		}));
		addHandler(new ConsoleHandler());
	}

}
