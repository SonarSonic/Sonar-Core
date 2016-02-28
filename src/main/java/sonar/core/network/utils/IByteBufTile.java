package sonar.core.network.utils;

import io.netty.buffer.ByteBuf;

public interface IByteBufTile {

	public void writePacket(ByteBuf buf, int id);	

	public void readPacket(ByteBuf buf, int id);
}
