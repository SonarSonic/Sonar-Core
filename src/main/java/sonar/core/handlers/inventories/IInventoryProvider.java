package sonar.core.handlers.inventories;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.SonarAPI;
import sonar.core.api.StorageSize;
import sonar.core.api.inventories.InventoryHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.InventoryHelper;

public class IInventoryProvider extends InventoryHandler {

	public static String name = "Standard Inventory";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canHandleItems(TileEntity tile, EnumFacing dir) {
		return tile instanceof IInventory;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, EnumFacing dir) {
		IInventory inv = (IInventory) tile;
		if (slot < inv.getSizeInventory()) {
			ItemStack stack = inv.getStackInSlot(slot);
			if (stack != null)
				return new StoredItemStack(stack);
		}
		return null;
	}

	@Override
	public StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, EnumFacing dir) {
		if (tile instanceof IInventory) {
			IInventory inv = (IInventory) tile;
			return SonarAPI.getItemHelper().addInventoryToList(storedStacks, inv);
		}
		return StorageSize.EMPTY;
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, EnumFacing dir, ActionType action) {
		final IInventory inv = (IInventory) tile;
		int invSize = inv.getSizeInventory();
		int limit = inv.getInventoryStackLimit();
		int[] slots = null;
		if (dir != null && tile instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) tile;
			slots = sidedInv.getSlotsForFace(dir);
			invSize = slots.length;
		}
		for (int i = 0; i < invSize; i++) {
			final int slot = slots != null ? slots[i] : i;
			if ((!(tile instanceof ISidedInventory) || ((ISidedInventory) tile).canInsertItem(slot, add.item, dir))) {
				if (!InventoryHelper.addStack(inv, add, slot, limit, action)) {
					return null;
				}
			}
		}
		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, EnumFacing dir, ActionType action) {
		final IInventory inv = (IInventory) tile;
		IInventory adjust = inv;
		int invSize = inv.getSizeInventory();
		int[] slots = null;
		if (tile instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) tile;
			slots = sidedInv.getSlotsForFace(dir);
			invSize = slots.length;
		}
		for (int i = 0; i < invSize; i++) {
			int slot = slots != null ? slots[i] : i;
			final ItemStack stored = inv.getStackInSlot(slot);
			if (stored != null) {
				if (!(tile instanceof ISidedInventory) || ((ISidedInventory) tile).canExtractItem(slot, stored, dir)) {
					if (!InventoryHelper.removeStack(inv, remove, stored, slot, action)) {
						return null;
					}
				}
			}
		}
		return new StoredItemStack(remove.item, remove.stored);
	}
}
