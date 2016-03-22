package sonar.core.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import sonar.core.integration.fmp.handlers.InventoryTileHandler;

public abstract class TileEntityHandlerInventory extends TileEntityHandler implements IInventory {

	public InventoryTileHandler getInv(){
		return (InventoryTileHandler)this.getTileHandler();					
	}
	/*
	public int getSizeInventory() {
		return getInv().getSizeInventory();
	}

	public ItemStack getStackInSlot(int var1) {
		return getInv().getStackInSlot(var1);
	}

	public ItemStack decrStackSize(int slot, int var2) {
		return getInv().decrStackSize(slot, var2);
	}

	public ItemStack getStackInSlotOnClosing(int i) {
		return getInv().getStackInSlot(i);
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		getInv().setInventorySlotContents(i, itemstack);
	}

	public int getInventoryStackLimit() {
		return getInv().getInventoryStackLimit();
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	public void openInventory() {
		getInv().openInventory();
	}

	public void closeInventory() {
		getInv().closeInventory();
	}

	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return getInv().isItemValidForSlot(slot, stack);
	}

	public String getInventoryName() {
		if(this.blockType==null){
			return "Sonar Inventory";
		}
		return this.blockType.getLocalizedName();
	}

	public boolean hasCustomInventoryName() {
		return getInv().hasCustomInventoryName();
	}
	*/
}
