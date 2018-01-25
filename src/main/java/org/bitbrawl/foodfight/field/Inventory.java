package org.bitbrawl.foodfight.field;

import org.bitbrawl.foodfight.field.Player.Hand;

public interface Inventory {

	public Food.Type get(Hand hand);

}
