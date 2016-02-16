package sonar.core.network;

import cofh.api.tileentity.IReconfigurableSides;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.network.utils.ISyncTile;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestSync extends PacketCoords<PacketRequestSync> {

	public PacketRequestSync() {
	}

	public PacketRequestSync(int x, int y, int z) {
		super(x, y, z);
	}
	public static class Handler extends PacketTileEntityHandler<PacketRequestSync> {

		@Override
		public IMessage processMessage(PacketRequestSync message, TileEntity tile) {
			if (!tile.getWorldObj().isRemote) {
				if (tile instanceof ISyncTile) {
					NBTTagCompound tag = new NBTTagCompound();
					ISyncTile sync = (ISyncTile) tile;
					sync.writeData(tag, SyncType.SYNC);

					if (!tag.hasNoTags()) {
						return new PacketTileSync(message.xCoord, message.yCoord, message.zCoord, tag);
					}
				}
			}
			return null;
		}
	}

}