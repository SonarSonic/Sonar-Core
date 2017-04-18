package sonar.core.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

public class PacketMultipartSync extends PacketMultipart {

	public NBTTagCompound tag;
	public SyncType type;

	public PacketMultipartSync() {
		super();
	}

	public PacketMultipartSync(BlockPos pos, NBTTagCompound tag, UUID partUUID) {
		super(partUUID, pos);
		this.tag = tag;
	}

	public PacketMultipartSync(BlockPos pos, NBTTagCompound tag, SyncType type, UUID partUUID) {
		super(partUUID, pos);
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
		public IMessage processMessage(PacketMultipartSync message, IMultipartContainer target, IMultipart part, MessageContext ctx) {
			if (part.getWorld().isRemote) {

				SonarCore.proxy.getThreadListener(ctx).addScheduledTask(new Runnable() {
					@Override
					public void run() {
						if (part != null && part instanceof INBTSyncable) {
							INBTSyncable sync = (INBTSyncable) part;
							sync.readData(message.tag, message.type != null ? message.type : SyncType.DEFAULT_SYNC);
						}
						part.getWorld().getChunkFromBlockCoords(part.getPos()).setChunkModified();
					}
				});
			}
			return null;
		}
	}
}
