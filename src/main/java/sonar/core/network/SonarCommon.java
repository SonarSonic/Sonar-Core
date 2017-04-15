package sonar.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SonarCommon {

	private IThreadListener serverListener = null;

	public Object getStateMapper() {
		return null;
	}
	
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity;
	}

	public World getDimension(int dimensionID) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return server.worldServerForDimension(dimensionID);
	}

	public IThreadListener getThreadListener(MessageContext ctx) {
		if (serverListener == null)
			serverListener = FMLCommonHandler.instance().getMinecraftServerInstance();
		return serverListener;
	}


	public void preInit(FMLPreInitializationEvent event) {}

	public void load(FMLInitializationEvent event) {}

	public void postLoad(FMLPostInitializationEvent evt) {}	

	public void serverClose(FMLServerStoppingEvent event) {}
}
