package org.bitbrawl.foodfight.engine.field;

import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

public final class PlayerState implements Player {

	private final char symbol;
	private final Vector location;
	private final double height;
	private final Direction heading;
	private final InventoryState inventory;
	private final double health;

	public PlayerState(char symbol, Vector location, double height, Direction heading, InventoryState inventory,
			double health) {
		this.symbol = symbol;
		this.location = location;
		this.height = height;
		this.heading = heading;
		this.inventory = inventory;
		this.health = health;
	}

	@SuppressWarnings("unused")
	private PlayerState() {
		symbol = 0;
		location = null;
		height = Double.NaN;
		heading = null;
		inventory = null;
		health = Double.NaN;
	}

	@Override
	public char getSymbol() {
		return symbol;
	}

	@Override
	public Vector getLocation() {
		return location;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public Direction getHeading() {
		return heading;
	}

	@Override
	public InventoryState getInventory() {
		return inventory;
	}

	@Override
	public double getHealth() {
		return health;
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
		result.append(']');
		return result.toString();
	}

}
