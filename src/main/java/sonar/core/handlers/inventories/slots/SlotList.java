package sonar.core.handlers.inventories.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotList extends Slot {

	public SlotList(IInventory inv, int slot, int x, int y) {
		super(inv, slot, x, y);
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return false;
	}

	@Nonnull
    @Override
	public ItemStack decrStackSize(int size) {
		this.inventory.setInventorySlotContents(this.getSlotIndex(), ItemStack.EMPTY);
		this.inventory.markDirty();
		return ItemStack.EMPTY;
	}

	@Override
	public void onSlotChanged() {
		putStack(this.getStack());
	}

	@Override
	public void putStack(@Nonnull ItemStack stack) {
		ItemStack copy = ItemStack.EMPTY;
		if (!stack.isEmpty()) {
			copy = stack.copy();
			copy.setCount(1);
		}
		this.inventory.setInventorySlotContents(this.getSlotIndex(), copy);
		this.inventory.markDirty();
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}
