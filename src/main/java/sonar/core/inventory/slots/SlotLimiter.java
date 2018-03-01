package sonar.core.inventory.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sonar.core.utils.SonarCompat;

public class SlotLimiter extends Slot {

	public Item item;

	public SlotLimiter(IInventory inv, int slot, int x, int y, Item disabled) {
		super(inv, slot, x, y);
		this.item = disabled;
	}

    @Override
	public boolean canTakeStack(EntityPlayer player) {
        return SonarCompat.isEmpty(getStack()) || this.getStack().getItem() != item;
	}

    @Override
	public boolean isItemValid(ItemStack stack) {
        return !SonarCompat.isEmpty(stack) && stack.getItem() != item;
	}
}
