package org.bitbrawl.foodfight.util;

import java.util.Comparator;

import org.bitbrawl.foodfight.field.Field;
import org.bitbrawl.foodfight.field.Player;

import net.jcip.annotations.Immutable;

@Immutable
public final class PlayerComparator implements Comparator<Player> {

	private final Field field;

	public PlayerComparator(Field field) {
		this.field = field;
	}

	@Override
	public int compare(Player p1, Player p2) {

		int teamCompare = Comparator.nullsFirst(TeamComparator.INSTANCE).compare(field.getTeam(p1), field.getTeam(p2));
		if (teamCompare == 0)
			return Character.compare(p1.getSymbol(), p2.getSymbol());
		return teamCompare;

	}

}
