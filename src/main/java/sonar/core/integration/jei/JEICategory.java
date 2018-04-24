package sonar.core.integration.jei;

import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import sonar.core.helpers.FontHelper;

import javax.annotation.Nonnull;

public abstract class JEICategory implements IRecipeCategory, IRecipeHandler<JEIRecipe> {

	private final IJEIHandler handler;

	public JEICategory(IJEIHandler handler) {
		this.handler = handler;
	}

	@Nonnull
    @Override
	public String getUid() {
		return handler.getUUID();
	}

	@Nonnull
    @Override
	public String getTitle() {
		return FontHelper.translate(handler.getTitle());
	}

	@Nonnull
    @Override
	public Class getRecipeClass() {
		return handler.getRecipeClass();
	}

	@Nonnull
    @Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull JEIRecipe recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull JEIRecipe recipe) {
		return recipe.recipeID.equals(getUid());
	}

	@Nonnull
    @Override
	public String getRecipeCategoryUid(@Nonnull JEIRecipe id) {
		return getUid();
	}
}
