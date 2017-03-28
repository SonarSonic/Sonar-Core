package sonar.core.common.block;

import com.google.common.base.Function;

import net.minecraft.block.Block;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import sonar.core.common.block.properties.IMetaRenderer;

public class SonarMetaBlock extends ItemMultiTexture {

	public SonarMetaBlock(Block block) {
		super(block, block, new ItemMultiTexture.Mapper() {
			public String apply(ItemStack stack) {
				Block block = Block.getBlockFromItem(stack.getItem());
				if (block instanceof IMetaRenderer) {
					IMetaRenderer meta = (IMetaRenderer) block;
					return String.valueOf(meta.getVariant(stack.getMetadata()).getMeta());
				}
				return String.valueOf(0);
			}
		});
	}
}