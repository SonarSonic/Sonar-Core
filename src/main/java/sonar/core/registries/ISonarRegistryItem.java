package sonar.core.registries;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sonar.core.common.block.properties.IMetaRenderer;

public interface ISonarRegistryItem<T extends Item> extends IAbstractSonarRegistry<T> {

    ISonarRegistryItem<T> setCreativeTab(CreativeTabs tab);

    default ModelResourceLocation getItemMetadataRendererLocation(String modid, ItemStack stack){
        String variant = "variant=meta" + stack.getItemDamage();
        if (getValue() instanceof IMetaRenderer) {
            IMetaRenderer meta = (IMetaRenderer) getValue();
            variant = "variant=" + meta.getVariant(stack.getItemDamage()).getName();
        }
        return new ModelResourceLocation(modid + ':'+ "items/"+ stack.getItem().getUnlocalizedName().substring(5), variant);
    }

    default ModelResourceLocation getItemRendererLocation(String modid, Item item){
        return new ModelResourceLocation(modid + ':' + item.getUnlocalizedName().substring(5), "inventory");
    }
}
