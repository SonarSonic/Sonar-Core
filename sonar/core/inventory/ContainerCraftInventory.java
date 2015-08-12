package sonar.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import sonar.core.common.item.InventoryItem;
import sonar.core.utils.SlotLimiter;

/** used by Calculators and other Hand-Held items with Inventories */
public abstract class ContainerCraftInventory extends Container {

	public final EntityPlayer player;
	protected final InventoryItem inventory;
	protected World worldObj;

	public ContainerCraftInventory(EntityPlayer player, InventoryPlayer inv, InventoryItem inventory) {
		this.worldObj = player.worldObj;
		this.inventory = inventory;
		this.player = player;
	}

	public boolean checkEmptySlot(int i) {
		Slot slot = (Slot) this.inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			return false;
		}
		return true;
	}

}
