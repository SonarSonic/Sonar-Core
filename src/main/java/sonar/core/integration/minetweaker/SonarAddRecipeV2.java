package sonar.core.integration.minetweaker;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import sonar.core.SonarCore;
import sonar.core.recipes.DefinedRecipeHelper;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.ISonarRecipeObject;
import sonar.core.recipes.RecipeHelperV2;
import sonar.core.recipes.RecipeItemStack;
import sonar.core.recipes.RecipeOreStack;
import sonar.core.recipes.ValueHelperV2;

public class SonarAddRecipeV2<T extends RecipeHelperV2> implements IAction {
	public ArrayList<ISonarRecipeObject> inputs;
	public ArrayList<ISonarRecipeObject> outputs;
	public boolean liquidStack, wasNull, wrongSize;
	public T helper;

	public SonarAddRecipeV2(T helper, ArrayList inputs, ArrayList<ItemStack> outputs) {
		this.helper = helper;
		if (helper instanceof DefinedRecipeHelper && (inputs.size() != ((DefinedRecipeHelper) helper).getInputSize() || outputs.size() != ((DefinedRecipeHelper) helper).getOutputSize())) {
            CraftTweakerAPI.logError("A " + helper.getRecipeID() + " recipe was the wrong size");
			wrongSize = true;
			return;
		}
        ArrayList<ISonarRecipeObject> adaptedInputs = new ArrayList<>();
        ArrayList<ISonarRecipeObject> adaptedOutputs = new ArrayList<>();
		for (Object input : inputs) {
			if (input == null) {
                CraftTweakerAPI.logError(String.format("An ingredient of a %s was null", helper.getRecipeID()));
				wasNull = true;
				return;
			}
			if (input instanceof IItemStack) {
                adaptedInputs.add(helper.buildRecipeObject(CraftTweakerMC.getItemStack((IItemStack) input)));
			} else if (input instanceof IOreDictEntry) {
				adaptedInputs.add(new RecipeOreStack(((IOreDictEntry) input).getName(), 1));
			} else if (input instanceof ILiquidStack) {
                CraftTweakerAPI.logError(String.format("A liquid was passed into a %s, aborting!", helper.getRecipeID()));
				liquidStack = true;
				return;
			} else if (!(input instanceof ItemStack)) {
                CraftTweakerAPI.logError(String.format("%s: Invalid ingredient: %s", helper.getRecipeID(), input));
			} else {
				adaptedInputs.add(helper.buildRecipeObject(input));
			}
		}
		for (ItemStack stack : outputs) {
			adaptedOutputs.add(new RecipeItemStack(stack, false));
		}
		this.inputs = adaptedInputs;
		this.outputs = adaptedOutputs;
	}

	@Override
	public void apply() {
		if (!wasNull && !liquidStack && !wrongSize) {
            boolean isShapeless = !(helper instanceof DefinedRecipeHelper) || ((DefinedRecipeHelper) helper).shapeless;
            ISonarRecipe recipe = helper.buildRecipe((ArrayList<ISonarRecipeObject>) inputs.clone(), (ArrayList<ISonarRecipeObject>) outputs.clone(), new ArrayList<>(), isShapeless);
			helper.addRecipe(recipe);	
		} else {
			SonarCore.logger.error(String.format("Failed to add %s recipe (%s = %s)", helper.getRecipeID(), inputs, outputs));
		}
	}

	@Override
	public String describe() {
		return String.format("Adding %s recipe (%s = %s)", helper.getRecipeID(), RecipeHelperV2.getValuesFromList(inputs), RecipeHelperV2.getValuesFromList(outputs));
	}

	public static class Value extends SonarAddRecipeV2<ValueHelperV2> {

        public int recipeValue;

		public Value(ValueHelperV2 helper, ArrayList inputs, ArrayList outputs, int recipeValue) {
			super(helper, inputs, outputs);
			this.recipeValue = recipeValue;
		}

		@Override
		public void apply() {
			if (!wasNull && !liquidStack && !wrongSize) {
				helper.addRecipe(helper.buildRecipe((ArrayList<ISonarRecipeObject>) inputs.clone(), (ArrayList<ISonarRecipeObject>) outputs.clone(), Lists.newArrayList(recipeValue), helper.shapeless));
			} else {
				SonarCore.logger.error(String.format("Failed to add %s recipe (%s = %s)", helper.getRecipeID(), inputs, outputs));
			}
		}
	}
}