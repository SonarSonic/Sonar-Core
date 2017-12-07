package sonar.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleContainer;

public class PacketFlexibleContainer implements IMessage {

	public Container container;
	public ByteBuf buf;

	public PacketFlexibleContainer() {}

	public PacketFlexibleContainer(EntityPlayer player) {
		this.container = player.openContainer;
		if (!(container instanceof IFlexibleContainer)) {
			SonarCore.logger.error(getClass().getSimpleName() + " was passed a non Flexible Container " + container.getClass().getSimpleName());
		} else {
			((IFlexibleContainer) container).refreshState();
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.buf = buf.retain();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		if (container instanceof IFlexibleContainer.Syncable) {
			((IFlexibleContainer.Syncable) container).writeState(buf);
		}
	}

	public static class Handler implements IMessageHandler<PacketFlexibleContainer, IMessage> {

		@Override
		public IMessage onMessage(PacketFlexibleContainer message, MessageContext ctx) {
			SonarCore.proxy.getThreadListener(ctx).addScheduledTask(() -> {
				EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
				Container container = player.openContainer;
				if (container != null && container instanceof IFlexibleContainer) {
					if (container instanceof IFlexibleContainer.Syncable) {
						((IFlexibleContainer.Syncable) container).readState(message.buf);
					}
					((IFlexibleContainer) container).refreshState();
				}
				message.buf.release();
			});
			return null;
		}
	}
}
