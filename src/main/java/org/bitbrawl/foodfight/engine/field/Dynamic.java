package org.bitbrawl.foodfight.engine.field;

public interface Dynamic<T> {

	public T getState();

	public void update(T state);

}
