package sonar.core.api.machines;

/**
 * implemented on machines which have progress bars
 */
public interface IProcessMachine {

    /**
     * current process
     */
    int getCurrentProcessTime();

    /**
     * current speed
     */
    int getProcessTime();

    /**
     * normal speed of the machine
     */
    int getBaseProcessTime();
	
    /**
     * current energy usage in RF per tick (can be less than 1)
     */
    double getEnergyUsage();
}