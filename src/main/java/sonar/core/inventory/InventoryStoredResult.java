package sonar.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import sonar.core.utils.SonarCompat;

public class InventoryStoredResult extends InventoryCraftResult {
	public IInventory module;
	public int offset;

	public InventoryStoredResult(IInventory module) {
		this.module = module;
	}

	public InventoryStoredResult(IInventory module, int offset) {
		this.module = module;
		this.offset = offset;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int par1) {
        return module.getStackInSlot(offset);
	}

	@Override
	public ItemStack decrStackSize(int slot, int remove) {
        ItemStack stack = module.getStackInSlot(offset);
		if (!SonarCompat.isEmpty(stack)) {
			ItemStack itemstack = stack;
			module.setInventorySlotContents(0, SonarCompat.getEmpty());
			return itemstack;
		} else {
			return SonarCompat.getEmpty();
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int par1) {
		return SonarCompat.getEmpty();
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        module.setInventorySlotContents(offset, par2ItemStack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		module.markDirty();
	}

	public boolean isStackValidForSlot(int par1, ItemStack par2ItemStack) {
		return true;
	}
}