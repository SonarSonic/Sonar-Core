package sonar.core.integration.jei;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.RecipeHelperV2;

public abstract class JEIRecipeV2<T extends JEIRecipeV2> implements IRecipeWrapper {

	public RecipeHelperV2 helper;
	public ISonarRecipe recipe;
	public List<Collection<ItemStack>> inputs;
	public List<Collection<ItemStack>> outputs;

	public JEIRecipeV2(RecipeHelperV2 helper, ISonarRecipe recipe) {
		this.helper = helper;
		this.recipe = recipe;
		this.inputs = helper.getJEIValuesFromList(recipe.inputs());
		this.outputs = helper.getJEIValuesFromList(recipe.outputs());
	}

	@Override
	public List getInputs() {
		return inputs;
	}

	@Override
	public List getOutputs() {
		return outputs;
	}

	@Override
	public List<FluidStack> getFluidInputs() {
		return null;
	}

	@Override
	public List<FluidStack> getFluidOutputs() {
		return null;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
	}

	@Override
	public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return null;
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}

}
