package sonar.core.recipes;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreStack implements ISonarRecipeObject {

	public String oreType;
	public List<ItemStack> cachedRegister;
	public int stackSize;

	public OreStack(String oreType, int stackSize) {
		this.oreType = oreType;
		this.cachedRegister = OreDictionary.getOres(oreType);
		this.stackSize = stackSize;
	}

	@Override
	public Object getValue() {
		return cachedRegister;
	}

	@Override
	public boolean matches(Object object) {
		if (object instanceof String) {
			return oreType.equals(object);
		} else if (object instanceof ItemStack) {
			for (ItemStack oreStack : cachedRegister) {
				if (ItemStack.areItemsEqual((ItemStack) object, oreStack)) {
					return true;
				}
			}
		}
		return false;
	}

}