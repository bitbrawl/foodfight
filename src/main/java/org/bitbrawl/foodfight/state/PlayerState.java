package org.bitbrawl.foodfight.state;

import java.util.concurrent.TimeUnit;

import org.bitbrawl.foodfight.player.Inventory;
import org.bitbrawl.foodfight.player.Player;
import org.bitbrawl.foodfight.team.Team;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.Immutable;

@Immutable
public final class PlayerState implements Player {

	private final char symbol;
	private final Team team;
	private final Vector location;
	private final double height;
	private final Direction heading;
	private final Inventory inventory;
	private final double health;
	private final long timeLeft;

	public PlayerState(char symbol, Team team, Vector location, double height, Direction heading, Inventory inventory,
			double health, long timeLeft, TimeUnit unit) {
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
	public Team getTeam() {
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

}
