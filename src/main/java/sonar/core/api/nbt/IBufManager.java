package sonar.core.api.nbt;

import io.netty.buffer.ByteBuf;

public interface IBufManager<T extends IBufObject> extends INBTManager<T> {
	
    T readFromBuf(ByteBuf buf);

    void writeToBuf(ByteBuf buf, T object);
}
