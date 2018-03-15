package org.bitbrawl.foodfight.engine.field;

import java.util.Objects;

import org.bitbrawl.foodfight.field.Event;
import org.bitbrawl.foodfight.field.Score;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public final class DynamicScore implements Score, Dynamic<ScoreState> {

	private ScoreState state;

	public DynamicScore(ScoreState state) {
		this.state = state;
	}

	@Override
	public int getCount(Event event) {
		return state.getCount(event);
	}

	@Override
	public int getPoints(Event event) {
		return state.getPoints(event);
	}

	@Override
	public int getTotalPoints() {
		return state.getTotalPoints();
	}

	@Override
	public ScoreState getState() {
		return state;
	}

	@Override
	public void update(ScoreState state) {
		Objects.requireNonNull(state, "state cannot be null");
		this.state = state;
	}

	@Override
	public String toString() {
		return "DynamicScore[totalPoints=" + state.getTotalPoints() + ']';
	}

}
