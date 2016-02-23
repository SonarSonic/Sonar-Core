package sonar.core.network.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import sonar.core.utils.helpers.NBTHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;

public abstract class SyncTagType<T> extends SyncPart {

	public static class BOOLEAN extends SyncTagType<Boolean> {
		public BOOLEAN(int id) {
			super(NBT.TAG_END, id);
			this.c = false;
			this.last = false;
		}

		public BOOLEAN(String name) {
			super(NBT.TAG_END, name);
			this.c = false;
			this.last = false;
		}

		public void invert() {
			setObject(!getObject());
		}
	}

	public static class BYTE extends SyncTagType<Byte> {
		public BYTE(int id) {
			super(NBT.TAG_BYTE, id);
			this.c = 0;
			this.last = 0;
		}

		public BYTE(String name) {
			super(NBT.TAG_BYTE, name);
			this.c = 0;
			this.last = 0;
		}
	}

	public static class SHORT extends SyncTagType<Short> {
		public SHORT(int id) {
			super(NBT.TAG_SHORT, id);
			this.c = 0;
			this.last = 0;
		}

		public SHORT(String name) {
			super(NBT.TAG_SHORT, name);
			this.c = 0;
			this.last = 0;
		}
	}

	public static class INT extends SyncTagType<Integer> {
		public INT(int id) {
			super(NBT.TAG_INT, id);
			this.c = 0;
			this.last = 0;
		}

		public INT(String name) {
			super(NBT.TAG_INT, name);
			this.c = 0;
			this.last = 0;
		}

		public void increaseBy(int i) {
			setObject(getObject() + i);
		}

		public void decreaseBy(int i) {
			setObject(getObject() - i);
		}
	}

	public static class LONG extends SyncTagType<Long> {
		public LONG(int id) {
			super(NBT.TAG_LONG, id);
			this.c = (long) 0;
			this.last = (long) 0;
		}

		public LONG(String name) {
			super(NBT.TAG_LONG, name);
			this.c = (long) 0;
			this.last = (long) 0;
		}

		public void increaseBy(int i) {
			setObject(getObject() + i);
		}

		public void decreaseBy(int i) {
			setObject(getObject() - i);
		}
	}

	public static class FLOAT extends SyncTagType<Float> {
		public FLOAT(int id) {
			super(NBT.TAG_FLOAT, id);
			this.c = (float) 0;
			this.last = (float) 0;
		}

		public FLOAT(String name) {
			super(NBT.TAG_FLOAT, name);
			this.c = (float) 0;
			this.last = (float) 0;
		}
	}

	public static class DOUBLE extends SyncTagType<Double> {
		public DOUBLE(int id) {
			super(NBT.TAG_DOUBLE, id);
			this.c = (double) 0;
			this.last = (double) 0;
		}

		public DOUBLE(String name) {
			super(NBT.TAG_DOUBLE, name);
			this.c = (double) 0;
			this.last = (double) 0;
		}
	}

	public static class BYTE_ARRAY extends SyncTagType<Byte[]> {
		public BYTE_ARRAY(int id, int size) {
			super(NBT.TAG_BYTE_ARRAY, id);
			this.c = new Byte[size];
			this.last = new Byte[size];
		}

		public BYTE_ARRAY(String name, int size) {
			super(NBT.TAG_BYTE_ARRAY, name);
			this.c = new Byte[size];
			this.last = new Byte[size];
		}
	}

	public static class STRING extends SyncTagType<String> {
		public STRING(int id) {
			super(NBT.TAG_STRING, id);
			this.c = "";
			this.last = "";
		}

		public STRING(String name) {
			super(NBT.TAG_STRING, name);
			this.c = "";
			this.last = "";
		}
	}

	public static class COMPOUND extends SyncTagType<NBTTagCompound> {
		public COMPOUND(int id) {
			super(NBT.TAG_COMPOUND, id);
			this.c = new NBTTagCompound();
			this.last = new NBTTagCompound();
		}

		public COMPOUND(String name) {
			super(NBT.TAG_COMPOUND, name);
			this.c = new NBTTagCompound();
			this.last = new NBTTagCompound();
		}
	}

	public static class INT_ARRAY extends SyncTagType<Integer[]> {
		public INT_ARRAY(int id, int size) {
			super(NBT.TAG_INT_ARRAY, id);
			this.c = new Integer[size];
			this.last = new Integer[size];
		}

		public INT_ARRAY(String name, int size) {
			super(NBT.TAG_INT_ARRAY, name);
			this.c = new Integer[size];
			this.last = new Integer[size];
		}
	}

	public T c;
	public T last;
	private int nbtType = -1;

	public SyncTagType(int nbtType, int id) {
		super(id);
		this.nbtType = nbtType;
	}

	public SyncTagType(int nbtType, String name) {
		super(name);
		this.nbtType = nbtType;
	}

	@Override
	public boolean equal() {
		if (last == null && c == null) {
			return true;
		}
		if (last == null || c == null) {
			return false;
		}
		return c.equals(last);
	}

	@Override
	public void updateSync() {
		last = c;
	}
	
	@Override
	public void writeObject(ByteBuf buf) {
		NBTHelper.writeBufBase(buf, nbtType, c, getTagName());
	}

	@Override
	public void readObject(ByteBuf buf) {
		c = (T) NBTHelper.readBufBase(buf, nbtType, getTagName());
	}

	@Override
	public void writeObject(NBTTagCompound nbt, SyncType type) {
		if (c != null) {
			NBTHelper.writeNBTBase(nbt, nbtType, c, getTagName());
		}
	}

	@Override
	public void readObject(NBTTagCompound nbt, SyncType type) {
		if (nbt.hasKey(getTagName()))
			c = (T) NBTHelper.readNBTBase(nbt, nbtType, getTagName());
	}
	
	public SyncTagType<T> setDefault(T def) {
		c = def;
		last = def;
		return this;
	}

	public T getObject() {
		return c;
	}

	public void setObject(T object) {
		c = object;
	}
}