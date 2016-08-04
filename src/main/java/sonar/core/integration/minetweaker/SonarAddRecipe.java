package sonar.core.integration.minetweaker;

import java.util.ArrayList;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import sonar.core.SonarCore;
import sonar.core.helpers.RecipeHelper;

public class SonarAddRecipe implements IUndoableAction {
	public ArrayList inputs;
	public ArrayList<ItemStack> outputs;
	public boolean liquidStack, wasNull, wrongSize;
	public RecipeHelper helper;

	public SonarAddRecipe(RecipeHelper helper, ArrayList inputs, ArrayList<ItemStack> outputs) {
		this.helper = helper;
		if (inputs.size() != helper.inputSize || outputs.size() != helper.outputSize) {
			MineTweakerAPI.logError("A " + helper.getRecipeID() + " recipe was the wrong size");
			wrongSize = true;
			return;
		}
		ArrayList adaptedInputs = new ArrayList();
		for (Object input : inputs) {
			if (input == null) {
				MineTweakerAPI.logError(String.format("An ingredient of a %s was null", helper.getRecipeID()));
				wasNull = true;
				return;
			}
			if (input instanceof IItemStack) {
				adaptedInputs.add(MineTweakerMC.getItemStack((IItemStack) input));
				continue;
			} else if (input instanceof IOreDictEntry) {
				adaptedInputs.add(new RecipeHelper.OreStack(((IOreDictEntry) input).getName(), 1));
				continue;
			} else if (input instanceof ILiquidStack) {
				MineTweakerAPI.logError(String.format("A liquid was passed into a %s, aborting!", helper.getRecipeID()));
				liquidStack = true;
				return;
			} else if (!(input instanceof ItemStack)) {
				MineTweakerAPI.logError(String.format("%s: Invalid ingredient: %s", helper.getRecipeID(), input));
				continue;
			} else {
				adaptedInputs.add(input);
				continue;
			}
		}
		this.inputs = adaptedInputs;
		this.outputs = outputs;
	}

	@Override
	public void apply() {
		if (!wasNull && !liquidStack && !wrongSize) {
			ArrayList toApply = (ArrayList) inputs.clone();
			toApply.addAll(outputs);
			helper.addRecipe(toApply.toArray());
		} else {
			SonarCore.logger.error(String.format("Failed to add %s recipe (%s = %s)", helper.getRecipeID(), inputs, outputs));
		}
	}

	@Override
	public void undo() {
		if (!wasNull && !liquidStack && !wrongSize) {
			boolean removed = helper.removeRecipe(inputs.toArray());
			if(!removed){
				MineTweakerAPI.logError(String.format("%s: Failed to remove recipe %s", helper.getRecipeID(), inputs.toArray()));
			}
			
		} else {
			SonarCore.logger.error(String.format("Failed to remove %s recipe (%s = %s)", helper.getRecipeID(), inputs, outputs));
		}
	}

	@Override
	public String describe() {
		return String.format("Adding %s recipe (%s = %s)", helper.getRecipeID(), inputs, outputs);
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

}