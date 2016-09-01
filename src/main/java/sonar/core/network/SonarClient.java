package sonar.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.BlockRenderRegister;
import sonar.core.SonarCore;
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

	public GuiContainer getClientElement(int id, EntityPlayer player, World world, BlockPos pos, NBTTagCompound tag) {
		GuiContainer screen = (GuiContainer) super.getClientElement(id, player, world, pos, tag);
		return screen;
	}

	public void openGui(EntityPlayer player, World world, BlockPos pos, int id, NBTTagCompound tag) {
		if (!(player instanceof FakePlayer)) {
			Object element = getClientElement(id, player, world, pos, tag);
			FMLCommonHandler.instance().showGuiScreen(element);
		}
	}
}
