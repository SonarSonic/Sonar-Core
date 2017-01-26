package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper.SyncType;

public class SyncEnum<E extends Enum> extends SyncPart {

	public E[] values = null;
	public E current;

	public SyncEnum(E[] values, int id) {
		super(id);
		this.values = values;
		this.current = values[0];
	}

	public SyncEnum(E[] values, String name) {
		super(name);
		this.values = values;
		this.current = values[0];
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeInt(current.ordinal());
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		current = values.clone()[buf.readInt()];
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (current != null) {
			nbt.setInteger(getTagName(), current.ordinal());
		}
		return nbt;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		if (nbt.hasKey(getTagName()))
			current = values.clone()[nbt.getInteger(getTagName())];
	}

	public void incrementEnum() {
		int ordinal = current.ordinal() + 1;
		if (ordinal < values.length) {
			current = values[ordinal];
		} else {
			current = values[0];
		}
	}

	public SyncEnum<E> setDefault(E def) {
		current = def;
		return this;
	}

	public E getObject() {
		return current;
	}

	public void setObject(E object) {
		if (current != object) {
			current = object;
			markDirty();
		}
	}

	public String toString() {
		return current.toString();
	}
}
