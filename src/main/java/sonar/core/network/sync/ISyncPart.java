package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper.SyncType;

public interface ISyncPart extends IDirtyPart{
	
	public void writeToBuf(ByteBuf buf);	

	public void readFromBuf(ByteBuf buf);
	
	public void writeToNBT(NBTTagCompound nbt, SyncType type);	

	public void readFromNBT(NBTTagCompound nbt, SyncType type);
	
	public boolean canSync(SyncType sync);
	
	public String getTagName();
}
