package sonar.core.utils;

public enum SortingDirection {

	DOWN, //highest first
	UP; //lowest first

	public SortingDirection switchDir() {
		switch (this) {
		case DOWN:
			return UP;
		default:
			return DOWN;
		}
	}
}