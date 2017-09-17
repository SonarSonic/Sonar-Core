package sonar.core.recipes;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * implemented on all Recipe Objects
 */
public interface ISonarRecipeObject {

    /**
     * the stored value of this Recipe Object
     */
    Object getValue();
    
    boolean isNull();
	
    int getStackSize();
	
    /**
     * return either as a ItemStack or a Collection of ItemStacks
     */
    List<ItemStack> getJEIValue();

    /**
     * if the provided is a match for the value stored
     */
    boolean matches(Object object, RecipeObjectType type);
}