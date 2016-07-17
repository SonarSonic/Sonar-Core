package sonar.core.api.energy;

import net.minecraft.util.EnumFacing;
import sonar.core.network.sync.SyncEnergyStorage;

public interface ISonarEnergyTile {

	public EnergyMode getModeForSide(EnumFacing side);
	
	public SyncEnergyStorage getStorage();
	
	public EnergyMode getMode();
}
