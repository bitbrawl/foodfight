package org.bitbrawl.foodfight.field;

public interface FoodPiece extends FieldElement {

	public FoodType getType();

	public static final double COLLISION_RADIUS = 20.0;

}
