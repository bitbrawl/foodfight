package org.bitbrawl.foodfight.state;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bitbrawl.foodfight.field.Collision;
import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.FoodPiece;
import org.bitbrawl.foodfight.player.Player;
import org.bitbrawl.foodfight.team.Team;

import net.jcip.annotations.Immutable;

@Immutable
public final class FieldState implements Field, Serializable {

	private final int turnNumber;
	private Set<Team> teams;
	private transient Set<Player> players;
	private Set<FoodPiece> foods;
	private Set<Collision> collisions;

	public FieldState(int turnNumber, Collection<? extends TeamState> teams, Collection<? extends FoodState> foods,
			Collection<? extends Collision> collisions) {
		this.turnNumber = turnNumber;
		this.teams = Collections.unmodifiableSet(new LinkedHashSet<>(teams));
		Set<Player> tempPlayers = new LinkedHashSet<>();
		for (TeamState team : teams)
			tempPlayers.addAll(team.getPlayers());
		this.players = Collections.unmodifiableSet(tempPlayers);
		this.foods = Collections.unmodifiableSet(new LinkedHashSet<>(foods));
		this.collisions = Collections.unmodifiableSet(new LinkedHashSet<>(collisions));
	}

	@Override
	public int getTurnNumber() {
		return turnNumber;
	}

	@Override
	public Set<Team> getTeams() {
		return teams;
	}

	@Override
	public Set<Player> getPlayers() {
		return players;
	}

	@Override
	public Set<FoodPiece> getFood() {
		return foods;
	}

	@Override
	public Set<Collision> getCollisions() {
		return collisions;
	}

	@Override
	public FieldState getState() {
		return this;
	}

	private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
		s.defaultReadObject();

		teams = Collections.unmodifiableSet(new LinkedHashSet<>(teams));
		Set<Player> tempPlayers = new LinkedHashSet<>();
		for (Team team : teams)
			tempPlayers.addAll(team.getPlayers());
		players = Collections.unmodifiableSet(tempPlayers);
		foods = Collections.unmodifiableSet(new LinkedHashSet<>(foods));
		collisions = Collections.unmodifiableSet(new LinkedHashSet<>(collisions));

	}

	private static final long serialVersionUID = 1L;

}
