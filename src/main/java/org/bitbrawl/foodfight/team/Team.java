package org.bitbrawl.foodfight.team;

import java.util.Set;

import org.bitbrawl.foodfight.field.Table;
import org.bitbrawl.foodfight.player.Player;
import org.bitbrawl.foodfight.state.TeamState;

public interface Team {

	public char getSymbol();

	public float getColor();

	public Set<Player> getPlayers();

	public Table getTable();

	public Score getScore();

	public TeamState getState();

}
