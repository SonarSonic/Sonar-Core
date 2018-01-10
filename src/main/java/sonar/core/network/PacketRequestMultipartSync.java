package sonar.core.network;

import java.util.Optional;

import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.multipart.MultipartHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.integration.multipart.TileSonarMultipart;

public class PacketRequestMultipartSync extends PacketMultipart {

	public PacketRequestMultipartSync() {}

	public PacketRequestMultipartSync(BlockPos pos, int slotID) {
		super(slotID, pos);
	}

	public static class Handler extends PacketMultipartHandler<PacketRequestMultipartSync> {

		@Override
		public IMessage processMessage(PacketRequestMultipartSync message, EntityPlayer player, World world, IMultipartTile part, MessageContext ctx) {
			if (!world.isRemote) {
				SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
					if(part instanceof TileSonarMultipart){
						TileSonarMultipart multipart = (TileSonarMultipart) part;
						multipart.forceNextSync();
						multipart.onSyncPacketRequested(player);
						SonarMultipartHelper.sendMultipartSyncToPlayer(multipart, (EntityPlayerMP) player);
					}
					
				});
				return null;
			}
			return null;
		}
	}

}