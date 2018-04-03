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

@Immutable
public final class Direction {

	private final double direction;

	public Direction(double direction) {
		this.direction = fixAngle(direction);
	}

	public static Direction random() {
		return new Direction(ThreadLocalRandom.current().nextDouble() * TWO_PI);
	}

	public double get() {
		return direction;
	}

	public Direction add(double angle) {
		return new Direction(direction + angle);
	}

	public double angle(Direction other) {
		double result = fixAngle(other.direction - direction);
		if (result > Math.PI)
			result = TWO_PI - result;
		return result;
	}

	public Direction getOpposite() {
		return add(Math.PI);
	}

	public Direction flipX() {
		return new Direction(Math.PI - direction);
	}

	public Direction flipY() {
		return new Direction(TWO_PI - direction);
	}

	@Override
	public String toString() {
		return String.format("%.2frad", direction);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Direction)
			return direction == ((Direction) other).direction;
		return false;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(direction);
	}

	public static final Direction EAST = new Direction(0.0);
	public static final Direction NORTH = new Direction(0.5 * Math.PI);
	public static final Direction WEST = new Direction(Math.PI);
	public static final Direction SOUTH = new Direction(1.5 * Math.PI);

	private static double fixAngle(double angle) {
		double remainder = Math.IEEEremainder(angle, TWO_PI);
		if (remainder < 0.0)
			remainder += TWO_PI;
		return remainder;
	}

	private static final double TWO_PI = 2.0 * Math.PI;

	public enum Serializer implements JsonSerializer<Direction> {
		INSTANCE;

		@Override
		public JsonElement serialize(Direction src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.direction);
		}

	}

	public enum Deserializer implements JsonDeserializer<Direction> {
		INSTANCE;

		@Override
		public Direction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return new Direction(json.getAsDouble());
		}

	}

}