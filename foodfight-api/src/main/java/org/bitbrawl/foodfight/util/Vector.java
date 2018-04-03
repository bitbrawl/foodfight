package org.bitbrawl.foodfight.util;

import java.lang.reflect.Type;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.jcip.annotations.Immutable;

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

	public static Vector cartesian(double x, double y) {
		double magnitude = Math.hypot(x, y);
		Direction direction = new Direction(Math.atan2(y, x));
		return new Vector(x, y, magnitude, direction);
	}

	public static Vector polar(double magnitude, Direction direction) {
		if (magnitude < 0.0)
			throw new IllegalArgumentException("magnitude cannot be negative");
		Objects.requireNonNull(direction, "direction cannot be null");

		double x = magnitude * Math.cos(direction.get());
		double y = magnitude * Math.sin(direction.get());
		return new Vector(x, y, magnitude, direction);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public Direction getDirection() {
		return direction;
	}

	public Vector getOpposite() {
		return new Vector(-x, -y, magnitude, direction.getOpposite());
	}

	public Vector add(Vector other) {
		return cartesian(x + other.x, y + other.y);
	}

	public Vector subtract(Vector other) {
		return cartesian(x - other.x, y - other.y);
	}

	public Vector multiply(double scalar) {
		return polar(magnitude * scalar, direction);
	}

	public Vector divide(double scalar) {
		return polar(magnitude / scalar, direction);
	}

	public double dot(Vector other) {
		return x * other.x + y * other.y;
	}

	public static Vector average(Vector vectorA, Vector vectorB) {
		return vectorA.add(vectorB).divide(2.0);
	}

	public double componentAlong(Vector other) {
		return dot(other) / other.magnitude;
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

	public static final Vector ZERO = new Vector(0.0, 0.0, 0.0, Direction.EAST);

	public enum Serializer implements JsonSerializer<Vector> {
		INSTANCE;

		@Override
		public JsonElement serialize(Vector src, Type typeOfSrc, JsonSerializationContext context) {
			JsonArray result = new JsonArray(2);
			result.add(new JsonPrimitive(src.x));
			result.add(new JsonPrimitive(src.y));
			return result;
		}

	}

	public enum Deserializer implements JsonDeserializer<Vector> {
		INSTANCE;

		@Override
		public Vector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonArray arr = json.getAsJsonArray();
			return Vector.cartesian(arr.get(0).getAsDouble(), arr.get(1).getAsDouble());
		}

	}

}
