package sonar.core.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.fmp.OLDMultipartHelper;

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
		public IMessage processMessage(PacketTileSyncUpdate message, TileEntity tile) {
			if (tile.getWorld().isRemote) {
				Object te = OLDMultipartHelper.checkObject(tile);
				if (te == null) {
					return null;
				}
				SyncType type = SyncType.DEFAULT_SYNC;
				if (message.type != null) {
					type = message.type;
				}
				if (te instanceof INBTSyncable) {
					INBTSyncable sync = (INBTSyncable) te;
					sync.readData(message.tag, type);
				}
				//TODO 
				//System.out.println("render");
				//tile.getWorld().markBlockRangeForRenderUpdate(tile.getPos(), tile.getPos());
				tile.getWorld().getChunkFromBlockCoords(tile.getPos()).setChunkModified();
			}
			return null;
		}
	}
}
