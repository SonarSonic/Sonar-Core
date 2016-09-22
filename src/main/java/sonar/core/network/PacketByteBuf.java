package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.integration.fmp.OLDMultipartHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.utils.ByteBufWritable;
import sonar.core.network.utils.IByteBufTile;

public class PacketByteBuf extends PacketCoords<PacketByteBuf> {

	public int id;
	public IByteBufTile tile;
	public ByteBufWritable[] writables = null;
	public ByteBuf buf;

	public PacketByteBuf() {
	}

	public PacketByteBuf(IByteBufTile tile, BlockPos pos, int id, ByteBufWritable... writables) {
		super(pos);
		this.tile = tile;
		this.id = id;
		this.writables = writables;
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
		
		if (writables != null) {
			for (ByteBufWritable writable : writables) {
				writable.writeToBuf(buf);
			}
		}
	}

	public static class Handler extends PacketTileEntityHandler<PacketByteBuf> {

		@Override
		public IMessage processMessage(PacketByteBuf message, TileEntity tile) {
			// if (!tile.getWorld().isRemote) {
			if (tile instanceof IByteBufTile) {
				IByteBufTile packet = (IByteBufTile) tile;
				packet.readPacket(message.buf, message.id);
			} else {
				TileHandler handler = OLDMultipartHelper.getHandler(tile);
				if (handler != null && handler instanceof IByteBufTile) {
					((IByteBufTile) handler).readPacket(message.buf, message.id);
				}
			}
			// }
			return null;
		}
	}

}