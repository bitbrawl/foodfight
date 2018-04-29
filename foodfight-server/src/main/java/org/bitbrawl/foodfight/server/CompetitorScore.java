package org.bitbrawl.foodfight.server;

import java.util.EnumMap;
import java.util.Map;

final class CompetitorScore {

	private final Division division;
	private final Map<MatchResult, Integer> counts = new EnumMap<>(MatchResult.class);

	CompetitorScore(Division division) {
		this.division = division;
	}

	Division getDivision() {
		return division;
	}

	void addResult(MatchResult result, int count) {
		counts.merge(result, count, Integer::sum);
	}

	int getCount(MatchResult result) {
		return counts.getOrDefault(result, 0);
	}

	int getScore() {
		return getCount(MatchResult.WIN) - getCount(MatchResult.LOSS);
	}

}
