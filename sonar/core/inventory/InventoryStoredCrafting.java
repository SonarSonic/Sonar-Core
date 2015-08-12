package sonar.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class InventoryStoredCrafting extends InventoryCrafting {
	private int inventoryWidth, inventoryHeight, offset;
	private Container eventHandler;
	private IInventory inv;

	public InventoryStoredCrafting(Container container, int size, int height, IInventory inv) {
		super(container, size, height);
		this.eventHandler = container;
		this.inventoryWidth = size;
		this.inventoryHeight = height;
		this.inv = inv;
		this.offset = 0;
	}

	public InventoryStoredCrafting(Container container, int size, int height, IInventory inv, int offset) {
		super(container, size, height);
		this.eventHandler = container;
		this.inventoryWidth = size;
		this.inventoryHeight = height;
		this.inv = inv;
		this.offset = offset;
	}

	@Override
	public int getSizeInventory() {
		return inventoryWidth * inventoryHeight;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot >= this.getSizeInventory() ? null : inv.getStackInSlot(slot + 1 + offset);
	}

	@Override
	public ItemStack getStackInRowAndColumn(int row, int column) {
		if (row >= 0 && row < this.inventoryWidth) {
			int k = row + column * this.inventoryWidth;
			return this.getStackInSlot(k);
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slotID, int par2) {
		ItemStack stack = inv.getStackInSlot(slotID + 1 + offset);
		if (stack != null) {
			ItemStack itemstack;

			if (stack.stackSize <= par2) {
				itemstack = stack.copy();
				stack = null;
				inv.setInventorySlotContents(slotID + 1 + offset, null);
				this.eventHandler.onCraftMatrixChanged(this);
				return itemstack;
			} else {
				itemstack = stack.splitStack(par2);

				if (stack.stackSize == 0) {
					stack = null;
				}

				this.eventHandler.onCraftMatrixChanged(this);
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		inv.setInventorySlotContents(slot + 1 + offset, itemstack);
		this.eventHandler.onCraftMatrixChanged(this);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		System.out.print("saved");
		inv.markDirty();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

}