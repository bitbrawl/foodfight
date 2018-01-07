package org.bitbrawl.foodfight.team;

import java.util.Set;

import org.bitbrawl.foodfight.player.Player;
import org.bitbrawl.foodfight.state.TeamState;

public interface Team extends Comparable<Team> {

	public char getSymbol();

	public Set<Player> getPlayers();

	public Score getScore();

	public TeamState getState();

	@Override
	public default int compareTo(Team o) {
		return Character.compare(getSymbol(), o.getSymbol());
	}

}
