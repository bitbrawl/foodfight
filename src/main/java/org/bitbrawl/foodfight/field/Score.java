package org.bitbrawl.foodfight.field;

public interface Score {

	public int getCount(Event event);

	public int getPoints(Event event);

	public int getTotalPoints();

}
