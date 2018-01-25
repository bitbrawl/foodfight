package org.bitbrawl.foodfight.field;

import java.util.Set;

public interface Field {

	public int getTurnNumber();

	public Set<Team> getTeams();

	public Set<Player> getPlayers();

	public Set<Food> getFood();

	public Set<Collision> getCollisions();

	public static final double DEPTH = 1080.0;
	public static final double WIDTH = 1440.0;
	public static final int MAX_FOOD = 4;
	public static final int TOTAL_TURNS = 1000;
	// seconds
	public static final long TIME_LIMIT = 60L;

}
