package org.bitbrawl.foodfight.server;

public enum Division {
	HIGH_SCHOOL(1, "High school"),
	COLLEGE(2, "College"),
	NONE(3, "None");

	private final int id;
	private final String name;

	private Division(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public static Division byId(int id) {
		switch (id) {
		case 1:
			return HIGH_SCHOOL;
		case 2:
			return COLLEGE;
		case 3:
			return NONE;
		}
		throw new IllegalArgumentException("Invalid division id " + id);
	}

	@Override
	public String toString() {
		return name;
	}

}
