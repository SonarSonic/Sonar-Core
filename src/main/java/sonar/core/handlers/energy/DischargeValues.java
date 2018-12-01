package sonar.core.handlers.energy;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sonar.core.SonarCore;
import sonar.core.helpers.SimpleRegistry;

public class DischargeValues extends SimpleRegistry<Item, Integer> {

	public static DischargeValues instance(){
		return SonarCore.instance.dischargeValues;
	}

	public void register() {
		register(Items.REDSTONE, 1000);
		register(Items.COAL, 500);
		register(Blocks.COAL_BLOCK, 4500);
		register(Blocks.REDSTONE_BLOCK, 9000);
	}

	public int getValue(ItemStack stack){
		Integer value = getValue(stack.getItem());
		return value != null ? value : 0;
	}

	public void register(Block block, Integer value){
		super.register(Item.getItemFromBlock(block), value);
	}
}
