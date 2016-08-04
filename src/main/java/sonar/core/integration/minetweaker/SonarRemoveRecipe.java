package sonar.core.integration.minetweaker;

import java.util.ArrayList;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import sonar.core.helpers.RecipeHelper;

public class SonarRemoveRecipe implements IUndoableAction {

	public ArrayList inputs;
	public ItemStack[] outputs;
	public boolean liquidStack, wasNull, wrongSize;
	public RecipeHelper helper;

	public SonarRemoveRecipe(RecipeHelper helper, ArrayList inputs, boolean crafter) {
		this.helper = helper;
		if (inputs.size() != helper.inputSize) {
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
			} else {
				adaptedInputs.add(input);
				continue;
			}
		}
		this.inputs = adaptedInputs;

		ItemStack[] dummyInputs = new ItemStack[helper.inputSize];
		for (int i = 0; i < adaptedInputs.size(); i++) {
			Object input = adaptedInputs.get(i);
			if (input instanceof ItemStack)
				dummyInputs[i] = (ItemStack) input;
			if (input instanceof RecipeHelper.OreStack)
				dummyInputs[i] = OreDictionary.getOres(((RecipeHelper.OreStack) input).oreString).get(0);
		}
		if (crafter) {
			outputs = new ItemStack[] { helper.getCraftingResult(dummyInputs) };
		} else {
			outputs = helper.getOutput(dummyInputs);
		}
	}

	@Override
	public void apply() {
		if (!wasNull && !liquidStack && !wrongSize && outputs.length != 0) {
			boolean removed = helper.removeRecipe(inputs.toArray());
			if (!removed)
				MineTweakerAPI.logError(String.format("%s: Failed to remove recipe %s", helper.getRecipeID(), inputs));
		}
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public void undo() {
		if (!wasNull && !liquidStack && !wrongSize && outputs.length != 0) {
			ArrayList toApply = (ArrayList) inputs.clone();
			for (int i = 0; i < outputs.length; i++) {
				toApply.add(outputs[i]);
			}
			helper.addRecipe(toApply.toArray());
		}
	}

	@Override
	public String describe() {
		return String.format("Removing %s recipe (%s = %s)", helper.getRecipeID(), inputs, outputs);
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