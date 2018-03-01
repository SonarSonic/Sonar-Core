package sonar.core.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SonarMetaItem extends SonarItem {

	public int numSubItems = 1;

	public SonarMetaItem(int numSubItems) {
		this.numSubItems = numSubItems;
		this.hasSubtypes = true;
	}

	@Override
	public int getMaxDamage() {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (int i = 0; i < numSubItems; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return getUnlocalizedName() + '.' + stack.getItemDamage();
	}
}
