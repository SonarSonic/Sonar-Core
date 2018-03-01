package sonar.core.network;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.client.BlockModelsCache;
import sonar.core.client.renderers.SonarCustomStateMapper;
import sonar.core.common.block.properties.IMetaRenderer;
import sonar.core.registries.ISonarRegistryBlock;
import sonar.core.registries.ISonarRegistryItem;
import sonar.core.translate.LocalisationManager;

public class SonarClient extends SonarCommon {

	private IThreadListener clientListener;
	public static final SonarCustomStateMapper mapper = new SonarCustomStateMapper();
	public static final LocalisationManager translator = new LocalisationManager();

	public <T extends Block> T registerBlock(String modid, ISonarRegistryBlock<T> block){
		T theBlock = super.registerBlock(modid, block);
		registerBlockRenderer(modid, block.getValue());
		return theBlock;
	}

	public void registerBlockRenderer(String modid, Block block) {
		Item item = Item.getItemFromBlock(block);
		if (item.getHasSubtypes()) {
			List<ItemStack> stacks = Lists.newArrayList();
			item.getSubItems(item, block.getCreativeTabToDisplayOn(), stacks);
			for (ItemStack stack : stacks) {
				String variant = "variant=meta" + stack.getItemDamage();
				if (block instanceof IMetaRenderer) {
					IMetaRenderer meta = (IMetaRenderer) block;
					variant = "variant=" + meta.getVariant(stack.getItemDamage()).getName();
				}
				ModelLoader.setCustomModelResourceLocation(item, stack.getItemDamage(), new ModelResourceLocation(modid + ':' + item.getUnlocalizedName().substring(5), variant));
			}
		} else {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(modid + ':' + item.getUnlocalizedName().substring(5), "inventory"));
		}
	}

	public <T extends Item> T registerItem(String modid, ISonarRegistryItem<T> item){
		T theItem = super.registerItem(modid, item);
		registerItemRenderer(modid, item.getValue());
		return theItem;
	}

	public void registerItemRenderer(String modid, Item item) {
		if (item.getHasSubtypes()) {
			List<ItemStack> stacks = Lists.newArrayList();
			item.getSubItems(item, item.getCreativeTab(), stacks);
			for (ItemStack stack : stacks) {
				String variant = "variant=meta" + stack.getItemDamage();
				if (item instanceof IMetaRenderer) {
					IMetaRenderer meta = (IMetaRenderer) item;
					variant = "variant=" + meta.getVariant(stack.getItemDamage()).getName();
				}
				ModelLoader.setCustomModelResourceLocation(item, stack.getItemDamage(), new ModelResourceLocation(modid + ":" + "items/" + item.getUnlocalizedName().substring(5), variant));
			}
		} else {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(new ResourceLocation(modid, item.getUnlocalizedName().substring(5)), "inventory"));

		}
	}

	@Override
	public Object getStateMapper() {
		return mapper;
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
		return ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx);
	}

	@Override
	public World getDimension(int dimensionID) {
		return FMLCommonHandler.instance().getEffectiveSide().isClient() ? Minecraft.getMinecraft().theWorld : super.getDimension(dimensionID);
	}

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
