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
	public int guiID;
	public int returnID;

	public PacketFlexibleChangeGui() {
	}

	public PacketFlexibleChangeGui(UUID partUUID, BlockPos pos, int guiID, int returnID) {
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

	public static class Handler extends PacketMultipartHandler<PacketFlexibleChangeGui> {

		@Override
		public IMessage processMessage(PacketFlexibleChangeGui message, IMultipartContainer target, IMultipart part, MessageContext ctx) {
			if (!(part instanceof IFlexibleGui)) {
				return null;
			}
			SonarCore.proxy.getThreadListener(ctx).addScheduledTask(new Runnable() {
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
