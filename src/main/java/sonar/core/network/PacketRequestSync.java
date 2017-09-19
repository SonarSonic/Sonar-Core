package sonar.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.helpers.NBTHelper.SyncType;

public class PacketRequestSync extends PacketCoords<PacketRequestSync> {

	public PacketRequestSync() {
	}

	public PacketRequestSync(BlockPos pos) {
		super(pos);
	}

	public static class Handler extends PacketTileEntityHandler<PacketRequestSync> {

		@Override
		public IMessage processMessage(EntityPlayer player, MessageContext ctx, PacketRequestSync message, TileEntity tile) {
			if (!tile.getWorld().isRemote) {
				if (tile instanceof INBTSyncable) {
					NBTTagCompound tag = new NBTTagCompound();
					INBTSyncable sync = (INBTSyncable) tile;
					sync.writeData(tag, SyncType.SYNC_OVERRIDE);
					if (tile instanceof TileEntitySonar) {
                        SonarCore.proxy.getThreadListener(ctx).addScheduledTask(() -> ((TileEntitySonar) tile).onSyncPacketRequested(player));
					}
					if (!tag.hasNoTags()) {
						return new PacketTileSync(message.pos, tag);
					}
				}
			}
			return null;
		}
	}
}