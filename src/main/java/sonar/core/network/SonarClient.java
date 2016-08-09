package sonar.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.BlockRenderRegister;
import sonar.core.client.renderers.SonarCustomStateMapper;

public class SonarClient extends SonarCommon {

	public static final SonarCustomStateMapper mapper = new SonarCustomStateMapper();

	public Object getStateMapper() {
		return mapper;
	}
	
	@Override
	public void registerRenderThings() {
		BlockRenderRegister.register();
	}

	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
	}

}
