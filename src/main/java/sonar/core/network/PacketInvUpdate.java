package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;

public class PacketInvUpdate extends PacketStackUpdate {

	public int slot;

	public PacketInvUpdate() {}

	public PacketInvUpdate(int slot, ItemStack stack) {
		super(stack);
		this.slot = slot;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.slot = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(slot);
	}

	public static class Handler implements IMessageHandler<PacketInvUpdate, IMessage> {

		public IMessage onMessage(PacketInvUpdate message, MessageContext ctx) {
			EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
			if (player != null && ctx.side == Side.CLIENT) {
				player.inventory.setInventorySlotContents(message.slot, message.stack);

			}
			return null;
		}

	}
}