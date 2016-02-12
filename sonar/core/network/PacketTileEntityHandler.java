package sonar.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public abstract class PacketTileEntityHandler<T extends PacketTileEntity> implements IMessageHandler<T, IMessage> {

	@Override
	public final IMessage onMessage(T message, MessageContext ctx) {
		World world = null;
		if (ctx.side == Side.SERVER) {
			world = ctx.getServerHandler().playerEntity.worldObj;
		} else if (ctx.side == Side.CLIENT) {
			world = Minecraft.getMinecraft().thePlayer.worldObj;
		}
		if (world != null) {
			TileEntity target = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
			if (target != null) {
				return processMessage(message, target);
			}
		}
		return null;
	}

	public abstract IMessage processMessage(T message, TileEntity target);

}