package org.bitbrawl.foodfight.state;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bitbrawl.foodfight.field.Collision;

import net.jcip.annotations.Immutable;

@Immutable
public final class FieldState implements Serializable {

	private final int turnNumber;
	private Set<TeamState> teams;
	private Set<FoodState> food;
	private Set<Collision> collisions;

	public FieldState(int turnNumber, Collection<? extends TeamState> teams, Collection<? extends FoodState> food,
			Collection<? extends Collision> collisions) {
		this.turnNumber = turnNumber;
		this.teams = Collections.unmodifiableSet(new LinkedHashSet<>(teams));
		this.food = Collections.unmodifiableSet(new LinkedHashSet<>(food));
		this.collisions = Collections.unmodifiableSet(new LinkedHashSet<>(collisions));
	}

	public int getTurnNumber() {
		return turnNumber;
	}

	public Set<TeamState> getTeams() {
		return teams;
	}

	public Set<PlayerState> getPlayers() {

		return new AbstractSet<PlayerState>() {

			@Override
			public Iterator<PlayerState> iterator() {
				return new Iterator<PlayerState>() {

					private final Iterator<TeamState> teamIt = getTeams().iterator();
					private Iterator<PlayerState> playerIt = teamIt.next().getPlayers().iterator();

					@Override
					public boolean hasNext() {
						if (playerIt.hasNext())
							return true;
						if (!teamIt.hasNext())
							return false;
						playerIt = teamIt.next().getPlayers().iterator();
						return playerIt.hasNext();
					}

					@Override
					public PlayerState next() {
						if (playerIt.hasNext())
							return playerIt.next();
						playerIt = teamIt.next().getPlayers().iterator();
						return playerIt.next();
					}

				};
			}

			@Override
			public int size() {
				return getTeams().stream().mapToInt(team -> team.getPlayers().size()).sum();
			}

		};

	}

	public Set<FoodState> getFood() {
		return food;
	}

	public Set<Collision> getCollisions() {
		return collisions;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("FieldState[turnNumber=");
		result.append(turnNumber);
		result.append(",teams=");
		result.append(teams);
		result.append(",food=");
		result.append(food);
		result.append(",collisions=");
		result.append(collisions);
		result.append(']');
		return result.toString();
	}

	private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
		s.defaultReadObject();

		teams = Collections.unmodifiableSet(new LinkedHashSet<>(teams));
		food = Collections.unmodifiableSet(new LinkedHashSet<>(food));
		collisions = Collections.unmodifiableSet(new LinkedHashSet<>(collisions));

	}

	private static final long serialVersionUID = 1L;

}
