package sonar.core.inventory.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.utils.SonarCompat;

public class SlotList extends Slot {

	public SlotList(IInventory inv, int slot, int x, int y) {
		super(inv, slot, x, y);
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int size) {
		this.inventory.setInventorySlotContents(this.getSlotIndex(), SonarCompat.getEmpty());
		this.inventory.markDirty();
		return SonarCompat.getEmpty();
	}

	@Override
	public void onSlotChanged() {
		putStack(this.getStack());
	}

	@Override
	public void putStack(ItemStack stack) {
		ItemStack copy = SonarCompat.getEmpty();
		if (!SonarCompat.isEmpty(stack)) {
			copy = stack.copy();
			copy = SonarCompat.setCount(copy, 1);
		}
		this.inventory.setInventorySlotContents(this.getSlotIndex(), copy);
		this.inventory.markDirty();
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}
