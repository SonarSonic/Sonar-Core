package sonar.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.BlockRenderRegister;
import sonar.core.SonarCore;
import sonar.core.client.BlockModelsCache;
import sonar.core.client.renderers.SonarCustomStateMapper;
import sonar.core.translate.LocalisationManager;

public class SonarClient extends SonarCommon {

	private IThreadListener clientListener = null;
	public static final SonarCustomStateMapper mapper = new SonarCustomStateMapper();
	public static final LocalisationManager translator = new LocalisationManager();

	public Object getStateMapper() {
		return mapper;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		SonarCore.logger.info("Registering Renderers");
		BlockRenderRegister.register();
		SonarCore.logger.info("Registered Renderers");
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		if (manager instanceof SimpleReloadableResourceManager) {
			SimpleReloadableResourceManager resources = ((SimpleReloadableResourceManager) manager);
			resources.registerReloadListener(BlockModelsCache.INSTANCE);
			resources.registerReloadListener(translator);
		}
	}
	
	@Override
	public void serverClose(FMLServerStoppingEvent event) {
		super.serverClose(event);
		translator.clear();
	}

	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
	}

	@Override
	public World getDimension(int dimensionID) {
		return FMLCommonHandler.instance().getEffectiveSide().isClient() ? Minecraft.getMinecraft().theWorld : super.getDimension(dimensionID);
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
