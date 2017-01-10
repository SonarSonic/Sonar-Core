package sonar.core.integration.jei;

import java.util.ArrayList;

import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;
import sonar.core.recipes.IRecipeHelperV2;

public interface IJEIHandler {
	
	public JEICategoryV2 getCategory(IGuiHelper guiHelper);
	
	public String getTextureName();
	
	public String getTitle();
	
	public Class getRecipeClass();

	public String getUUID();
		
	public IRecipeHelperV2 getRecipeHelper();
	
	public ArrayList<JEIRecipeV2> getJEIRecipes();
	
	public ItemStack getCrafterItemStack();
}
