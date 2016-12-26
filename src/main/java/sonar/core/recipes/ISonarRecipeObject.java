package sonar.core.recipes;

import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;

/**implemented on all Recipe Objects*/
public interface ISonarRecipeObject {

	/**the stored value of this Recipe Object*/
	public Object getValue();
	
	public int getStackSize();
	
	/**return either as a ItemStack or a Collection of ItemStacks*/
	public List<ItemStack> getJEIValue();

	/**if the provided is a match for the value stored*/
	public boolean matches(Object object, RecipeObjectType type);
}