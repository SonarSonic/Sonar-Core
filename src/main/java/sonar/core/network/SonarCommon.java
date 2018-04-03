package sonar.core.network;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.registries.ISonarRegistryBlock;
import sonar.core.registries.ISonarRegistryItem;

public class SonarCommon {

	private IThreadListener serverListener;

	public <T extends Block> T registerBlock(String modid, ISonarRegistryBlock<T> block){
        ForgeRegistries.BLOCKS.register(block.getValue());
        ForgeRegistries.ITEMS.register(block.getItemBlock().setUnlocalizedName(block.getRegistryName()).setRegistryName(modid, block.getRegistryName()));
		if (block.hasTileEntity()) {
			GameRegistry.registerTileEntity(block.getTileEntity(), block.getRegistryName());
		}
		return block.getValue();
	}
	
	public <T extends Item> T registerItem(String modid, ISonarRegistryItem<T> item){
        ForgeRegistries.ITEMS.register(item.getValue());
		return item.getValue();
	}
	
	public Object getStateMapper() {
		return null;
	}

	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return ctx.getServerHandler().player;
	}

	public World getDimension(int dimensionID) {
		return DimensionManager.getWorld(dimensionID);
	}

	public IThreadListener getThreadListener(Side side) {
		if (serverListener == null)
			serverListener = FMLCommonHandler.instance().getMinecraftServerInstance();
		return serverListener;
	}

	public void preInit(FMLPreInitializationEvent event) {}

	public void load(FMLInitializationEvent event) {}

	public void postLoad(FMLPostInitializationEvent event) {}

	public void serverClose(FMLServerStoppingEvent event) {}
}
