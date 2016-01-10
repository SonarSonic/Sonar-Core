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

public class PacketByteBufClient implements IMessage {

	public int xCoord, yCoord, zCoord, id;
	public IByteBufTile tile;
	public ByteBuf buf;

	public PacketByteBufClient() {
	}

	public PacketByteBufClient(IByteBufTile tile, int x, int y, int z, int id) {
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
		if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null) {
			if (Minecraft.getMinecraft().thePlayer.worldObj != null) {
				Object tile = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(xCoord, yCoord, zCoord);
				if (tile != null) {
					if (tile instanceof IByteBufTile) {
						IByteBufTile packet = (IByteBufTile) tile;
						packet.readPacket(buf, id);
					} else {
						TileHandler handler = FMPHelper.getHandler(tile);
						if (handler != null && handler instanceof IByteBufTile) {
							((IByteBufTile) handler).readPacket(buf, id);
						}
					}
				}
			}
		}

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
		buf.writeInt(id);
		tile.writePacket(buf, id);
	}

	public static class HandlerClient implements IMessageHandler<PacketByteBufClient, IMessage> {

		@Override
		public IMessage onMessage(PacketByteBufClient message, MessageContext ctx) {

			return null;
		}
	}
}