package sonar.core.handlers.inventories.slots;

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

    @Override
	public boolean canTakeStack(EntityPlayer player) {
        return this.getStack().isEmpty() || this.getStack().getItem() != item;
	}

    @Override
	public boolean isItemValid(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() != item;
	}
}
