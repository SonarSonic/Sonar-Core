package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;

public class PacketStackUpdate implements IMessage {

	public ItemStack stack;

	public PacketStackUpdate() {
	}

	public PacketStackUpdate(ItemStack stack) {
		this.stack = stack;
	}

    @Override
	public void fromBytes(ByteBuf buf) {
		if (buf.readBoolean())
			this.stack = ByteBufUtils.readItemStack(buf);
	}

    @Override
	public void toBytes(ByteBuf buf) {
		if (stack != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeItemStack(buf, stack);
		}else{
			buf.writeBoolean(false);
		}
	}

	public static class Handler implements IMessageHandler<PacketStackUpdate, IMessage> {

        @Override
		public IMessage onMessage(PacketStackUpdate message, MessageContext ctx) {
			EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
			if (player != null && ctx.side == Side.CLIENT) {
				player.inventory.setItemStack(message.stack);
			}
			return null;
		}
	}
}