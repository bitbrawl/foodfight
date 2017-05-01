package org.bitbrawl.foodfight.team;

public interface Score {

	public int getCount(EventType event);

	public int getPoints(EventType event);

	public int getTotalPoints();

	public static enum EventType {

		FIRST_MOVE(EventCategory.ONE_TIME, 1),
		FIRST_PICKUP(EventCategory.ONE_TIME, 5),
		FIRST_THROW(EventCategory.ONE_TIME, 10),
		EVERY_PICKUP(EventCategory.ONGOING, 1),
		EVERY_THROW(EventCategory.ONGOING, 2),
		TIE_BREAK(EventCategory.ONGOING, 1);

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
