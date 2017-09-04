package sonar.core.recipes;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class DefaultSonarRecipe implements ISonarRecipe {
	public ArrayList<ISonarRecipeObject> recipeInputs;
	public ArrayList<ISonarRecipeObject> recipeOutputs;
	public boolean shapeless;

	public DefaultSonarRecipe(ArrayList<ISonarRecipeObject> inputs, ArrayList<ISonarRecipeObject> outputs, boolean shapeless) {
		this.recipeInputs = inputs;
		this.recipeOutputs = outputs;
		this.shapeless = shapeless;
	}

	@Override
	public List<ISonarRecipeObject> inputs() {
		return recipeInputs;
	}

	@Override
	public List<ISonarRecipeObject> outputs() {
		return recipeOutputs;
	}

    @Override
	public boolean matchingInputs(Object[] inputs) {
		return RecipeHelperV2.matchingIngredients(RecipeObjectType.INPUT, recipeInputs, shapeless, inputs);
	}

    @Override
	public boolean matchingOutputs(Object[] outputs) {
		return RecipeHelperV2.matchingIngredients(RecipeObjectType.OUTPUT, recipeOutputs, shapeless, outputs);
	}

	@Override
	public boolean canUseRecipe(EntityPlayer player) {
		return true;
	}

	public static class Value extends DefaultSonarRecipe {

		public int value;

		public Value(ArrayList<ISonarRecipeObject> inputs, ArrayList<ISonarRecipeObject> outputs, boolean shapeless, int value) {
			super(inputs, outputs, shapeless);
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}