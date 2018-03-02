package sonar.core.integration.jei;

import java.util.Collections;
import java.util.List;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.Ingredients;
import net.minecraft.client.Minecraft;
import sonar.core.helpers.FontHelper;

public abstract class JEICategoryV2 implements IRecipeCategory, IRecipeHandler<JEIRecipeV2> {

	private final IJEIHandler handler;

	public JEICategoryV2(IJEIHandler handler) {
		this.handler = handler;
	}

	@Override
	public String getRecipeCategoryUid() {
		return getUid();
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
	public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper) {
		IIngredients ingredients = new Ingredients();
		recipeWrapper.getIngredients(new Ingredients());
		setRecipe(recipeLayout, recipeWrapper, ingredients);
	}

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {

    }

	@Override
	public void drawAnimations(Minecraft minecraft) {
		
	}

    //@Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }
}
