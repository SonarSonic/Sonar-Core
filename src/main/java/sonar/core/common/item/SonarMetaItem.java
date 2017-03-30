package sonar.core.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SonarMetaItem extends SonarItem {

	public int numSubItems = 1;

	public SonarMetaItem(int numSubItems) {
		this.numSubItems = numSubItems;
		this.hasSubtypes = true;
	}

	public int getMaxDamage() {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
		for (int i = 0; i < numSubItems; i++) {
			list.add(new ItemStack(this, 1, i));
		}
	}

	public String getUnlocalizedName(ItemStack stack) {
		return getUnlocalizedName() + "." + stack.getItemDamage();
	}
}
