package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.helpers.NBTHelper.SyncType;

public class SyncInt implements ISyncPart {
	private int c = 0;
	private int last = 0;
	private byte id;

	public SyncInt(int id) {
		this.id = (byte) id;
	}

	public SyncInt(int id, int def) {
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
			buf.writeInt(c);
			last = c;
		} else
			buf.writeBoolean(false);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		if (buf.readBoolean()) {
			this.c = buf.readInt();
		}
	}

	public void writeToNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (!equal()) {
				nbt.setInteger(String.valueOf(id), c);
				last = c;
			}
		} else if (type == SyncType.SAVE) {
			nbt.setInteger(String.valueOf(id), c);
			last = c;
		}
	}

	public void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = nbt.getInteger(String.valueOf(id));
			}
		} else if (type == SyncType.SAVE) {
			this.c = nbt.getInteger(String.valueOf(id));
		}
	}

	public void setInt(int value) {
		c = value;
	}

	public int getInt() {
		return c;
	}

	public void increaseBy(int increase) {
		c += increase;
	}
}
