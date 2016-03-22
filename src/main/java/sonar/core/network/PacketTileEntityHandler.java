package sonar.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;

public abstract class PacketTileEntityHandler<T extends PacketCoords> implements IMessageHandler<T, IMessage> {

	public final IMessage onMessage(T message, MessageContext ctx) {
		EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
		if (player != null && player.worldObj != null) {
			TileEntity target = player.worldObj.getTileEntity(message.pos);
			if (target != null) {
				return processMessage(message, target);
			}
		}
		return null;
	}

	public abstract IMessage processMessage(T message, TileEntity target);

}