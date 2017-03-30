package sonar.core.integration.jei;

import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import sonar.core.helpers.FontHelper;

public abstract class JEICategory extends BlankRecipeCategory implements IRecipeHandler<JEIRecipe> {

	private final IJEIHandler handler;

	public JEICategory(IJEIHandler handler) {
		this.handler = handler;
	}

	@Override
	public String getUid() {
		return handler.getUUID();
	}

	@Override
	public String getTitle() {
		return FontHelper.translate(handler.getTitle());
	}

	@Override
	public Class getRecipeClass() {
		return handler.getRecipeClass();
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(JEIRecipe recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(JEIRecipe recipe) {
		return recipe.recipeID.equals(getUid());
	}

	@Override
	public String getRecipeCategoryUid(JEIRecipe id) {
		return getUid();
	}
}
