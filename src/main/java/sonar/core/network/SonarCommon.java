package sonar.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
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

	public IThreadListener getThreadListener() {
		if (serverListener == null)
			serverListener = FMLCommonHandler.instance().getMinecraftServerInstance();
		return serverListener;
	}
}
