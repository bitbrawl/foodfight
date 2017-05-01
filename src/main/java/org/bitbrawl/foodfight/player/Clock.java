package org.bitbrawl.foodfight.player;

import org.bitbrawl.foodfight.field.Field;

public final class Clock {

	private long timeSpentBeforeThisTurn = 0, timeSpentThisTurn = 0;
	private int turnNumber = 0;

	Clock() {
	}

	void startTurn(int turnNumber) {
		this.turnNumber = turnNumber;
		timeSpentBeforeThisTurn += timeSpentThisTurn;
		timeSpentThisTurn = 0;
	}

	public void increment() {
		timeSpentThisTurn++;
	}

	public long getTotalTimePointsSpent() {
		return timeSpentBeforeThisTurn + timeSpentThisTurn;
	}

	public double getTimeLeft() {
		return (double) getTotalTimePointsSpent() / TOTAL_TIME_POINTS;
	}

	public double getTimeLeftThisTurn() {
		long timeLeft = TOTAL_TIME_POINTS - timeSpentBeforeThisTurn;
		long timePerTurn = timeLeft / (Field.TOTAL_TURNS - turnNumber);
		return (double) (timePerTurn - timeSpentThisTurn) / timePerTurn;
	}

	public static final long TOTAL_TIME_POINTS = 1_000_000_000;

}
