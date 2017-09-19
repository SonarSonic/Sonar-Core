package sonar.core.registries;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class SonarRegistryItem implements ISonarRegistryItem {

	public Item item;
	public String name;	
    public boolean ignoreNormalTab;

	public SonarRegistryItem(String name) {
		this.item = new Item();
		this.name = name;
	}

	public SonarRegistryItem(Item item, String name) {
		this.item = item;
		this.name = name;
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public SonarRegistryItem setItem(Item item) {
		this.item = item;
		return this;
	}

	@Override
	public String getRegistryName() {
		return name;
	}

	@Override
	public SonarRegistryItem setRegistryName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public SonarRegistryItem setCreativeTab(CreativeTabs tab) {
		item.setCreativeTab(tab);
		ignoreNormalTab=true;
		return this;
	}
}
