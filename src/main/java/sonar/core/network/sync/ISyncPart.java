package sonar.core.network.sync;

import io.netty.buffer.ByteBuf;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

public interface ISyncPart extends IDirtyPart, INBTSyncable {
	
    void writeToBuf(ByteBuf buf);

    void readFromBuf(ByteBuf buf);
	
    boolean canSync(SyncType sync);
	
    String getTagName();
}
