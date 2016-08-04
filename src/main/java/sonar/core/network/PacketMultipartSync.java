package sonar.core.network;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

public class PacketMultipartSync extends PacketTileSync {

	public PartSlot slot;

	public PacketMultipartSync() {
		super();
	}

	public PacketMultipartSync(BlockPos pos, NBTTagCompound tag, PartSlot slot) {
		super(pos, tag);
		this.slot = slot;
	}

	public PacketMultipartSync(BlockPos pos, NBTTagCompound tag, SyncType type, PartSlot slot) {
		super(pos, tag, type);
		this.slot = slot;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		slot = PartSlot.VALUES[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(slot.ordinal());
	}

	public static class Handler extends PacketTileEntityHandler<PacketMultipartSync> {
		@Override
		public IMessage processMessage(PacketMultipartSync message, TileEntity tile) {
			if (tile.getWorld().isRemote) {
				IMultipartContainer container = MultipartHelper.getPartContainer(tile.getWorld(), tile.getPos());
				if (container != null) {
					IMultipart part = container.getPartInSlot(message.slot);
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
