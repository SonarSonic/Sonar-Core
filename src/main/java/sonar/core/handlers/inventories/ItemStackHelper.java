package sonar.core.handlers.inventories;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import sonar.core.api.utils.ICalculatorCircuit;

public class ItemStackHelper {

    /**checks if two itemstacks are the same ignoring the size*/
	public static boolean equalStacksRegular(ItemStack stack1, ItemStack stack2) {
		return !stack1.isEmpty() && !stack2.isEmpty() && stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage() && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

    /** turns blocks/items into ItemStacks */
	public static ItemStack getOrCreateStack(Object obj) {
		if (obj instanceof ItemStack) {
			ItemStack stack = ((ItemStack) obj).copy();
			if (stack.getCount() == 0) {
				stack.setCount(1);
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

    /** checks if the two input itemstacks come from the same mod */
	public static boolean matchingModid(ItemStack target, ItemStack stack) {
		String targetID = target.getItem().getRegistryName().getResourceDomain();
		String stackID = stack.getItem().getRegistryName().getResourceDomain();
        return targetID != null && !targetID.isEmpty() && !stackID.isEmpty() && targetID.equals(stackID);
	}

	/**checks if stacks have matching ore dictionary entries*/
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

	/** gets the ItemStack to represent a block in the world*/
	public static ItemStack getBlockItem(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		ItemStack stack = state.getBlock().getItem(world, pos, state);
		if (stack.isEmpty()) {
			stack = new ItemStack(Item.getItemFromBlock(state.getBlock()));
		}
		return stack;
	}
}
