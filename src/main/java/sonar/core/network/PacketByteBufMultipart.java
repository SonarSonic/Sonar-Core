package sonar.core.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.network.utils.IByteBufTile;

public class PacketByteBufMultipart extends PacketMultipart {

	public int id;
	public IByteBufTile tile;
	public ByteBuf buf;

	public PacketByteBufMultipart() {}

	public PacketByteBufMultipart(UUID partUUID, IByteBufTile tile, BlockPos pos, int id) {
		super(partUUID, pos);
		this.tile = tile;
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.id = buf.readInt();
		this.buf = buf;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(id);
		tile.writePacket(buf, id);
	}

	public static class Handler extends PacketMultipartHandler<PacketByteBufMultipart> {

		@Override
		public IMessage processMessage(PacketByteBufMultipart message, IMultipartContainer tile, IMultipart part, MessageContext ctx) {
			if (part != null && part instanceof IByteBufTile) {
				((IByteBufTile) part).readPacket(message.buf, message.id);
			}
			return null;
		}
	}

}