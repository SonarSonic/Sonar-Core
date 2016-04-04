package sonar.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.BlockRenderRegister;

public class SonarClient extends SonarCommon {

	@Override
	public void registerRenderThings() {
		BlockRenderRegister.register();
	}

	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
	}

}
