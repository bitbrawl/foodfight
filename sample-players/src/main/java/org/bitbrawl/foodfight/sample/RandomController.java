package org.bitbrawl.foodfight.sample;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import org.bitbrawl.foodfight.controller.JavaController;
import org.bitbrawl.foodfight.engine.match.DebugUtils;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.MatchType;
import org.bitbrawl.foodfight.field.Player;
import org.bitbrawl.foodfight.field.Team;
import org.bitbrawl.foodfight.util.PlayerUtils;

public final class RandomController extends JavaController {

	@Override
	public Action playAction(Field field, Team team, Player player) {

		Action[] actions = Action.values();
		Collections.shuffle(Arrays.asList(actions), ThreadLocalRandom.current());
		for (Action action : actions)
			if (PlayerUtils.isValidAction(field, player, action))
				return action;
		return null;

	}

	public static void main(String[] args) {
		DebugUtils.runDebugMatch(MatchType.DUEL, RandomController.class);
	}

}
