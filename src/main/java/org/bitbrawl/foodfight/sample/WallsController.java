package org.bitbrawl.foodfight.sample;

import java.util.concurrent.ThreadLocalRandom;

import org.bitbrawl.foodfight.controller.JavaController;
import org.bitbrawl.foodfight.engine.match.Match;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;
import org.bitbrawl.foodfight.util.PlayerUtils;

public final class WallsController extends JavaController {

	private final boolean turnLeft = ThreadLocalRandom.current().nextBoolean();

	@Override
	public Action playAction(Field field, Team team, Player player) {
		if (PlayerUtils.isValidAction(field, player, Action.PICKUP_LEFT))
			return Action.PICKUP_LEFT;
		if (PlayerUtils.isValidAction(field, player, Action.PICKUP_RIGHT))
			return Action.PICKUP_RIGHT;
		if (ThreadLocalRandom.current().nextDouble() < 0.01) {
			if (PlayerUtils.isValidAction(field, player, Action.THROW_LEFT))
				return Action.THROW_LEFT;
			if (PlayerUtils.isValidAction(field, player, Action.THROW_RIGHT))
				return Action.THROW_RIGHT;
		}
		if (PlayerUtils.isValidAction(field, player, Action.MOVE_FORWARD))
			return Action.MOVE_FORWARD;
		if (turnLeft)
			return Action.TURN_LEFT;
		return Action.TURN_RIGHT;
	}

	public static void main(String[] args) {
		runDebugMatch(Match.Type.TEAM, DummyController.class, RandomController.class, HidingController.class,
				WallsController.class);
	}

}
