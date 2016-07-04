package sonar.core.api.wrappers;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.EnergyHandler;
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
	/** returns amount received **/
	public long receiveEnergy(TileEntity tile, long maxReceive, EnumFacing dir, ActionType type) {
		return 0;
	}

	/** returns amount extracted **/
	public long extractEnergy(TileEntity tile, long maxExtract, EnumFacing dir, ActionType type) {
		return 0;
	}

	public long transferEnergy(TileEntity from, TileEntity to, EnumFacing dirFrom, EnumFacing dirTo, final long maxTransferRF) {
		return 0;
	}

	public EnergyHandler canTransferEnergy(TileEntity tile, EnumFacing dir) {
		return null;
	}
}