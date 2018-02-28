package sonar.core.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

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
	public boolean isNull() {
		return false;
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
