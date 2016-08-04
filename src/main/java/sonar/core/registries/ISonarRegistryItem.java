package sonar.core.registries;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public interface ISonarRegistryItem {

	/** gets the item to register */
	public Item getItem();

	/** @param item the item you wish to register
	 * @return this; */
	public ISonarRegistryItem setItem(Item item);

	/** @return gets the name to register the item under */
	public String getRegistryName();

	/** sets the name to register this item under*/
	public ISonarRegistryItem setRegistryName(String name);

	/** sets the creative tab to register this item under*/
	public ISonarRegistryItem setCreativeTab(CreativeTabs tab);

}
