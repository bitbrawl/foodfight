package org.bitbrawl.foodfight.field;

import org.bitbrawl.foodfight.state.FoodState;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.RandomScalar;

public interface FoodPiece extends FieldElement {

	public FoodType getType();

	public Direction getHeading();

	public FoodState getState();

	public static final RandomScalar SPEED = new RandomScalar(20.0, 1.0);
	public static final RandomScalar FALL_SPEED = new RandomScalar(2.0, 1.0);

}
