package sonar.core.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeOreStack implements ISonarRecipeObject, ISonarRecipeItem {

	public String oreType;
	public List<ItemStack> cachedRegister;
	public int stackSize;

	public RecipeOreStack(String oreType, int stackSize) {
		this.oreType = oreType;
        List<ItemStack> stacks = new ArrayList<>();
		for (ItemStack ore : OreDictionary.getOres(oreType)) {
			ItemStack newStack = ore.copy();
			newStack.setCount(stackSize);
			stacks.add(newStack);
		}
		this.cachedRegister = stacks;
		this.stackSize = stackSize;
	}

	@Override
	public Object getValue() {
		return cachedRegister;
	}

	@Override
	public boolean isNull() {
		return cachedRegister.isEmpty();
	}

	@Override
	public ItemStack getOutputStack() {
		return cachedRegister.get(0).copy();
	}

	@Override
	public boolean matches(Object object, RecipeObjectType type) {
		if (object instanceof RecipeOreStack) {
			RecipeOreStack oreStack = (RecipeOreStack) object;
            return oreStack.oreType.equals(oreType) && oreStack.stackSize >= stackSize;
		} else if (object instanceof String) {
			return oreType.equals(object);
		} else if (object instanceof ItemStack && type.checkStackSize(stackSize, ((ItemStack) object).getCount())) {
			int oreID = OreDictionary.getOreID(oreType);
			for (int id : OreDictionary.getOreIDs((ItemStack) object)) {
				if (oreID == id) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<ItemStack> getJEIValue() {
		return cachedRegister;
	}

	@Override
	public int getStackSize() {
		return stackSize;
	}
}