package sonar.core.recipes;

import net.minecraft.item.ItemStack;

public class RecipeItemStack implements ISonarRecipeObject, IRecipeItemStack {

	public ItemStack stack;

	public RecipeItemStack(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public Object getValue() {
		return stack;
	}

	@Override
	public boolean matches(Object object) {
		if (object instanceof ItemStack) {
			return ItemStack.areItemStacksEqual((ItemStack) object, stack);
		}
		return false;
	}

	@Override
	public ItemStack getOutputStack() {
		return stack;
	}

}