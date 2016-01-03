package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.helpers.NBTHelper.SyncType;

public class SyncByte implements ISyncPart {
	private byte c = 0;
	private byte last = 0;
	private byte id;

	public SyncByte(int id) {
		this.id = (byte) id;
	}

	public SyncByte(int id, int def) {
		this.id = (byte) id;
		this.c = (byte)def;
		this.last = (byte)def;
	}

	@Override
	public boolean equal() {
		return c == last;
	}

	public void writeToBuf(ByteBuf buf) {
		if (!equal()) {
			buf.writeBoolean(true);
			buf.writeByte(c);
			last = c;
		} else
			buf.writeBoolean(false);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		if (buf.readBoolean()) {
			this.c = buf.readByte();
		}
	}

	public void writeToNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (!equal()) {
				nbt.setByte(String.valueOf(id), c);
				last = c;
			}
		} else if (type == SyncType.SAVE) {
			nbt.setByte(String.valueOf(id), c);
			last = c;
		}
	}

	public void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = nbt.getByte(String.valueOf(id));
			}
		} else if (type == SyncType.SAVE) {
			this.c = nbt.getByte(String.valueOf(id));
		}
	}

	public void setByte(byte value) {
		c = value;
	}

	public byte getByte() {
		return c;
	}

	public void increaseBy(int increase) {
		c += increase;
	}
}
