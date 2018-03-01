package sonar.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;

public abstract class PacketCoordsHandler<T extends PacketCoords> implements IMessageHandler<T, IMessage> {

    @Override
	public IMessage onMessage(T message, MessageContext ctx) {
		EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);		
		if (player != null) {
			return processMessage(message, player.getEntityWorld(), player);
        }
		return null;
	}

	public abstract IMessage processMessage(T message, World world, EntityPlayer player);
}