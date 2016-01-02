package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.utils.IByteBufTile;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class PacketByteBuf implements IMessage {

	public int xCoord, yCoord, zCoord, id;
	public IByteBufTile tile;
	public ByteBuf buf;

	public PacketByteBuf() {
	}

	public PacketByteBuf(TileEntity tile, int id) {
		this.tile = (IByteBufTile) tile;
		this.xCoord = tile.xCoord;
		this.yCoord = tile.yCoord;
		this.zCoord = tile.zCoord;
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

	public static class Handler implements IMessageHandler<PacketByteBuf, IMessage> {

		@Override
		public IMessage onMessage(PacketByteBuf message, MessageContext ctx) {
			if (ctx.side == Side.SERVER) {
				World world = ctx.getServerHandler().playerEntity.worldObj;
				if (!world.isRemote) {
					TileEntity te = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
					if (te == null) {
						return null;
					}

					if (te instanceof IByteBufTile) {
						IByteBufTile packet = (IByteBufTile) te;
						packet.readPacket(message.buf, message.id);
					}
				}
			} else {
				if (Minecraft.getMinecraft().thePlayer.worldObj != null) {
					if (Minecraft.getMinecraft().thePlayer.worldObj.isRemote) {
						Object tile = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
						tile = FMPHelper.checkObject(tile);
						if (tile == null) {
							return null;
						}
						if (tile instanceof IByteBufTile) {
							IByteBufTile packet = (IByteBufTile) tile;
							packet.readPacket(message.buf, message.id);
						}

					}
				}
			}
			return null;
		}
	}
}