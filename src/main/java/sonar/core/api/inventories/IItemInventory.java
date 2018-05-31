package sonar.core.api.inventories;

import net.minecraft.item.ItemStack;
import sonar.core.common.item.InventoryItem;

public interface IItemInventory {
	
    InventoryItem getInventory(ItemStack stack);
}

