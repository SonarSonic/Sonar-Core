package sonar.core.utils;

import sonar.core.api.IRegistryObject;
import io.netty.buffer.ByteBuf;

public interface IBufObject<T> extends IRegistryObject, INBTObject<T> {

	public void readFromBuf(ByteBuf buf);

	public void writeToBuf(ByteBuf buf);

}
