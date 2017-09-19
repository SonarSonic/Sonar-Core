package sonar.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;

public abstract class PacketTileEntityHandler<T extends PacketCoords> implements IMessageHandler<T, IMessage> {

    @Override
	public final IMessage onMessage(T message, MessageContext ctx) {
		IMessage returnMessage = null;
		EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
		if (player != null && player.getEntityWorld() != null) {
			TileEntity target = player.getEntityWorld().getTileEntity(message.pos);
			if (target != null) {
				processMessage(player, ctx, message, target);
			}
		}
		return null;
	}

	public abstract IMessage processMessage(EntityPlayer player, MessageContext ctx, T message, TileEntity target);
}