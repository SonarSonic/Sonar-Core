package sonar.core.registries;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import sonar.core.common.block.SonarMetaBlock;

public class SonarRegistryMetablock<T extends Block> extends SonarRegistryBlock<T>{
	
	public SonarRegistryMetablock(T block, String name) {
		super(block, name);
	}
	
	public SonarRegistryMetablock(T block, String name, Class<? extends TileEntity> tile) {
		super(block, name, tile);
	}

	@Override
	public Item getItemBlock() {
		return new SonarMetaBlock(value);
	}
}