package org.bitbrawl.foodfight.controller;

import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;

import net.jcip.annotations.Immutable;

public interface Controller {

	public Action playAction(Field field, Team team, Player player);

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
