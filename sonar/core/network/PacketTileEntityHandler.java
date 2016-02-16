package sonar.core.network;

import sonar.core.SonarCore;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public abstract class PacketTileEntityHandler<T extends PacketCoords> implements IMessageHandler<T, IMessage> {

	@Override
	public final IMessage onMessage(T message, MessageContext ctx) {
		EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
		if (player != null && player.worldObj != null) {
			TileEntity target = player.worldObj.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
			if (target != null) {
				return processMessage(message, target);
			}
		}
		return null;
	}

	public abstract IMessage processMessage(T message, TileEntity target);

}