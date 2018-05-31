package sonar.core;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import sonar.core.registries.ISonarRegistryBlock;
import sonar.core.registries.ISonarRegistryItem;
import sonar.core.registries.SonarRegistryBlock;
import sonar.core.registries.SonarRegistryItem;

public class SonarRegister {

	//// BLOCKS \\\\
	public static <T extends Block> T addBlock(String modid, CreativeTabs tab, String name, T block) {
		block.setCreativeTab(tab);
		return addBlock(modid, name, block);
	}

	public static <T extends Block> T addBlock(String modid, String name, T block) {
		return addBlock(modid, new SonarRegistryBlock<>(block, name));
	}

	public static <T extends Block> T addBlock(String modid, CreativeTabs tab, ISonarRegistryBlock<T> block) {
		block.setCreativeTab(tab);
		return addBlock(modid, block);
	}

	public static <T extends Block> T addBlock(String modid, ISonarRegistryBlock<T> block) {
		setRegistryName(modid, block);
		return SonarCore.proxy.registerBlock(modid, block);
	}
	
	//// ITEMS \\\\	
	public static <T extends Item> T addItem(String modid, CreativeTabs tab, String name, T item) {
		item.setCreativeTab(tab);
		return addItem(modid, name, item);
	}

	public static <T extends Item> T addItem(String modid, String name, T item) {
		return addItem(modid, new SonarRegistryItem<>(item, name));
	}

	public static <T extends Item> T addItem(String modid, CreativeTabs tab,ISonarRegistryItem<T> item) {
		item.setCreativeTab(tab);
		return addItem(modid, item);
	}

	public static <T extends Item> T addItem(String modid, ISonarRegistryItem<T> item) {
		setRegistryName(modid, item);
		return SonarCore.proxy.registerItem(modid, item);
	}

	//TODO 1.3 - ADD MODID TO RESOURCE
	public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String modid, String key){
		GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(key));
	}

	//// SET REGISTRY NAME \\\\
	
	private static <T extends Block> void setRegistryName(String modid, ISonarRegistryBlock<T> block) {
		T theBlock = block.getValue();
		setRegistryName(modid, block.getRegistryName(), theBlock);
		block.setValue(theBlock);
	}

	private static void setRegistryName(String modid, String name, Block block) {
		block.setUnlocalizedName(name);
		block.setRegistryName(modid, name);
	}

	private static <T extends Item> void setRegistryName(String modid, ISonarRegistryItem<T> item) {
		T theItem = item.getValue();
		setRegistryName(modid, item.getRegistryName(), theItem);
		item.setValue(theItem);
	}

	private static void setRegistryName(String modid, String name, Item item) {
		item.setUnlocalizedName(name);
		item.setRegistryName(modid, name);
	}
}
