package sonar.core.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.MultipartHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.SonarCore;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.integration.multipart.SonarMultipartHelper;

public class PacketRequestMultipartSync extends PacketCoords<PacketRequestMultipartSync> {

	public UUID uuid;

	public PacketRequestMultipartSync() {
	}

	public PacketRequestMultipartSync(BlockPos pos, UUID uuid) {
		super(pos);
		this.uuid = uuid;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		long msb = buf.readLong();
		long lsb = buf.readLong();
		uuid = new UUID(msb, lsb);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeLong(uuid.getMostSignificantBits());
		buf.writeLong(uuid.getLeastSignificantBits());
	}

	public static class Handler extends PacketTileEntityHandler<PacketRequestMultipartSync> {

		@Override
		public IMessage processMessage(PacketRequestMultipartSync message, TileEntity tile) {
			if (!tile.getWorld().isRemote) {
				IMultipartContainer container = MultipartHelper.getPartContainer(tile.getWorld(), tile.getPos());
				if (container != null) {
					IMultipart part = container.getPartFromID(message.uuid);
					if (part != null && part instanceof SonarMultipart) {
						SonarCore.proxy.getThreadListener().addScheduledTask(new Runnable() {
							public void run() {
								((SonarMultipart) part).forceNextSync();
								SonarMultipartHelper.sendMultipartSyncAround((SonarMultipart) part, 64);
							}
						});
						return null;
					}
				}
			}
			return null;
		}
	}

}