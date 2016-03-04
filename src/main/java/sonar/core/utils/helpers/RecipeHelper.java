package sonar.core.utils.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import sonar.calculator.mod.api.CalculatorAPI;
import sonar.core.SonarCore;
import sonar.core.integration.SonarAPI;
import cpw.mods.fml.common.FMLLog;

/** Recipe Template allows gigantic recipes with full Ore Dict compatibility */
public abstract class RecipeHelper {

	public int outputSize, inputSize;
	public boolean shapeless;

	protected Map<Object[], Object[]> recipeList = new ConcurrentHashMap<Object[], Object[]>();

	/** add all your recipes here */
	public abstract void addRecipes();

	/** the recipeOutput name used in the API */
	public abstract String getRecipeID();

	/**
	 * @param inputSize
	 *            number of stacks required in the input
	 * @param outputSize
	 *            number of stacks to be created
	 * @param shapeless
	 *            does the order matter?
	 */
	public RecipeHelper(int inputSize, int outputSize, boolean shapeless) {
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.shapeless = shapeless;
		this.addRecipes();
		this.addRegisteredRecipes();
	}

	public void addRegisteredRecipes() {
		if (SonarAPI.calculatorLoaded()) {
			List<Object[]> recipes = CalculatorAPI.getRecipes(this.getRecipeID());
			if (recipes != null && recipes.size() != 0) {
				for (Object[] object : recipes) {
					this.addRecipe(object);
				}
			}
		}
	}

	/** get the full list of recipes */
	public Map<Object[], Object[]> getRecipes() {
		return this.recipeList;
	}

	/** makes sure each item/block is an itemstack */
	public void addRecipe(Object... objects) {
		Object[] stack = new Object[objects.length];
		if (objects.length != this.inputSize + this.outputSize) {
			SonarCore.logger.error("RecipeHelper - A recipeOutput wasn't added because the size didn't match!");
			return;
		}
		try {
			for (int i = 0; i < objects.length; i++) {
				if (objects[i] == null) {
					return;
				}
				if (objects[i] instanceof String) {
					if (i < inputSize) {
						stack[i] = new OreStack((String) objects[i], 1);
					} else if (!(i - inputSize > outputSize)) {
						ArrayList<ItemStack> ores = OreDictionary.getOres((String) objects[i]);
						if (ores.size() > 0) {
							stack[i] = ores.get(0);
						} else {
							return;
						}
					}
				} else if (objects[i] instanceof OreStack) {
					if (i - inputSize < outputSize) {
						ArrayList<ItemStack> ores = OreDictionary.getOres(((OreStack) objects[i]).oreString);
						if (ores.size() > 0) {
							stack[i] = new ItemStack(ores.get(0).getItem(), ((OreStack) objects[i]).stackSize, ores.get(0).getItemDamage());
						} else {
							return;
						}
					}
				} else if (objects[i] instanceof ItemStack[]) {
					for (ItemStack checkStack : (ItemStack[]) objects[i]) {
						if (checkStack == null) {
							return;
						}
					}
					stack[i] = objects[i];
				} else if (objects[i] instanceof Integer) {
					stack[i] = objects[i];
				} else {
					stack[i] = ItemStackHelper.createStack(objects[i]);
				}
			}
			addFinal(stack);
		} catch (Exception exception) {
			SonarCore.logger.error("RecipeHelper : Exception Loading Recipe : " + exception.getLocalizedMessage());
		}
	}

	/** separates the recipeOutput into an input and output list */
	private void addFinal(Object[] stacks) {
		Object[] input = new Object[inputSize], output = new Object[outputSize];

		for (int i = 0; i < stacks.length; i++) {
			if (i < inputSize) {
				input[i] = stacks[i];
			} else if (!(i - inputSize > outputSize)) {
				output[i - inputSize] = stacks[i];
			} else {
				throw new RuntimeException("Something went wrong! A recipeOutput was too big");
			}
		}
		addRecipe(input, output);
	}

	/** adds the two input and output lists */
	public void addRecipe(Object[] input, Object[] output) {
		recipeList.put(input, output);
	}

	/**
	 * @param output
	 *            stack you wish to find
	 * @param input
	 *            full list of inputs
	 * @return
	 */
	public ItemStack getOutput(int output, ItemStack... input) {
		ItemStack[] objs = getOutput(input);
		if (objs == null)
			return null;
		return objs[output];
	}

	/**
	 * @param input
	 *            full list of inputs
	 * @return full list of output stacks
	 */
	public ItemStack[] getOutput(ItemStack... input) {
		return convertOutput(getOutputRaw(input));
	}

	public Object[] getOutputRaw(ItemStack... input) {
		if (input == null || !(input.length >= inputSize)) {
			return null;
		}
		for (int i = 0; i < inputSize; i++) {

			if (input[i] == null) {
				return null;
			}
			if (!SonarAPI.isEnabled(input[i])) {
				return null;
			}

		}
		for (Map.Entry<Object[], Object[]> entry : recipeList.entrySet()) {
			if (checkInput(input, entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}

	public int getInputSize(int input, ItemStack... output) {
		Object[] inputs = this.getInput(output);
		if (inputs == null) {
			return 1;
		}
		return Math.max(1, this.getInputSize(inputs)[input]);
	}

	/**
	 * gets the full list of inputs from list of outputs
	 * 
	 * @param input
	 *            stack to check
	 * @return
	 */
	private Object[] getInput(ItemStack... output) {

		if (output.length != outputSize) {
			return new Object[inputSize];
		}
		for (int i = 0; i < output.length; i++) {
			if (output[i] == null) {
				return new Object[inputSize];
			}
		}
		Iterator iterator = this.recipeList.entrySet().iterator();

		Map.Entry entry;
		do {
			if (!iterator.hasNext()) {
				return new Object[inputSize];
			}

			entry = (Map.Entry) iterator.next();
		} while (!checkOutput(output, (Object[]) entry.getValue()));

		return (Object[]) entry.getKey();
	}

	/**
	 * fixed check if the stack is used in any recipeOutput ignoring its stack
	 * size
	 * 
	 * @param input
	 *            stack to check
	 * @return validity
	 */
	public boolean validInput(ItemStack input) {
		if (input == null) {
			return false;
		}
		Iterator iterator = this.recipeList.entrySet().iterator();

		Map.Entry entry;
		do {
			if (!iterator.hasNext()) {
				return false;
			}

			entry = (Map.Entry) iterator.next();
		} while (containsStack(input, (Object[]) entry.getKey(), false) == -1);

		return true;
	}

	/**
	 * fixed check if the stack is used in any recipeOutput ignoring its stack
	 * size
	 * 
	 * @param output
	 *            stack to check
	 * @return validity
	 */
	public boolean validOutput(ItemStack output) {
		if (output == null) {
			return false;
		}
		Iterator iterator = this.recipeList.entrySet().iterator();

		Map.Entry entry;
		do {
			if (!iterator.hasNext()) {
				return false;
			}

			entry = (Map.Entry) iterator.next();
		} while (containsStack(output, (Object[]) entry.getKey(), false) == -1);

		return true;
	}

	private ItemStack[] convertOutput(Object[] output) {
		if (output == null)
			return new ItemStack[0];
		ItemStack[] stack = new ItemStack[output.length];
		for (int i = 0; i < output.length; i++) {
			if (output[i] instanceof ItemStack) {
				stack[i] = (ItemStack) output[i];
			} else if (output[i] instanceof OreStack) {
				ArrayList<ItemStack> ore = OreDictionary.getOres(((OreStack) output[i]).oreString);
				stack[i] = new ItemStack(ore.get(0).getItem(), ((OreStack) output[i]).stackSize, ore.get(0).getItemDamage());
			}
		}
		return stack;
	}

	private int[] getInputSize(Object[] input) {
		int[] sizes = new int[input.length];
		for (int i = 0; i < input.length; i++) {
			if (input[i] instanceof ItemStack) {
				sizes[i] = ((ItemStack) input[i]).stackSize;
			} else if (input[i] instanceof ItemStack[]) {
				sizes[i] = ((ItemStack[]) input[i])[0].stackSize;
			} else if (input[i] instanceof OreStack) {
				sizes[i] = ((OreStack) input[i]).stackSize;
			}
		}
		return sizes;
	}

	/**
	 * @param input
	 *            input stacks to check
	 * @param key
	 *            input stacks to check
	 * @return if they are same
	 */
	private boolean checkInput(ItemStack[] input, Object[] key) {
		if (input.length != key.length && input.length == inputSize) {
			return false;
		}
		if (!shapeless) {
			for (int i = 0; i < inputSize; i++) {
				if (key[i] instanceof ItemStack) {
					if (!equalStack(input[i], (ItemStack) key[i], true)) {
						return false;
					}
				} else if (key[i] instanceof ItemStack[]) {
					if (containsStack(input[i], (ItemStack[]) key[i], true) == -1) {
						return false;
					}
				} else if (key[i] instanceof OreStack) {
					int oreID = OreDictionary.getOreID(((OreStack) key[i]).oreString);
					boolean found = false;
					for (int id : OreDictionary.getOreIDs(input[i])) {
						if (id == oreID) {
							found = true;
							break;
						}
					}
					if (!found)
						return false;
				}
			}
		} else {
			ArrayList<Object> recipe = new ArrayList<Object>(Arrays.asList(key));
			boolean[] used = new boolean[inputSize];
			for (int i = 0; i < inputSize; i++) {
				ItemStack target = input[i];

				if (target != null) {
					boolean flag = false;

					for (Object obj : recipe) {
						if (obj instanceof ItemStack) {
							if (equalStack(target, (ItemStack) obj, true)) {
								flag = true;
								recipe.remove(obj);
								break;
							}
						} else if (obj instanceof ItemStack[]) {
							if (!(containsStack(input[i], (ItemStack[]) obj, true) == -1)) {
								flag = true;
								recipe.remove(obj);
								break;
							}
						} else if (obj instanceof OreStack) {
							int oreID = OreDictionary.getOreID(((OreStack) obj).oreString);
							for (int id : OreDictionary.getOreIDs(target)) {
								if (id == oreID) {
									flag = true;
									break;
								}
							}
						}

					}

					if (!flag) {
						return false;
					}
				}

			}
		}

		return true;
	}

	/**
	 * @param output
	 *            output stacks to check
	 * @param key
	 *            output stacks to check
	 * @return if they are same
	 */
	private boolean checkOutput(ItemStack[] output, Object[] key) {
		if (output.length != key.length && output.length == inputSize) {
			return false;
		}
		for (int i = 0; i < output.length; i++) {
			if (key[i] instanceof ItemStack) {
				if (!equalStack(output[i], ((ItemStack) key[i]), true)) {
					return false;
				}
			} else if (key[i] instanceof ItemStack[]) {
				if (containsStack(output[i], (ItemStack[]) key[i], true) == -1) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param stack
	 *            ItemStack to search for
	 * @param key
	 *            search field
	 * @param checkSize
	 *            does the stacksize matter
	 * @return if a match was found
	 */
	public int containsStack(ItemStack stack, Object[] key, boolean checkSize) {
		for (int i = 0; i < key.length; i++) {
			if (key[i] != null) {
				if (key[i] instanceof ItemStack) {
					if (equalStack(stack, ((ItemStack) key[i]), checkSize)) {
						return i;
					}
				} else if (key[i] instanceof ItemStack[]) {
					for (int s = 0; s < ((ItemStack[]) key[i]).length; s++) {
						ItemStack target = ((ItemStack[]) key[i])[s];
						if (target != null) {
							if (equalStack(stack, target, checkSize)) {
								return i;
							}
						}
					}
				} else if (key[i] instanceof OreStack) {
					int oreID = OreDictionary.getOreID(((OreStack) key[i]).oreString);
					for (int id : OreDictionary.getOreIDs(stack)) {
						if (id == oreID) {
							return i;
						}
					}
				}
			}
		}
		return -1;
	}

	/**
	 * @param stack
	 *            ItemStack to check
	 * @param key
	 *            ItemStack to compare against
	 * @param checkSize
	 *            does the stack size matter
	 * @return if the stacks are equal or not
	 */
	private boolean equalStack(ItemStack stack, ItemStack key, boolean checkSize) {
		return stack.getItem() == key.getItem() && (key.getItemDamage() == 32767 || (stack.getItemDamage() == key.getItemDamage())) && (!checkSize || key.stackSize <= stack.stackSize);
	}

	/**
	 * 
	 * @param stack
	 *            ItemStack you wish to obtain the stack size of
	 * @param key
	 *            field you wish to find it in
	 * @param pos
	 *            the position of the stack in the output
	 * @return
	 */
	private int findStackSize(ItemStack stack, Object[] key, int pos) {
		if (key[pos] != null) {
			if (key[pos] instanceof ItemStack) {
				if (equalStack(stack, ((ItemStack) key[pos]), false)) {
					return ((ItemStack) key[pos]).stackSize;
				}
			} else if (key[pos] instanceof ItemStack[]) {
				return findStackSize(stack, (ItemStack[]) key[pos], pos);

			} else if (key[pos] instanceof OreStack) {
				return ((OreStack) key[pos]).stackSize;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param inputs
	 *            list of inputs stacks to check
	 * @return the crafting result
	 */
	public ItemStack getCraftingResult(ItemStack... inputs) {
		ItemStack[] output = getOutput(inputs);
		if (output == null || output.length == 0 || output[0] == null) {
			return null;
		}
		ItemStack result = output[0].copy();

		if (result.stackSize <= 0) {
			result.stackSize = 1;

		}
		return result;

	}

	/**
	 * Removes the recipeOutput with the specified inputs
	 * 
	 * @param inputs
	 *            inputs of the recipeOutput to remove
	 * @return if the recipeOutput was found and removed
	 */
	public boolean removeRecipe(Object... inputs) {
		if (inputs.length != inputSize) {
			FMLLog.severe("Attempted to remove a recipeOutput but input amount doesn't match recipeOutput input");
			return false;
		}
		for (Map.Entry<Object[], Object[]> entry : recipeList.entrySet()) {
			boolean[] found = new boolean[inputSize];
			for (int i = 0; i < inputSize; i++) {
				found[i] = false;
				Object input = inputs[i];
				for (int ii = 0; ii < inputSize; ii++) {
					Object recipeInput = entry.getKey()[ii];

					if (input instanceof ItemStack && recipeInput instanceof ItemStack && ItemStackHelper.equalStacksRegular((ItemStack) input, (ItemStack) recipeInput)) {
						found[i] = true;
					}

					if (input instanceof OreStack && recipeInput instanceof OreStack && ((OreStack) input).oreString.equals(((OreStack) recipeInput).oreString)) {
						found[i] = true;
					}

					if (input == recipeInput)
						found[i] = true;
				}
			}

			boolean remove = true;
			for (boolean result : found) {
				if (!result) {
					remove = false;
				}
			}
			if (remove) {
				recipeList.remove(entry.getKey());
				return true;
			}

		}
		return false;
	}

	/**
	 * used for inputs/outputs using OreDict which require a custom stack size
	 * instead of 1.
	 */
	public static class OreStack {
		public String oreString;
		public int stackSize;
		private List<ItemStack> stacks;

		public OreStack(String oreString, int stackSize) {
			this.oreString = oreString;
			this.stackSize = stackSize;
		}

		@Override
		public String toString() {
			return String.format("%dx%s", stackSize, oreString);
		}

		public List<ItemStack> getStacks() {
			if (stacks == null) {
				stacks = OreDictionary.getOres(oreString);
			}
			return stacks;
		}
	}
}
