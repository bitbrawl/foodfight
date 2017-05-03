package org.bitbrawl.foodfight.util;

public final class Vector {

	private final double x;
	private final double y;

	private transient double magnitude;
	private transient Direction direction;

	private Vector(double x, double y, double magnitude, Direction direction) {
		this.x = x;
		this.y = y;
		this.magnitude = magnitude;
		this.direction = direction;
	}

	private Vector() {
		x = -0.0;
		y = -0.0;
	}

	void init() {
		magnitude = Math.hypot(x, y);
		direction = new Direction(Math.toDegrees(Math.atan2(x, -y)));
	}

	public static Vector cartesian(double x, double y) {
		double speed = Math.hypot(x, y);
		Direction direction = new Direction(Math.toDegrees(Math.atan2(x, -y)));
		return new Vector(x, y, speed, direction);
	}

	public static Vector polar(double magnitude, Direction direction) {
		if (magnitude < 0.0)
			throw new IllegalArgumentException();
		if (direction == null)
			throw new NullPointerException();

		double x = magnitude * Math.sin(Math.toRadians(direction.get()));
		double y = -magnitude * Math.cos(Math.toRadians(direction.get()));
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

		int result = 17;

		result = 31 * result + Double.hashCode(x);
		result = 31 * result + Double.hashCode(y);

		return result;

	}

	public static final Vector ZERO = new Vector(0.0, 0.0, 0.0, new Direction(0.0));

}
