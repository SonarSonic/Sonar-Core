package sonar.core.utils;

import io.netty.buffer.ByteBuf;

public interface IBufObject<T> extends IRegistryObject, INBTObject {

	public String getName();

	public void readFromBuf(ByteBuf buf);

	public void writeToBuf(ByteBuf buf);

	public T instance();
}
