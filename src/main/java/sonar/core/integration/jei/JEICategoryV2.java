package sonar.core.integration.jei;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Ingredients;
import sonar.core.helpers.FontHelper;

public abstract class JEICategoryV2 extends BlankRecipeCategory implements IRecipeHandler<JEIRecipeV2> {

	private final IJEIHandler handler;

	public JEICategoryV2(IJEIHandler handler) {
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
	public IRecipeWrapper getRecipeWrapper(JEIRecipeV2 recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(JEIRecipeV2 recipe) {
		return recipe.helper.getRecipeID().equals(getUid());
	}

	@Override
	public String getRecipeCategoryUid(JEIRecipeV2 id) {
		return getUid();
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		recipeWrapper.getIngredients(ingredients);
		setRecipe(recipeLayout, recipeWrapper, ingredients);
	}

}
