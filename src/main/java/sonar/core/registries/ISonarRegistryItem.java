package sonar.core.registries;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public interface ISonarRegistryItem {

    /**
     * gets the item to register
     */
    Item getItem();

    /**
     * @param item the item you wish to register
     * @return this;
     */
    ISonarRegistryItem setItem(Item item);

    /**
     * @return gets the name to register the item under
     */
    String getRegistryName();

    /**
     * sets the name to register this item under
     */
    ISonarRegistryItem setRegistryName(String name);

    /**
     * sets the creative tab to register this item under
     */
    ISonarRegistryItem setCreativeTab(CreativeTabs tab);
}
