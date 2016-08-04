package sonar.core.network.sync;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;

public class SyncUUID extends SyncPart {

	public UUID current = null;

	public SyncUUID(int id) {
		super(id);
	}

	public UUID getUUID() {
		return current;
	}

	public void setObject(UUID id) {
		current = id;
		setChanged(true);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeBoolean(current != null);
		if (current != null)
			ByteBufUtils.writeUTF8String(buf, current.toString());
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		if (buf.readBoolean())
			current = UUID.fromString(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, SyncType type) {
		if (current != null)
			nbt.setUniqueId(getTagName(), current);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (nbt.hasUniqueId(getTagName())) {
			current = nbt.getUniqueId(getTagName());
		}else if(nbt.hasKey(getTagName())){
			current = SonarHelper.getGameProfileForUsername(nbt.getString(getTagName())).getId();
		}
	}

}
