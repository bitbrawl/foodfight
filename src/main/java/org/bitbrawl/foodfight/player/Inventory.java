package org.bitbrawl.foodfight.player;

import java.util.Objects;

import org.bitbrawl.foodfight.field.FoodType;

public class Inventory {

	private final FoodType left, right;

	public Inventory() {
		this(null, null);
	}

	public Inventory(FoodType leftHand, FoodType rightHand) {
		this.left = leftHand;
		this.right = rightHand;
	}

	public FoodType get(Hand hand) {
		Objects.requireNonNull(hand, "hand cannot be null");

		switch (hand) {
		case LEFT:
			return left;
		case RIGHT:
			return right;
		default:
			throw new AssertionError();
		}

	}

	@Override
	public String toString() {
		return "Inventory[left=" + left + ",right=" + right + "]";
		// return "(" + leftHand + ", " + rightHand + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Inventory))
			return false;
		Inventory inventory = (Inventory) o;
		return left.equals(inventory.left) && right.equals(inventory.right);
	}

	@Override
	public int hashCode() {
		return left.hashCode() * 31 + right.hashCode();
	}

	public Inventory add(FoodType food, Hand hand) {
		Objects.requireNonNull(food, "food cannot be null");
		Objects.requireNonNull(hand, "hand cannot be null");

		switch (hand) {
		case LEFT:
			if (left == null)
				return new Inventory(food, right);
			throw new IllegalStateException("Left hand already full");
		case RIGHT:
			if (right == null)
				return new Inventory(left, food);
			throw new IllegalStateException("Right hand already full");

		default:
			throw new AssertionError();
		}

	}

	public Inventory remove(Hand hand) {
		Objects.requireNonNull(hand, "hand cannot be null");

		switch (hand) {
		case LEFT:
			if (left == null)
				throw new IllegalStateException("Left hand already empty");
			return new Inventory(null, right);
		case RIGHT:
			if (right == null)
				throw new IllegalStateException("Right hand already empty");
			return new Inventory(left, null);
		default:
			throw new AssertionError();
		}

	}

}
