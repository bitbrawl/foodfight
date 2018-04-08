package org.bitbrawl.foodfight.engine.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bitbrawl.foodfight.engine.field.FieldState;

import net.jcip.annotations.Immutable;

@Immutable
public final class MatchHistory {

	private final int matchNumber;
	private final List<FieldState> fieldStates;

	public MatchHistory(int matchNumber, List<? extends FieldState> fieldStates) {
		this.matchNumber = matchNumber;
		this.fieldStates = Collections.unmodifiableList(new ArrayList<>(fieldStates));
	}

	public int getMatchNumber() {
		return matchNumber;
	}

	public List<FieldState> getFieldStates() {
		return fieldStates;
	}

	public FieldState getFinalState() {
		return fieldStates.get(fieldStates.size() - 1);
	}

}
