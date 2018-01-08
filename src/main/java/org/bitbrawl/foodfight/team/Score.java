package org.bitbrawl.foodfight.team;

import java.util.EnumMap;
import java.util.Map;

public final class Score {

	private final Map<EventType, Integer> eventCounts;
	private final int totalPoints;

	public Score() {
		this(new EnumMap<EventType, Integer>(EventType.class));
	}

	public Score(Map<EventType, Integer> eventCounts) {
		this.eventCounts = eventCounts;
		totalPoints = eventCounts.entrySet().stream().mapToInt(e -> e.getValue() * e.getKey().getPointValue()).sum();
	}

	public int getCount(EventType event) {
		Integer count = eventCounts.get(event);
		return count == null ? 0 : count;
	}

	public int getPoints(EventType event) {
		return getCount(event) * event.getPointValue();
	}

	public int getTotalPoints() {
		return totalPoints;
	}

	public Score addEvent(EventType type) {

		if (type.getCategory().equals(EventCategory.ONE_TIME) && eventCounts.get(type) == 1)
			return this;

		Map<EventType, Integer> newEventCounts = new EnumMap<>(eventCounts);
		newEventCounts.compute(type, (eventType, count) -> count + 1);

		return new Score(newEventCounts);

	}

	public static enum EventType {

		FIRST_MOVE(EventCategory.ONE_TIME, 1), FIRST_PICKUP(EventCategory.ONE_TIME, 5), FIRST_THROW(
				EventCategory.ONE_TIME, 10), EVERY_PICKUP(EventCategory.ONGOING,
						1), EVERY_THROW(EventCategory.ONGOING, 2), TIE_BREAK(EventCategory.ONGOING, 1);

		private final EventCategory category;
		private final int pointValue;

		private EventType(EventCategory category, int pointValue) {
			this.category = category;
			this.pointValue = pointValue;
		}

		public EventCategory getCategory() {
			return category;
		}

		public int getPointValue() {
			return pointValue;
		}

	}

	public static enum EventCategory {
		ONE_TIME, ONGOING;
	}

}
