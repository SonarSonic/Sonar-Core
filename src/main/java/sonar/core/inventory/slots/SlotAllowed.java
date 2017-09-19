package sonar.core.inventory.slots;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotAllowed extends Slot {

	public Object items;

	public SlotAllowed(IInventory inv, int id, int x, int y, Object items) {
		super(inv, id, x, y);
		this.items=items;
	}

    @Override
	public boolean isItemValid(ItemStack stack) {
		if (items instanceof ItemStack[]) {
			ItemStack[] itemList = (ItemStack[]) items;
            for (ItemStack itemstack : itemList) {
                if (!itemstack.isEmpty() && itemstack.getItem() == stack.getItem()) {
					return true;
				}
			}
		} else if (items instanceof ItemStack) {
			ItemStack itemstack = (ItemStack) items;
			if (!itemstack.isEmpty() && itemstack.getItem() == stack.getItem()) {
				return true;
			}
		} else if (items instanceof Item) {
			Item item = (Item) items;
			if (item != null && item == stack.getItem()) {
				return true;
			}
		} else if (items instanceof Block) {
			Block block = (Block) items;
			if (block != null && Item.getItemFromBlock(block) == stack.getItem()) {
				return true;
			}
		}
		return false;
	}
}
