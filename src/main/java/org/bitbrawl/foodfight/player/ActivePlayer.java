package org.bitbrawl.foodfight.player;

import org.bitbrawl.foodfight.state.PlayerState;
import org.bitbrawl.foodfight.team.Team;

public abstract class ActivePlayer extends DynamicPlayer {

	protected ActivePlayer(Team team, PlayerState state) {
		super(team, state);
	}

	public abstract Action playTurn();

	public static ActivePlayer newDummy(Team team, PlayerState state) {
		return new ActivePlayer(team, state) {
			@Override
			public Action playTurn() {
				return null;
			}
		};
	}

}
