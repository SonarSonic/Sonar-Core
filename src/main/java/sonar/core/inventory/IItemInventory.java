package sonar.core.inventory;

import net.minecraft.item.ItemStack;
import sonar.core.common.item.InventoryItem;

public interface IItemInventory {
	
    InventoryItem getInventory(ItemStack stack);
}

