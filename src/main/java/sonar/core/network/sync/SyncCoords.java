package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;

public class SyncCoords extends SyncPart {
	private BlockCoords c;

	public SyncCoords(int id) {
		super(id);
	}

	public SyncCoords(String name) {
		super(name);
	}

	public void setCoords(BlockCoords value) {
		c = value;
		this.markDirty();
	}

	public BlockCoords getCoords() {
		return c;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		if (c != null) {
			buf.writeBoolean(true);
			BlockCoords.writeToBuf(buf, c);
		}else{
			buf.writeBoolean(false);
		}
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		if (buf.readBoolean())
			this.c = BlockCoords.readFromBuf(buf);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (c != null) {
			NBTTagCompound infoTag = new NBTTagCompound();
			BlockCoords.writeToNBT(infoTag, c);
			nbt.setTag(this.getTagName(), infoTag);
		}
		return nbt;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		if (nbt.hasKey(getTagName()))
			this.c = BlockCoords.readFromNBT(nbt.getCompoundTag(this.getTagName()));
	}

}