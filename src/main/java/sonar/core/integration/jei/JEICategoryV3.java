package sonar.core.integration.jei;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import sonar.core.helpers.FontHelper;

import javax.annotation.Nonnull;

public abstract class JEICategoryV3 implements IRecipeCategory {

    private final IJEIHandlerV3 handler;

    public JEICategoryV3(IJEIHandlerV3 handler) {
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
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
        recipeWrapper.getIngredients(ingredients);
    }
}