package org.bitbrawl.foodfight.player;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
final class Clock {

	// nanoseconds
	private volatile long timeLeft;
	// nanoseconds
	private volatile long startTime;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	Clock(long limit, TimeUnit unit) {
		timeLeft = unit.toNanos(limit);
		startTime = -1;
	}

	@Override
	public String toString() {
		return "Clock[timeLeft=" + timeLeft + ",startTime=" + startTime + "]";
	}

	void start() {
		lock.writeLock().lock();
		try {
			assert startTime < 0;
			assert timeLeft > 0;
			startTime = System.nanoTime();
		} finally {
			lock.writeLock().unlock();
		}
	}

	void endIfRunning() {
		lock.writeLock().lock();
		try {
			if (startTime < 0)
				return;
			long timeSpentThisTurn = System.nanoTime() - startTime;
			timeLeft -= timeSpentThisTurn;
			startTime = -1;
		} finally {
			lock.writeLock().unlock();
		}
	}

	long timeLeft(TimeUnit unit) {
		return unit.convert(timeLeftNanos(), TimeUnit.NANOSECONDS);
	}

	private long timeLeftNanos() {
		lock.readLock().lock();
		try {
			if (startTime < 0)
				return timeLeft;
			long timeSpentThisTurn = System.nanoTime() - startTime;
			return timeLeft - timeSpentThisTurn;
		} finally {
			lock.readLock().unlock();
		}
	}

}
