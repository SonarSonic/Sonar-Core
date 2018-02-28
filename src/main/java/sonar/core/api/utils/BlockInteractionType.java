package sonar.core.api.utils;

/** useful for organising block interaction involving right clicking, left clicking etc */
public enum BlockInteractionType {

	RIGHT, LEFT, SHIFT_RIGHT, SHIFT_LEFT;

	public boolean isLeft() {
		return this == LEFT || this == SHIFT_LEFT;
	}

	public boolean isRight() {
		return this == RIGHT || this == SHIFT_RIGHT;
	}

	public boolean isShifting() {
		return this == SHIFT_RIGHT || this == SHIFT_LEFT;
	}
}
