package org.bitbrawl.foodfight.util;

import java.util.Comparator;

import org.bitbrawl.foodfight.field.Team;

import net.jcip.annotations.Immutable;

@Immutable
public enum TeamComparator implements Comparator<Team> {

	INSTANCE;

	@Override
	public int compare(Team t1, Team t2) {

		return Character.compare(t1.getSymbol(), t2.getSymbol());

	}

}
