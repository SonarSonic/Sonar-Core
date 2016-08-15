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
		if (tile instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) tile;
			slots = sidedInv.getSlotsForFace(dir);
			invSize = slots.length;
		}
		for (int i = 0; i < invSize; i++) {
			final int slot = slots != null ? slots[i] : i;
			if (inv.isItemValidForSlot(slot, add.item) && (!(tile instanceof ISidedInventory) || ((ISidedInventory) tile).canInsertItem(slot, add.item, dir))) {
				final ItemStack stored = inv.getStackInSlot(slot);
				if (stored != null) {
					ItemStack stack = stored.copy();
					if (add.equalStack(stack) && stack.stackSize < limit && stack.stackSize < stack.getMaxStackSize()) {
						long used = Math.min(add.item.getMaxStackSize() - stack.stackSize, Math.min(add.stored, limit - stack.stackSize));
						if (used > 0) {
							stack.stackSize += used;
							add.stored -= used;
							if (!action.shouldSimulate()) {
								inv.setInventorySlotContents(slot, stack.copy());
								inv.markDirty();
							}
						}
					}
				} else {
					long used = Math.min(add.item.getMaxStackSize(), Math.min(add.stored, limit));
					if (used > 0) {
						add.stored -= used;
						if (!action.shouldSimulate()) {
							inv.setInventorySlotContents(slot, new StoredItemStack(add.getFullStack()).setStackSize(used).getFullStack());
							inv.markDirty();
						}
					}
				}
				if (add.stored == 0) {
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
				ItemStack stack = stored.copy();
				if (!(tile instanceof ISidedInventory) || ((ISidedInventory) tile).canExtractItem(slot, stack, dir)) {
					if (remove.equalStack(stack)) {
						long used = (long) Math.min(remove.stored, Math.min(inv.getInventoryStackLimit(), stack.stackSize));
						stack.stackSize -= used;
						remove.stored -= used;
						if (stack.stackSize == 0) {
							stack = null;
						}
						if (!action.shouldSimulate()) {
							inv.setInventorySlotContents(slot, stack);
							inv.markDirty();
						}
						if (remove.stored == 0) {
							return null;
						}
					}
				}
			}
		}
		return new StoredItemStack(remove.item, remove.stored);
	}
}
