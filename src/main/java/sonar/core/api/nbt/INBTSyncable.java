package sonar.core.api.nbt;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper.SyncType;

/**add a empty constructor if you need initialisation to work*/
public interface INBTSyncable {

	/** @param nbt the tag you wish the data read the data from
	 * @param type the data type you are trying to sync */
	public void readData(NBTTagCompound nbt, SyncType type);

	/** @param nbt the tag you wish to write the data to
	 * @param type the data type you are trying to sync */
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type);
}
