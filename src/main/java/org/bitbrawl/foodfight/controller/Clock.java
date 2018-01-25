package org.bitbrawl.foodfight.controller;

import java.util.concurrent.TimeUnit;

public interface Clock {

	public long getTimeLeft(TimeUnit unit);

}
