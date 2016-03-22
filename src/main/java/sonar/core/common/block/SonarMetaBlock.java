package sonar.core.common.block;

import sonar.core.common.block.properties.IMetaRenderer;

import com.google.common.base.Function;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;

public class SonarMetaBlock extends ItemMultiTexture {

	public SonarMetaBlock(Block block) {
		super(block, block, new Function<ItemStack, String>() {
			public String apply(ItemStack stack) {
				Block block = Block.getBlockFromItem(stack.getItem());
				if (block instanceof IMetaRenderer) {
					IMetaRenderer meta = (IMetaRenderer) block;
					return ""+meta.getVariant(stack.getMetadata()).getMeta();
				}
				return ""+0;
			}
		});
	}	
}