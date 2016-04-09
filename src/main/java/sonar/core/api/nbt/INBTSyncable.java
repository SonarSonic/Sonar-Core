package sonar.core.api.nbt;

import sonar.core.helpers.NBTHelper.SyncType;
import net.minecraft.nbt.NBTTagCompound;

public interface INBTSyncable {
	
	public void writeData(NBTTagCompound tag, SyncType type);

	public void readData(NBTTagCompound tag, SyncType type);
}
