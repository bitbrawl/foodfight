package org.bitbrawl.foodfight.state;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bitbrawl.foodfight.player.Player;
import org.bitbrawl.foodfight.team.Score;
import org.bitbrawl.foodfight.team.Team;

import net.jcip.annotations.Immutable;

@Immutable
public final class TeamState implements Team, Serializable {

	private final char symbol;
	private Set<Player> players;
	private final Score score;

	public TeamState(char symbol, Collection<? extends PlayerState> players, Score score) {
		this.symbol = symbol;
		this.players = Collections.unmodifiableSet(new LinkedHashSet<>(players));
		this.score = score;
	}

	@Override
	public char getSymbol() {
		return symbol;
	}

	@Override
	public Set<Player> getPlayers() {
		return players;
	}

	@Override
	public Score getScore() {
		return score;
	}

	@Override
	public TeamState getState() {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TeamState))
			return false;
		TeamState team = (TeamState) o;
		return symbol == team.symbol && players.equals(team.players) && score.equals(team.score);
	}

	@Override
	public int hashCode() {
		int result = Character.hashCode(symbol);
		result = result * 31 + players.hashCode();
		return result * 31 + score.hashCode();
	}

	@Override
	public String toString() {
		return "TeamState[symbol=" + symbol + ",players=" + players + ",score=" + score;
	}

	private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
		s.defaultReadObject();

		players = Collections.unmodifiableSet(new LinkedHashSet<>(players));

	}

	private static final long serialVersionUID = 1L;

}
