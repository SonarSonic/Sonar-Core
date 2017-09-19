/*package sonar.core.network;

import io.netty.buffer.ByteBuf;
import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.multipart.MultipartHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.integration.multipart.SonarMultipartHelper;

import java.util.Optional;
import java.util.UUID;

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
		public IMessage processMessage(EntityPlayer player, MessageContext ctx, PacketRequestMultipartSync message, TileEntity tile) {
			if (!tile.getWorld().isRemote) {
                SonarCore.proxy.getThreadListener(ctx).addScheduledTask(new Runnable() {
                    public void run() {
                        Optional<IMultipartContainer> container = MultipartHelper.getContainer(tile.getWorld(), tile.getPos());
				if (container != null) {
					IMultipart part = container.getPartFromID(message.uuid);
					if (part != null && part instanceof SonarMultipart) {
                                SonarMultipart multipart = (SonarMultipart) part;
                                multipart.forceNextSync();
                                multipart.onSyncPacketRequested(player);
                                SonarMultipartHelper.sendMultipartSyncToPlayer(multipart, (EntityPlayerMP) player);
                                multipart.sendUpdatePacket(true);
								
							}
					}
				}
                });
                return null;
			}
			return null;
		}
	}

}*/