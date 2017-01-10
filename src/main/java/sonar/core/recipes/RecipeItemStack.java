package sonar.core.recipes;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import sonar.core.api.inventories.StoredItemStack;

/** works for matching StoredItemStacks */
public class RecipeItemStack implements ISonarRecipeObject, ISonarRecipeItem {

	public ItemStack stack;
	public boolean ignoreNBT;

	public RecipeItemStack(ItemStack stack, boolean ignoreNBT) {
		this.stack = stack;
		this.ignoreNBT = ignoreNBT;
	}

	@Override
	public Object getValue() {
		return stack;
	}

	@Override
	public boolean matches(Object object, RecipeObjectType type) {
		if (object instanceof ItemStack) {
			ItemStack stack2 = (ItemStack) object;
			if (!stack2.isItemEqual(stack)) {
				return false;
			}
			if (!ignoreNBT && !ItemStack.areItemStackTagsEqual(stack2, stack)) {
				return false;
			}
			return type.checkStackSize(stack.stackSize, stack2.stackSize);
		}
		if (object instanceof StoredItemStack) {
			StoredItemStack stack2 = (StoredItemStack) object;
			if (!stack2.equalStack(stack)) {
				return false;
			}
			return type.checkStackSize(stack.stackSize, (int) stack2.stored);
		}
		return false;
	}

	@Override
	public ItemStack getOutputStack() {
		return stack.copy();
	}

	@Override
	public List<ItemStack> getJEIValue() {
		return Lists.newArrayList(stack);
	}

	@Override
	public int getStackSize() {
		return stack.stackSize;
	}

}