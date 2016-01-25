package sonar.core.integration.fmp.handlers;

import sonar.core.utils.helpers.NBTHelper.SyncType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class InventoryTileHandler extends TileHandler implements IInventory {

	public InventoryTileHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}
	public ItemStack[] slots;
	
	public void readData(NBTTagCompound nbt, SyncType type){
		super.readData(nbt, type);
		if(type==SyncType.SAVE){
			NBTTagList list = nbt.getTagList("Items", 10);
			this.slots = new ItemStack[this.getSizeInventory()];
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				byte b = compound.getByte("Slot");
				if (b >= 0 && b < this.slots.length) {
					this.slots[b] = ItemStack.loadItemStackFromNBT(compound);
				}
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type){
		super.writeData(nbt, type);
		if(type==SyncType.SAVE){
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < this.slots.length; i++) {
				if (this.slots[i] != null) {
					NBTTagCompound compound = new NBTTagCompound();
					compound.setByte("Slot", (byte) i);
					this.slots[i].writeToNBT(compound);
					list.appendTag(compound);
				}
			}
			nbt.setTag("Items", list);
		}
	}
	
	@Override
	public int getSizeInventory() {
		return this.slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return this.slots[var1];
	}

	@Override
	public ItemStack decrStackSize(int slot, int var2) {
		if (this.slots[slot] != null) {

			if (this.slots[slot].stackSize <= var2) {
				ItemStack itemstack = this.slots[slot];
				this.slots[slot] = null;
				return itemstack;
			}
			ItemStack itemstack = this.slots[slot].splitStack(var2);

			if (this.slots[slot].stackSize == 0) {
				this.slots[slot] = null;
			}

			return itemstack;
		}

		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.slots[i] != null) {
			ItemStack itemstack = this.slots[i];
			this.slots[i] = null;
			return itemstack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.slots[i] = itemstack;

		if ((itemstack != null) && (itemstack.stackSize > getInventoryStackLimit())) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public String getInventoryName() {
		return "Sonar Inventory";
	}

	@Override
	public void markDirty() {
		System.out.print("mark");
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}
}
