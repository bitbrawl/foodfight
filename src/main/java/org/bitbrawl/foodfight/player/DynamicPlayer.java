package org.bitbrawl.foodfight.player;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.bitbrawl.foodfight.state.PlayerState;
import org.bitbrawl.foodfight.team.Team;
import org.bitbrawl.foodfight.util.Direction;
import org.bitbrawl.foodfight.util.Vector;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class DynamicPlayer implements Player {

	private final char symbol;
	private final Team team;
	private volatile Vector location;
	private volatile double height;
	private volatile Direction heading;
	private volatile Inventory inventory;
	private volatile double health;
	private volatile long timeLeft;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	DynamicPlayer(char symbol, Team team, Vector location, double height, Direction heading, Inventory inventory,
			double health, long timeLeft) {
		this.symbol = symbol;
		this.team = team;
		this.location = location;
		this.height = height;
		this.heading = heading;
		this.inventory = inventory;
		this.health = health;
		this.timeLeft = timeLeft;
	}

	DynamicPlayer(Player input) {
		symbol = input.getSymbol();
		team = input.getTeam();
		location = input.getLocation();
		height = input.getHeight();
		heading = input.getHeading();
		inventory = input.getInventory();
		health = input.getHealth();
		timeLeft = input.getTimeLeft(TimeUnit.NANOSECONDS);
	}

	@Override
	public final char getSymbol() {
		return symbol;
	}

	@Override
	public final Team getTeam() {
		return team;
	}

	@Override
	public final Vector getLocation() {
		lock.readLock().lock();
		try {
			return location;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public final double getHeight() {
		lock.readLock().lock();
		try {
			return height;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public final Direction getHeading() {
		lock.readLock().lock();
		try {
			return heading;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public final Inventory getInventory() {
		lock.readLock().lock();
		try {
			return inventory;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public final double getHealth() {
		lock.readLock().lock();
		try {
			return health;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public long getTimeLeft(TimeUnit unit) {
		lock.readLock().lock();
		try {
			return unit.convert(timeLeft, TimeUnit.NANOSECONDS);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(getClass().getSimpleName());
		result.append("[id=");
		result.append(team.getSymbol());
		result.append(symbol);
		result.append(",location=");
		result.append(location);
		result.append(",height=");
		lock.readLock().lock();
		try {
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
		} finally {
			lock.readLock().unlock();
		}
		return result.toString();
	}

	@Override
	public PlayerState getState() {
		lock.readLock().lock();
		try {
			return new PlayerState(symbol, team.getState(), location, height, heading, inventory, health, timeLeft,
					TimeUnit.NANOSECONDS);
		} finally {
			lock.readLock().unlock();
		}
	}

	void update(Player state) {
		lock.writeLock().lock();
		try {
			location = state.getLocation();
			height = state.getHeight();
			heading = state.getHeading();
			inventory = state.getInventory();
			health = state.getHealth();
			timeLeft = state.getTimeLeft(TimeUnit.NANOSECONDS);
		} finally {
			lock.writeLock().unlock();
		}
	}

}
