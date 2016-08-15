package sonar.core.network.sync;

import java.util.List;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper.SyncType;

public abstract class SyncPart extends DirtyPart implements ISyncPart {
	private byte id = -1;
	private String name;
	private List<SyncType> types = Lists.newArrayList(SyncType.SAVE, SyncType.DEFAULT_SYNC);

	public abstract void writeToBuf(ByteBuf buf);

	public abstract void readFromBuf(ByteBuf buf);

	public abstract void writeToNBT(NBTTagCompound nbt, SyncType type);

	public abstract void readFromNBT(NBTTagCompound nbt, SyncType type);
	
	public SyncPart(int id) {
		this.id = (byte) id;
	}

	public SyncPart(String name) {
		this.name = name;
	}

	public String getTagName() {
		if (name == null) {
			return String.valueOf(id);
		} else {
			return name;
		}
	}
	public boolean canSync(SyncType syncType) {
		SyncType[] array = new SyncType[types.size()];
		if (syncType.isType(types.toArray(array))) {
			return true;
		}
		return false;
	}

	public SyncPart addSyncType(SyncType... add) {
		SyncType[] array = types.toArray(new SyncType[types.size()]);
		for (SyncType type : add) {
			if (!type.isType(array)) {
				types.add(type);
			}
		}
		return this;
	}

	public SyncPart removeSyncType(SyncType... remove) {
		SyncType[] array = types.toArray(new SyncType[types.size()]);
		for (SyncType type : remove) {
			if (type.isType(array)) {
				types.remove(type);
			}
		}
		return this;
	}

}
