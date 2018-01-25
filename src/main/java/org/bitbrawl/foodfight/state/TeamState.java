package org.bitbrawl.foodfight.state;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bitbrawl.foodfight.team.Score;

import net.jcip.annotations.Immutable;

@Immutable
public final class TeamState implements Serializable {

	private final char symbol;
	private final float color;
	private Set<PlayerState> players;
	private final TableState table;
	private final Score score;

	public TeamState(char symbol, float color, Collection<? extends PlayerState> players, TableState table,
			Score score) {
		this.symbol = symbol;
		this.color = color;
		this.players = Collections.unmodifiableSet(new LinkedHashSet<>(players));
		this.table = table;
		this.score = score;
	}

	public char getSymbol() {
		return symbol;
	}

	public float getColor() {
		return color;
	}

	public Set<PlayerState> getPlayers() {
		return players;
	}

	public TableState getTable() {
		return table;
	}

	public Score getScore() {
		return score;
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
