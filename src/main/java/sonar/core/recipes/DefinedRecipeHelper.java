package sonar.core.recipes;

import java.util.ArrayList;

public abstract class DefinedRecipeHelper<T extends ISonarRecipe> extends RecipeHelperV2<T> {

	public int inputSize, outputSize;
	public boolean shapeless;

	public DefinedRecipeHelper(int inputSize, int outputSize, boolean shapeless) {
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.shapeless = shapeless;
		this.addRecipes();
	}

	public void addRecipe(Object... objs) {
		ArrayList inputs = new ArrayList();
		ArrayList outputs = new ArrayList();
		ArrayList additionals = new ArrayList();
		for (int i = 0; i < objs.length; i++) {
			Object obj = objs[i];
			if (i < inputSize) {
				inputs.add(obj);
			} else if (i < inputSize + outputSize) {
				outputs.add(obj);
			} else {
				additionals.add(obj);
			}
		}

		addRecipe(buildDefaultRecipe(inputs, outputs, additionals, shapeless));
	}

	public boolean isValidRecipe(ArrayList<ISonarRecipeObject> recipeInputs, ArrayList<ISonarRecipeObject> recipeOutputs) {
		return recipeInputs.size() == inputSize && recipeOutputs.size() == outputSize;
	}
}
