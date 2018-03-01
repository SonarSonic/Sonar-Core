package sonar.core.api.nbt;

import io.netty.buffer.ByteBuf;
import sonar.core.api.IRegistryObject;

public interface IBufObject<T> extends IRegistryObject, INBTObject<T> {

    void readFromBuf(ByteBuf buf);

    void writeToBuf(ByteBuf buf);
}
