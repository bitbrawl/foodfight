package org.bitbrawl.foodfight.engine.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class EngineLogger extends Logger {

	private EngineLogger() {
		super("org.bitbrawl", null);
		setUseParentHandlers(false);
		Handler handler = new ConsoleHandler();
		handler.setFormatter(new EngineFormatter());
		addHandler(new ConsoleHandler());
	}

	public static final EngineLogger INSTANCE = new EngineLogger();

	private static final class EngineFormatter extends Formatter {

		@Override
		public String format(LogRecord record) {

			StringBuilder result = new StringBuilder();
			long time = record.getMillis();
			Level level = record.getLevel();

			try (java.util.Formatter stringFormatter = new java.util.Formatter(result)) {

				for (String line : formatMessage(record).split("\\R"))
					stringFormatter.format("%tT.%tL [%s] %s%n", time, time, level, line);

			}

			result.append(exceptionToString(record.getThrown()));

			return result.toString();

		}

		private static String exceptionToString(Throwable exception) {
			if (exception == null)
				return "";
			try (StringWriter out = new StringWriter(); PrintWriter s = new PrintWriter(out)) {
				exception.printStackTrace(s);
				return out.toString();
			} catch (IOException e) {
				throw new IllegalStateException("Unable to convert exception to string", e);
			}
		}

	}

}
