package org.bitbrawl.foodfight.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bitbrawl.foodfight.field.Player;

import net.jcip.annotations.Immutable;

/**
 * A comparator that can be used to order players by their symbols. For example,
 * if you had a list of players, you could sort it by calling
 * {@link Collections#sort(List, Comparator)}, passing in
 * {@link PlayerComparator#INSTANCE} as your comparator.
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals,
 * because any two players that have the same symbol will be treated as equal.
 * However, all players passed to competitors are uniquely identified by their
 * symbols, so this is not an issue unless you create your own implementation of
 * the {@link Player} interface.
 * 
 * @author Finn
 */
@Immutable
public enum PlayerComparator implements Comparator<Player> {

	/** The singleton instance of this class. */
	INSTANCE;

	@Override
	public int compare(Player p1, Player p2) {
		return Character.compare(p1.getSymbol(), p2.getSymbol());
	}

}
