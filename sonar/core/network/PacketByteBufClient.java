package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.utils.IByteBufTile;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PacketByteBufClient extends PacketCoords<PacketByteBufClient> {

	public int id;
	public IByteBufTile tile;
	public ByteBuf buf;

	public PacketByteBufClient() {
	}

	public PacketByteBufClient(IByteBufTile tile, int x, int y, int z, int id) {
		super(x,y,z);
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

	public static class Handler extends PacketTileEntityHandler<PacketByteBufClient> {

		@Override
		public IMessage processMessage(PacketByteBufClient message, TileEntity tile) {
			if (tile.getWorldObj().isRemote) {
				if (tile instanceof IByteBufTile) {
					IByteBufTile packet = (IByteBufTile) tile;
					packet.readPacket(message.buf, message.id);
				} else {
					TileHandler handler = FMPHelper.getHandler(tile);
					if (handler != null && handler instanceof IByteBufTile) {
						((IByteBufTile) handler).readPacket(message.buf, message.id);
					}
				}
			}
			return null;
		}
	}

}