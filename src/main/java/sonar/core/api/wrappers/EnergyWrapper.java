package sonar.core.api.wrappers;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

public class EnergyWrapper {

	/** convenience method, gets the energy to be added from the remainder, can return null.
	 * @param inputSize maximum size to add
	 * @param stack the original stack
	 * @param returned the returned stack
	 * @return */
	public StoredEnergyStack getStackToAdd(long inputSize, StoredEnergyStack stack, StoredEnergyStack returned) {
		return null;
	}
	
	public long receiveEnergy(TileEntity tile, long maxReceive, EnumFacing dir, ActionType type) {
		return maxReceive;
	}

	public long extractEnergy(TileEntity tile, long maxExtract, EnumFacing dir, ActionType type) {
		return maxExtract;
	}

	public long transferEnergy(TileEntity from, TileEntity to, EnumFacing dirFrom, EnumFacing dirTo, final long maxTransferRF) {
		return 0;
	}
}