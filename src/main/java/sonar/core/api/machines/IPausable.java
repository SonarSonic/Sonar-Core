package sonar.core.api.machines;

/**
 * for machines which can be paused or stopped altogether
 */
public interface IPausable {
	
    /**
     * when the machine is paused/resumed
     */
    void onPause();

    /**
     * if the machine is running
     */
    boolean isActive();

    /**
     * if the machine is paused
     */
    boolean isPaused();
}
