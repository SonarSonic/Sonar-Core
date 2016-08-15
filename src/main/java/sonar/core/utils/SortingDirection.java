package sonar.core.utils;

public enum SortingDirection {
	DOWN, UP;

	public SortingDirection switchDir() {
		switch (this) {
		case DOWN:
			return UP;
		default:
			return DOWN;
		}
	}
}