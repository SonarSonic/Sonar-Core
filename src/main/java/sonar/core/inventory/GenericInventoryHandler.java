package sonar.core.inventory;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import sonar.core.api.SonarAPI;
import sonar.core.api.StorageSize;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.InventoryHelper;
import sonar.core.utils.SonarCompat;

public class GenericInventoryHandler {

	public static StoredItemStack getStack(int slot, IInventory inv, EnumFacing dir) {
		if (slot < inv.getSizeInventory()) {
			ItemStack stack = inv.getStackInSlot(slot);
			if (!SonarCompat.isEmpty(stack))
				return new StoredItemStack(stack);
		}
		return null;
	}

	public static StorageSize getItems(List<StoredItemStack> storedStacks, IInventory inv, EnumFacing dir) {
		return SonarAPI.getItemHelper().addInventoryToList(storedStacks, inv);
	}

	public static StoredItemStack addStack(StoredItemStack add, IInventory inv, EnumFacing dir, ActionType action) {
		int invSize = inv.getSizeInventory();
		int limit = inv.getInventoryStackLimit();
		int[] slots = null;
		if (dir != null && inv instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) inv;
			slots = sidedInv.getSlotsForFace(dir);
			invSize = slots.length;
		}
		for (int i = 0; i < invSize; i++) {
			final int slot = slots != null ? slots[i] : i;
            if (!(inv instanceof ISidedInventory) || ((ISidedInventory) inv).canInsertItem(slot, add.item, dir)) {
				if (!InventoryHelper.addStack(inv, add, slot, limit, action)) {
					return null;
				}
			}
		}
		return add;
	}

	public static StoredItemStack removeStack(StoredItemStack remove, IInventory inv, EnumFacing dir, ActionType action) {
		IInventory adjust = inv;
		int invSize = inv.getSizeInventory();
		int[] slots = null;
		if (inv instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) inv;
			slots = sidedInv.getSlotsForFace(dir);
			invSize = slots.length;
		}
		for (int i = 0; i < invSize; i++) {
			int slot = slots != null ? slots[i] : i;
			final ItemStack stored = inv.getStackInSlot(slot);
			if (!SonarCompat.isEmpty(stored)) {
				if (!(inv instanceof ISidedInventory) || ((ISidedInventory) inv).canExtractItem(slot, stored, dir)) {
					if (!InventoryHelper.removeStack(inv, remove, stored, slot, action)) {
						return null;
					}
				}
			}
		}
		return new StoredItemStack(remove.item, remove.stored);
	}
}
