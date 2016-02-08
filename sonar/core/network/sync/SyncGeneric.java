package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.network.sync.ISyncPart;
import sonar.core.utils.IBufManager;
import sonar.core.utils.IBufObject;
import sonar.core.utils.INBTObject;
import sonar.core.utils.helpers.NBTHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.core.utils.helpers.NBTRegistryHelper;
import sonar.core.utils.helpers.RegistryHelper;

public class SyncGeneric<T extends IBufObject> implements ISyncPart {
	private T c;
	private T last;
	private byte id;
	private IBufManager<T> manager;

	public SyncGeneric(IBufManager<T> manager, int id) {
		this.manager = manager;
		this.id = (byte) id;
	}

	public SyncGeneric(IBufManager<T> manager, int id, T def) {
		this.manager = manager;
		this.id = (byte) id;
		this.c = def;
		this.last = def;
	}


	@Override
	public boolean equal() {
		return manager.areTypesEqual(c, last);
	}

	public void writeToBuf(ByteBuf buf) {
		if (!equal()) {
			buf.writeBoolean(true);
			manager.writeToBuf(buf, c);
			last = c;
		} else
			buf.writeBoolean(false);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		if (buf.readBoolean()) {
			this.c = manager.readFromBuf(buf);
		}
	}

	public void writeToNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (!equal()) {
				NBTTagCompound infoTag = new NBTTagCompound();
				manager.writeToNBT(infoTag, c);
				nbt.setTag(String.valueOf(id), infoTag);
				last = c;
			}
		}
		if (type == SyncType.SAVE) {
			NBTTagCompound infoTag = new NBTTagCompound();
			manager.writeToNBT(infoTag, c);
			nbt.setTag(String.valueOf(id), infoTag);

		}
	}

	public void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = manager.readFromNBT(nbt.getCompoundTag(String.valueOf(id)));
			}
		}
		if (type == SyncType.SAVE) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = manager.readFromNBT(nbt.getCompoundTag(String.valueOf(id)));
			}
		}
	}

	public void setObject(T value) {
		c = value;
	}

	public T getObject() {
		return c;
	}

}
