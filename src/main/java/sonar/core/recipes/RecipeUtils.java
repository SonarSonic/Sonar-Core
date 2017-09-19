package sonar.core.recipes;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeUtils {

	public static List<ItemStack> addStack(List<ItemStack> stacks, ItemStack stack) {
		if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
			for (int i = 0; i <= stack.getMaxDamage(); i++) {
				stacks.add(new ItemStack(stack.getItem(), stack.getCount(), i));
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

	public static List<ItemStack> getListFromObject(Object obj) {
		if (obj instanceof List) {
            return addStacks(new ArrayList<>(), (List<ItemStack>) obj);
		} else if (obj instanceof ItemStack) {
            return addStack(new ArrayList<>(), ((ItemStack) obj).copy());
		} else if (obj instanceof Item) {
			return Lists.newArrayList(new ItemStack((Item) obj));
		} else if (obj instanceof Block) {
            return addStack(new ArrayList<>(), new ItemStack((Block) obj, 1, OreDictionary.WILDCARD_VALUE));
		} else if (obj instanceof String) {
            return addStacks(new ArrayList<>(), OreDictionary.getOres((String) obj));
		}
        return new ArrayList<>();
	}
}
