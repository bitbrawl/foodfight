package org.bitbrawl.foodfight.adapter;

public final class IllegalMethodException extends RuntimeException {

	public IllegalMethodException(String methodName) {
		super("Illegal method: " + methodName);
	}

	private static final long serialVersionUID = 1L;

}
