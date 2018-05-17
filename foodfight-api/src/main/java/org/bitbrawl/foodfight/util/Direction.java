package org.bitbrawl.foodfight.util;

import java.lang.reflect.Type;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.jcip.annotations.Immutable;

/**
 * A direction on 2-dimensional plane of the field. A direction is essentially
 * an angle measurement, in radians. The directions used are the ones commonly
 * used in math, where {@link #EAST} is a direction of 0 and {@link #NORTH} is a
 * direction of <sup>&pi;</sup>&frasl;<sub>2</sub>. The angle measurement of a
 * direction will always be in the range [0, 2&pi;).
 * 
 * @author Finn
 */
@Immutable
public final class Direction {

	private final double direction;

	/**
	 * Constructs a direction object with the given direction measurement. Note
	 * that calling {@link #get()} on the returned object may return something
	 * different than the passed-in direction measurement if the given
	 * measurement is outside the range [0, 2&pi;). For example,
	 * <sup>9&pi;</sup>&frasl;<sub>4</sub> would be converted to
	 * <sup>&pi;</sup>&frasl;<sub>4</sub>.
	 * 
	 * @param direction
	 *            the angle measurement of the desired direction
	 * @throws IllegalArgumentException
	 *             if direction is not finite
	 */
	public Direction(double direction) {
		if (!Double.isFinite(direction))
			throw new IllegalArgumentException("direction must be finite, but is: " + direction);
		this.direction = fixAngle(direction);
	}

	/**
	 * Returns a randomly-generated direction. The returned directions are
	 * uniformly distributed on the range [0, 2&pi;).
	 * 
	 * @return a random direction
	 */
	public static Direction random() {
		return new Direction(ThreadLocalRandom.current().nextDouble(TWO_PI));
	}

	/**
	 * Returns the measurement of this direction object, in radians, where
	 * {@link #EAST} is 0.0 and measurements increase as they rotate
	 * counter-clockwise. Direction objects are immutable, so this will always
	 * return the same value.
	 * 
	 * @return the angle measurement of this direction
	 */
	public double get() {
		return direction;
	}

	/**
	 * Adds the given amount to this direction, returning the result of the
	 * addition. Note that this direction is not modified by this method
	 * (because direction objects are immutable), but a new direction is
	 * created. Note that a negative number can be used to subtract from this
	 * direction, but if the result is outside the range [0, 2&pi;), it will
	 * "wrap around."
	 * 
	 * @param angle
	 *            the angle to add to this direction's angle measurement
	 * @return the result of adding the angle to this direction
	 * @throws IllegalArgumentException
	 *             if angle is not finite
	 */
	public Direction add(double angle) {
		if (!Double.isFinite(angle))
			throw new IllegalArgumentException("angle must be finite, but is: " + angle);
		return new Direction(direction + angle);
	}

	/**
	 * Returns the direction that is the opposite of this one. The returned
	 * angle will differ from this one by &pi;. As an example, the opposite of a
	 * player's direction is the direction that they would move if they were to
	 * move backward.
	 * 
	 * @return the opposite of this direction
	 */
	public Direction getOpposite() {
		return add(Math.PI);
	}

	/**
	 * Reflects this direction across the y-axis, returning the result. This
	 * method does not modify the object it is called on. If the object that
	 * this method is called on has measurement &theta;, the resulting angle
	 * would have measurement &pi; - &theta;, but in the range [0, 2&pi;).
	 * 
	 * @return the resulting direction after the reflection.
	 */
	public Direction reflectAcrossY() {
		return new Direction(Math.PI - direction);
	}

	/**
	 * Reflects this direction across the x-axis, returning the result. This
	 * method does not modify the object it is called on. If the object that
	 * this method is called on has measurement &theta;, the resulting angle
	 * would have measurement -&theta;, but in the range [0, 2&pi;).
	 * 
	 * @return the resulting direction after the reflection.
	 */
	public Direction reflectAcrossX() {
		return new Direction(-direction);
	}

	/**
	 * Returns the signed difference between two angles. If called on two
	 * directions with measurements &alpha; and &beta;, the result would be
	 * &beta; - &alpha;, but in the range [-&pi;, &pi;). If the
	 * {@link #add(double)} method is used to add the result of this method to
	 * alpha, the resulting angle will be very close to beta, and will differ
	 * only due to issues with floating-point arithmetic.
	 * 
	 * @param alpha
	 *            the first angle
	 * @param beta
	 *            the second angle
	 * @return the angular displacement of beta relative to alpha
	 */
	public static double difference(Direction alpha, Direction beta) {
		double result = Math.IEEEremainder(beta.direction - alpha.direction, TWO_PI);
		return result < Math.PI ? result : -Math.PI;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof Direction))
			return false;
		return direction == ((Direction) other).direction;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(direction);
	}

	@Override
	public String toString() {
		return String.format("%.2f rad", direction);
	}

	private static final double TWO_PI = 2.0 * Math.PI;
	/** The direction with measurement 0. */
	public static final Direction EAST = new Direction(0.0);
	/** The direction with measurement <sup>&pi;</sup>&frasl;<sub>2</sub>. */
	public static final Direction NORTH = new Direction(0.5 * Math.PI);
	/** The direction with measurement &pi;. */
	public static final Direction WEST = new Direction(Math.PI);
	/** The direction with measurement <sup>3&pi;</sup>&frasl;<sub>2</sub>. */
	public static final Direction SOUTH = new Direction(1.5 * Math.PI);

	private static double fixAngle(double angle) {
		double remainder = Math.IEEEremainder(angle, TWO_PI);
		return remainder >= 0.0 ? remainder : remainder + TWO_PI;
	}

	/**
	 * A serializer for directions. <b>Competitors should not use this
	 * class</b>; it is used internally by the game engine. It serializes a
	 * direction into only its angle measurement.
	 * 
	 * @author Finn
	 */
	public enum Serializer implements JsonSerializer<Direction> {

		/** The singleton instance of this class. */
		INSTANCE;

		@Override
		public JsonElement serialize(Direction src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.direction);
		}

	}

	/**
	 * A deserializer for directions. <b>Competitors should not use this
	 * class</b>; it is used internally by the game engine. It deserializes a
	 * direction from only its angle measurement.
	 * 
	 * @author Finn
	 */
	public enum Deserializer implements JsonDeserializer<Direction> {

		/** The singleton instance of this class. */
		INSTANCE;

		@Override
		public Direction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return new Direction(json.getAsDouble());
		}

	}

}
