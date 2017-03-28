package sonar.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.BlockRenderRegister;
import sonar.core.client.renderers.SonarCustomStateMapper;

public class SonarClient extends SonarCommon {

	private IThreadListener clientListener = null;
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
		return (ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntity(ctx));
	}

	@Override
	public World getDimension(int dimensionID) {
		return FMLCommonHandler.instance().getEffectiveSide().isClient() ? Minecraft.getMinecraft().world : super.getDimension(dimensionID);
	}

	public IThreadListener getThreadListener(MessageContext ctx) {
		if (ctx.side.isClient()) {
			if (clientListener == null)
				clientListener = Minecraft.getMinecraft();
			return clientListener;
		} else {
			return super.getThreadListener(ctx);
		}
	}
}
