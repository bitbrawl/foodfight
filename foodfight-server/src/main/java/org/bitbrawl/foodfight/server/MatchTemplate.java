package org.bitbrawl.foodfight.server;

import org.bitbrawl.foodfight.engine.field.FieldState;
import org.bitbrawl.foodfight.engine.match.CharFunction;

public final class MatchTemplate {

	private final int matchId;
	private final FieldState field;
	private final CharFunction<Competitor> competitors;

	public MatchTemplate(int matchId, FieldState field, CharFunction<Competitor> competitors) {
		this.matchId = matchId;
		this.field = field;
		this.competitors = competitors;
	}

	public int getMatchId() {
		return matchId;
	}

	public FieldState getField() {
		return field;
	}

	public CharFunction<Competitor> getCompetitors() {
		return competitors;
	}

}
