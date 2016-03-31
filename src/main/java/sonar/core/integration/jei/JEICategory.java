package sonar.core.integration.jei;

import sonar.core.helpers.ISonarRecipeHelper;
import net.minecraft.util.StatCollector;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public abstract class JEICategory extends BlankRecipeCategory implements IRecipeHandler<JEIRecipe> {

	private final ISonarRecipeHelper helper;
	private final String name;

	public JEICategory(ISonarRecipeHelper helper, String name) {
		this.helper = helper;
		this.name = name;
	}

	@Override
	public String getUid() {
		return helper.getRecipeID();
	}

	@Override
	public String getTitle() {
		return StatCollector.translateToLocal(name);
	}
	
	@Override
	public Class getRecipeClass() {
		return JEIRecipe.class;
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
		return recipe.recipeID.equals(helper.getRecipeID());
	}

}
