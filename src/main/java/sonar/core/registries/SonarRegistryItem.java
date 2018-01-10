package sonar.core.registries;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class SonarRegistryItem<T extends Item> extends AbstractSonarRegistry<T> implements ISonarRegistryItem<T> {

	public boolean ignoreNormalTab;

	public SonarRegistryItem(String name) {
		super((T) new Item(), name);
	}

	public SonarRegistryItem(T item, String name) {
		super(item, name);
	}
	
	public SonarRegistryItem removeCreativeTab(){
		value.setCreativeTab(null);
		ignoreNormalTab=true;
		return this;
	}

	@Override
	public SonarRegistryItem setCreativeTab(CreativeTabs tab) {
		value.setCreativeTab(tab);
		ignoreNormalTab = true;
		return this;
	}
}
