package sonar.core.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;

public class PacketFlexibleChangeGui extends PacketMultipart {
	public int windowID;

	public PacketFlexibleChangeGui() {
	}

	public PacketFlexibleChangeGui(UUID partUUID, BlockPos pos, int windowID) {
		super(partUUID, pos);
		this.windowID = windowID;
	}

	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		windowID = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(windowID);
	}

	public static class Handler extends PacketMultipartHandler<PacketFlexibleChangeGui> {

		@Override
		public IMessage processMessage(PacketFlexibleChangeGui message, IMultipartContainer target, IMultipart part, MessageContext ctx) {
			if (!(part instanceof IFlexibleGui)) {
				return null;
			}
			EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
			SonarCore.proxy.getThreadListener(ctx).addScheduledTask(new Runnable() {
				public void run() {
					SonarCore.instance.guiHandler.openBasicMultipart(true, message.partUUID, player, player.getEntityWorld(), message.pos, message.windowID);
				}
			});
			return null;
		}
	}

}
