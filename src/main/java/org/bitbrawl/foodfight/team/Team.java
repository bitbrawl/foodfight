package org.bitbrawl.foodfight.team;

import java.util.Collection;

import org.bitbrawl.foodfight.player.Player;
import org.bitbrawl.foodfight.state.TeamState;

public interface Team {

	public char getSymbol();

	public float getColor();

	public Collection<? extends Player> getPlayers();

	public Score getScore();

	public TeamState getState();

}
