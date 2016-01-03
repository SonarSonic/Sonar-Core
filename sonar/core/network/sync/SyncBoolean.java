package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.helpers.NBTHelper.SyncType;

public class SyncBoolean implements ISyncPart {
	private boolean c = false;
	private boolean last = false;
	private byte id;

	public SyncBoolean(int id) {
		this.id = (byte) id;
	}

	public SyncBoolean(int id, boolean def) {
		this.id = (byte) id;
		this.c = def;
		this.last = def;
	}

	@Override
	public boolean equal() {
		return c == last;
	}

	public void writeToBuf(ByteBuf buf) {
		buf.writeBoolean(c);
		last = c;
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.c = buf.readBoolean();
	}

	public void writeToNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (!equal()) {
				nbt.setBoolean(String.valueOf(id), c);
				last = c;
			}
		} else if (type == SyncType.SAVE) {
			nbt.setBoolean(String.valueOf(id), c);
			last = c;
		}
	}

	public void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = nbt.getBoolean(String.valueOf(id));
			}
		} else if (type == SyncType.SAVE) {
			this.c = nbt.getBoolean(String.valueOf(id));
		}
	}

	public void setBoolean(boolean value) {
		c = value;
	}

	public boolean getBoolean() {
		return c;
	}
}
