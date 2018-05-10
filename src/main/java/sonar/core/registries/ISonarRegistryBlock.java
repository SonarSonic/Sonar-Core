package sonar.core.registries;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

public interface ISonarRegistryBlock<T extends Block> extends IAbstractSonarRegistry<T> {

    
    Item getItemBlock();

    /**
     * sets the creative tab to register this Block under
     */
    ISonarRegistryBlock setCreativeTab(CreativeTabs tab);
	
    boolean hasTileEntity();
	
    Class<? extends TileEntity> getTileEntity();

    default String getTileEntityRegistryName(){
        return getRegistryName();
    };

    ISonarRegistryBlock addTileEntity(Class<? extends TileEntity> tile);
    
    boolean shouldRegisterRenderer();
}
