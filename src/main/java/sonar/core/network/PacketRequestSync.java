package sonar.core.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.network.utils.ISyncTile;
import sonar.core.utils.helpers.NBTHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;

public class PacketRequestSync extends PacketCoords<PacketRequestSync> {

	public PacketRequestSync() {
	}

	public PacketRequestSync(BlockPos pos) {
		super(pos);
	}
	public static class Handler extends PacketTileEntityHandler<PacketRequestSync> {

		@Override
		public IMessage processMessage(PacketRequestSync message, TileEntity tile) {
			if (!tile.getWorld().isRemote) {
				if (tile instanceof ISyncTile) {
					NBTTagCompound tag = new NBTTagCompound();
					ISyncTile sync = (ISyncTile) tile;
					sync.writeData(tag, SyncType.SYNC);

					if (!tag.hasNoTags()) {
						return new PacketTileSync(message.pos, tag);
					}
				}
			}
			return null;
		}
	}

}