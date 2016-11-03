package sonar.core.integration.minetweaker;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import sonar.core.SonarCore;
import sonar.core.recipes.DefinedRecipeHelper;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.ISonarRecipeObject;
import sonar.core.recipes.RecipeHelperV2;
import sonar.core.recipes.RecipeItemStack;
import sonar.core.recipes.RecipeOreStack;
import sonar.core.recipes.ValueHelperV2;

public class SonarAddRecipeV2<T extends RecipeHelperV2> implements IUndoableAction {
	public ArrayList<ISonarRecipeObject> inputs;
	public ArrayList<ISonarRecipeObject> outputs;
	public boolean liquidStack, wasNull, wrongSize;
	public T helper;

	public SonarAddRecipeV2(T helper, ArrayList inputs, ArrayList<ItemStack> outputs) {
		this.helper = helper;
		if (helper instanceof DefinedRecipeHelper && (inputs.size() != ((DefinedRecipeHelper) helper).getInputSize() || outputs.size() != ((DefinedRecipeHelper) helper).getOutputSize())) {
			MineTweakerAPI.logError("A " + helper.getRecipeID() + " recipe was the wrong size");
			wrongSize = true;
			return;
		}
		ArrayList<ISonarRecipeObject> adaptedInputs = new ArrayList<ISonarRecipeObject>();
		ArrayList<ISonarRecipeObject> adaptedOutputs = new ArrayList<ISonarRecipeObject>();
		for (Object input : inputs) {
			if (input == null) {
				MineTweakerAPI.logError(String.format("An ingredient of a %s was null", helper.getRecipeID()));
				wasNull = true;
				return;
			}
			if (input instanceof IItemStack) {
				adaptedInputs.add(helper.buildRecipeObject(MineTweakerMC.getItemStack((IItemStack) input)));
				continue;
			} else if (input instanceof IOreDictEntry) {
				adaptedInputs.add(new RecipeOreStack(((IOreDictEntry) input).getName(), 1));
				continue;
			} else if (input instanceof ILiquidStack) {
				MineTweakerAPI.logError(String.format("A liquid was passed into a %s, aborting!", helper.getRecipeID()));
				liquidStack = true;
				return;
			} else if (!(input instanceof ItemStack)) {
				MineTweakerAPI.logError(String.format("%s: Invalid ingredient: %s", helper.getRecipeID(), input));
				continue;
			} else {
				adaptedInputs.add(helper.buildRecipeObject(input));
				continue;
			}
		}
		for (ItemStack stack : outputs) {
			adaptedOutputs.add(new RecipeItemStack(stack));
		}
		this.inputs = adaptedInputs;
		this.outputs = adaptedOutputs;
	}

	@Override
	public void apply() {
		if (!wasNull && !liquidStack && !wrongSize) {
			boolean isShapeless = helper instanceof DefinedRecipeHelper ? ((DefinedRecipeHelper) helper).shapeless : true;
			helper.addRecipe(helper.buildRecipe((ArrayList<ISonarRecipeObject>) inputs.clone(), (ArrayList<ISonarRecipeObject>) outputs.clone(), new ArrayList(), isShapeless));
		} else {
			SonarCore.logger.error(String.format("Failed to add %s recipe (%s = %s)", helper.getRecipeID(), inputs, outputs));
		}
	}

	@Override
	public void undo() {
		if (!wasNull && !liquidStack && !wrongSize) {
			List values = helper.getValuesFromList(inputs);
			ISonarRecipe recipe = helper.getRecipeFromInputs(null, values.toArray());
			if (recipe == null) {
				MineTweakerAPI.logError(String.format("%s: Adding Recipe - Couldn't find matching recipe %s", helper.getRecipeID(), values));
				return;
			}
			boolean removed = helper.removeRecipe(recipe);
			if (!removed) {
				MineTweakerAPI.logError(String.format("%s: Adding Recipe - Failed to remove recipe %s", helper.getRecipeID(), values));
			}

		} else {
			SonarCore.logger.error(String.format("Adding Recipe - Failed to remove %s recipe (%s = %s)", helper.getRecipeID(), inputs, outputs));
		}
	}

	@Override
	public String describe() {
		return String.format("Adding %s recipe (%s = %s)", helper.getRecipeID(), helper.getValuesFromList(inputs), helper.getValuesFromList(outputs));
	}

	@Override
	public String describeUndo() {
		return String.format("Reverting /%s/", describe());
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public Object getOverrideKey() {
		return null;
	}

	public static class Value extends SonarAddRecipeV2<ValueHelperV2> {

		public int recipeValue = 0;

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