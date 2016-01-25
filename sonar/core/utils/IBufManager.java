package sonar.core.utils;

import io.netty.buffer.ByteBuf;
import sonar.core.utils.helpers.NBTHelper;

public interface IBufManager<T extends IBufObject> extends INBTManager<T> {
	public T readFromBuf(ByteBuf buf);

	public void writeToBuf(ByteBuf buf, T object);
}
