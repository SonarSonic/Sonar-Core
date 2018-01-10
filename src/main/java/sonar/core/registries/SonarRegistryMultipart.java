package sonar.core.registries;

import mcmultipart.api.item.ItemBlockMultipart;
import mcmultipart.api.multipart.IMultipart;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

public class SonarRegistryMultipart<T extends Block & IMultipart> extends SonarRegistryBlock<T>{
	
	public SonarRegistryMultipart(T block, String name) {
		super(block, name);
	}
	
	public SonarRegistryMultipart(T block, String name, Class<? extends TileEntity> tile) {
		super(block, name, tile);
	}

	@Override
	public Item getItemBlock() {
		return new ItemBlockMultipart(value);
	}
}