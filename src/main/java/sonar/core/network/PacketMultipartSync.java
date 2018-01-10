package sonar.core.network;

import io.netty.buffer.ByteBuf;
import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.multipart.IMultipartTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

import java.util.UUID;

public class PacketMultipartSync extends PacketMultipart {

	public NBTTagCompound tag;
	public SyncType type;

	public PacketMultipartSync() {
		super();
	}

	public PacketMultipartSync(BlockPos pos, NBTTagCompound tag, int slotID) {
		super(slotID, pos);
		this.tag = tag;
	}

	public PacketMultipartSync(BlockPos pos, NBTTagCompound tag, SyncType type, int slotID) {
		super(slotID, pos);
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

	public static class Handler extends PacketMultipartHandler<PacketMultipartSync> {

		@Override
		public IMessage processMessage(PacketMultipartSync message, EntityPlayer player, World world, IMultipartTile part, MessageContext ctx) {
			if (world.isRemote) {
				SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
					if (part != null && part instanceof INBTSyncable) {
						INBTSyncable sync = (INBTSyncable) part;
						sync.readData(message.tag, message.type != null ? message.type : SyncType.DEFAULT_SYNC);
					}
					world.markBlockRangeForRenderUpdate(part.getPartPos(), part.getPartPos());
					//world.getChunkFromBlockCoords(part.getPartPos()).markDirty();
				});
			}
			return null;
		}
	}
}
