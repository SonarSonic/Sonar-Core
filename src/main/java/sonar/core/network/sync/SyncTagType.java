package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;

public abstract class SyncTagType<T> extends SyncPart {

	public static class BOOLEAN extends SyncTagType<Boolean> {
		public BOOLEAN(int id) {
			super(NBT.TAG_END, id);
			this.current = false;
			this.last = false;
		}

		public BOOLEAN(String name) {
			super(NBT.TAG_END, name);
			this.current = false;
			this.last = false;
		}

		public void invert() {
			setObject(!getObject());
			this.setChanged(true);
		}
	}

	public static class BYTE extends SyncTagType<Byte> {
		public BYTE(int id) {
			super(NBT.TAG_BYTE, id);
			this.current = 0;
			this.last = 0;
		}

		public BYTE(String name) {
			super(NBT.TAG_BYTE, name);
			this.current = 0;
			this.last = 0;
		}
	}

	public static class SHORT extends SyncTagType<Short> {
		public SHORT(int id) {
			super(NBT.TAG_SHORT, id);
			this.current = 0;
			this.last = 0;
		}

		public SHORT(String name) {
			super(NBT.TAG_SHORT, name);
			this.current = 0;
			this.last = 0;
		}

		public void increaseBy(int i) {
			setObject((short) (getObject() + i));
			this.setChanged(true);
		}

		public void decreaseBy(int i) {
			setObject((short) (getObject() - i));
			this.setChanged(true);
		}
	}

	public static class INT extends SyncTagType<Integer> {
		public INT(int id) {
			super(NBT.TAG_INT, id);
			this.current = 0;
			this.last = 0;
		}

		public INT(String name) {
			super(NBT.TAG_INT, name);
			this.current = 0;
			this.last = 0;
		}

		public void increaseBy(int i) {
			setObject(getObject() + i);
			this.setChanged(true);
		}

		public void decreaseBy(int i) {
			setObject(getObject() - i);
			this.setChanged(true);
		}
	}

	public static class LONG extends SyncTagType<Long> {
		public LONG(int id) {
			super(NBT.TAG_LONG, id);
			this.current = (long) 0;
			this.last = (long) 0;
		}

		public LONG(String name) {
			super(NBT.TAG_LONG, name);
			this.current = (long) 0;
			this.last = (long) 0;
		}

		public void increaseBy(int i) {
			setObject(getObject() + i);
			this.setChanged(true);
		}

		public void decreaseBy(int i) {
			setObject(getObject() - i);
			this.setChanged(true);
		}
	}

	public static class FLOAT extends SyncTagType<Float> {
		public FLOAT(int id) {
			super(NBT.TAG_FLOAT, id);
			this.current = (float) 0;
			this.last = (float) 0;
		}

		public FLOAT(String name) {
			super(NBT.TAG_FLOAT, name);
			this.current = (float) 0;
			this.last = (float) 0;
		}
	}

	public static class DOUBLE extends SyncTagType<Double> {
		public DOUBLE(int id) {
			super(NBT.TAG_DOUBLE, id);
			this.current = (double) 0;
			this.last = (double) 0;
		}

		public DOUBLE(String name) {
			super(NBT.TAG_DOUBLE, name);
			this.current = (double) 0;
			this.last = (double) 0;
		}
	}

	public static class BYTE_ARRAY extends SyncTagType<Byte[]> {
		public BYTE_ARRAY(int id, int size) {
			super(NBT.TAG_BYTE_ARRAY, id);
			this.current = new Byte[size];
			this.last = new Byte[size];
		}

		public BYTE_ARRAY(String name, int size) {
			super(NBT.TAG_BYTE_ARRAY, name);
			this.current = new Byte[size];
			this.last = new Byte[size];
		}
	}

	public static class STRING extends SyncTagType<String> {
		public STRING(int id) {
			super(NBT.TAG_STRING, id);
			this.current = "";
			this.last = "";
		}

		public STRING(String name) {
			super(NBT.TAG_STRING, name);
			this.current = "";
			this.last = "";
		}
	}

	public static class COMPOUND extends SyncTagType<NBTTagCompound> {
		public COMPOUND(int id) {
			super(NBT.TAG_COMPOUND, id);
			this.current = new NBTTagCompound();
			this.last = new NBTTagCompound();
		}

		public COMPOUND(String name) {
			super(NBT.TAG_COMPOUND, name);
			this.current = new NBTTagCompound();
			this.last = new NBTTagCompound();
		}
	}

	public static class INT_ARRAY extends SyncTagType<Integer[]> {
		public INT_ARRAY(int id, int size) {
			super(NBT.TAG_INT_ARRAY, id);
			this.current = new Integer[size];
			this.last = new Integer[size];
		}

		public INT_ARRAY(String name, int size) {
			super(NBT.TAG_INT_ARRAY, name);
			this.current = new Integer[size];
			this.last = new Integer[size];
		}
	}

	public T current;
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
	public void writeToBuf(ByteBuf buf) {
		NBTHelper.writeBufBase(buf, nbtType, current, getTagName());
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		current = (T) NBTHelper.readBufBase(buf, nbtType, getTagName());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, SyncType type) {
		if (current != null) {
			NBTHelper.writeNBTBase(nbt, nbtType, current, getTagName());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (nbt.hasKey(getTagName()))
			current = (T) NBTHelper.readNBTBase(nbt, nbtType, getTagName());
	}

	public SyncTagType<T> setDefault(T def) {
		current = def;
		last = def;
		return this;
	}

	public T getObject() {
		return current;
	}

	public void setObject(T object) {
		if (current != object) {
			current = object;
			setChanged(true);
		}
	}

	public String toString() {
		return current.toString();
	}

}
