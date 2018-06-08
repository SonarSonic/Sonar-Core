package sonar.core.network;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.client.BlockModelsCache;
import sonar.core.registries.ISonarRegistryBlock;
import sonar.core.registries.ISonarRegistryItem;
import sonar.core.translate.LocalisationManager;

public class SonarClient extends SonarCommon {

	private IThreadListener clientListener;
	public static final LocalisationManager translator = new LocalisationManager();

	public <T extends Block> T registerBlock(String modid, ISonarRegistryBlock<T> block){
		T theBlock = super.registerBlock(modid, block);
		if(block.shouldRegisterRenderer()) {
			registerBlockRenderer(modid, block);
		}
		return theBlock;
	}

	public <T extends Block> void registerBlockRenderer(String modid, ISonarRegistryBlock<T> registryBlock) {
		Block block = registryBlock.getValue();
		Item item = Item.getItemFromBlock(block);
		if (item.getHasSubtypes()) {
			NonNullList<ItemStack> stacks = NonNullList.create();
			item.getSubItems(block.getCreativeTabToDisplayOn(), stacks);
			stacks.forEach(s -> ModelLoader.setCustomModelResourceLocation(item, s.getItemDamage(), registryBlock.getItemBlockMetadataRendererLocation(modid, s)));
		} else {
			ModelLoader.setCustomModelResourceLocation(item, 0, registryBlock.getItemBlockRendererLocation(modid, item));
		}
	}

	public <T extends Item> T registerItem(String modid, ISonarRegistryItem<T> item){
		T theItem = super.registerItem(modid, item);
		if(item.shouldRegisterRenderer()) {
			registerItemRenderer(modid, item);
		}
		return theItem;
	}

	public <T extends Item> void registerItemRenderer(String modid, ISonarRegistryItem<T> registryItem) {
		Item item = registryItem.getValue();
		if (item.getHasSubtypes()) {
			NonNullList<ItemStack> stacks = NonNullList.create();
			item.getSubItems(item.getCreativeTab(), stacks);
			stacks.forEach(s -> ModelLoader.setCustomModelResourceLocation(item, s.getItemDamage(), registryItem.getItemMetadataRendererLocation(modid, s)));
		} else {
			ModelLoader.setCustomModelResourceLocation(item, 0, registryItem.getItemRendererLocation(modid, item));

		}
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		SonarCore.logger.info("Registering Renderers");
		SonarCore.logger.info("Registered Renderers");
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		if (manager instanceof SimpleReloadableResourceManager) {
			SimpleReloadableResourceManager resources = (SimpleReloadableResourceManager) manager;
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
		return ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntity(ctx);
	}
	/*
	@Override
	public World getDimension(int dimensionID) {
		return FMLCommonHandler.instance().getSide().isClient() ? Minecraft.getMinecraft().world : super.getDimension(dimensionID);
	}
	*/
	@Override
	public IThreadListener getThreadListener(Side side) {
		if (side.isClient()) {
			if (clientListener == null)
				clientListener = Minecraft.getMinecraft();
			return clientListener;
		} else {
			return super.getThreadListener(side);
		}
	}
}
