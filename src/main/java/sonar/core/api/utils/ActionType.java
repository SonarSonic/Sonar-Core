package sonar.core.api.utils;

public enum ActionType {
	
    /**
     * simulates the intended action
     */
	SIMULATE,
    /**
     * performs the intended action
     */
	PERFORM;
	
	public boolean shouldSimulate() {
		switch (this) {
		case PERFORM:
			return false;
		default:
			return true;
		}
	}
	
	public static ActionType getTypeForAction(boolean simulate){
        if (simulate) {
			return SIMULATE;
		}			
		return PERFORM;
	}
}
