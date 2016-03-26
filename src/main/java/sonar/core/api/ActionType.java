package sonar.core.api;

public enum ActionType {
	/** performs the intended action */
	PERFORM,
	/** simulates the intended action */
	SIMULATE;

	public boolean shouldSimulate() {
		switch (this) {
		case PERFORM:
			return false;
		default:
			return true;
		}
	}
}
