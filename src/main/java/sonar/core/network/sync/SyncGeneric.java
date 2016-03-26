package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.utils.IBufManager;
import sonar.core.utils.IBufObject;

public class SyncGeneric<T extends IBufObject> extends SyncPart {
	private T c;
	private T last;
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
		this.last = def;
	}

	@Override
	public boolean equal() {
		return manager.areTypesEqual(c, last);
	}

	public void setObject(T value) {
		c = value;
	}

	public T getObject() {
		return c;
	}

	@Override
	public void updateSync() {
		last = c;
	}

	@Override
	public void writeObject(ByteBuf buf) {
		manager.writeToBuf(buf, c);
	}

	@Override
	public void readObject(ByteBuf buf) {
		this.c = manager.readFromBuf(buf);
	}

	@Override
	public void writeObject(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound infoTag = new NBTTagCompound();
		manager.writeToNBT(infoTag, c);
		nbt.setTag(this.getTagName(), infoTag);

	}

	@Override
	public void readObject(NBTTagCompound nbt, SyncType type) {
		this.c = manager.readFromNBT(nbt.getCompoundTag(this.getTagName()));
	}

	public String toString() {
		return c.toString();
	}
}
