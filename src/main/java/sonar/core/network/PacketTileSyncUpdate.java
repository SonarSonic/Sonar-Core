package sonar.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

public class PacketTileSyncUpdate extends PacketTileSync {

	public PacketTileSyncUpdate() {
		super();
	}

	public PacketTileSyncUpdate(BlockPos pos, NBTTagCompound tag) {
		super(pos, tag);
	}

	public PacketTileSyncUpdate(BlockPos pos, NBTTagCompound tag, SyncType type) {
		super(pos, tag, type);

	}
	public static class Handler extends PacketTileEntityHandler<PacketTileSyncUpdate> {

		@Override
		public IMessage processMessage(EntityPlayer player, MessageContext ctx, PacketTileSyncUpdate message, TileEntity tile) {
			if (tile.getWorld().isRemote) {
				SyncType type = SyncType.DEFAULT_SYNC;
				if (message.type != null) {
					type = message.type;
				}
				if (tile instanceof INBTSyncable) {
					INBTSyncable sync = (INBTSyncable) tile;
					sync.readData(message.tag, type);
				}
				//TODO 
				//tile.getWorld().markBlockRangeForRenderUpdate(tile.getPos(), tile.getPos());
				tile.getWorld().getChunkFromBlockCoords(tile.getPos()).setChunkModified();
				tile.getWorld().markBlockRangeForRenderUpdate(tile.getPos(), tile.getPos());
			}
			return null;
		}
	}
}
