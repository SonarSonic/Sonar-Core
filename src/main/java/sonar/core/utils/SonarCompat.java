package sonar.core.utils;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
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
	
	public static Item getItem(ItemStack stack){
		if(isEmpty(stack)){
			return null;
		}
		return stack.getItem();
	}

	public static ItemStack setCount(ItemStack stack, int count) {
		if (stack != null) {
			stack.stackSize = Math.min(stack.getMaxStackSize(), count);
		}
		if (isEmpty(stack)) {
			return getEmpty();
		}
		return stack;
	}

	public static ItemStack shrinkAndSet(List<ItemStack> stacks, int pos, int shrink) {
		if (pos >= stacks.size()) {
			return getEmpty();
		}
		ItemStack stack = stacks.get(pos);
		ItemStack shrinked = shrink(stack, shrink);
		stacks.set(pos, shrinked);
		return shrinked;
	}

	public static ItemStack shrink(ItemStack stack, int shrink) {
		return setCount(stack, getCount(stack) - shrink);
	}

	public static ItemStack growAndSet(List<ItemStack> stacks, int pos, int grow) {
		if (pos >= stacks.size()) {
			return getEmpty();
		}
		ItemStack stack = stacks.get(pos);
		ItemStack grown = grow(stack, grow);
		stacks.set(pos, grown);
		return grown;
	}

	public static ItemStack grow(ItemStack stack, int grow) {
		return setCount(stack, getCount(stack) + grow);
	}

	public static ItemStack getItem(NBTTagCompound nbt) {
		return ItemStack.loadItemStackFromNBT(nbt);
	}
	
	public static List<ItemStack> buildItemList(int size){
		List<ItemStack> slots = Lists.newArrayList();		
		for(int i=0;i<size;i++){
			slots.add(SonarCompat.getEmpty());
		}
		return slots;
	}
}
