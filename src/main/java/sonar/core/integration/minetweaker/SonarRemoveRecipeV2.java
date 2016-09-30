package sonar.core.integration.minetweaker;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import sonar.core.SonarCore;
import sonar.core.recipes.DefinedRecipeHelper;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.ISonarRecipeObject;
import sonar.core.recipes.RecipeObjectType;
import sonar.core.recipes.RecipeOreStack;
import sonar.core.recipes.ValueHelperV2;

public class SonarRemoveRecipeV2<T extends DefinedRecipeHelper> implements IUndoableAction {

	public ArrayList ingredients;
	public RecipeObjectType type;
	public ISonarRecipe recipe;
	public boolean liquidStack, wasNull, wrongSize;
	public T helper;

	public SonarRemoveRecipeV2(T helper, RecipeObjectType type, ArrayList ingredients) {
		this.helper = helper;
		this.type = type;
		if (type == RecipeObjectType.OUTPUT ? ingredients.size() != helper.outputSize : ingredients.size() != helper.inputSize) {
			MineTweakerAPI.logError("A " + helper.getRecipeID() + " recipe was the wrong size");
			wrongSize = true;
			return;
		}

		ArrayList adaptedIngredients = new ArrayList();
		for (Object output : ingredients) {
			if (output == null) {
				MineTweakerAPI.logError(String.format("An ingredient of a %s was null", helper.getRecipeID()));
				wasNull = true;
				return;
			}
			if (output instanceof IItemStack) {
				adaptedIngredients.add(MineTweakerMC.getItemStack((IItemStack) output));
				continue;
			} else if (output instanceof IOreDictEntry) {
				adaptedIngredients.add(new RecipeOreStack(((IOreDictEntry) output).getName(), 1));
				continue;
			} else if (output instanceof ILiquidStack) {
				MineTweakerAPI.logError(String.format("A liquid was passed into a %s, aborting!", helper.getRecipeID()));
				liquidStack = true;
				return;
			} else if (!(output instanceof ItemStack)) {
				MineTweakerAPI.logError(String.format("%s: Invalid ingredient: %s", helper.getRecipeID(), output));
			} else {
				adaptedIngredients.add(output);
				continue;
			}
		}

		this.ingredients = adaptedIngredients;
		this.recipe = type == RecipeObjectType.OUTPUT ? helper.getRecipeFromOutputs(null, adaptedIngredients.toArray()) : helper.getRecipeFromInputs(null, adaptedIngredients.toArray());
	}

	@Override
	public void apply() {
		if (recipe == null) {
			MineTweakerAPI.logError(String.format("%s: Removing Recipe - Couldn't find matching recipe %s", helper.getRecipeID(), ingredients));
			return;
		}
		if (!wasNull && !liquidStack && !wrongSize) {
			boolean removed = helper.removeRecipe(recipe);
			if (!removed)
				MineTweakerAPI.logError(String.format("%s: Removing Recipe - Failed to remove recipe %s", helper.getRecipeID(), ingredients));
		}
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		if (recipe != null && !wasNull && !liquidStack && !wrongSize) {
			helper.addRecipe(recipe);
		}
	}

	@Override
	public String describe() {
		if (recipe == null) {
			return "ERROR: RECIPE IS NULL";
		}
		return String.format("Removing %s recipe (%s = %s)", helper.getRecipeID(), recipe.inputs(), recipe.outputs());
	}

	@Override
	public String describeUndo() {
		return String.format("Reverting /%s/", describe());
	}

	@Override
	public Object getOverrideKey() {
		return null;
	}

}