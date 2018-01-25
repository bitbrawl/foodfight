package org.bitbrawl.foodfight.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;

import net.jcip.annotations.Immutable;

public abstract class Controller {

	private final Logger logger;
	private final Clock clock;

	public Controller() {
		logger = loggerCopy.get();
		clock = clockCopy.get();
	}

	public abstract Action playTurn(Field field, Team team, Player player);

	public Logger getLogger() {
		return logger;
	}

	public Clock getClock() {
		return clock;
	}

	static Controller newInstance(Class<? extends Controller> clazz, Logger logger, Clock clock)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		assert clazz != null;
		assert loggerCopy == null;
		assert clockCopy == null;

		try {
			loggerCopy.set(logger);
			clockCopy.set(clock);
			return clazz.getConstructor().newInstance();
		} finally {
			loggerCopy.set(null);
			clockCopy.set(null);
		}

	}

	private static final ThreadLocal<Logger> loggerCopy = new ThreadLocal<>();
	private static final ThreadLocal<Clock> clockCopy = new ThreadLocal<>();

	@Immutable
	public enum Action {

		MOVE_FORWARD("Move forward"),
		MOVE_BACKWARD("Move backward"),
		TURN_LEFT("Turn left"),
		TURN_RIGHT("Turn right"),
		DUCK("Duck"),
		PICKUP_LEFT("Pick up with left hand"),
		PICKUP_RIGHT("Pick up with right hand"),
		THROW_LEFT("Throw from left hand"),
		THROW_RIGHT("Throw from right hand"),
		EAT_LEFT("Eat from left hand"),
		EAT_RIGHT("Eat from right hand");

		private final String name;

		private Action(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		public boolean isMoving() {
			return equals(Action.MOVE_FORWARD) || equals(Action.MOVE_BACKWARD);
		}

		public boolean isTurning() {
			return equals(Action.TURN_LEFT) || equals(Action.TURN_RIGHT);
		}

		public boolean isPickingUp() {
			return equals(Action.PICKUP_LEFT) || equals(Action.PICKUP_RIGHT);
		}

		public boolean isThrowing() {
			return equals(Action.THROW_LEFT) || equals(Action.THROW_RIGHT);
		}

		public boolean isEating() {
			return equals(Action.EAT_LEFT) || equals(Action.EAT_RIGHT);
		}

	}

}
