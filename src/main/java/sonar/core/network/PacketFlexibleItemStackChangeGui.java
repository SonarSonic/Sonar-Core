package sonar.core.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
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

public class PacketFlexibleItemStackChangeGui extends PacketCoords {
	public int guiID;
	public int returnID;

	public PacketFlexibleItemStackChangeGui() {
	}

	public PacketFlexibleItemStackChangeGui(BlockPos pos, int guiID, int returnID) {
		super(pos);
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

	public static class Handler implements IMessageHandler<PacketFlexibleItemStackChangeGui, IMessage> {

		@Override
		public IMessage onMessage(PacketFlexibleItemStackChangeGui message, MessageContext ctx) {
			SonarCore.proxy.getThreadListener(ctx).addScheduledTask(new Runnable() {
				public void run() {
					EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
					ItemStack stack = player.getHeldItemMainhand();
					if (!(stack.getItem() instanceof IFlexibleGui)) {
						return;
					}
					SonarCore.instance.guiHandler.lastID.put(player, message.returnID);
					SonarCore.instance.guiHandler.openBasicItemStack(true, stack, player, player.getEntityWorld(), message.pos, message.guiID);
				}
			});
			return null;
		}
	}

}
