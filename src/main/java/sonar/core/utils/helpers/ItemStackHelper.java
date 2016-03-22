package sonar.core.utils.helpers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraftforge.oredict.OreDictionary;
import sonar.core.integration.SonarAPI;

public class ItemStackHelper {

	/** checks if the two itemstacks are equal and can be merged
	 * @param stack1 first stack your checking
	 * @param stack2 second stack your checking
	 * @return if they are equal and can be merged */
	public static boolean equalStacks(ItemStack stack1, ItemStack stack2) {
		return equalStacksRegular(stack1, stack2) && !isCircuit(stack1.getItem()) && stack1.stackSize < stack1.getMaxStackSize();
	}

	/** checks if two itemstacks are the same (and nothing more!)
	 * @param stack1 first stack your checking
	 * @param stack2second stack your checking
	 * @return if they are equal and can be merged */
	public static boolean equalStacksRegular(ItemStack stack1, ItemStack stack2) {
		return stack1 != null && stack2 != null && stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage() && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	/** fixes the problem with ItemStacks having no stack size, and sets it to the inputted number */
	public static ItemStack restoreItemStack(ItemStack stack, int size) {
		ItemStack result = stack.copy();

		if (result.stackSize <= 0) {
			result.stackSize = 1;

		}
		return result;
	}

	/** @param item Item you are checking
	 * @return if the stack is an circuit */
	public static boolean isCircuit(Item item) {

		if (SonarAPI.calculatorLoaded()) {
			if (item == GameRegistry.findItem("Calculator", "CircuitBoard")) {
				return true;
			} else if (item == GameRegistry.findItem("Calculator", "CircuitDamaged")) {
				return true;
			} else if (item == GameRegistry.findItem("Calculator", "CircuitDirty")) {
				return true;
			}
		}
		return false;
	}

	/** turns blocks/items into ItemStacks */
	public static ItemStack createStack(Object obj) {
		if (obj instanceof ItemStack) {
			return ((ItemStack) obj).copy();
		} else if (obj instanceof Item) {
			return new ItemStack((Item) obj, 1);
		} else {
			if (!(obj instanceof Block)) {
				throw new RuntimeException("Invalid ItemStack!");
			}
			return new ItemStack((Block) obj, 1);
		}
	}

	/** checks if the two input itemstacks come from the same mod.
	 * 
	 * @param target
	 * @param stack
	 * @return */
	public static boolean matchingModid(ItemStack target, ItemStack stack) {
		UniqueIdentifier targetID = GameRegistry.findUniqueIdentifierFor(target.getItem());
		UniqueIdentifier stackID = GameRegistry.findUniqueIdentifierFor(stack.getItem());
		if (targetID != null && stackID != null && targetID.modId != null && stackID.modId != null) {
			return targetID.modId.equals(stackID.modId);
		}
		return false;
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
