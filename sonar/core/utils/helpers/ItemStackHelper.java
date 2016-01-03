package sonar.core.utils.helpers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackHelper {

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
}
