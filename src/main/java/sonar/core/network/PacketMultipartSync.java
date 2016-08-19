package sonar.core.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.MultipartHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

public class PacketMultipartSync extends PacketTileSync {

	public UUID partUUID;

	public PacketMultipartSync() {
		super();
	}

	public PacketMultipartSync(BlockPos pos, NBTTagCompound tag, UUID partUUID) {
		super(pos, tag);
		this.partUUID = partUUID;
	}

	public PacketMultipartSync(BlockPos pos, NBTTagCompound tag, SyncType type, UUID partUUID) {
		super(pos, tag, type);
		this.partUUID = partUUID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		long msb = buf.readLong();
		long lsb = buf.readLong();
		partUUID = new UUID(msb, lsb);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeLong(partUUID.getMostSignificantBits());
		buf.writeLong(partUUID.getLeastSignificantBits());
	}

	public static class Handler extends PacketTileEntityHandler<PacketMultipartSync> {
		@Override
		public IMessage processMessage(PacketMultipartSync message, TileEntity tile) {
			if (tile.getWorld().isRemote) {
				IMultipartContainer container = MultipartHelper.getPartContainer(tile.getWorld(), tile.getPos());
				if (container != null) {
					IMultipart part = container.getPartFromID(message.partUUID);
					if (part != null && part instanceof INBTSyncable) {
						INBTSyncable sync = (INBTSyncable) part;
						sync.readData(message.tag, message.type != null ? message.type : SyncType.DEFAULT_SYNC);
					}
				}
				tile.getWorld().getChunkFromBlockCoords(tile.getPos()).setChunkModified();
			}
			return null;
		}
	}
}
