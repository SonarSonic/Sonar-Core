package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class PacketCoords<T extends PacketCoords> implements IMessage {

	public BlockPos pos;

	public PacketCoords() {}

	public PacketCoords(BlockPos pos) {
		this.pos = pos;
	}

	public void fromBytes(ByteBuf buf) {
		pos = new BlockPos(buf.readInt(),buf.readInt(),buf.readInt());
	}

	public void toBytes(ByteBuf buf) {		
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}

}