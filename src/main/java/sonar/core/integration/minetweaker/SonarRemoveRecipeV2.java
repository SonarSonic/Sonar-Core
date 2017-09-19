package sonar.core.integration.minetweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import sonar.core.recipes.*;

import java.util.ArrayList;

public class SonarRemoveRecipeV2<T extends RecipeHelperV2> implements IAction {

	public ArrayList ingredients;
	public RecipeObjectType type;
	public ISonarRecipe recipe;
	public boolean liquidStack, wasNull, wrongSize;
	public T helper;

	public SonarRemoveRecipeV2(T helper, RecipeObjectType type, ArrayList ingredients) {
		this.helper = helper;
		this.type = type;
		if (helper instanceof DefinedRecipeHelper && (type == RecipeObjectType.OUTPUT ? ingredients.size() != ((DefinedRecipeHelper) helper).getOutputSize() : ingredients.size() != ((DefinedRecipeHelper) helper).getInputSize())) {
            CraftTweakerAPI.logError("A " + helper.getRecipeID() + " recipe was the wrong size");
			wrongSize = true;
			return;
		}

        ArrayList adaptedIngredients = new ArrayList<>();
		for (Object output : ingredients) {
			if (output == null) {
                CraftTweakerAPI.logError(String.format("An ingredient of a %s was null", helper.getRecipeID()));
				wasNull = true;
				return;
			}
			if (output instanceof IItemStack) {
                adaptedIngredients.add(CraftTweakerMC.getItemStack((IItemStack) output));
			} else if (output instanceof IOreDictEntry) {
				adaptedIngredients.add(new RecipeOreStack(((IOreDictEntry) output).getName(), 1));
			} else if (output instanceof ILiquidStack) {
                CraftTweakerAPI.logError(String.format("A liquid was passed into a %s, aborting!", helper.getRecipeID()));
				liquidStack = true;
				return;
			} else if (!(output instanceof ItemStack)) {
                CraftTweakerAPI.logError(String.format("%s: Invalid ingredient: %s", helper.getRecipeID(), output));
			} else {
				adaptedIngredients.add(output);
			}
		}

		this.ingredients = adaptedIngredients;
		this.recipe = type == RecipeObjectType.OUTPUT ? helper.getRecipeFromOutputs(null, adaptedIngredients.toArray()) : helper.getRecipeFromInputs(null, adaptedIngredients.toArray());
	}

	@Override
	public void apply() {
		if (recipe == null) {
            CraftTweakerAPI.logError(String.format("%s: Removing Recipe - Couldn't find matching recipe %s", helper.getRecipeID(), ingredients));
			return;
		}
		if (!wasNull && !liquidStack && !wrongSize) {
			boolean removed = helper.removeRecipe(recipe);
			if (!removed){
                CraftTweakerAPI.logError(String.format("%s: Removing Recipe - Failed to remove recipe %s", helper.getRecipeID(), ingredients));
			}
		}
	}

	@Override
	public String describe() {
		if (recipe == null) {
			return "ERROR: RECIPE IS NULL";
		}
		return String.format("Removing %s recipe (%s = %s)", helper.getRecipeID(), recipe.inputs(), recipe.outputs());
	}
}