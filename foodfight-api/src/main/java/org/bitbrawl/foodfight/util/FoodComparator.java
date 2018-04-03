package org.bitbrawl.foodfight.util;

import java.util.Comparator;

import org.bitbrawl.foodfight.field.Food;

import net.jcip.annotations.Immutable;

@Immutable
public enum FoodComparator implements Comparator<Food> {

	INSTANCE;

	@Override
	public int compare(Food f1, Food f2) {

		if (f1 == null) {
			if (f2 == null)
				return 0;
			return -1;
		}
		if (f2 == null)
			return 1;

		return f1.getType().compareTo(f2.getType());

	}

}
