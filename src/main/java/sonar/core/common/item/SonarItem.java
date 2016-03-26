package sonar.core.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SonarItem extends Item {

	public NBTTagCompound getTagCompound(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(getDefaultTag());
		}
		return stack.getTagCompound();
	}

	public NBTTagCompound getDefaultTag() {
		return new NBTTagCompound();
	}
}