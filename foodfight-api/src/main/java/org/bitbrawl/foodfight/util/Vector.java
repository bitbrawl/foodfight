package org.bitbrawl.foodfight.util;

import java.lang.reflect.Type;
import java.util.Objects;

import org.bitbrawl.foodfight.field.Locatable;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.jcip.annotations.Immutable;

/**
 * A two-dimensional vector in the horizontal plane of the field. This is often
 * used to represent location, as in the {@link Locatable} interface, but it can
 * also be used to represent other concepts that can be represented by vectors,
 * such as velocity or translation.
 * <p>
 * Note: this class has no public constructors. To construct a vector, use one
 * of the two factory methods, {@link #cartesian(double, double)} and
 * {@link #polar(double, Direction)}.
 * 
 * @author Finn
 */
@Immutable
public final class Vector {

	private final double x;
	private final double y;

	private final double magnitude;
	private final Direction direction;

	private Vector(double x, double y, double magnitude, Direction direction) {
		this.x = x;
		this.y = y;
		this.magnitude = magnitude;
		this.direction = direction;
	}

	/**
	 * Constructs a vector with Cartesian coordinates. If this vector is used to
	 * represent location on the field, then (when viewing the field from
	 * above), x is the distance of the location from the left wall, and y is
	 * the distance of the location from the bottom wall.
	 * 
	 * @param x
	 *            the x-coordinate of the vector
	 * @param y
	 *            the y-coordinate of the vector
	 * @return a vector with the desired parameters
	 * @throws IllegalArgumentException
	 *             if x or y is not finite
	 */
	public static Vector cartesian(double x, double y) {
		if (!Double.isFinite(x))
			throw new IllegalArgumentException("x must be finite, but is: " + x);
		if (!Double.isFinite(y))
			throw new IllegalArgumentException("y must be finite, but is: " + y);
		double magnitude = Math.hypot(x, y);
		Direction direction = new Direction(Math.atan2(y, x));
		return new Vector(x, y, magnitude, direction);
	}

	/**
	 * Constructs a vector with polar coordinates. If this vector is used to
	 * represent translation (for example, from one player to another), then the
	 * magnitude would represent the distance between the two players, and the
	 * direction would represent the direction from the first player to the
	 * second.
	 * 
	 * @param magnitude
	 *            the magnitude of the vector
	 * @param direction
	 *            the direction of the vector
	 * @return a vector with the desired parameters
	 * @throws IllegalArgumentException
	 *             if magnitude is not finite and non-negative
	 * @throws NullPointerException
	 *             if direction is null
	 */
	public static Vector polar(double magnitude, Direction direction) {
		if (!Double.isFinite(magnitude))
			throw new IllegalArgumentException("magnitude must be finite, but is: " + magnitude);
		if (magnitude < 0.0)
			throw new IllegalArgumentException("magnitude cannot be negative, but is: " + magnitude);
		Objects.requireNonNull(direction, "direction cannot be null");

		double x = magnitude * Math.cos(direction.get());
		double y = magnitude * Math.sin(direction.get());
		return new Vector(x, y, magnitude, direction);

	}

	/**
	 * Returns the x-coordinate of this vector.
	 * 
	 * @return this vector's x-coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y-coordinate of this vector.
	 * 
	 * @return this vector's y-coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the magnitude of this vector.
	 * 
	 * @return this vector's magnitude.
	 */
	public double getMagnitude() {
		return magnitude;
	}

	/**
	 * Returns the direction of this vector.
	 * 
	 * @return this vector's direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Returns the vector with the same magnitude as this one, but in the
	 * opposite direction.
	 * 
	 * @return this vector's opposite
	 */
	public Vector getOpposite() {
		return new Vector(-x, -y, magnitude, direction.getOpposite());
	}

	/**
	 * Adds the given vector to this one, returning the result. Both vector's
	 * x-coordinates are added to get the new x-coordinate, and both vector's
	 * y-coordinates are added to get the new y-coordinate.
	 * 
	 * @param other
	 *            the vector to add to this one
	 * @return the sum of the two vectors
	 * @throws NullPointerException
	 *             if other is null
	 */
	public Vector add(Vector other) {
		Objects.requireNonNull(other, "other cannot be null");
		return cartesian(x + other.x, y + other.y);
	}

	/**
	 * Subtracts the given vector from this one. This is equivalent to calling
	 * the {@link #add(Vector)} method on other's opposite.
	 * 
	 * @param other
	 *            the vector to subtract from this one
	 * @return the difference of the two vectors
	 * @throws NullPointerException
	 *             if other is null
	 */
	public Vector subtract(Vector other) {
		Objects.requireNonNull(other, "other cannot be null");
		return cartesian(x - other.x, y - other.y);
	}

	/**
	 * Multiplies the vector by given scalar. The x and y coordinates are
	 * multiplied by the given scalar. The direction will be unchanged, unless
	 * the scalar is negative, in which case the new direction will be the
	 * opposite of the current one.
	 * 
	 * @param scalar
	 *            the amount by which to multiply the vector
	 * @return the result of the multiplication
	 * @throws IllegalArgumentException
	 *             if scalar is not finite
	 */
	public Vector multiply(double scalar) {
		if (!Double.isFinite(scalar))
			throw new IllegalArgumentException("scalar must be finite, but is: " + scalar);
		if (scalar > 0)
			return new Vector(x * scalar, y * scalar, magnitude * scalar, direction);
		if (scalar < 0)
			return new Vector(x * scalar, y * scalar, magnitude * -scalar, direction.getOpposite());
		return Vector.ZERO;
	}

	/**
	 * Divides the vector by the given scalar. This is equivalent to multiplying
	 * by scalar's reciprocal.
	 * 
	 * @param scalar
	 *            the amount by which to divide the scalar
	 * @return the result of the division
	 * @throws IllegalArgumentException
	 *             if scalar is not finite or is zero
	 */
	public Vector divide(double scalar) {
		if (!Double.isFinite(scalar))
			throw new IllegalArgumentException("scalar must be finite, but is: " + scalar);
		if (scalar == 0.0)
			throw new IllegalArgumentException("scalar cannot be zero");
		if (scalar > 0)
			return new Vector(x / scalar, y / scalar, magnitude / scalar, direction);
		else
			return new Vector(x / scalar, y / scalar, magnitude / -scalar, direction.getOpposite());
	}

	/**
	 * Performs the scalar product of this vector with the given one.
	 * 
	 * @param other
	 *            the vector with which to multiply this one
	 * @return the dot product of the two vectors
	 * @throws NullPointerException
	 *             if other is null
	 */
	public double dot(Vector other) {
		Objects.requireNonNull(other, "other cannot be null");
		return x * other.x + y * other.y;
	}

	/**
	 * Returns the scalar projection of this vector onto other. This is also the
	 * magnitude of the vector projection of this vector onto other, or the
	 * component of this vector along other.
	 * 
	 * @param other
	 *            the vector to project this vector onto
	 * @return the component of this vector along other
	 * @throws NullPointerException
	 *             if other is null
	 */
	public double componentAlong(Vector other) {
		Objects.requireNonNull(other, "other cannot be null");
		return dot(other) / other.magnitude;
	}

	/**
	 * Computes the average of the two vectors. If the two vectors represent
	 * locations, this is equivalent to the midpoint of the two locations.
	 * 
	 * @param v1
	 *            the first vector to average
	 * @param v2
	 *            the second vector to average
	 * @return the average of the two vectors
	 * @throws NullPointerException
	 *             if either vector is null
	 */
	public static Vector average(Vector v1, Vector v2) {
		Objects.requireNonNull(v1, "v1 cannot be null");
		Objects.requireNonNull(v2, "v2 cannot be null");
		return v1.add(v2).divide(2.0);
	}

	@Override
	public String toString() {
		return "<" + Math.round(x) + ", " + Math.round(y) + ">";
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Vector))
			return false;

		Vector vector = (Vector) other;
		return x == vector.x && y == vector.y;

	}

	@Override
	public int hashCode() {
		return Double.hashCode(x) * 31 + Double.hashCode(y);
	}

	/** The zero vector, with both an x and y of zero, pointing east. */
	public static final Vector ZERO = new Vector(0.0, 0.0, 0.0, Direction.EAST);

	/**
	 * A serializer for vectors. <b>Competitors should not use this class</b>;
	 * it is used internally by the game engine. It serializes a vector into
	 * only its x and y components.
	 * 
	 * @author Finn
	 */
	public enum Serializer implements JsonSerializer<Vector> {
		/** The singleton instance of this class. */
		INSTANCE;

		@Override
		public JsonElement serialize(Vector src, Type typeOfSrc, JsonSerializationContext context) {
			JsonArray result = new JsonArray(2);
			result.add(new JsonPrimitive(src.x));
			result.add(new JsonPrimitive(src.y));
			return result;
		}

		@Override
		public String toString() {
			return "Vector.Serializer";
		}

	}

	/**
	 * A deserializer for vectors. <b>Competitors should not use this class</b>;
	 * it is used internally by the game engine. It deserializes a vector from
	 * only its x and y components.
	 * 
	 * @author Finn
	 */
	public enum Deserializer implements JsonDeserializer<Vector> {
		/** The singleton instance of this class */
		INSTANCE;

		@Override
		public Vector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonArray arr = json.getAsJsonArray();
			return Vector.cartesian(arr.get(0).getAsDouble(), arr.get(1).getAsDouble());
		}

		@Override
		public String toString() {
			return "Vector.Deserializer";
		}

	}

}
