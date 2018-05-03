package org.bitbrawl.foodfight.field;

/**
 * A collision between two objects on the field. This will be either between two
 * players, or between a player and a food piece. Note that collision objects
 * indicate where objects collided on the previous turn, and that the collisions
 * from one turn will be different than the collisions from a previous turn. You
 * will typically get collisions via the {@link Field#getCollisions()} method.
 * 
 * @author Finn
 */
public interface Collision extends Locatable {

	/**
	 * Gets the total amount of damage inflicted on the players involved in this
	 * collision. This damage was inflicted on each player involved in the
	 * collision.
	 * 
	 * @return the damage inflicted on each player involved in this collision
	 */
	public double getDamage();

}
