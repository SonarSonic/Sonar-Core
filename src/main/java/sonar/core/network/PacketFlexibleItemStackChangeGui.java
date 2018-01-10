package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
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

    @Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		guiID = buf.readInt();
		returnID = buf.readInt();
	}

    @Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(guiID);
		buf.writeInt(returnID);
	}

	public static class Handler implements IMessageHandler<PacketFlexibleItemStackChangeGui, IMessage> {

		@Override
		public IMessage onMessage(PacketFlexibleItemStackChangeGui message, MessageContext ctx) {
            SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
					EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
					ItemStack stack = player.getHeldItemMainhand();
					if (!(stack.getItem() instanceof IFlexibleGui)) {
						return;
					}
					SonarCore.instance.guiHandler.lastID.put(player, message.returnID);
					SonarCore.instance.guiHandler.openBasicItemStack(true, stack, player, player.getEntityWorld(), message.pos, message.guiID);
			});
			return null;
		}
	}
}
