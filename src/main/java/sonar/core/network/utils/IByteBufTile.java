package sonar.core.network.utils;

import io.netty.buffer.ByteBuf;

public interface IByteBufTile {
    void writePacket(ByteBuf buf, int id);

    void readPacket(ByteBuf buf, int id);
}
