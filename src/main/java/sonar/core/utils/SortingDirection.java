package sonar.core.utils;

public enum SortingDirection {
	
	/**highest first*/
	DOWN, 
	/**lowest first*/
	UP;

	public SortingDirection switchDir() {
		switch (this) {
		case DOWN:
			return UP;
		default:
			return DOWN;
		}
	}
}