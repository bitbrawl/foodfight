package org.bitbrawl.foodfight.field;

public interface Score {

	public int getCount();

	public int getPoints(EventType event);

	public int getTotalPoints();

	public static enum EventType {

		FIRST_MOVE("First move", EventCategory.ONE_TIME, 1),
		FIRST_PICKUP("First pickup", EventCategory.ONE_TIME, 5),
		FIRST_THROW("First throw", EventCategory.ONE_TIME, 10),
		EVERY_PICKUP("Every pickup", EventCategory.ONGOING, 1),
		EVERY_THROW("Every throw", EventCategory.ONGOING, 2),
		TIE_BREAK("Tie break", EventCategory.ONGOING, 1);

		private final String name;
		private final EventCategory category;
		private final int pointValue;

		private EventType(String name, EventCategory category, int pointValue) {
			this.name = name;
			this.category = category;
			this.pointValue = pointValue;
		}

		@Override
		public String toString() {
			return name;
		}

		public EventCategory getCategory() {
			return category;
		}

		public int getPointValue() {
			return pointValue;
		}

	}

	public static enum EventCategory {

		ONE_TIME("One-time"), ONGOING("Ongoing");

		private final String name;

		private EventCategory(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

}
