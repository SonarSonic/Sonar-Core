package sonar.core.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public abstract class JEISonarCategory implements IRecipeCategory {

    public final JEISonarProvider handler;

    public JEISonarCategory(IGuiHelper guiHelper, JEISonarProvider handler) {
        this.handler = handler;
    }

    @Nonnull
    @Override
    public String getModName() {
        return handler.modid;
    }

    @Nonnull
    @Override
    public String getUid() {
        return handler.recipes.getRecipeID();
    }

    @Nonnull
    @Override
    public String getTitle() {
        return handler.catalyst.getDisplayName();
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients){
        recipeWrapper.getIngredients(ingredients);
    }
}