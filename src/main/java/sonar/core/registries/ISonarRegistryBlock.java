package sonar.core.registries;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;

public interface ISonarRegistryBlock {

	/** gets the Block to register */
	public Block getBlock();

	/** @param Block the item you wish to register
	 * @return this; */
	public ISonarRegistryBlock setBlock(Block block);

	/** @return gets the name to register the Block under */
	public String getRegistryName();

	/** sets the name to register this Block under*/
	public ISonarRegistryBlock setRegistryName(String name);

	/** sets the creative tab to register this Block under*/
	public ISonarRegistryBlock setCreativeTab(CreativeTabs tab);
	
	public boolean hasTileEntity();
	
	public Class<? extends TileEntity> getTileEntity();
	
	public ISonarRegistryBlock addTileEntity(Class<? extends TileEntity> tile);

}
