package sonar.core.inventory.slots;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.inventory.SonarLargeInventory;

public class SlotLarge extends Slot {
	public SonarLargeInventory largeInv;

	public SlotLarge(SonarLargeInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		largeInv = inventoryIn;
	}

	public int getSlotStackLimit() {
		return largeInv.numStacks * 64;
	}

	public boolean isItemValid(ItemStack stack) {
		return largeInv.isItemValidForSlot(getSlotIndex()*largeInv.numStacks, stack);
	}

	public ItemStack getStack() {
		StoredItemStack stored = largeInv.buildItemStack(largeInv.slots[this.getSlotIndex()]);
		if (stored != null) {
			ItemStack item = stored.getFullStack();
			item.stackSize = (int) stored.stored;
			return item;
		}
		return null;
	}

	public void putStack(ItemStack stack) {
		StoredItemStack stored = largeInv.buildItemStack(largeInv.slots[this.getSlotIndex()]);
		if (stack != null) {
			this.largeInv.slots[getSlotIndex()] = largeInv.buildArrayList(new StoredItemStack(stack));
		} else {
			this.largeInv.slots[getSlotIndex()] = null;
		}

		this.onSlotChanged();
	}

	public ItemStack decrStackSize(int amount) {
		return this.inventory.decrStackSize(getSlotIndex() * largeInv.numStacks, amount);
	}
}
