package sonar.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;

public class PacketFlexibleCloseGui extends PacketCoords {
	
    public PacketFlexibleCloseGui() {
    }

	public PacketFlexibleCloseGui(BlockPos pos) {
		super(pos);
	}

	public static class Handler implements IMessageHandler<PacketFlexibleCloseGui, IMessage> {
		@Override
		public IMessage onMessage(PacketFlexibleCloseGui message, MessageContext ctx) {
            SonarCore.proxy.getThreadListener(ctx).addScheduledTask(() -> {
					EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
					FlexibleGuiHandler.closeGui(player, ctx.side);
			});
			if (ctx.side == Side.SERVER) {
				return new PacketFlexibleCloseGui(message.pos);
			}
			return null;
		}
	}
}
