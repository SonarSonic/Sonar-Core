package sonar.core.recipes;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeUtils {

	public static List<ItemStack> addStack(List<ItemStack> stacks, ItemStack stack) {
		if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
			for (int i = 0; i <= stack.getMaxDamage(); i++) {
				stacks.add(new ItemStack(stack.getItem(), stack.stackSize, i));
			}
		} else {
			stacks.add(stack);
		}
		return stacks;
	}

	public static List<ItemStack> addStacks(List<ItemStack> stacks, List<ItemStack> toAdd) {
		for (ItemStack ore : toAdd) {
			stacks = addStack(stacks, ore);
		}
		return stacks;
	}

	public static List<List<ItemStack>> configureStacks(IRecipe recipe) {
		List<List<ItemStack>> stacks = Lists.newArrayList(new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList());

		if (recipe instanceof ShapedRecipes) {
			ItemStack[] shaped = ((ShapedRecipes) recipe).recipeItems;
			int i = 0;
			for (ItemStack stack : shaped) {
				stacks.set(i, addStack(new ArrayList(), stack.copy()));
				i++;
			}
		} else if (recipe instanceof ShapelessRecipes) {
			ShapelessRecipes shaped = (ShapelessRecipes) recipe;
			int i = 0;
			for (ItemStack stack : shaped.recipeItems) {
				stacks.set(i, addStack(new ArrayList(), stack.copy()));
				i++;
			}
		} else if (recipe instanceof ShapedOreRecipe) {
			ShapedOreRecipe oreRecipe = (ShapedOreRecipe) recipe;
			int i = 0;
			for (Object obj : oreRecipe.getInput()) {
				stacks.set(i, getListFromObject(obj));
				i++;
			}
		} else if (recipe instanceof ShapelessOreRecipe) {
			ShapelessOreRecipe oreRecipe = (ShapelessOreRecipe) recipe;
			int i = 0;
			for (Object obj : oreRecipe.getInput()) {
				stacks.set(i, getListFromObject(obj));
				i++;
			}
		}
		return stacks;
	}

	public static List<ItemStack> getListFromObject(Object obj) {
		if (obj instanceof List) {
			return addStacks(new ArrayList(), (List<ItemStack>) obj);
		} else if (obj instanceof ItemStack) {
			return addStack(new ArrayList(), ((ItemStack) obj).copy());
		} else if (obj instanceof Item) {
			return Lists.newArrayList(new ItemStack((Item) obj));
		} else if (obj instanceof Block) {
			return addStack(new ArrayList(), new ItemStack((Block) obj, 1, OreDictionary.WILDCARD_VALUE));
		} else if (obj instanceof String) {
			return addStacks(new ArrayList(), OreDictionary.getOres((String) obj));
		}
		return new ArrayList();
	}
}
