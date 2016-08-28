package sonar.core.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class RecipeInterchangable implements ISonarRecipeObject, IRecipeItemStack {

	public List<ISonarRecipeObject> validInputs;
	public List<Object> cachedObjects = new ArrayList();

	public RecipeInterchangable(List<ISonarRecipeObject> validInputs) {
		this.validInputs = validInputs;
		validInputs.forEach(input -> cachedObjects.add(input.getValue()));
	}

	@Override
	public Object getValue() {
		return cachedObjects;
	}

	@Override
	public boolean matches(Object object) {
		for (ISonarRecipeObject recipeObject : validInputs) {
			if (recipeObject.matches(object)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getOutputStack() {
		for (Object object : cachedObjects) {
			if (object instanceof ItemStack) {
				return (ItemStack) object;
			}
		}
		return null;
	}

}