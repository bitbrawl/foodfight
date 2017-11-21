package org.bitbrawl.foodfight.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bitbrawl.foodfight.team.Score;
import org.bitbrawl.foodfight.team.Team;

import net.jcip.annotations.Immutable;

@Immutable
public final class TeamState implements Team {

	private final char symbol;
	private final float color;
	private final Collection<PlayerState> players;
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

}
