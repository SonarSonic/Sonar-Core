package sonar.core.recipes;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeInteger implements ISonarRecipeObject {

	public int value;

	public RecipeInteger(int value) {
		this.value = value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public int getStackSize() {
		return value;
	}

	@Override
	public List<ItemStack> getJEIValue() {
        return new ArrayList<>();
	}

	@Override
	public boolean matches(Object object, RecipeObjectType type) {
        return object instanceof Integer && (Integer) object == value;
	}
}
