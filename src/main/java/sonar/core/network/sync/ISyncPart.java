package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

public interface ISyncPart extends IDirtyPart, INBTSyncable {
	
	public void writeToBuf(ByteBuf buf);	

	public void readFromBuf(ByteBuf buf);
	
	public boolean canSync(SyncType sync);
	
	public String getTagName();
}
