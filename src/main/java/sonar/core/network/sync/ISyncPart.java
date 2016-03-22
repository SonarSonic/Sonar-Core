package sonar.core.network.sync;

import sonar.core.utils.helpers.NBTHelper.SyncType;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;


public interface ISyncPart{
	
	public boolean equal();
	
	public void writeToBuf(ByteBuf buf);	

	public void readFromBuf(ByteBuf buf);
	
	public void writeToNBT(NBTTagCompound nbt, SyncType type);	

	public void readFromNBT(NBTTagCompound nbt, SyncType type);
	
	public boolean canSync(SyncType sync);
}
