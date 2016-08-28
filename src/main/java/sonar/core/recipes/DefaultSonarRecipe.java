package sonar.core.recipes;

import java.util.ArrayList;
import java.util.List;

public class DefaultSonarRecipe implements ISonarRecipe {
	public ArrayList<ISonarRecipeObject> recipeInputs;
	public ArrayList<ISonarRecipeObject> recipeOutputs;

	public DefaultSonarRecipe(ArrayList<ISonarRecipeObject> inputs, ArrayList<ISonarRecipeObject> outputs) {
		this.recipeInputs = inputs;
		this.recipeOutputs = outputs;
	}

	@Override
	public List inputs() {
		return recipeInputs;
	}

	@Override
	public List outputs() {
		return recipeOutputs;
	}

	public boolean matchingInputs(Object... inputs) {
		return RecipeHelperV2.matchingIngredients(recipeInputs, inputs);
	}

	public boolean matchingOutputs(Object... outputs) {
		return RecipeHelperV2.matchingIngredients(recipeOutputs, outputs);
	}

}