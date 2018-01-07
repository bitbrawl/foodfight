package org.bitbrawl.foodfight.state;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bitbrawl.foodfight.team.Score;
import org.bitbrawl.foodfight.team.Team;

import net.jcip.annotations.Immutable;

@Immutable
public final class TeamState implements Team, Serializable {

	private final char symbol;
	private final float color;
	private Collection<PlayerState> players;
	private final Score score;

	public TeamState(char symbol, float color, Collection<PlayerState> players, Score score) {
		this.symbol = symbol;
		this.color = color;
		this.players = Collections.unmodifiableList(new ArrayList<>(players));
		this.score = score;
	}

	@Override
	public char getSymbol() {
		return symbol;
	}

	@Override
	public float getColor() {
		return color;
	}

	@Override
	public Collection<PlayerState> getPlayers() {
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

	private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
		s.defaultReadObject();

		players = Collections.unmodifiableList(new ArrayList<>(players));

	}

	private static final long serialVersionUID = 1L;

}
