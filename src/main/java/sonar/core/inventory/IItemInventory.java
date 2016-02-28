package sonar.core.inventory;

import net.minecraft.item.ItemStack;
import sonar.core.common.item.InventoryItem;

public interface IItemInventory {
	
	public InventoryItem getInventory(ItemStack stack);		
}

