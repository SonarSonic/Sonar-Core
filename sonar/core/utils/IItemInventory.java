package sonar.core.utils;

import sonar.core.common.item.InventoryItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IItemInventory {
	
	public InventoryItem getInventory(ItemStack stack);		
}

