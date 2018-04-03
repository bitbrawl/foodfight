package org.bitbrawl.foodfight.engine.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public final class EngineFormatter extends Formatter {

	private final LineFormatter lineFormatter;

	public EngineFormatter(LineFormatter lineFormatter) {
		this.lineFormatter = lineFormatter;
	}

	@Override
	public String format(LogRecord record) {

		StringBuilder result = new StringBuilder();
		long time = record.getMillis();
		Level level = record.getLevel();

		try (java.util.Formatter stringFormatter = new java.util.Formatter(result)) {

			for (String line : formatMessage(record).split("\\R"))
				lineFormatter.format(stringFormatter, time, level, line);

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
