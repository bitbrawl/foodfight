package org.bitbrawl.foodfight.player;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
final class Clock {

	private volatile long timeLeftNanos;
	private volatile long startTimeNanos;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	Clock(long limit, TimeUnit unit) {
		timeLeftNanos = unit.toNanos(limit);
		startTimeNanos = -1;
	}

	void start() {
		lock.writeLock().lock();
		try {
			assert startTimeNanos < 0;
			assert timeLeftNanos > 0;
			startTimeNanos = System.nanoTime();
		} finally {
			lock.writeLock().unlock();
		}
	}

	void endIfRunning() {
		lock.writeLock().lock();
		try {
			if (startTimeNanos < 0)
				return;
			long timeSpentThisTurn = System.nanoTime() - startTimeNanos;
			timeLeftNanos -= timeSpentThisTurn;
			startTimeNanos = -1;
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
			if (startTimeNanos < 0)
				return timeLeftNanos;
			long timeSpentThisTurn = System.nanoTime() - startTimeNanos;
			return timeLeftNanos - timeSpentThisTurn;
		} finally {
			lock.readLock().unlock();
		}
	}

}
