package org.bitbrawl.foodfight.field;

import java.util.Set;

public interface Team {

	public char getSymbol();

	public Set<Player> getPlayers();

	public Player getPlayer(char symbol);

	public Table getTable();

	public Score getScore();

}