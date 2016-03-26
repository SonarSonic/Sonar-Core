package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper.SyncType;

import com.google.common.collect.Lists;

public abstract class SyncPart implements ISyncPart {
	private byte id = -1;
	private String name;
	private List<SyncType> types = Lists.newArrayList(SyncType.SAVE, SyncType.SYNC);
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

	public final void writeToBuf(ByteBuf buf) {
		if (!equal()) {
			buf.writeBoolean(true);
			writeObject(buf);
			updateSync();
		} else
			buf.writeBoolean(false);
	}

	@Override
	public final void readFromBuf(ByteBuf buf) {
		if (buf.readBoolean()) {
			readObject(buf);
		}
	}

	public final void writeToNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (!equal()) {
				writeObject(nbt, type);
				updateSync();
			}
		} else if (type == SyncType.SAVE) {
			writeObject(nbt, type);
			updateSync();
		}
	}

	public final void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (nbt.hasKey(getTagName())) {
				this.readObject(nbt, type);
			}
		} else if (type == SyncType.SAVE && nbt.hasKey(getTagName())) {
			this.readObject(nbt, type);
		}
	}

	public boolean canSync(SyncType syncType){
		if(types.contains(syncType)){
			return true;
		}
		return false;
	}
	
	public SyncPart addSyncType(SyncType syncType){
		if(!types.contains(syncType)){
			types.add(syncType);
		}
		return this;
	}
	public SyncPart removeSyncType(SyncType syncType){
		if(types.contains(syncType)){
			types.remove(syncType);
		}
		return this;
	}
	
	public abstract void updateSync();

	public abstract void writeObject(ByteBuf buf);

	public abstract void readObject(ByteBuf buf);

	public abstract void writeObject(NBTTagCompound nbt, SyncType type);

	public abstract void readObject(NBTTagCompound nbt, SyncType type);
}
