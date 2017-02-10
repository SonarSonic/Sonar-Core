package sonar.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SonarCommon {

	private IThreadListener serverListener = null;

	public Object getStateMapper() {
		return null;
	}

	public void registerRenderThings() {
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
}
