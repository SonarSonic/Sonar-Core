package sonar.core.integration.jei;

import java.util.ArrayList;

import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;
import sonar.core.helpers.IRecipeHelper;

public interface IJEIHandler {
	
	public JEICategory getCategory(IGuiHelper guiHelper);
	
	public String getTextureName();
	
	public String getTitle();
	
	public Class getRecipeClass();
	
	public IRecipeHelper getRecipeHelper();
	
	public ArrayList<JEIRecipe> getJEIRecipes();
	
	public ItemStack getCrafterItemStack();
}
