package sonar.core.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.RecipeHelperV2;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class JEIRecipeV2<T extends JEIRecipeV2> implements IRecipeWrapper {

	public RecipeHelperV2 helper;
	public ISonarRecipe recipe;
	public List<List<ItemStack>> inputs;
	public List<ItemStack> outputs;

	public JEIRecipeV2(RecipeHelperV2 helper, ISonarRecipe recipe) {
		this.helper = helper;
		this.recipe = recipe;
		this.inputs = RecipeHelperV2.getJEIInputsFromList(recipe.inputs());
		this.outputs = RecipeHelperV2.getJEIOutputsFromList(recipe.outputs());
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

}
