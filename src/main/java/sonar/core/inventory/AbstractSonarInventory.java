package sonar.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.DirtyPart;

public abstract class AbstractSonarInventory<T extends AbstractSonarInventory> extends DirtyPart implements ISonarInventory{

	public ItemStack[] slots;
	public int limit = 64;

	public AbstractSonarInventory(int size) {
		this.slots = new ItemStack[size];
	}

	public T setStackLimit(int limit) {
		this.limit = limit;
		return (T) this;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SAVE) {
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

	public void writeData(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SAVE) {
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

	public int getSizeInventory() {
		return this.slots.length;
	}

	public ItemStack getStackInSlot(int slot) {
		return this.slots[slot];
	}

	public ItemStack decrStackSize(int slot, int var2) {
		if (this.slots[slot] != null) {
			setChanged(true);
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

	public ItemStack removeStackFromSlot(int i) {
		if (this.slots[i] != null) {
			setChanged(true);
			ItemStack itemstack = this.slots[i];
			this.slots[i] = null;
			return itemstack;
		}
		return null;
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.slots[i] = itemstack;

		if ((itemstack != null) && (itemstack.stackSize > getInventoryStackLimit())) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		setChanged(true);
	}

	public int getInventoryStackLimit() {
		return limit;
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	public void openInventory(EntityPlayer player) {}

	public void closeInventory(EntityPlayer player) {}

	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}
/*
	*/
	public int getField(int id) {
		return 0;
	}

	public void setField(int id, int value) {}

	public int getFieldCount() {
		return 0;
	}

	public void clear() {
		for (int i = 0; i < this.getSizeInventory(); i++)
			this.setInventorySlotContents(i, null);
	}
}
