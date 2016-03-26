package sonar.core.utils;

import io.netty.buffer.ByteBuf;
import sonar.core.api.IRegistryObject;

public interface IBufObject<T> extends IRegistryObject, INBTObject<T> {

	public void readFromBuf(ByteBuf buf);

	public void writeToBuf(ByteBuf buf);

}
