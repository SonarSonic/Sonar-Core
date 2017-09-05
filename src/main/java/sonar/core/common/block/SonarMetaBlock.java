package sonar.core.common.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemMultiTexture;
import sonar.core.common.block.properties.IMetaRenderer;

public class SonarMetaBlock extends ItemMultiTexture {

	public SonarMetaBlock(Block block) {
        super(block, block, stack -> {
            Block block1 = Block.getBlockFromItem(stack.getItem());
            if (block1 instanceof IMetaRenderer) {
                IMetaRenderer meta = (IMetaRenderer) block1;
					return String.valueOf(meta.getVariant(stack.getMetadata()).getMeta());
				}
				return String.valueOf(0);
		});
	}
}