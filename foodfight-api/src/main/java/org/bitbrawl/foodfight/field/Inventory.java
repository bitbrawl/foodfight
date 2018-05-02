package org.bitbrawl.foodfight.field;

/**
 * The food that a player is holding in their hands. An inventory has two slots:
 * one for each hand. To get an inventory, one typically calls the
 * {@link Player#getInventory()} method.
 * 
 * @author Finn
 */
public interface Inventory {

	/**
	 * Gets the type of food that is held in a given hand. If the hand is not
	 * currently holding anything, this method returns null.
	 * 
	 * @param hand
	 *            the hand to check for food
	 * @return the type of food held in the given hand, or null if none
	 */
	Food.Type get(Player.Hand hand);

}
