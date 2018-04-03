package org.bitbrawl.foodfight.field;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface Field {

	public int getTurnNumber();

	public Set<Team> getTeams();

	public Team getTeam(char symbol);

	public Team getTeam(Player player);

	public Set<Player> getPlayers();

	public Player getPlayer(char symbol);

	public Set<Food> getFood();

	public Food getFood(Food.Type type);

	public Set<Collision> getCollisions();

	public static final double DEPTH = 1080.0;
	public static final double WIDTH = 1440.0;
	public static final int MAX_FOOD = 4;
	public static final int TOTAL_TURNS = 2000;
	// seconds
	public static final long TIME_LIMIT_NANOS = TimeUnit.SECONDS.toNanos(60L);

}
