package sonar.core.registries;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public interface ISonarRegistryItem<T extends Item> extends IAbstractSonarRegistry<T> {

    ISonarRegistryItem<T> setCreativeTab(CreativeTabs tab);
}
