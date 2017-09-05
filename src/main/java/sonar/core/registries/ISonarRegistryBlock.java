package sonar.core.registries;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;

public interface ISonarRegistryBlock {

    /**
     * gets the Block to register
     */
    Block getBlock();

    /**
     * @param block the item you wish to register
     * @return this;
     */
    ISonarRegistryBlock setBlock(Block block);

    /**
     * @return gets the name to register the Block under
     */
    String getRegistryName();

    /**
     * sets the name to register this Block under
     */
    ISonarRegistryBlock setRegistryName(String name);

    /**
     * sets the creative tab to register this Block under
     */
    ISonarRegistryBlock setCreativeTab(CreativeTabs tab);
	
    boolean hasTileEntity();
	
    Class<? extends TileEntity> getTileEntity();

    ISonarRegistryBlock addTileEntity(Class<? extends TileEntity> tile);
}
