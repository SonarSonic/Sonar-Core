package sonar.core.inventory.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/** a slot which doesn't allow Item Stacks to be placed in it */
public class SlotBlockedInventory extends Slot {
	private int stackSize;

	public SlotBlockedInventory(IInventory inv, int slotNumber,	int x, int y) {
		super(inv, slotNumber, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int size) {
		if (getHasStack()) {
			this.stackSize += Math.min(size, getStack().stackSize);
		}

		return super.decrStackSize(size);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
		onCrafting(stack);
		super.onPickupFromSlot(player, stack);
	}

	@Override
	protected void onCrafting(ItemStack stack, int size) {
		this.stackSize += size;
		onCrafting(stack);
	}
}
