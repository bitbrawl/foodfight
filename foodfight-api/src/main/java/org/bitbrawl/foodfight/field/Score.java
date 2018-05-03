package org.bitbrawl.foodfight.field;

/**
 * A team's score. A score object contains not only the total number of points,
 * but the number of times each event was scored as well. Competitors will
 * typically access Score objects via the {@link Team#getScore()} method.
 * 
 * @author Finn
 */
public interface Score {

	/**
	 * Returns the total number of times that the given event was scored. For
	 * {@link Event.Type#ONE_TIME} events, this method will return 0 or 1.
	 * 
	 * @param event
	 *            the event to count
	 * @return the number of times the given event was scored
	 */
	public int getCount(Event event);

	/**
	 * Returns the total number of points that the given event has scored. This
	 * is equivalent to the result of {@link #getCount(Event)}, multiplied by
	 * the event's {@link Event#getPointValue()}.
	 * 
	 * @param event
	 *            the event for which to count points
	 * @return the number of points that the given event has scored
	 */
	public int getPoints(Event event);

	/**
	 * Returns the total number of points scored. This is equivalent to the sum
	 * of {@link #getPoints(Event)} for all {@link Event}s.
	 * 
	 * @return the total number of points scored by the associated team
	 */
	public int getTotalPoints();

}
