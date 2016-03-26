package sonar.core.common.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import sonar.core.common.block.properties.IMetaRenderer;

import com.google.common.base.Function;

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