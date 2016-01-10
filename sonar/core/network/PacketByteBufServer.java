package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.utils.IByteBufTile;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class PacketByteBufServer implements IMessage {

	public int xCoord, yCoord, zCoord, id;
	public IByteBufTile tile;
	public ByteBuf buf;

	public PacketByteBufServer() {
	}

	public PacketByteBufServer(IByteBufTile tile, int x, int y, int z, int id) {
		this.tile = tile;
		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xCoord = buf.readInt();
		this.yCoord = buf.readInt();
		this.zCoord = buf.readInt();
		this.id = buf.readInt();
		this.buf = buf;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
		buf.writeInt(id);
		tile.writePacket(buf, id);
	}

	public static class HandlerServer implements IMessageHandler<PacketByteBufServer, IMessage> {

		@Override
		public IMessage onMessage(PacketByteBufServer message, MessageContext ctx) {
			World world = ctx.getServerHandler().playerEntity.worldObj;
			if (world != null) {
				Object tile = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
				if (tile == null) {
					return null;
				}
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