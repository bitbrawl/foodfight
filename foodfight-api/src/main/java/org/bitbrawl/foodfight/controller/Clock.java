package org.bitbrawl.foodfight.controller;

import java.util.concurrent.TimeUnit;

import org.bitbrawl.foodfight.field.Field;

/**
 * A timer that can be used to determine how much time a controller has left for
 * all of its moves.
 * 
 * Time limits in the game work like a chess timer: each controller gets a
 * certain amount of time total to make all of its moves
 * ({@link Field#TIME_LIMIT_NANOS}), but it can spend that time among its turns
 * however it wishes. For example, a controller could spend half of its allotted
 * time on its first move if it spends less on its remaining turns.
 * 
 * @author Finn
 */
public interface Clock {

	/**
	 * Returns the amount of time that a controller has left in the game. This
	 * is the amount of time left for all of this player's moves, not just the
	 * current turn. To determine how many moves are left in the game, a
	 * controller could use {@link Field#getTurnNumber()} and
	 * {@link Field#TOTAL_TURNS}. Note that this method rounds down to the
	 * nearest time unit, so it will provide more accurate results if a smaller
	 * time unit is used.
	 * 
	 * @param unit
	 *            the unit of time to use for time-left calculations
	 * @return the amount of time remaining for the associated controller
	 */
	public long getTimeLeft(TimeUnit unit);

}
