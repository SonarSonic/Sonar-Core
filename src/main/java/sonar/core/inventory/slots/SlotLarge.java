package sonar.core.inventory.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.inventory.SonarLargeInventory;

public class SlotLarge extends Slot {
	public SonarLargeInventory largeInv;

	public SlotLarge(SonarLargeInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(null, index, xPosition, yPosition);
		largeInv = inventoryIn;
	}

	public int getSlotStackLimit() {
		return largeInv.numStacks * 64;
	}

	public boolean isItemValid(ItemStack stack) {
		if (stack == null)
			return false;
		StoredItemStack stored = largeInv.getLargeStack(getSlotIndex());
		return stored == null ? largeInv.isItemValidForSlot(getSlotIndex(), stack) : stored.equalStack(stack);
	}

	public ItemStack getStack() {
		StoredItemStack stored = largeInv.getLargeStack(getSlotIndex());
		if (stored != null && stored.getStackSize()!=0) {
			ItemStack item = stored.getFullStack();
			item.setCount((int) stored.stored);
			return item;
		}
		return null;
	}

	public void putStack(ItemStack stack) {
		largeInv.slots[getSlotIndex()] = stack != null && stack.getCount() != 0 ? new StoredItemStack(stack) : null;
		onSlotChanged();
	}

	public ItemStack decrStackSize(int amount) {
		return this.inventory.decrStackSize(getSlotIndex() * largeInv.numStacks, amount);
	}

	public void onSlotChanged() {
		largeInv.markDirty();
	}

	public boolean isHere(IInventory inv, int slotIn) {
		return slotIn == this.getSlotIndex();
	}

	public boolean isSameInventory(Slot other) {
		if (other instanceof SlotLarge) {
			return this.largeInv == ((SlotLarge) other).largeInv;
		}
		return false;
	}
}
