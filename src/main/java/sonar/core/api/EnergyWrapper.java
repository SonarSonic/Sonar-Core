package sonar.core.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.helpers.InventoryHelper.IInventoryFilter;

public class EnergyWrapper {

	/** convenience method, gets the energy to be added from the remainder, can return null.
	 * @param inputSize maximum size to add
	 * @param stack the original stack
	 * @param returned the returned stack
	 * @return */
	public StoredEnergyStack getStackToAdd(long inputSize, StoredEnergyStack stack, StoredEnergyStack returned) {
		return null;
	}
	
	public StoredEnergyStack addEnergy(TileEntity tile, StoredEnergyStack stack, EnumFacing dir, ActionType type) {
		return stack;
	}

	public StoredEnergyStack removeEnergy(TileEntity tile, StoredEnergyStack stack, EnumFacing dir, ActionType type) {
		return stack;
	}

	public void transferEnergy(TileEntity from, TileEntity to, EnumFacing dirFrom, EnumFacing dirTo) {
	}

}