package sonar.core.integration.jei;

import net.minecraft.util.StatCollector;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public abstract class JEICategory extends BlankRecipeCategory implements IRecipeHandler<JEIRecipe> {

	private final IJEIHandler handler;

	public JEICategory(IJEIHandler handler) {
		this.handler = handler;
	}

	@Override
	public String getUid() {
		return handler.getRecipeHelper().getRecipeID();
	}

	@Override
	public String getTitle() {
		return StatCollector.translateToLocal(handler.getTitle());
	}

	@Override
	public Class getRecipeClass() {
		return handler.getRecipeClass();
	}

	@Override
	public String getRecipeCategoryUid() {
		return getUid();
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(JEIRecipe recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(JEIRecipe recipe) {
		return recipe.recipeID.equals(getUid());
	}

}
