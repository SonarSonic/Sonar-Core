package sonar.core.network.sync;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;

public class BaseSyncListPart extends DirtyPart implements ISyncableListener, INBTSyncable  {

	public SyncableList syncList = new SyncableList(this);

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt, type, syncList);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.writeSyncParts(nbt, type, syncList, type.isType(SyncType.SAVE));
		return nbt;
	}

	@Override
	public void markChanged(IDirtyPart part) {
		syncList.markSyncPartChanged(part);
		markChanged();
	}
}