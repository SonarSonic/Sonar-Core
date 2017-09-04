package sonar.core.api.energy;

import net.minecraft.util.EnumFacing;
import sonar.core.network.sync.SyncEnergyStorage;

public interface ISonarEnergyTile {

    EnergyMode getModeForSide(EnumFacing side);
	
    SyncEnergyStorage getStorage();
	
    EnergyMode getMode();
}
