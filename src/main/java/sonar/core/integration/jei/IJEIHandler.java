package sonar.core.integration.jei;

import mezz.jei.api.IGuiHelper;
import net.minecraft.item.ItemStack;
import sonar.core.recipes.IRecipeHelperV2;

import java.util.List;

public interface IJEIHandler {
	
    JEICategoryV2 getCategory(IGuiHelper guiHelper);
	
    String getTextureName();
	
    String getTitle();
	
    Class getRecipeClass();

    String getUUID();
		
    IRecipeHelperV2 getRecipeHelper();
	
    List<JEIRecipeV2> getJEIRecipes();
	
    ItemStack getCrafterItemStack();
}
