package sonar.core.network.sync;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;

public abstract class BaseSyncListPart extends DirtyPart implements ISyncableListener, INBTSyncable {

	public SyncableList syncList = new SyncableList(this);

	public BaseSyncListPart() {}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		if (shouldEmbed()) {
			NBTTagCompound tag = nbt.getCompoundTag(((ISyncPart) this).getTagName());
			if (!tag.hasNoTags()) {
				NBTHelper.readSyncParts(tag, type, syncList);
			}
		} else {
			NBTHelper.readSyncParts(nbt, type, syncList);
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (shouldEmbed()) {
			NBTTagCompound tag = NBTHelper.writeSyncParts(new NBTTagCompound(), type, syncList, type == SyncType.SYNC_OVERRIDE);
			if (!tag.hasNoTags()) {
				nbt.setTag(((ISyncPart) this).getTagName(), tag);
			}
		} else {
			NBTHelper.writeSyncParts(nbt, type, syncList, type.isType(SyncType.SAVE));
		}
		return nbt;
	}

	@Override
	public void markChanged(IDirtyPart part) {
		syncList.markSyncPartChanged(part);
		markChanged();
	}

	public boolean shouldEmbed() {
		return this instanceof ISyncPart;
	}
}