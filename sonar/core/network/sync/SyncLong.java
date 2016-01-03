package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.helpers.NBTHelper.SyncType;

public class SyncLong implements ISyncPart {
	private long c = 0;
	private long last = 0;
	private byte id;

	public SyncLong(int id) {
		this.id = (byte) id;
	}

	public SyncLong(int id, long def) {
		this.id = (byte) id;
		this.c = def;
		this.last = def;
	}

	@Override
	public boolean equal() {
		return c == last;
	}

	public void writeToBuf(ByteBuf buf) {
		if (!equal()) {
			buf.writeBoolean(true);
			buf.writeLong(c);
			last = c;
		} else
			buf.writeBoolean(false);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		if (buf.readBoolean()) {
			this.c = buf.readLong();
		}
	}

	public void writeToNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (!equal()) {
				nbt.setLong(String.valueOf(id), c);
				last = c;
			}
		} else if (type == SyncType.SAVE) {
			nbt.setLong(String.valueOf(id), c);
			last = c;
		}
	}

	public void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = nbt.getLong(String.valueOf(id));
			}
		} else if (type == SyncType.SAVE) {
			this.c = nbt.getLong(String.valueOf(id));
		}
	}

	public void setLong(long value) {
		c = value;
	}

	public long getLong() {
		return c;
	}

	public void increaseBy(int increase) {
		c += increase;
	}
}
