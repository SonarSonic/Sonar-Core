package sonar.core.api.energy;

import sonar.core.api.utils.ActionType;

public interface ISonarEnergyStorage {
	
	public long addEnergy(long maxReceive, ActionType action);
	
	public long removeEnergy(long maxExtract, ActionType action);

	public long getEnergyLevel();

	public long getFullCapacity();
	
}
