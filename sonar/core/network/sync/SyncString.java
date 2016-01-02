package sonar.core.network.sync;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.helpers.NBTHelper.SyncType;

public class SyncString implements ISyncPart {
	private String c = "";
	private String last = "";
	private byte id;

	public SyncString(int id) {
		this.id = (byte) id;
	}

	public SyncString(int id, String def) {
		this.id = (byte) id;
		this.c = def;
		this.last = def;
	}

	@Override
	public boolean equal() {
		return c.equals(last);
	}

	public void writeToBuf(ByteBuf buf) {
		if (!equal()) {
			buf.writeBoolean(true);
			ByteBufUtils.writeUTF8String(buf, c);
			last = c;
		} else
			buf.writeBoolean(false);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		if (buf.readBoolean()) {
			this.c = ByteBufUtils.readUTF8String(buf);
			
		}
	}

	public void writeToNBT(NBTTagCompound nbt, SyncType type) {
		if(c.isEmpty()||c==null){
			c="";
		}
		if (type == SyncType.SYNC) {
			if (!equal()) {
				nbt.setString(String.valueOf(id), c);
				last = c;
			}
		} else if (type == SyncType.SAVE) {
			nbt.setString(String.valueOf(id), c);
			last = c;
		}
	}

	public void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = nbt.getString(String.valueOf(id));
			}
		} else if (type == SyncType.SAVE) {
			this.c = nbt.getString(String.valueOf(id));
		}
	}

	public void setString(String string) {
		c = string;
	}

	public String getString() {
		return c;
	}
}
