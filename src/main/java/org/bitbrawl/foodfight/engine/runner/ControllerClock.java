package org.bitbrawl.foodfight.engine.runner;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.bitbrawl.foodfight.controller.Clock;

public final class ControllerClock implements Clock {

	private volatile long timeLeft;
	private volatile long startTime;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	public ControllerClock(long limit, TimeUnit unit) {
		timeLeft = unit.toNanos(limit);
		startTime = -1;
	}

	@Override
	public long getTimeLeft(TimeUnit unit) {
		return unit.convert(getTimeLeftNanos(), TimeUnit.NANOSECONDS);
	}

	@Override
	public String toString() {
		return "ControllerClock[timeLeft=" + getTimeLeftNanos() + "ns]";
	}

	void start() {
		Lock writeLock = lock.writeLock();
		writeLock.lock();
		try {
			assert startTime < 0;
			assert timeLeft > 0;
			startTime = System.nanoTime();
		} finally {
			writeLock.unlock();
		}
	}

	void end() {
		Lock writeLock = lock.writeLock();
		writeLock.lock();
		try {
			if (startTime < 0)
				return;
			long timeSpentThisTurn = System.nanoTime() - startTime;
			timeLeft -= timeSpentThisTurn;
			startTime = -1;
		} finally {
			writeLock.unlock();
		}
	}

	private long getTimeLeftNanos() {
		Lock readLock = lock.readLock();
		readLock.lock();
		try {
			if (startTime < 0)
				return timeLeft;
			long timeSpentThisTurn = System.nanoTime() - startTime;
			return timeLeft - timeSpentThisTurn;
		} finally {
			readLock.unlock();
		}
	}

}
