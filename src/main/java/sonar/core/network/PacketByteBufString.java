package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.utils.IByteBufTile;

public class PacketByteBufString extends PacketCoords<PacketByteBufString> {

	public int id;
	public IByteBufTile tile;
	public ByteBuf buf;
	public String string;

	public PacketByteBufString() {
	}

	public PacketByteBufString(IByteBufTile tile, String string, BlockPos pos, int id) {
		super(pos);
		this.tile = tile;
		this.id = id;
		this.string = string;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.id = buf.readInt();
		this.buf = buf;
		this.string = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(id);
		ByteBufUtils.writeUTF8String(buf, string);
	}

	public static class Handler extends PacketTileEntityHandler<PacketByteBufString> {

		@Override
		public IMessage processMessage(PacketByteBufString message, TileEntity tile) {
			if (tile instanceof IByteBufTile) {
				IByteBufTile packet = (IByteBufTile) tile;
				packet.readPacket(message.buf, message.id);
			} else {
				TileHandler handler = FMPHelper.getHandler(tile);
				if (handler != null && handler instanceof IByteBufTile) {
					((IByteBufTile) handler).readPacket(message.buf, message.id);
				}
			}
			return null;
		}
	}

}