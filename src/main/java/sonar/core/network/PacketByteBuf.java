package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.network.utils.ByteBufWritable;
import sonar.core.network.utils.IByteBufTile;

public class PacketByteBuf extends PacketCoords<PacketByteBuf> {

	public int id;
	public IByteBufTile tile;
	public ByteBufWritable[] writables;
	public ByteBuf buf;

	public PacketByteBuf() {}

	public PacketByteBuf(IByteBufTile tile, BlockPos pos, int id, ByteBufWritable... writables) {
		super(pos);
		this.tile = tile;
		this.id = id;
		this.writables = writables;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.buf = buf.retain();
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(id);
		if (writables != null) {
			boolean replaces = false;
			for (ByteBufWritable writable : writables) {
				writable.writeToBuf(buf);
				if (writable.replacesDefaults) {
					replaces = true;
				}
			}
			if (replaces) {
				return;
			}
		}
		tile.writePacket(buf, id);
	}

	public static class Handler extends PacketTileEntityHandler<PacketByteBuf> {

		@Override
		public IMessage processMessage(EntityPlayer player, MessageContext ctx, PacketByteBuf message, TileEntity tile) {

			SonarCore.proxy.getThreadListener(ctx).addScheduledTask(() -> {
				if (tile instanceof IByteBufTile) {
					IByteBufTile packet = (IByteBufTile) tile;
					packet.readPacket(message.buf, message.id);
				}
				message.buf.release();
				/* else { TileHandler handler =
				 * OLDMultipartHelper.getHandler(tile); if (handler != null &&
				 * handler instanceof IByteBufTile) { ((IByteBufTile)
				 * handler).readPacket(message.buf, message.id); } } */
			});
			return null;
		}
	}
}