package sonar.core.network;

import io.netty.buffer.ByteBuf;
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

public class PacketFlexibleCloseGui extends PacketCoords {
	public int windowID;

	public PacketFlexibleCloseGui() {}

	public PacketFlexibleCloseGui(BlockPos pos) {
		super(pos);
	}

	public static class Handler implements IMessageHandler<PacketFlexibleCloseGui, IMessage> {
		@Override
		public IMessage onMessage(PacketFlexibleCloseGui message, MessageContext ctx) {
			EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
			SonarCore.proxy.getThreadListener(ctx).addScheduledTask(new Runnable() {
				public void run() {
					FlexibleGuiHandler.closeGui(player, ctx.side);
				}
			});
			if (ctx.side == Side.SERVER) {
				return new PacketFlexibleCloseGui(message.pos);
			}
			return null;
		}
	}

}
