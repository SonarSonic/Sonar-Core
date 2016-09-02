package sonar.core.recipes;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sonar.core.SonarCore;
import sonar.core.helpers.ItemStackHelper;
import sonar.core.utils.Pair;

public abstract class RecipeHelperV2 {

	public ArrayList<ISonarRecipe> recipes = new ArrayList();
	public ArrayList<Class<?>> validInputs = Lists.newArrayList(ISonarRecipeObject.class, ItemStack.class, Item.class, Block.class, ItemStack[].class, List.class);
	public ArrayList<Class<?>> validOutputs = Lists.newArrayList(ISonarRecipeObject.class, ItemStack.class, Item.class, Block.class);
	public ArrayList<Class<?>> adjusted = Lists.newArrayList(Item.class, Block.class);

	public abstract String getRecipeID();

	public ArrayList<ISonarRecipe> getRecipes() {
		return recipes;
	}
	
	public ISonarRecipe getRecipeFromInputs(List inputs) {
		for (ISonarRecipe recipe : recipes) {
			if (recipe.matchingInputs(inputs)) {
				return recipe;
			}
		}
		return null;
	}

	public ISonarRecipe getRecipeFromOutputs(List outputs) {
		for (ISonarRecipe recipe : recipes) {
			if (recipe.matchingOutputs(outputs)) {
				return recipe;
			}
		}
		return null;
	}
	public boolean addRecipe(ISonarRecipe recipe) {
		if (recipe != null) {
			return recipes.add(recipe);
		}
		return false;
	}

	public boolean removeRecipe(ISonarRecipe recipe) {
		if (recipe != null) {
			return recipes.remove(recipe);
		}
		return false;
	}
	
	public void addValidInput(Class inputTypes) {
		validInputs.add(inputTypes);
	}
	
	public boolean isValidInput(Object input) {
		for (Class<?> inputType : validInputs) {
			if (inputType == List.class) {
				List list = (List) input;
				if (!list.isEmpty() && isValidInput(list.get(0))) {
					return true;
				}
			} else if (inputType.isInstance(input)) {
				return true;
			}
		}
		return false;
	}

	public boolean isValidOutput(Object output) {
		for (Class<?> outputType : validOutputs) {
			if (outputType.isInstance(output)) {
				return true;
			}
		}
		return false;
	}

	public boolean requiresAdjustment(Object obj) {
		for (Class<?> objectType : adjusted) {
			if (objectType.isInstance(obj)) {
				return true;
			}
		}
		return false;
	}

	public Object adjustObject(Object obj) {
		return ItemStackHelper.createStack(obj);
	}

	public ISonarRecipeObject buildRecipeObject(Object obj) {
		if (requiresAdjustment(obj)) {
			obj = adjustObject(obj);
		}
		if (obj instanceof ISonarRecipeObject) {
			return (ISonarRecipeObject) obj;
		} else if (obj instanceof List) {
			List list = (List) obj;
			List<ISonarRecipeObject> buildList = new ArrayList();
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
			return new OreStack((String) obj, 1);
		}
		if (obj instanceof ItemStack) {
			return new RecipeItemStack((ItemStack) obj);
		}
		return null;
	}

	public void addRecipe(List inputs, List outputs) {
		addRecipe(buildDefaultRecipe(inputs, outputs));
	}

	public ISonarRecipe buildDefaultRecipe(List inputs, List outputs) {
		ArrayList<ISonarRecipeObject> recipeInputs = Lists.<ISonarRecipeObject>newArrayList();
		ArrayList<ISonarRecipeObject> recipeOutputs = Lists.<ISonarRecipeObject>newArrayList();
		for (Pair<List, ArrayList<ISonarRecipeObject>> builder : Lists.newArrayList(new Pair(inputs, recipeInputs), new Pair(outputs, recipeOutputs))) {
			for (Object obj : builder.a) {
				if (isValidInput(obj)) {//might needto check if input or output here
					recipeInputs.add(buildRecipeObject(obj));
				} else {
					SonarCore.logger.error("Invalid recipe value! " + obj);
					return null;
				}
			}
		}
		return new DefaultSonarRecipe(recipeOutputs, recipeOutputs);
	}
	

	public static boolean matchingIngredients(ArrayList<ISonarRecipeObject> ingredients, Object... objs) {
		ArrayList<ISonarRecipeObject> matches = (ArrayList<ISonarRecipeObject>) ingredients.clone();
		inputs: for (Object obj : objs) {
			for (ISonarRecipeObject ingredient : (ArrayList<ISonarRecipeObject>) matches.clone()) {
				if (ingredient.matches(obj)) {
					matches.remove(ingredient);
					continue inputs;
				}
			}
			return false;
		}
		return true;
	}

}
