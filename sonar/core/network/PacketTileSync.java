package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.utils.ISyncTile;
import sonar.core.utils.helpers.NBTHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketTileSync implements IMessage {

	public int xCoord, yCoord, zCoord;
	public NBTTagCompound tag;
	public SyncType type;

	public PacketTileSync() {
	}

	public PacketTileSync(int xCoord, int yCoord, int zCoord, NBTTagCompound tag) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
		this.tag = tag;
	}

	public PacketTileSync(int xCoord, int yCoord, int zCoord, NBTTagCompound tag, SyncType type) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
		this.tag = tag;
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xCoord = buf.readInt();
		this.yCoord = buf.readInt();
		this.zCoord = buf.readInt();
		this.tag = ByteBufUtils.readTag(buf);
		if (tag!=null && Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null) {
			if (Minecraft.getMinecraft().thePlayer.worldObj != null) {
				Object tile = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(xCoord, yCoord, zCoord);
				tile = FMPHelper.checkObject(tile);
				if (tile == null) {
					return;
				}
				SyncType type = NBTHelper.SyncType.SYNC;
				if (buf.readBoolean()) {
					type = SyncType.getType(buf.readByte());
				}
				if (tile instanceof ISyncTile) {
					ISyncTile sync = (ISyncTile) tile;
					sync.readData(this.tag, type);
				}
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
		ByteBufUtils.writeTag(buf, tag);
		if (type == null) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			buf.writeByte(SyncType.getID(type));
		}
	}

	public static class Handler implements IMessageHandler<PacketTileSync, IMessage> {

		@Override
		public IMessage onMessage(PacketTileSync message, MessageContext ctx) {
			return null;
		}
	}
}