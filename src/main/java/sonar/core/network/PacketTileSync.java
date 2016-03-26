package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.utils.ISyncTile;


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
			type = SyncType.getType(buf.readByte());
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
			buf.writeByte(SyncType.getID(type));
		}
	}

	public static class Handler extends PacketTileEntityHandler<PacketTileSync> {

		@Override
		public IMessage processMessage(PacketTileSync message, TileEntity tile) {
			if (tile.getWorld().isRemote) {
				Object te = FMPHelper.checkObject(tile);
				if (te == null) {
					return null;
				}
				SyncType type = NBTHelper.SyncType.SYNC;
				if (message.type != null) {
					type = message.type;
				}
				if (te instanceof ISyncTile) {
					ISyncTile sync = (ISyncTile) te;
					sync.readData(message.tag, type);
				}
			}
			return null;
		}
	}
}