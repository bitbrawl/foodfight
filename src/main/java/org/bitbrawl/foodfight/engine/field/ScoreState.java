package org.bitbrawl.foodfight.engine.field;

import java.util.EnumMap;
import java.util.Map;

import org.bitbrawl.foodfight.field.Event;
import org.bitbrawl.foodfight.field.Score;

public final class ScoreState implements Score {

	private final Map<Event, Integer> counts;
	private final int totalPoints;

	public ScoreState(Map<Event, ? extends Integer> counts) {
		this.counts = new EnumMap<>(counts);
		totalPoints = counts.entrySet().stream().mapToInt(e -> e.getValue() * e.getKey().getPointValue()).sum();
	}

	@SuppressWarnings("unused")
	private ScoreState() {
		counts = null;
		totalPoints = -1;
	}

	@Override
	public int getCount(Event event) {
		return counts.getOrDefault(event, 0);
	}

	@Override
	public int getPoints(Event event) {
		return getCount(event) * event.getPointValue();
	}

	@Override
	public int getTotalPoints() {
		return totalPoints;
	}

	public ScoreState addEvent(Event event) {

		if (event.getType().equals(Event.Type.ONE_TIME) && getCount(event) >= 1)
			return this;

		Map<Event, Integer> newEventCounts = new EnumMap<>(counts);
		newEventCounts.merge(event, 1, Integer::sum);

		return new ScoreState(newEventCounts);

	}

	@Override
	public String toString() {
		return counts.toString();
	}

}
