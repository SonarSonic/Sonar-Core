package sonar.core.utils.helpers;

import net.minecraft.nbt.NBTTagCompound;
import cofh.api.energy.EnergyStorage;

public class NBTHelper {

	public static void writeEnergyStorage(EnergyStorage storage, NBTTagCompound nbt){
		NBTTagCompound energyTag = new NBTTagCompound();
		storage.writeToNBT(energyTag);
		nbt.setTag("energyStorage", energyTag);
	}
	public static void readEnergyStorage(EnergyStorage storage, NBTTagCompound nbt){
		if (nbt.hasKey("energyStorage")) {
			storage.readFromNBT(nbt.getCompoundTag("energyStorage"));
		}
	}
	
	public static enum SyncType{
		SAVE,
		SYNC,
		DROP,
		SPECIAL;
	}
	
}
