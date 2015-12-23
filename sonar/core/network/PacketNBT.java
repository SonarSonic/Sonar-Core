package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.utils.INBTPacket;
import sonar.core.utils.ISyncTile;
import sonar.core.utils.ITextField;
import sonar.core.utils.helpers.FMPHelper;
import sonar.core.utils.helpers.NBTHelper;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class PacketNBT implements IMessage {

	public int xCoord, yCoord, zCoord, id;
	public NBTTagCompound tag;

	public PacketNBT() {
	}

	public PacketNBT(TileEntity tile, int id) {
		INBTPacket packet = (INBTPacket) tile;
		this.xCoord = tile.xCoord;
		this.yCoord = tile.yCoord;
		this.zCoord = tile.zCoord;
		this.id = id;
		NBTTagCompound tag = new NBTTagCompound();
		packet.writePacket(tag, id);
		this.tag = tag;
	}

	public PacketNBT(int xCoord, int yCoord, int zCoord, NBTTagCompound tag, int id) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
		this.tag = tag;
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xCoord = buf.readInt();
		this.yCoord = buf.readInt();
		this.zCoord = buf.readInt();
		this.id = buf.readInt();
		this.tag = ByteBufUtils.readTag(buf);

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
		buf.writeInt(id);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<PacketNBT, IMessage> {

		@Override
		public IMessage onMessage(PacketNBT message, MessageContext ctx) {
			if (ctx.side == Side.SERVER) {
				World world = ctx.getServerHandler().playerEntity.worldObj;
				if (!world.isRemote) {
					TileEntity te = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
					if (te == null) {
						return null;
					}

					if (te instanceof INBTPacket) {
						INBTPacket packet = (INBTPacket) te;
						packet.readPacket(message.tag, message.id);
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
						if (tile instanceof INBTPacket) {
							INBTPacket packet = (INBTPacket) tile;
							packet.readPacket(message.tag, message.id);
						}

					}
				}
			}
			return null;
		}
	}
}