package sonar.core.recipes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sonar.core.SonarCore;
import sonar.core.helpers.ItemStackHelper;

/**
 * the new Flexible Recipe Helper, WARNING: addRecipes() needs to be called
 */
public abstract class RecipeHelperV2<T extends ISonarRecipe> implements IRecipeHelperV2<T> {

    /**
     * the list of recipes
     */
    public ArrayList<T> recipes = new ArrayList<>();
	public ArrayList<Class<?>> validInputs = Lists.newArrayList(ISonarRecipeObject.class, ItemStack.class, Item.class, Block.class, ItemStack[].class, List.class, String.class, Integer.class);
	public ArrayList<Class<?>> validOutputs = Lists.newArrayList(ISonarRecipeObject.class, ItemStack.class, Item.class, Block.class, String.class, Integer.class);

    /**
     * types which must be adjusted before being added to a recipe
     */
	public ArrayList<Class<?>> adjusted = Lists.newArrayList(Item.class, Block.class);

	public RecipeHelperV2() {}

    @Override
	public abstract String getRecipeID();

	public abstract void addRecipes();

    @Override
	public ArrayList<T> getRecipes() {
		return recipes;
	}

	public List<ISonarRecipeObject> getInputs(EntityPlayer player, Object... outputs) {
		T recipe = getRecipeFromOutputs(player, outputs);
        return recipe != null ? recipe.inputs() : new ArrayList<>();
	}

	public List<ISonarRecipeObject> getOutputs(EntityPlayer player, Object... inputs) {
		T recipe = getRecipeFromInputs(player, inputs);
        return recipe != null ? recipe.outputs() : new ArrayList<>();
	}

	@Nullable
	public T getRecipeFromInputs(@Nullable EntityPlayer player, Object[] inputs) {
		for (T recipe : recipes) {
			if ((player == null || recipe.canUseRecipe(player)) && recipe.matchingInputs(inputs)) {
				return recipe;
			}
		}
		return null;
	}

	@Nullable
	public T getRecipeFromOutputs(@Nullable EntityPlayer player, Object[] outputs) {
		for (T recipe : recipes) {
			if ((player == null || recipe.canUseRecipe(player)) && recipe.matchingOutputs(outputs)) {
				return recipe;
			}
		}
		return null;
	}

	public boolean addRecipe(T recipe) {
        return recipe != null && recipes.add(recipe);
	}

	public boolean removeRecipe(T recipe) {
        return recipe != null && recipes.remove(recipe);
	}

	public void addValidInput(Class inputTypes) {
		validInputs.add(inputTypes);
	}

	private boolean isValidInputType(Object input) {
		for (Class<?> inputType : validInputs) {
			if (input instanceof List && inputType == List.class) {
				List list = (List) input;
				if (!list.isEmpty() && isValidInputType(list.get(0))) {
					return true;
				}
			} else if (inputType.isInstance(input)) {
				return true;
			}
		}
		return false;
	}

	private boolean isValidOutputType(Object output) {
		for (Class<?> outputType : validOutputs) {
			if (outputType.isInstance(output)) {
				return true;
			}
		}
		return false;
	}

	private boolean requiresAdjustment(Object obj) {
		for (Class<?> objectType : adjusted) {
			if (objectType.isInstance(obj)) {
				return true;
			}
		}
		return false;
	}

    /**
     * builds a recipe object, can be null, this can be overridden if needed
     */
	@Nullable
	public ISonarRecipeObject buildRecipeObject(Object obj) {
		if (requiresAdjustment(obj)) {
			obj = adjustObject(obj);
		}
		if (obj instanceof ISonarRecipeObject) {
			return (ISonarRecipeObject) obj;
		} else if (obj instanceof List) {
			List list = (List) obj;
            List<ISonarRecipeObject> buildList = new ArrayList<>();
			if (!list.isEmpty()) {
				for (Object listObj : list) {
					if (listObj != null) {
						ISonarRecipeObject recipeObject = buildRecipeObject(listObj);
						if (recipeObject != null) {
							buildList.add(recipeObject);
						} else {
							return null;
						}
					}
				}
				return new RecipeInterchangable(buildList);
			} else {
				return null;
			}
		} else if (obj instanceof String) {
			return new RecipeOreStack((String) obj, 1);
		} else if (obj instanceof Integer) {
            return new RecipeInteger((Integer) obj);
		}
		if (obj instanceof ItemStack) {
			return new RecipeItemStack((ItemStack) obj, true);
		}
		return null;
	}

    /**
     * adds a recipe to the current recipelist
	 * 
	 * @param inputs the inputs of the recipe
	 * @param outputs the outputs of the recipe
	 * @param shapeless if the order of the inputs matters or not
     * @param additionals any extra objects which could be used for custom buildRecipe methods
     */
	public void addRecipe(List inputs, List outputs, List additionals, boolean shapeless) {
		addRecipe(buildDefaultRecipe(inputs, outputs, additionals, shapeless));
	}

    /**
     * builds a default recipe, this can also be overridden if needed
     */
	@Nullable
	public T buildDefaultRecipe(List inputs, List outputs, List additionals, boolean shapeless) {
        ArrayList<ISonarRecipeObject> recipeInputs = new ArrayList<>();
        ArrayList<ISonarRecipeObject> recipeOutputs = new ArrayList<>();
		for (Object obj : inputs) {
			if (obj != null && isValidInputType(obj)) {
				ISonarRecipeObject input = buildRecipeObject(obj);
				if (input == null)
					return null;
				recipeInputs.add(input);
			} else {
				SonarCore.logger.error("Invalid Recipe Input for " + getRecipeID() + ": " + obj);
				return null;
			}
		}
		for (Object obj : outputs) {
			if (obj != null && isValidOutputType(obj)) {
				ISonarRecipeObject output = buildRecipeObject(obj);
				if (output == null)
					return null;
				recipeOutputs.add(output);
			} else {
				SonarCore.logger.error("Invalid Recipe Output for " + getRecipeID() + ": " + obj);
				return null;
			}
		}
		if (!isValidRecipe(recipeInputs, recipeOutputs)) {
			SonarCore.logger.error("Invalid Recipe for " + getRecipeID() + ": " + recipeInputs.toString() + " -> " + recipeOutputs.toString());
		}
		return buildRecipe(recipeInputs, recipeOutputs, additionals, shapeless);
	}

	public boolean isValidRecipe(ArrayList<ISonarRecipeObject> recipeInputs, ArrayList<ISonarRecipeObject> recipeOutputs) {
		return recipeInputs.size() != 0 && recipeOutputs.size() != 0;
	}

    /**
     * builds a recipe with the given inputs and outputs, this recipe can be overridden and customised,
     */
	public T buildRecipe(ArrayList<ISonarRecipeObject> recipeInputs, ArrayList<ISonarRecipeObject> recipeOutputs, List additionals, boolean shapeless) {
		return (T) new DefaultSonarRecipe(recipeInputs, recipeOutputs, shapeless);
	}

    /**
     * if the given output can be used in a recipe
     */
	public boolean isValidOutput(Object obj) {
		for (T recipe : recipes) {
			for (ISonarRecipeObject output : recipe.outputs()) {
				if (output.matches(obj, RecipeObjectType.OUTPUT)) {
					return true;
				}
			}
		}
		return false;
	}

    /**
     * if the given input can be used in a recipe
     */
	public boolean isValidInput(Object obj) {
		for (T recipe : recipes) {
			for (ISonarRecipeObject input : recipe.inputs()) {
				if (input.matches(obj, RecipeObjectType.INPUT)) {
					return true;
				}
			}
		}
		return false;
	}

    /**
     * used on objects which require adjustment, typically used on objects which need to be converted to an itemstack
     */
	public static Object adjustObject(Object obj) {
		return ItemStackHelper.createStack(obj);
	}

    /**
     * gets a list compatible with JEI
     */
	public static ArrayList<List<ItemStack>> getJEIInputsFromList(List<ISonarRecipeObject> list) {
        ArrayList<List<ItemStack>> values = new ArrayList<>();
		list.forEach(obj -> values.add(obj.getJEIValue()));
		return values;
	}
	
	public static List<ItemStack> getJEIOutputsFromList(List<ISonarRecipeObject> list) {
        List<ItemStack> values = new ArrayList<>();
		list.forEach(obj -> values.add(obj.getJEIValue().get(0)));
		return values;
	}
	
    /**
     * extracts the value of each {@link ISonarRecipeObject} into a new list
     */
	public static List getValuesFromList(List<ISonarRecipeObject> list) {
        ArrayList values = new ArrayList<>();
		list.forEach(obj -> values.add(obj.getValue()));
		return values;
	}

    /**
     * @param list the list of given list items
	 * @param pos the itemstack's position in the list
     * @return the itemstack, can be null
     */
	@Nonnull
	public static ItemStack getItemStackFromList(List<ISonarRecipeObject> list, int pos) {
		if (!list.isEmpty() && pos < list.size()) {
			ISonarRecipeObject obj = list.get(pos);
			if (obj instanceof ISonarRecipeItem) {
				return ((ISonarRecipeItem) obj).getOutputStack();
			}
		}
		return ItemStack.EMPTY;
	}

    /**
     * @param type        the type of ingredients to match
	 * @param ingredients the required ingredients
	 * @param shapeless if this recipe is shapeless or not
	 * @param objs the current available ingredients
     * @return if all ingredients had matching objects or not
     */
	public static boolean matchingIngredients(RecipeObjectType type, ArrayList<ISonarRecipeObject> ingredients, boolean shapeless, Object[] objs) {
		ArrayList<ISonarRecipeObject> matches = (ArrayList<ISonarRecipeObject>) ingredients.clone();
		if (ingredients.size() != objs.length) {
			return false;
		}
		int pos = -1;
        inputs:
        for (Object obj : objs) {
			if (obj == null) {
				return false;
			}
			pos++;
			if (obj instanceof List) {
				List list = (List) obj;
				for (Object listObj : list) {
					if (matchingIngredient(listObj, pos, type, matches, ingredients, shapeless)) {
						continue inputs;
					}
				}
			}
			if (matchingIngredient(obj, pos, type, matches, ingredients, shapeless)) {
                continue;
			}
			return false;
		}
		return true;
	}

	public static boolean matchingIngredient(Object obj, int pos, RecipeObjectType type, ArrayList<ISonarRecipeObject> matches, ArrayList<ISonarRecipeObject> ingredients, boolean shapeless) {
		if (shapeless) {
			for (ISonarRecipeObject ingredient : (ArrayList<ISonarRecipeObject>) matches.clone()) {
				if (ingredient.matches(obj, type)) {
					matches.remove(ingredient);
					return true;
				}
			}
		} else if (ingredients.get(pos).matches(obj, type)) {
			matches.remove(ingredients.get(pos));
			return true;
		}
		if (type == RecipeObjectType.INPUT) {
			return false;
		}
		return false;
	}
}
