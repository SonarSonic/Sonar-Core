package sonar.core.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;

public class PacketFlexibleMultipartChangeGui extends PacketMultipart {
	public int guiID;
	public int returnID;

	public PacketFlexibleMultipartChangeGui() {
	}

	public PacketFlexibleMultipartChangeGui(UUID partUUID, BlockPos pos, int guiID, int returnID) {
		super(partUUID, pos);
		this.guiID = guiID;
		this.returnID = returnID;
	}

	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		guiID = buf.readInt();
		returnID = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(guiID);
		buf.writeInt(returnID);
	}

	public static class Handler extends PacketMultipartHandler<PacketFlexibleMultipartChangeGui> {

		@Override
		public IMessage processMessage(PacketFlexibleMultipartChangeGui message, IMultipartContainer target, IMultipart part, MessageContext ctx) {
			if (!(part instanceof IFlexibleGui)) {
				return null;
			}
			SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(new Runnable() {
				public void run() {
					EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
					SonarCore.instance.guiHandler.lastID.put(player, message.returnID);
					SonarCore.instance.guiHandler.openBasicMultipart(true, message.partUUID, player, player.getEntityWorld(), message.pos, message.guiID);
				}
			});
			return null;
		}
	}

}
