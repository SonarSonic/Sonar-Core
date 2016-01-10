package sonar.core.utils.helpers;

import java.util.ArrayList;
import java.util.List;

import sonar.core.inventory.StoredItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import cofh.api.energy.EnergyStorage;

public class NBTHelper {

	public static void writeEnergyStorage(EnergyStorage storage, NBTTagCompound nbt) {
		NBTTagCompound energyTag = new NBTTagCompound();
		storage.writeToNBT(energyTag);
		nbt.setTag("energyStorage", energyTag);
	}

	public static void readEnergyStorage(EnergyStorage storage, NBTTagCompound nbt) {
		if (nbt.hasKey("energyStorage")) {
			storage.readFromNBT(nbt.getCompoundTag("energyStorage"));
		}
	}


	public static enum SyncType {
		SAVE, SYNC, DROP, SPECIAL;

		public static byte getID(SyncType type) {
			switch (type) {
			case SYNC:
				return 1;
			case DROP:
				return 2;
			case SPECIAL:
				return 3;
			default:
				return 0;
			}
		}

		public static SyncType getType(int i) {
			switch (i) {
			case 1:
				return SYNC;
			case 2:
				return DROP;
			case 3:
				return SPECIAL;
			default:
				return SAVE;
			}

		}
	}

}
