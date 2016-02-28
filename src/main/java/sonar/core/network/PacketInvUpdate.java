package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import sonar.core.SonarCore;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

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

		@Override
		public IMessage onMessage(PacketInvUpdate message, MessageContext ctx) {
			EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
			if (player != null && ctx.side == Side.CLIENT) {
				player.inventory.setInventorySlotContents(message.slot, message.stack);

			}
			return null;
		}

	}
}