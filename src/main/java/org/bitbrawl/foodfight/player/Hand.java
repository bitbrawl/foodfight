package org.bitbrawl.foodfight.player;

import net.jcip.annotations.Immutable;

@Immutable
public enum Hand {

	LEFT("Left hand"), RIGHT("Right hand");

	private final String name;

	private Hand(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
