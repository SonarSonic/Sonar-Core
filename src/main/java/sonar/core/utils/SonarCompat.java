package sonar.core.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SonarCompat {

	public static boolean isEmpty(ItemStack stack) {
		return stack == null || stack.stackSize <= 0;
	}

	public static int getCount(ItemStack stack) {
		return isEmpty(stack) ? 0 : stack.stackSize;
	}

	public static ItemStack getEmpty() {
		return null;
	}

	public static ItemStack setCount(ItemStack stack, int count) {
		if (stack != null) {
			stack.stackSize = Math.min(stack.getMaxStackSize(), count);
		}
		if(stack.stackSize <= 0){
			return null;
		}
		return stack;
	}

	public static ItemStack shrink(ItemStack stack, int shrink) {
		return setCount(stack, getCount(stack)-shrink);
	}

	public static ItemStack grow(ItemStack stack, int grow) {
		return setCount(stack, getCount(stack)+grow);
	}

	public static ItemStack getItem(NBTTagCompound nbt) {
		return ItemStack.loadItemStackFromNBT(nbt);
	}

}
