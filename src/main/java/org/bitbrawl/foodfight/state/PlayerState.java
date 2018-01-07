package org.bitbrawl.foodfight.state;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.bitbrawl.foodfight.player.Inventory;
import org.bitbrawl.foodfight.player.Player;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.Immutable;

@Immutable
public final class PlayerState implements Player, Serializable {

	private final char symbol;
	private final TeamState team;
	private final Vector location;
	private final double height;
	private final Direction heading;
	private final Inventory inventory;
	private final double health;
	private final long timeLeft;

	public PlayerState(char symbol, TeamState team, Vector location, double height, Direction heading,
			Inventory inventory, double health, long timeLeft, TimeUnit unit) {
		this.symbol = symbol;
		this.team = team;
		this.location = location;
		this.height = height;
		this.heading = heading;
		this.inventory = inventory;
		this.health = health;
		this.timeLeft = unit.toNanos(timeLeft);
	}

	@Override
	public Vector getLocation() {
		return location;
	}

	@Override
	public char getSymbol() {
		return symbol;
	}

	@Override
	public TeamState getTeam() {
		return team;
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
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public double getHealth() {
		return health;
	}

	@Override
	public long getTimeLeft(TimeUnit unit) {
		return unit.convert(timeLeft, TimeUnit.NANOSECONDS);
	}

	@Override
	public PlayerState getState() {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PlayerState))
			return false;
		PlayerState state = (PlayerState) o;
		return symbol == state.symbol && team.equals(state.team) && height == state.height
				&& heading.equals(state.heading) && inventory.equals(state.inventory) && health == state.health
				&& timeLeft == state.timeLeft;
	}

	@Override
	public int hashCode() {
		int result = Character.hashCode(symbol);
		result = result * 31 + team.hashCode();
		result = result * 31 + Double.hashCode(height);
		result = result * 31 + heading.hashCode();
		result = result * 31 + inventory.hashCode();
		result = result * 31 + Double.hashCode(health);
		return result * 31 + Long.hashCode(timeLeft);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("PlayerState[id=");
		result.append(team.getSymbol());
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
