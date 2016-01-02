package sonar.core.inventory.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotLimiter extends Slot {

	public Item item;

	public SlotLimiter(IInventory inv, int slot, int x, int y, Item disabled) {
		super(inv, slot, x, y);
		this.item = disabled;
	}

	public boolean canTakeStack(EntityPlayer player) {
		if (this.getStack() != null && this.getStack().getItem() == item) {
			return false;
		}
		return true;
	}

	public boolean isItemValid(ItemStack stack) {
		if (stack ==null || stack.getItem() == item) {
			return false;
		}
		return true;
	}
}
