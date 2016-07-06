package sonar.core.api.energy;

import sonar.core.common.tileentity.TileEntityEnergy.EnergyMode;
import sonar.core.network.sync.SyncEnergyStorage;

public interface ISonarEnergyTile {

	public SyncEnergyStorage getStorage();
	
	public EnergyMode getMode();
}
