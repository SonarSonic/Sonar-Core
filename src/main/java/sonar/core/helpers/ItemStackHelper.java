package sonar.core.helpers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import sonar.core.api.utils.ICalculatorCircuit;
import sonar.core.utils.SonarCompat;

public class ItemStackHelper {
    /**
     * checks if the two itemstacks are equal and can be merged
	 * 
	 * @param stack1 first stack your checking
	 * @param stack2 second stack your checking
     * @return if they are equal and can be merged
     */
	public static boolean equalStacks(ItemStack stack1, ItemStack stack2) {
		return equalStacksRegular(stack1, stack2) && !isCircuit(stack1.getItem()) && SonarCompat.getCount(stack1) < stack1.getMaxStackSize();
	}

    /**
     * checks if two itemstacks are the same - ignores the stack size
	 * 
	 * @param stack1 first stack your checking
     * @param stack2 stack your checking
     * @return if they are equal and can be merged
     */
	public static boolean equalStacksRegular(ItemStack stack1, ItemStack stack2) {
		return !SonarCompat.isEmpty(stack1) && !SonarCompat.isEmpty(stack2) && stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage() && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

    /**
     * fixes the problem with ItemStacks having no stack size, and sets it to the inputted number
     */
	public static ItemStack restoreItemStack(ItemStack stack, int size) {
		ItemStack result = stack.copy();

		if (SonarCompat.getCount(result) <= 0) {
			result = SonarCompat.setCount(result, size);
		}
		return result;
	}

    /**
     * @param item Item you are checking
     * @return if the stack is an circuit
     */
	public static boolean isCircuit(Item item) {
		return item instanceof ICalculatorCircuit;
	}

    public static ItemStack grow(ItemStack stack, int stackSize) {
        if (!SonarCompat.isEmpty(stack)) {
            stack = SonarCompat.setCount(stack, SonarCompat.getCount(stack) + stackSize);
        }
        return stack;
    }

    public static ItemStack shrink(ItemStack stack, int stackSize) {
        if (!SonarCompat.isEmpty(stack)) {
            stack = SonarCompat.setCount(stack, SonarCompat.getCount(stack) - stackSize);
        }
        if (SonarCompat.getCount(stack) <= 0) {
            stack = SonarCompat.getEmpty();
        }
        return stack;
    }

    /**
     * turns blocks/items into ItemStacks
     */
	public static ItemStack createStack(Object obj) {
		if (obj instanceof ItemStack) {
			ItemStack stack = ((ItemStack) obj).copy();
			if (SonarCompat.getCount(stack) == 0) {
				stack = SonarCompat.setCount(stack, 1);
			}
			return stack;
		} else if (obj instanceof Item) {
			return new ItemStack((Item) obj, 1);
		} else {
			if (!(obj instanceof Block)) {
				throw new RuntimeException(String.format("Invalid ItemStack: %s", obj));
			}
			return new ItemStack((Block) obj, 1);
		}
	}

    /**
     * checks if the two input itemstacks come from the same mod.
	 * 
	 * @param target
	 * @param stack
     * @return
     */
	public static boolean matchingModid(ItemStack target, ItemStack stack) {
		String targetID = target.getItem().getRegistryName().getResourceDomain();
		String stackID = stack.getItem().getRegistryName().getResourceDomain();
        return targetID != null && stackID != null && !targetID.isEmpty() && !stackID.isEmpty() && targetID.equals(stackID);
	}

	public static boolean matchingOreDictID(ItemStack target, ItemStack stack) {
		int[] stackIDs = OreDictionary.getOreIDs(stack);
		int[] filterIDs = OreDictionary.getOreIDs(target);
		for (int sID : stackIDs) {
			for (int fID : filterIDs) {
				if (sID == fID) {
					return true;
				}
			}
		}
		return false;
	}
}
