package sonar.core.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;

import java.util.Arrays;
import java.util.List;

public abstract class JEIRecipe<T extends JEIRecipe> implements IRecipeWrapper {

	public String recipeID;
	public List<Object> inputs;
	public List<Object> outputs;

    public JEIRecipe() {
    }

	public abstract T getInstance(String recipeID, Object[] inputs, Object[] outputs);

	public T setRecipe(String recipeID, Object[] inputs, Object[] outputs) {
		this.recipeID = recipeID;
		this.inputs = Arrays.asList(inputs);
		this.outputs = Arrays.asList(outputs);
		return (T) this;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputs(Object.class, inputs);
		ingredients.setOutputs(Object.class, outputs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
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
