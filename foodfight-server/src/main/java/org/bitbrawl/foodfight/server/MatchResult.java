package org.bitbrawl.foodfight.server;

public enum MatchResult {
	WIN, TIE, LOSS;

	public static MatchResult byName(String name) {
		switch (name) {
		case "win":
			return WIN;
		case "tie":
			return TIE;
		case "loss":
			return LOSS;
		}
		throw new IllegalArgumentException(name);
	}
}
