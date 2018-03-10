package org.bitbrawl.foodfight.engine.match;

import java.util.Arrays;
import java.util.List;

public final class FieldZone {

	private final double xMin, xMax, yMin, yMax;

	public FieldZone(double xMin, double xMax, double yMin, double yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}

	public double getXMin() {
		return xMin;
	}

	public double getXMax() {
		return xMax;
	}

	public double getYMin() {
		return yMin;
	}

	public double getYMax() {
		return yMax;
	}

	public List<FieldZone> split() {

		if (xMax - xMin > yMax - yMin) {
			double xMid = (xMin + xMax) / 2.0;
			return Arrays.asList(new FieldZone(xMin, xMid, yMin, yMax), new FieldZone(xMid, xMax, yMin, yMax));
		} else {
			double yMid = (yMin + yMax) / 2.0;
			return Arrays.asList(new FieldZone(xMin, xMax, yMin, yMid), new FieldZone(xMin, xMax, yMid, yMax));
		}
	}

}
