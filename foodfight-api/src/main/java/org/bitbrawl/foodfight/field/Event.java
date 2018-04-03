package org.bitbrawl.foodfight.field;

import com.google.gson.annotations.SerializedName;

public enum Event {

	@SerializedName("firstMove")
	FIRST_MOVE("First move", Type.ONE_TIME, 1),
	@SerializedName("firstPickup")
	FIRST_PICKUP("First pickup", Type.ONE_TIME, 5),
	@SerializedName("firstThrow")
	FIRST_THROW("First throw", Type.ONE_TIME, 10),
	@SerializedName("everyPickup")
	EVERY_PICKUP("Every pickup", Type.ONGOING, 1),
	@SerializedName("everyThrow")
	EVERY_THROW("Every throw", Type.ONGOING, 2),
	@SerializedName("everyFoodCollision")
	EVERY_FOOD_COLLISION("Every time hit by food", Type.ONGOING, -500),
	@SerializedName("everyPlayerCollision")
	EVERY_PLAYER_COLLISION("Every time hit by player", Type.ONGOING, -1),
	@SerializedName("foodOnTable")
	FOOD_ON_TABLE("Food on table", Type.ONGOING, 1),
	@SerializedName("firstEat")
	FIRST_EAT("First time eating", Type.ONE_TIME, 10),
	@SerializedName("everyEat")
	EVERY_EAT("Every time eating", Type.ONGOING, 2),
	@SerializedName("firstFoodCollision")
	FIRST_FOOD_COLLISION("First time hit by food", Type.ONE_TIME, -100),
	@SerializedName("firstPlayerCollision")
	FIRST_PLAYER_COLLISION("First time hit by player", Type.ONE_TIME, -2),
	@SerializedName("tieBreak")
	TIE_BREAK("Tie break", Type.ONGOING, 1);

	private final String name;
	private final Type type;
	private final int pointValue;

	private Event(String name, Type type, int pointValue) {
		this.name = name;
		this.type = type;
		this.pointValue = pointValue;
	}

	 @Override
	 public String toString() {
		 return name;
	 }

	public Type getType() {
		return type;
	}

	public int getPointValue() {
		return pointValue;
	}

	public enum Type {

		ONE_TIME("One-time"), ONGOING("Ongoing");

		private final String name;

		private Type(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

}
