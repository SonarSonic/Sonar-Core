package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.IBufManager;
import sonar.core.api.nbt.IBufObject;
import sonar.core.helpers.NBTHelper.SyncType;

public class SyncGeneric<T extends IBufObject> extends SyncPart {
	private T c;
	private IBufManager<T> manager;

	public SyncGeneric(IBufManager<T> manager, int id) {
		super(id);
		this.manager = manager;
	}

	public SyncGeneric(IBufManager<T> manager, String name) {
		super(name);
		this.manager = manager;
	}

	public void setDefault(T def) {
		this.c = def;
	}

	public void setObject(T value) {
		c = value;
		this.markDirty();
	}

	public T getObject() {
		return c;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		manager.writeToBuf(buf, c);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.c = manager.readFromBuf(buf);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound infoTag = new NBTTagCompound();
		manager.writeToNBT(infoTag, c);
		if (!infoTag.hasNoTags())
			nbt.setTag(this.getTagName(), infoTag);

		return nbt;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		if (nbt.hasKey(this.getTagName()))
			this.c = manager.readFromNBT(nbt.getCompoundTag(this.getTagName()));
	}

	public String toString() {
		return c.toString();
	}

}
