package sonar.core.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class RecipeInterchangable implements ISonarRecipeObject, ISonarRecipeItem {

	public List<ISonarRecipeObject> validInputs;
    public List<Object> cachedObjects = new ArrayList<>();

	public RecipeInterchangable(List<ISonarRecipeObject> validInputs) {
		this.validInputs = validInputs;
		validInputs.forEach(input -> cachedObjects.add(input.getValue()));
	}

	@Override
	public Object getValue() {
		return cachedObjects;
	}

	@Override
	public boolean isNull() {
		return cachedObjects.isEmpty();
	}

	@Override
	public boolean matches(Object object, RecipeObjectType type) {
		for (ISonarRecipeObject recipeObject : validInputs) {
			if (recipeObject.matches(object, type)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getOutputStack() {
		ISonarRecipeObject obj = validInputs.get(0);
		if (obj instanceof ISonarRecipeItem) {
			return ((ISonarRecipeItem) obj).getOutputStack();
		}
		for (Object object : cachedObjects) {
			if (object instanceof ItemStack) {
				return (ItemStack) object;
			}
		}
		return null;
	}

	@Override
	public List<ItemStack> getJEIValue() {
        ArrayList<ItemStack> values = new ArrayList<>();
        validInputs.forEach(obj -> values.addAll(obj.getJEIValue()));
		return values;
	}

	@Override
	public int getStackSize() {
		return validInputs.get(0).getStackSize();
	}
}