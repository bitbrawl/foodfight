package org.bitbrawl.foodfight.engine.match;

@FunctionalInterface
public interface CharFunction<R> {

	public R apply(char c);

}
