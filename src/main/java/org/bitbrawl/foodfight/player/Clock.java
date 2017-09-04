package org.bitbrawl.foodfight.player;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.bitbrawl.foodfight.field.Field;

public final class Clock {

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private volatile long timeSpentBeforeThisTurn = 0;
	private volatile int turnNumber = 0;
	private volatile long startTime;
	private volatile boolean isRunning = false;

	Clock() {
	}

	void startTurn(int turnNumber) {

		lock.writeLock().lock();
		this.turnNumber = turnNumber;
		startTime = System.nanoTime();
		isRunning = true;
		lock.writeLock().unlock();

	}

	public long getTimeSpent(TimeUnit unit) {
		return unit.convert(getTimeSpent(), TimeUnit.NANOSECONDS);
	}

	private long getTimeSpent() {
		lock.readLock().lock();
		long result = timeSpentBeforeThisTurn;
		if (isRunning)
			result += getTimeSpentThisTurn();
		lock.readLock().unlock();
		return result;
	}

	public long getTimeSpentThisTurn(TimeUnit unit) {
		return unit.convert(getTimeSpentThisTurn(), TimeUnit.NANOSECONDS);
	}

	private long getTimeSpentThisTurn() {
		lock.readLock().lock();
		long result = System.nanoTime() - startTime;
		lock.readLock().unlock();
		return result;
	}

	public long getTimeLeft(TimeUnit unit) {
		return unit.convert(TIME_LIMIT - getTimeSpent(), TimeUnit.NANOSECONDS);
	}

	public long getTimeLeftThisTurn(TimeUnit unit) {
		lock.readLock().lock();
		long timeLeft = TIME_LIMIT - timeSpentBeforeThisTurn;
		long timePerTurn = timeLeft / (Field.TOTAL_TURNS - turnNumber);
		long timeLeftThisTurn = timePerTurn - getTimeSpentThisTurn();
		lock.readLock().unlock();
		return unit.convert(timeLeftThisTurn, TimeUnit.NANOSECONDS);
	}

	void endTurn() {
		lock.writeLock().lock();
		timeSpentBeforeThisTurn += getTimeSpentThisTurn();
		isRunning = false;
		lock.writeLock().unlock();
	}

	boolean isOutOfTime() {
		lock.readLock().lock();
		boolean result = timeSpentBeforeThisTurn > TIME_LIMIT;
		lock.readLock().unlock();
		return result;
	}

	private static final long TIME_LIMIT = TimeUnit.SECONDS.toNanos(60);

}
