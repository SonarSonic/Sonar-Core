package sonar.core.recipes;

import java.util.ArrayList;

public abstract class DefinedRecipeHelper<T extends ISonarRecipe> extends RecipeHelperV2<T> {

	private int inputSize, outputSize;
	public boolean shapeless;

	public DefinedRecipeHelper(int inputSize, int outputSize, boolean shapeless) {
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.shapeless = shapeless;
		this.addRecipes();
	}

	public void addRecipe(Object... objs) {
        ArrayList inputs = new ArrayList<>();
        ArrayList outputs = new ArrayList<>();
        ArrayList additionals = new ArrayList<>();
		for (int i = 0; i < objs.length; i++) {
			Object obj = objs[i];
			if (i < (reverseRecipes() ? getOutputSize() : getInputSize())) {
				inputs.add(obj);
			} else if (i < getInputSize() + getOutputSize()) {
				outputs.add(obj);
			} else {
				additionals.add(obj);
			}
		}

		addRecipe(buildDefaultRecipe(reverseRecipes() ? outputs : inputs, reverseRecipes() ? inputs : outputs, additionals, shapeless));
	}

	public boolean reverseRecipes() {
		return false;
	}

    @Override
	public boolean isValidRecipe(ArrayList<ISonarRecipeObject> recipeInputs, ArrayList<ISonarRecipeObject> recipeOutputs) {
		return recipeInputs.size() == getInputSize() && recipeOutputs.size() == getOutputSize();
	}

	public int getInputSize() {
		return inputSize;
	}

	public int getOutputSize() {
		return outputSize;
	}
}
