package sonar.core.integration.jei;


import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import sonar.core.helpers.FontHelper;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
public abstract class JEICategoryV2 implements IRecipeCategory, IRecipeHandler<JEIRecipeV2> {

	private final IJEIHandler handler;

	public JEICategoryV2(IJEIHandler handler) {
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
	public IRecipeWrapper getRecipeWrapper(@Nonnull JEIRecipeV2 recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nonnull JEIRecipeV2 recipe) {
		return recipe.helper.getRecipeID().equals(getUid());
	}

	@Nonnull
    @Override
	public String getRecipeCategoryUid(@Nonnull JEIRecipeV2 id) {
		return getUid();
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
		recipeWrapper.getIngredients(ingredients);
		//setRecipe(recipeLayout, recipeWrapper, ingredients);
	}

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {

    }

    @Nonnull
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }
}
