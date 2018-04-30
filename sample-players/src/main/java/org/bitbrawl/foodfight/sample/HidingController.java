package org.bitbrawl.foodfight.sample;

import org.bitbrawl.foodfight.controller.JavaController;
import org.bitbrawl.foodfight.engine.match.DebugUtils;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.MatchType;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;
import org.bitbrawl.foodfight.util.PlayerUtils;

public final class HidingController extends JavaController {

	@Override
	public Action playAction(Field field, Team team, Player player) {
		if (PlayerUtils.isValidAction(field, player, Action.MOVE_FORWARD))
			return Action.MOVE_FORWARD;
		return Action.DUCK;
	}

	public static void main(String[] args) {
		DebugUtils.runDebugMatch(MatchType.FREE_FOR_ALL, DummyController.class, RandomController.class,
				HidingController.class);
	}

}
