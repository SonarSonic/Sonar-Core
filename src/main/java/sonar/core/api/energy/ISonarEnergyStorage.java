package sonar.core.api.energy;

import sonar.core.api.utils.ActionType;

public interface ISonarEnergyStorage {
	
    long addEnergy(long maxReceive, ActionType action);
	
    long removeEnergy(long maxExtract, ActionType action);

    long getEnergyLevel();
	
    long getFullCapacity();
}
