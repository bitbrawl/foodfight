package org.bitbrawl.foodfight.state;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.bitbrawl.foodfight.player.Inventory;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.Immutable;

@Immutable
public final class PlayerState implements Serializable {

	private final char symbol;
	private final float color;
	private final Vector location;
	private final double height;
	private final Direction heading;
	private final Inventory inventory;
	private final double health;
	private final long timeLeft;

	public PlayerState(char symbol, float color, Vector location, double height, Direction heading, Inventory inventory,
			double health, long timeLeft, TimeUnit unit) {
		this.symbol = symbol;
		this.color = color;
		this.location = location;
		this.height = height;
		this.heading = heading;
		this.inventory = inventory;
		this.health = health;
		this.timeLeft = unit.toNanos(timeLeft);
	}

	public char getSymbol() {
		return symbol;
	}

	public float getColor() {
		return color;
	}

	public Vector getLocation() {
		return location;
	}

	public double getHeight() {
		return height;
	}

	public Direction getHeading() {
		return heading;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public double getHealth() {
		return health;
	}

	public long getTimeLeft(TimeUnit unit) {
		return unit.convert(timeLeft, TimeUnit.NANOSECONDS);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PlayerState))
			return false;
		PlayerState state = (PlayerState) o;
		return symbol == state.symbol && height == state.height && heading.equals(state.heading)
				&& inventory.equals(state.inventory) && health == state.health && timeLeft == state.timeLeft;
	}

	@Override
	public int hashCode() {
		int result = Character.hashCode(symbol);
		result = result * 31 + Double.hashCode(height);
		result = result * 31 + heading.hashCode();
		result = result * 31 + inventory.hashCode();
		result = result * 31 + Double.hashCode(health);
		return result * 31 + Long.hashCode(timeLeft);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("PlayerState[symbol=");
		result.append(symbol);
		result.append(",location=");
		result.append(location);
		result.append(",height=");
		result.append(height);
		result.append(",heading=");
		result.append(heading);
		result.append(",inventory=");
		result.append(inventory);
		result.append(",health=");
		result.append(health);
		result.append(",timeLeft=");
		result.append(timeLeft);
		result.append("ns]");
		return result.toString();
	}

	private static final long serialVersionUID = 1L;

}
