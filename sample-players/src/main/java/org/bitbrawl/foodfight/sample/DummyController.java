package org.bitbrawl.foodfight.sample;

import org.bitbrawl.foodfight.controller.JavaController;
import org.bitbrawl.foodfight.engine.match.DebugUtils;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.MatchType;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;

public final class DummyController extends JavaController {

	@Override
	public Action playAction(Field field, Team team, Player player) {
		return null;
	}

	public static void main(String[] args) {
		DebugUtils.runDebugMatch(MatchType.DUEL, DummyController.class, RandomController.class);
	}

}
