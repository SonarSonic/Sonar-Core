package sonar.core.network.utils;

import sonar.core.utils.helpers.NBTHelper.SyncType;
import net.minecraft.nbt.NBTTagCompound;


/** used for managing the saving of the NBT on Tile Entities */
public interface ISyncTile {

	/**
	 * @param nbt the tag you wish the data read the data from
	 * @param type the data type you are trying to sync
	 */
	public void readData(NBTTagCompound nbt, SyncType type);

	/**
	 * @param nbt the tag you wish to write the data to
	 * @param type the data type you are trying to sync
	 */
	public void writeData(NBTTagCompound nbt, SyncType type);

}
