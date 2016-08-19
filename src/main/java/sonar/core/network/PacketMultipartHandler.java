package sonar.core.network;

import javax.annotation.Nullable;

import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.integration.multipart.SonarMultipartHelper;

public abstract class PacketMultipartHandler<T extends PacketMultipart> implements IMessageHandler<T, IMessage> {

	public final IMessage onMessage(T message, MessageContext ctx) {
		EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
		if (player != null && player.worldObj != null) {
			Object target = SonarMultipartHelper.getTile(player.worldObj, message.pos);
			if (target != null && target instanceof IMultipartContainer) {
				return processMessage(message, (IMultipartContainer) target, ((IMultipartContainer) target).getPartFromID(message.partUUID));
			}
		}
		return null;
	}

	public abstract IMessage processMessage(T message, IMultipartContainer target, @Nullable IMultipart part);

}