package org.bitbrawl.foodfight.field;

import org.bitbrawl.foodfight.random.RandomScalar;

public interface FoodPiece extends FieldElement {

	public FoodType getType();

	public static final RandomScalar SPEED = new RandomScalar(20.0, 1.0);
	public static final RandomScalar FALL_SPEED = new RandomScalar(2.0, 1.0);

}
