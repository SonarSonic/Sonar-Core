package sonar.core.recipes;

import java.util.Collection;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import sonar.core.helpers.ItemStackHelper;

public class RecipeItemStack implements ISonarRecipeObject, ISonarRecipeItem {

	public ItemStack stack;

	public RecipeItemStack(ItemStack stack) {
		this.stack = stack;
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
			if (!ItemStack.areItemStackTagsEqual(stack2, stack)) {
				return false;
			}
			return type.checkStackSize(stack.stackSize, stack2.stackSize);
		}
		return false;
	}

	@Override
	public ItemStack getOutputStack() {
		return stack.copy();
	}

	@Override
	public Collection<ItemStack> getJEIValue() {
		return Lists.newArrayList(stack);
	}

	@Override
	public int getStackSize() {
		return stack.stackSize;
	}

}