package sonar.core.inventory;

import net.minecraft.item.ItemStack;

public interface IFilteredInventory {
	boolean canPushItem(ItemStack item, int side);

	boolean canPullItem(ItemStack item, int side);
}
