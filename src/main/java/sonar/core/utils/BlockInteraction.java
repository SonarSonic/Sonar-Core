package sonar.core.utils;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;

public class BlockInteraction {

	public int side;
	public float hitx, hity, hitz;
	public BlockInteractionType type;

	public BlockInteraction(int side, float hitX, float hitY, float hitZ, BlockInteractionType interact) {
		this.side = side;
		this.hitx = hitX;
		this.hity = hitY;
		this.hitz = hitZ;
		this.type = interact;
	}

	public static BlockInteraction readFromBuf(ByteBuf buf) {
		return new BlockInteraction(buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), BlockInteractionType.valueOf(ByteBufUtils.readUTF8String(buf)));
	}

	public void writeToBuf(ByteBuf buf) {
		buf.writeInt(side);
		buf.writeFloat(hitx);
		buf.writeFloat(hity);
		buf.writeFloat(hitz);
		ByteBufUtils.writeUTF8String(buf, type.name());
	}
}
