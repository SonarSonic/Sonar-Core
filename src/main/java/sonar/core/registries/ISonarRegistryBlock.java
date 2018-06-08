package sonar.core.registries;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import sonar.core.common.block.properties.IMetaRenderer;

public interface ISonarRegistryBlock<T extends Block> extends IAbstractSonarRegistry<T> {

    ISonarRegistryBlock setCreativeTab(CreativeTabs tab);
    
    Item getItemBlock();
	
    boolean hasTileEntity();
	
    Class<? extends TileEntity> getTileEntity();

    default String getTileEntityRegistryName(){
        return getRegistryName();
    };

    ISonarRegistryBlock addTileEntity(Class<? extends TileEntity> tile);

    default ModelResourceLocation getItemBlockMetadataRendererLocation(String modid, ItemStack stack){
        String variant = "variant=meta" + stack.getItemDamage();
        if (getValue() instanceof IMetaRenderer) {
            IMetaRenderer meta = (IMetaRenderer) getValue();
            variant = "variant=" + meta.getVariant(stack.getItemDamage()).getName();
        }
        return new ModelResourceLocation(modid + ':' + stack.getItem().getUnlocalizedName().substring(5), variant);
    }

    default ModelResourceLocation getItemBlockRendererLocation(String modid, Item item){
        Item itemBlock = getItemBlock();
        return new ModelResourceLocation(modid + ':' + item.getUnlocalizedName().substring(5), "inventory");
    }
}
