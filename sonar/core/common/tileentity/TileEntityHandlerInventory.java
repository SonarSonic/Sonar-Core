package sonar.core.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import sonar.core.integration.fmp.handlers.InventoryTileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;

public abstract class TileEntityHandlerInventory extends TileEntityHandler implements IInventory {

	public InventoryTileHandler getInv(){
		return (InventoryTileHandler)this.getTileHandler();					
	}
	
	@Override
	public int getSizeInventory() {
		return getInv().getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return getInv().getStackInSlot(var1);
	}

	@Override
	public ItemStack decrStackSize(int slot, int var2) {
		return getInv().decrStackSize(slot, var2);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return getInv().getStackInSlot(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		getInv().setInventorySlotContents(i, itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		return getInv().getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
		getInv().openInventory();
	}

	@Override
	public void closeInventory() {
		getInv().closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return getInv().isItemValidForSlot(slot, stack);
	}

	@Override
	public String getInventoryName() {
		if(this.blockType==null){
			return "Sonar Inventory";
		}
		return this.blockType.getLocalizedName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return getInv().hasCustomInventoryName();
	}

}
