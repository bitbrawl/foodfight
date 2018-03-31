package org.bitbrawl.foodfight.engine.logging;

import java.util.Formatter;
import java.util.logging.Level;

@FunctionalInterface
public interface LineFormatter {

	public void format(Formatter formatter, long time, Level level, String line);

}
