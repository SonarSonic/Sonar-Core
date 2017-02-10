package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

public class PacketTileSync extends PacketCoords<PacketTileSync> {

	public NBTTagCompound tag;
	public SyncType type;

	public PacketTileSync() {
	}

	public PacketTileSync(BlockPos pos, NBTTagCompound tag) {
		super(pos);
		this.tag = tag;
	}

	public PacketTileSync(BlockPos pos, NBTTagCompound tag, SyncType type) {
		super(pos);
		this.tag = tag;
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.tag = ByteBufUtils.readTag(buf);
		if (buf.readBoolean()) {
			type = SyncType.values()[buf.readByte()];
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		ByteBufUtils.writeTag(buf, tag);
		if (type == null) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			buf.writeByte(type.ordinal());
		}
	}

	public static class Handler extends PacketTileEntityHandler<PacketTileSync> {

		@Override
		public IMessage processMessage(EntityPlayer player, MessageContext ctx, PacketTileSync message, TileEntity tile) {
			if (tile != null && tile.getWorld().isRemote) {
				/* Object te = OLDMultipartHelper.checkObject(tile); if (te == null) { return null; } */
				SonarCore.proxy.getThreadListener(ctx).addScheduledTask(new Runnable() {
					public void run() {
						SyncType type = SyncType.DEFAULT_SYNC;
						if (message.type != null) {
							type = message.type;
						}
						if (tile instanceof INBTSyncable) {
							INBTSyncable sync = (INBTSyncable) tile;
							sync.readData(message.tag, type);
						}
					}
				});
			}
			return null;
		}
	}
}