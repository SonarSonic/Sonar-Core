package sonar.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

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

	@Nonnull
    @Override
	public ItemStack getStackInSlot(int par1) {
        return module.getStackInSlot(offset);
	}

	@Nonnull
    @Override
	public ItemStack decrStackSize(int slot, int remove) {
        ItemStack stack = module.getStackInSlot(offset);
		if (!stack.isEmpty()) {
			module.setInventorySlotContents(0, ItemStack.EMPTY);
			return stack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Nonnull
    @Override
	public ItemStack removeStackFromSlot(int par1) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int par1, @Nonnull ItemStack par2ItemStack) {
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