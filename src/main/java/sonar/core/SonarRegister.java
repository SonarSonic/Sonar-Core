package sonar.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appeng.core.CreativeTab;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import sonar.core.common.block.SonarBlockTip;
import sonar.core.helpers.FunctionHelper;
import sonar.core.registries.ISonarRegistryBlock;
import sonar.core.registries.ISonarRegistryItem;
import sonar.core.registries.SonarRegistryBlock;
import sonar.core.registries.SonarRegistryItem;

public class SonarRegister {

	//public static Map<String, List<ISonarRegistryBlock>> registeredBlocks = new HashMap();
	//public static Map<String, List<ISonarRegistryItem>> registeredItems = new HashMap();
	
	//// BLOCKS \\\\
	public static <T extends Block> T addBlock(String modid, CreativeTabs tab, String name, T block) {
		block.setCreativeTab(tab);
		return addBlock(modid, name, block);
	}

	public static <T extends Block> T addBlock(String modid, String name, T block) {
		return addBlock(modid, new SonarRegistryBlock<T>(block, name));
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
		return addItem(modid, new SonarRegistryItem<T>(item, name));
	}

	public static <T extends Item> T addItem(String modid, CreativeTabs tab,ISonarRegistryItem<T> item) {
		item.setCreativeTab(tab);
		return addItem(modid, item);
	}

	public static <T extends Item> T addItem(String modid, ISonarRegistryItem<T> item) {
		setRegistryName(modid, item);
		return SonarCore.proxy.registerItem(modid, item);
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
