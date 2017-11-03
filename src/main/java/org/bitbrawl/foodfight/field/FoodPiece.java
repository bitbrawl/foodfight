package org.bitbrawl.foodfight.field;

public interface FoodPiece extends FieldElement {

	public FoodType getType();

	public static final double SPEED = 20.0;
	public static final double FALL_SPEED = 1.0;

}
