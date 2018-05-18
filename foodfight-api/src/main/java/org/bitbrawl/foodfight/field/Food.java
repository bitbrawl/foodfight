package org.bitbrawl.foodfight.field;

import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.RandomScalar;

import com.google.gson.annotations.SerializedName;

import net.jcip.annotations.ThreadSafe;

/**
 * A piece of food on the field. At any time, if a food piece is not on a table
 * or in a player's inventory, it is either on the ground (in which case
 * {@link #getHeight()} is zero) or it is in the air. If it is on the ground, it
 * will not move between turns. If it in the air, it will move a distance of
 * {@link #SPEED} each turn in the direction of {@link #getHeading()}, and its
 * height will decrease by {@link #FALL_SPEED} each turn.
 * <p>
 * For collision purposes, food pieces can be thought of as disks parallel to
 * the ground plane of the field. {@link #getHeight()} represents the distance
 * between the food piece disk and the ground. The radius of the disk is
 * dependent on the type of food, and can be retrieved with the
 * {@link Type#getRadius()} method. The center of the disk can be located with
 * {@link #getLocation()}, which is relative to the lower-left corner of the
 * field when viewed from above.
 * <p>
 * Note that this class is not used to represent food on tables or in players'
 * inventories, only food on the ground or in the air. Food pieces on tables and
 * in inventories do not have a location, height, or heading, so they are
 * represented only by their {@link Type}.
 * 
 * @author Finn
 */
public interface Food extends Locatable {

	/**
	 * Returns the type of this piece of food.
	 * 
	 * @return this food object's type
	 */
	public Type getType();

	/**
	 * Returns the distance between the ground plane and this piece of food.
	 * 
	 * @return the height of this piece of food
	 */
	public double getHeight();

	/**
	 * Returns the direction of travel of this food piece. This food piece will
	 * continue moving in this direction every turn unless it reaches the ground
	 * or collides with something.
	 * 
	 * @return the direction in which this food piece is moving
	 */
	public Direction getHeading();

	/** The horizontal distance traveled by an airborne food piece each turn. */
	public static final RandomScalar SPEED = new RandomScalar(20.0, 1.0);
	/** The vertical distance traveled by an airborne food piece each turn. */
	public static final RandomScalar FALL_SPEED = new RandomScalar(2.0, 1.0);
	/** The probability of new food spawning on a turn with missing food. */
	public static final double RESPAWN_RATE = 0.01;

	/**
	 * A type of food. Each food type has slightly different properties. Here is
	 * a table representing the various food types: <table BORDER CELLPADDING=3
	 * CELLSPACING=1 summary="Food types and their properties">
	 * <tr>
	 * <th>Name</th>
	 * <th>Radius</th>
	 * <th>Energy</th>
	 * <th>Damage</th>
	 * </tr>
	 * <tr>
	 * <td>APPLE</td>
	 * <td>20</td>
	 * <td>20 &plusmn; 1</td>
	 * <td>20 &plusmn; 1</td>
	 * </tr>
	 * <tr>
	 * <td>BANANA</td>
	 * <td>20</td>
	 * <td>20 &plusmn; 2</td>
	 * <td>20 &plusmn; 1</td>
	 * </tr>
	 * <tr>
	 * <td>RASPBERRY</td>
	 * <td>10</td>
	 * <td>10 &plusmn; 1</td>
	 * <td>10 &plusmn; 2</td>
	 * </tr>
	 * <tr>
	 * <td>BROCCOLI</td>
	 * <td>20</td>
	 * <td>50 &plusmn; 1</td>
	 * <td>10 &plusmn; 1</td>
	 * </tr>
	 * <tr>
	 * <td>MILK</td>
	 * <td>20</td>
	 * <td>50 &plusmn; 1</td>
	 * <td>20 &plusmn; 5</td>
	 * </tr>
	 * <tr>
	 * <td>CHOCOLATE</td>
	 * <td>20</td>
	 * <td>50 &plusmn; 1</td>
	 * <td>20 &plusmn; 1</td>
	 * </tr>
	 * <tr>
	 * <td>SANDWICH</td>
	 * <td>20</td>
	 * <td>50 &plusmn; 2</td>
	 * <td>20 &plusmn; 1</td>
	 * </tr>
	 * <tr>
	 * <td>PIE</td>
	 * <td>50</td>
	 * <td>50 &plusmn; 1</td>
	 * <td>100 &plusmn; 1</td>
	 * </tr>
	 * </table>
	 * <p>
	 * For information about what the "&plusmn;" means, please see the
	 * {@link RandomScalar} class.
	 * 
	 * @author Finn
	 */
	@ThreadSafe
	public enum Type {

		/** An apple. */
		@SerializedName("apple")
		APPLE("Apple", 20.0, new RandomScalar(20.0, 1.0), new RandomScalar(20.0, 1.0)),
		/** A banana. */
		@SerializedName("banana")
		BANANA("Banana", 20.0, new RandomScalar(20.0, 2.0), new RandomScalar(20.0, 1.0)),
		/** A raspberry, the least powerful piece of food. */
		@SerializedName("raspberry")
		RASPBERRY("Raspberry", 10.0, new RandomScalar(10.0, 1.0), new RandomScalar(10.0, 2.0)),
		/** A piece of broccoli. */
		@SerializedName("broccoli")
		BROCCOLI("Broccoli", 20.0, new RandomScalar(50.0, 1.0), new RandomScalar(10.0, 1.0)),
		/** A milk carton. */
		@SerializedName("milk")
		MILK("Milk", 20.0, new RandomScalar(50.0, 1.0), new RandomScalar(20.0, 5.0)),
		/** A bar of chocolate. */
		@SerializedName("chocolate")
		CHOCOLATE("Chocolate", 20.0, new RandomScalar(50.0, 1.0), new RandomScalar(20.0, 1.0)),
		/** A sandwich. */
		@SerializedName("sandwich")
		SANDWICH("Sandwich", 20.0, new RandomScalar(50.0, 2.0), new RandomScalar(20.0, 1.0)),
		/** A pie, the most powerful weapon. */
		@SerializedName("pie")
		PIE("Pie", 50.0, new RandomScalar(50.0, 1.0), new RandomScalar(100.0, 1.0));

		private final String name;
		private final double radius;
		private final RandomScalar energy;
		private final RandomScalar damage;

		private Type(String name, double radius, RandomScalar energy, RandomScalar damage) {
			this.name = name;
			this.radius = radius;
			this.energy = energy;
			this.damage = damage;
		}

		/**
		 * Returns a human-readable name for this food type that differs from
		 * what will be returned by {@link #name()}. It will be a bit more
		 * readable by human eyes, and is intended primarily for debugging
		 * purposes.
		 * 
		 * @return a human-readable name for this food type
		 */
		@Override
		public String toString() {
			return name;
		}

		/**
		 * Returns the radius of food pieces of this type. Larger food pieces
		 * may be more likely to collide with their targets.
		 * 
		 * @return the radius of all food pieces of this type
		 */
		public double getRadius() {
			return radius;
		}

		/**
		 * Returns the energy gained by eating food of this type. If a player
		 * spends a turn eating food of this type from their inventory, their
		 * {@link Player#getEnergy()} will increase by this amount.
		 * 
		 * @return the energy contained in food of this type
		 */
		public RandomScalar getEnergy() {
			return energy;
		}

		/**
		 * Returns the damage incurred when a player collides with an airborne
		 * food piece of this type.
		 * 
		 * @return the damage value of this food type
		 */
		public RandomScalar getDamage() {
			return damage;
		}

	}

}
